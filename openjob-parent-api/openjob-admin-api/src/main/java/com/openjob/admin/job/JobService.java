package com.openjob.admin.job;

import com.openjob.admin.company.CompanyService;
import com.openjob.admin.setting.SettingService;
import com.openjob.admin.statistics.StatisticService;
import com.openjob.admin.trackinginvoice.InvoiceService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.*;
import com.openjob.common.model.*;
import com.openjob.web.util.JobCVUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@EnableAsync
public class JobService {
    private final JobRepository jobRepo;
    private final CustomJavaMailSender mailSender;
    private final SettingService settingService;
    private final InvoiceService invoiceService;
    private final CompanyService companyService;
    private final CvRepository cvRepo;
    private final JobCvService jobCvService;
    private final StatisticService statisticService;

    public Optional<Job> getById(String jobId) {
        return jobRepo.findById(jobId);
    }

    public Page<Job> search(Specification<Job> jobSpec, Pageable pageable) {
        return jobRepo.findAll(jobSpec, pageable);
    }

    public void approve(List<Job> jobs) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.approveByIds(ids);
        // send mail notification to company
        for (Job job : jobs){
            Company company = companyService.get(job.getCompany().getId()).orElseThrow();
            job.setCompany(company);
            MailSetting mailSetting = new MailSetting(
                    company.getEmail(),
                    "Công việc đã được duyệt",
                    settingService.getByName(MailCase.MAIL_JOB_APPROVED.name()).orElseThrow().getValue(),
                    null,
                    company,
                    job,
                    null);
            mailSender.sendMail(mailSetting); // async

            if (MemberType.PREMIUM.equals(company.getMemberType()))
                findCVmatchJob(job); // async

            // tracking for statistics
            CompanyStatistic companyStatistic = new CompanyStatistic();
            companyStatistic.setCompanyId(company.getId());
            companyStatistic.setCompanyName(company.getName());
            companyStatistic.setJobId(job.getId());
            companyStatistic.setJobTitle(job.getTitle());
            companyStatistic.setJobCreatedAt(new Date());
            statisticService.trackJob(companyStatistic); // async
        }

    }

    @Async
    public void findCVmatchJob(Job savedJob) {
        List<CV> listCV = cvRepo.findBySpecializationId(savedJob.getSpecialization().getId());
        List<JobSkill> jobSkills = savedJob.getJobSkills();
        Set<Integer> mustHaveSkillIds = jobSkills.stream()
                .filter(JobSkill::isRequired)
                .map(jobSkill -> jobSkill.getSkill().getId())
                .collect(Collectors.toSet());
        // filter list CV by job requirement: must-have & yoe
        listCV = listCV.stream()
                .filter(cv -> { // must-have
                    Set<Integer> cvSkillIds = cv.getSkills().stream()
                            .map(cvSkill -> cvSkill.getSkill().getId())
                            .collect(Collectors.toSet());
                    return cvSkillIds.containsAll(mustHaveSkillIds);
                })
                .filter(cv -> { // yoe
                    Skill mutualSkill;
                    for (CvSkill cvSkill : cv.getSkills()){
                        for (JobSkill jobSkill : jobSkills){
                            if (Objects.equals(cvSkill.getSkill().getId(), jobSkill.getSkill().getId())){
                                mutualSkill = cvSkill.getSkill();
                                if (mustHaveSkillIds.contains(mutualSkill.getId()) && cvSkill.getYoe() < jobSkill.getYoe())
                                    return false;
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        boolean hasMatch = false;
        for (CV cv : listCV) {
            double matchingPoint = JobCVUtils.scoreCv(savedJob, cv);
            if (matchingPoint > 0) {
                Optional<JobCV> existingJobCv =  jobCvService.getByJobIdAndCvId(savedJob.getId(), cv.getId());
                if (existingJobCv.isPresent()){
                    existingJobCv.get().setIsMatched(true);
                    existingJobCv.get().setPoint(matchingPoint);
                    jobCvService.save(existingJobCv.get());
                }
                else {
                    JobCV newJobCv = new JobCV();
                    newJobCv.setJob(savedJob);
                    newJobCv.setStatus(CvStatus.NEW);
                    newJobCv.setIsMatched(true);
                    newJobCv.setPoint(matchingPoint);
                    newJobCv.setCv(cv);
                    newJobCv.setApplyDate(null);
                    newJobCv.setIsApplied(false);
                    jobCvService.save(newJobCv);
                }
                hasMatch = true;
            }
        }

        if (hasMatch){ // send mail
            MailSetting mailSetting = new MailSetting(
                    savedJob.getCompany().getEmail(),
                    "Đã có ứng viên phù hợp với công việc",
                    settingService.getByName(MailCase.MAIL_JOB_HAS_MATCH.name()).orElseThrow().getValue(),
                    null,
                    savedJob.getCompany(),
                    savedJob,
                    null);
            mailSender.sendMail(mailSetting);
        }

    }

    public void reject(List<Job> jobs, List<String> rejectReasons) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.rejectByIds(ids);
        // send mail about the reason and refund
        for (int i=0; i<jobs.size(); i++){
            // refund
            Company company = companyService.get(jobs.get(i).getCompany().getId()).orElseThrow();
            company.setAccountBalance(company.getAccountBalance() + jobs.get(i).getPrice());
            companyService.save(company);

            // tracking
            Invoice invoice = new Invoice();
            invoice.setCompanyId(jobs.get(i).getCompany().getId());
            invoice.setCompanyName(jobs.get(i).getCompany().getName());
            invoice.setServiceType(ServiceType.REFUND);
            invoice.setAmount(jobs.get(i).getPrice());
            invoiceService.save(invoice);

            // send mail
            Map<String, String> extraData = new HashMap<>();
            extraData.put(MailTemplateVariable.REASON.getTemplateVariable(), rejectReasons.get(i) == null ? "" : rejectReasons.get(i));

            MailSetting mailSetting = new MailSetting(
                    company.getEmail(),
                    "Công việc không được duyệt",
                    settingService.getByName(MailCase.MAIL_JOB_REJECTED.name()).orElseThrow().getValue(),
                    null,
                    company,
                    jobs.get(i),
                    extraData);
            mailSender.sendMail(mailSetting); // async
        }
    }


    public void deactivate(String jobId, String reason) {
        Job job = getById(jobId).orElseThrow();
        job.setJobStatus(JobStatus.HIDDEN);
        job.setUpdatedAt(new Date());
        job.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        jobRepo.save(job);
        // send mail to company
        Map<String, String> extraData = new HashMap<>();
        extraData.put(MailTemplateVariable.REASON.getTemplateVariable(), reason);
        MailSetting mailSetting = new MailSetting(
                job.getCompany().getEmail(),
                "Tin tuyển dụng của công ty đã bị vô hiệu hoá",
                settingService.getByName(MailCase.MAIL_JOB_DEACTIVATED.name()).orElseThrow().getValue(),
                null,
                job.getCompany(),
                job,
                extraData);
        mailSender.sendMail(mailSetting); // async
        // send mail to all users applied this job
        List<User> users = jobRepo.findAllUserAppliedJob(jobId);
        for (User user : users){
            MailSetting mailSettingTemp = new MailSetting(
                    user.getEmail(),
                    "Công việc bạn ứng tuyển đã ngưng tuyển",
                    settingService.getByName(MailCase.MAIL_JOB_STOP_TO_USER.name()).orElseThrow().getValue(),
                    user,
                    job.getCompany(),
                    job,
                    null);
            mailSender.sendMail(mailSettingTemp); // async
        }
    }

    public void activate(String jobId, String reason) {
        Job job = getById(jobId).orElseThrow();
        if (!JobStatus.HIDDEN.equals(job.getJobStatus()))
            return;
        job.setJobStatus(JobStatus.APPROVED);
        job.setUpdatedAt(new Date());
        job.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        jobRepo.save(job);

        // send mail to company
        Map<String, String> extraData = new HashMap<>();
        extraData.put(MailTemplateVariable.REASON.getTemplateVariable(), reason);
        MailSetting mailSetting = new MailSetting(
                job.getCompany().getEmail(),
                "Tin tuyển dụng của công ty đã được kích hoạt",
                settingService.getByName(MailCase.MAIL_JOB_REACTIVATED.name()).orElseThrow().getValue(),
                null,
                job.getCompany(),
                job,
                extraData);
        mailSender.sendMail(mailSetting); // async

        // send mail to all users applied this job
        List<User> users = jobRepo.findAllUserAppliedJob(jobId);
        for (User user : users){
            MailSetting mailSettingTemp = new MailSetting(
                    user.getEmail(),
                    "Công việc bạn ứng tuyển đã mở lại",
                    settingService.getByName(MailCase.MAIL_JOB_REOPEN_TO_USER.name()).orElseThrow().getValue(),
                    user,
                    job.getCompany(),
                    job,
                    null);
            mailSender.sendMail(mailSettingTemp); // async
        }
    }
}
