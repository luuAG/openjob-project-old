package com.openjob.web.job;

import com.openjob.common.enums.JobStatus;
import com.openjob.common.enums.MailCase;
import com.openjob.common.enums.Role;
import com.openjob.common.enums.ServiceType;
import com.openjob.common.model.*;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.JobRequestDTO;
import com.openjob.web.dto.JobResponseDTO;
import com.openjob.web.dto.JobSkillDTO;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.jobskill.JobSkillRepository;
import com.openjob.web.setting.SettingService;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.trackinginvoice.InvoiceService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.AuthenticationUtils;
import com.openjob.web.util.CustomJavaMailSender;
import com.openjob.web.util.NullAwareBeanUtils;
import com.openjob.web.util.PriceCalculationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableAsync
@Service
@Transactional
@RequiredArgsConstructor
public class JobService {
    private final UserService userService;
    private final JobRepository jobRepo;
    private final SkillRepository skillRepo;
    private final JobCvService jobCvService;
    private final CompanyService companyService;
    private final SpecializationService speService;
    private final JobSkillRepository jobSkillRepo;
    private final AuthenticationUtils authenticationUtils;
    private final PriceCalculationUtils priceCalculationUtils;
    private final InvoiceService invoiceService;
    private final SettingService settingService;
    private final CustomJavaMailSender mailSender;

    private List<Job> expiredJobs = new ArrayList<>();

    public Optional<Job> getById(String id) {
        return jobRepo.findById(id);
    }

    public Job saveUpdate(JobRequestDTO jobDTO, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        // validation
        Company company = companyService.getById(jobDTO.getCompanyId());
        Optional<Specialization> specialization = speService.getById(jobDTO.getSpecializationId());
        if (Objects.isNull(company) || specialization.isEmpty())
            throw new IllegalArgumentException("Company/Specialization not found!");

        NullAwareBeanUtils beanCopier = NullAwareBeanUtils.getInstance();
        Job job;
        if (jobDTO.getId() == null) { // new job
            job = new Job();
            job.setCreatedAt(new Date());
            job.setJobStatus(JobStatus.NEW);

            // charge for job
            double price = priceCalculationUtils.calculateJobPrice(company.getId(), jobDTO);
            job.setPrice(price);
            if (price == 0.0f){
                company.setAmountOfFreeJobs(company.getAmountOfFreeJobs() - 1);
                companyService.save(company);
            }
            else{
                companyService.updateAccountBalance(company.getId(), -price);
                // tracking
                Invoice invoice = new Invoice();
                invoice.setCompanyId(company.getId());
                invoice.setCompanyName(company.getName());
                invoice.setServiceType(ServiceType.JOB_POST);
                invoice.setAmount(price);
                invoiceService.save(invoice);
            }
        }
        else { // update job
            job = getById(jobDTO.getId()).orElseThrow();
            job.setUpdatedAt(new Date());
            // charge for job
            double price = priceCalculationUtils.calculateJobPrice(company.getId(), jobDTO);
            if (price != 0.0f){
                companyService.updateAccountBalance(company.getId(), job.getPrice() - price);
                job.setPrice(price);
                // tracking
                Invoice invoice = new Invoice();
                invoice.setCompanyId(company.getId());
                invoice.setCompanyName(company.getName());
                invoice.setServiceType(ServiceType.UPDATE_JOB);
                invoice.setAmount(job.getPrice() - price);
                invoiceService.save(invoice);
            }
            // send mail notify to user if job turn hidden
            if (!JobStatus.HIDDEN.equals(job.getJobStatus()) && JobStatus.HIDDEN.equals(jobDTO.getJobStatus())){
                List<User> usersAppliedToJob = jobCvService.getByJobId(job.getId()).stream()
                        .map(jobCV -> jobCV.getCv().getUser())
                        .collect(Collectors.toList());
                for (User user : usersAppliedToJob){
                    MailSetting mailSetting = new MailSetting(
                            user.getEmail(),
                            "Công việc bạn ứng tuyển đã ngưng tuyển",
                            settingService.getByMailCase(MailCase.MAIL_JOB_STOP_TO_USER).getValue(),
                            user,
                            job.getCompany(),
                            job,
                            null);
                    mailSender.sendMail(mailSetting); // async
                }
            }

        }
        beanCopier.copyProperties(job, jobDTO);
        Major major = specialization.get().getMajor();
        job.setMajor(major);
        job.setCompany(company);
        job.setSpecialization(specialization.get());


        job.getJobSkills().clear();
        Job savedJob = jobRepo.save(job);
        jobSkillRepo.deleteByJobId(savedJob.getId());

        // detect new skill
        for (int i = 0; i < jobDTO.getListJobSkillDTO().size(); i++) {
            JobSkillDTO JSfromRequest = jobDTO.getListJobSkillDTO().get(i);
            JobSkill jobSkill = new JobSkill();

            Skill skillFromRequest = JSfromRequest.getSkill();
            Optional<Skill> skillInDB = skillRepo.findByName(skillFromRequest.getName());
            // if new skill -> save new skill and set to jobSkill
            if (skillInDB.isEmpty()){
                skillFromRequest.setSpecialization(savedJob.getSpecialization());
                skillFromRequest.setIsVerified(false);
                skillFromRequest.setCreatedAt(new Date());
                skillFromRequest.setCreatedBy(authenticationUtils.getLoggedInUser(request).getFirstName());
                Skill savedSkill = skillRepo.save(skillFromRequest);
                jobSkill.setSkill(savedSkill);
            } else { // if existing skill -> set to jobSkill
                jobSkill.setSkill(skillInDB.get());
            }
            jobSkill.setRequired(JSfromRequest.getIsRequired());
            jobSkill.setJob(savedJob);
            jobSkill.setWeight(JSfromRequest.getWeight());
            jobSkill.setYoe(JSfromRequest.getYoe());
            savedJob.getJobSkills().add(jobSkillRepo.save(jobSkill));
        }

        companyService.updateAccountBalance(company.getId(), - jobDTO.getJobPrice());
        return jobRepo.save(savedJob);
    }

    public void deleteById(String jobId) {
        jobCvService.deleteByJobId(jobId);
        jobRepo.deleteById(jobId);
        // refund
        Job job = getById(jobId).orElseThrow();
        if (job.getJobStatus().equals(JobStatus.NEW)){
            companyService.updateAccountBalance(job.getCompany().getId(), job.getPrice());
        }
    }


    @Scheduled(cron = "0 0 0 * * *")
    @Async
    @PostConstruct
    public void updateStatusExpiredJob() {
        List<Job> expiredJob = jobRepo.findUnhiddenExpiredJob().stream().peek(job -> job.setJobStatus(JobStatus.HIDDEN)).collect(Collectors.toList());
        jobRepo.saveAll(expiredJob);
    }

    @Scheduled(fixedDelay = 5 * 60000)
    @Async
    public void sendMailTo_CompanyAndUser_AboutJobExpiration(){
        if (this.expiredJobs.size() == 0)
            this.expiredJobs = getExpiredJob();
        for (Job job : this.expiredJobs) {
            // mail to company
            MailSetting mailSetting = new MailSetting(
                    job.getCompany().getEmail(),
                    "Tin tuyển dụng đã hết hạn",
                    settingService.getByMailCase(MailCase.MAIL_JOB_EXPIRED).getValue(),
                    null,
                    job.getCompany(),
                    job,
                    null);
            mailSender.sendMail(mailSetting); // async

            // mail to users
            List<User> users = jobCvService.getUserAppliedJob(job.getId());
            for (User user : users){
                MailSetting mailSettingTemp = new MailSetting(
                        user.getEmail(),
                        "Công việc bạn ứng tuyển đã hết hạn",
                        settingService.getByMailCase(MailCase.MAIL_JOB_STOP_TO_USER).getValue(),
                        user,
                        job.getCompany(),
                        job,
                        null);
                mailSender.sendMail(mailSettingTemp); // async
            }
        }
    }

    public List<Job> getExpiredJob() {
        return jobRepo.findExpiredJob();
    }

    public void setExpiredDate(String jobId, Date expiredDate) {
        Optional<Job> job = jobRepo.findById(jobId);
        if (job.isPresent()){
            job.get().setExpiredAt(expiredDate);
            jobRepo.save(job.get());
        } else
            throw new IllegalArgumentException("Job not found with id: " + jobId);
    }

    public List<Job> getRelevantJobs(Job job) {
        Specialization specialization = job.getSpecialization();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jobRepo.findBySpecialization(specialization.getId(), pageable);
    }


    public JobResponseDTO mapJobToJobResponseDTO(Job job, User loggedInUser){
        JobResponseDTO toReturn = new JobResponseDTO();
        NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
        try {
            copier.copyProperties(toReturn, job);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(loggedInUser) &&  !Role.HR.equals(loggedInUser.getRole()))
            toReturn.setIsApplied(jobCvService.checkUserAppliedJob(loggedInUser.getId(),job.getId()));
        return  toReturn;
    }



    public Page<Job> search(Specification<Job> jobSpec, Pageable pageable) {
        return jobRepo.findAll(jobSpec, pageable);
    }

    public Page<Job> getSuggestionJobs(Pageable pageable, User loggedInUser) {
        if (loggedInUser == null) {
            loggedInUser = userService.getByEmail("duongvannam2001@gmail.com");
        }
        if (loggedInUser.getCv() != null){
            Set<Integer> skillIds = loggedInUser.getCv().getSkills().stream()
                    .map(cvSkill -> cvSkill.getSkill().getId())
                    .collect(Collectors.toSet());

            return jobRepo.findBySkillIds(skillIds, pageable);
        }
        return new PageImpl<>(new ArrayList<>());
    }
}
