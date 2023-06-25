package com.openjob.web.cv;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.enums.MailCase;
import com.openjob.common.model.*;
import com.openjob.web.business.OpenjobBusinessService;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.CVwithExtraDataDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.major.MajorService;
import com.openjob.web.setting.SettingService;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.AuthenticationUtils;
import com.openjob.web.util.CustomJavaMailSender;
import com.openjob.web.util.JobCVUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@EnableAsync
public class CvService {
    private final CvRepository cvRepo;
    private final SkillRepository skillRepo;
    private final SpecializationService speService;
    private final UserService userService;
    private final JobCvService jobCvService;
    private final MajorService majorService;
    private final CvSkillRepository cvSkillRepo;
    private final AuthenticationUtils authenticationUtils;
    private final CustomJavaMailSender mailSender;
    private final SettingService settingService;
    private final CvCompanyRepository cvCompanyRepo;
    private final CompanyService companyService;
    private final OpenjobBusinessService openjobBusinessService;

    public Optional<CV> getById(String id) {
        return cvRepo.findById(id);
    }

    public Optional<CV> getByUserId(String userId) {
        return cvRepo.findByUserId(userId);
    }

    public CV saveUpdate(CVRequestDTO cvDto, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        Optional<CV> optionalCV = getByUserId(cvDto.getUserId());
        CV cv;
        if (optionalCV.isPresent()){
            cv = optionalCV.get();
        }
        else {
            cv = new CV();
            Optional<User> user = userService.get(cvDto.getUserId());
            cv.setUser(user.orElseThrow());
        }
        NullAwareBeanUtils mapper = NullAwareBeanUtils.getInstance();
        mapper.copyProperties(cv, cvDto);

        Optional<Specialization> specialization = speService.getById(cvDto.getSpecializationId());
        Optional<Major> major = majorService.getById(cvDto.getMajorId());

        cv.setSpecialization(specialization.orElseThrow());
        cv.setMajor(major.orElseThrow());


        cv.getSkills().clear();
        CV savedCV = cvRepo.save(cv);
        if (cv.getId() != null) {
            cvSkillRepo.deleteByCvId(cv.getId());
        }

        // detect new skill
        for (int i = 0; i < cvDto.getListSkill().size(); i++) {
            Skill skillFromRequest = cvDto.getListSkill().get(i).getSkill();
            Optional<Skill> skillInDB = skillRepo.findByName(skillFromRequest.getName());

            CvSkill realCvSkill = new CvSkill();
            realCvSkill.setCv(savedCV);
            realCvSkill.setYoe(cvDto.getListSkill().get(i).getYoe());
            if (skillInDB.isEmpty()) { // new skill
                skillFromRequest.setSpecialization(cv.getSpecialization());
                skillFromRequest.setIsVerified(false);
                skillFromRequest.setCreatedAt(new Date());
                skillFromRequest.setCreatedBy(authenticationUtils.getLoggedInUser(request).getFirstName());
                Skill savedSkill = skillRepo.save(skillFromRequest);
                realCvSkill.setSkill(savedSkill);
            } else {                   // old skill
                realCvSkill.setSkill(skillInDB.get());
            }
            savedCV.getSkills().add(realCvSkill);
        }
        return cvRepo.save(cv);
    }

    public Page<CV> search(Specification<CV> cvSpec, Pageable pageable) {
        return cvRepo.findAll(cvSpec, pageable);
    }


    public List<CvDTO> mapToCvDto(List<?> content) {
        if (content == null || content.size() == 0)
            return (List<CvDTO>) content;
        if (content.get(0) instanceof JobCV) {
            return content.stream().map(item -> {
                CvDTO cvDTO = new CvDTO();
                JobCV jobCV = (JobCV) item;
                try {
                    NullAwareBeanUtils.getInstance().copyProperties(cvDTO, jobCV.getCv());
                    NullAwareBeanUtils.getInstance().copyProperties(cvDTO, jobCV);
                    cvDTO.setUserId(jobCV.getCv().getUser().getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println("Error while copying JobCV to CvDTO in CvService");
                    throw new RuntimeException("Lỗi hệ thống");
                }
                return cvDTO;
            }).collect(Collectors.toList());
        }
        else if (content.get(0) instanceof CV) {
            return content.stream().map(item -> {
                CV cv = (CV) item;
                CvDTO cvDTO = new CvDTO();
                try {
                    NullAwareBeanUtils.getInstance().copyProperties(cvDTO, cv);
                    cvDTO.setUserId(cv.getUser().getId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println("Error while copying CV to CvDTO in CvService");
                    throw new RuntimeException("Lỗi hệ thống");
                }
                return cvDTO;
            }).collect(Collectors.toList());
        }
        return null;

    }

    @Scheduled(cron = "0 0 */12 * * *")
    @Async
    public void matchCvAndJobs(){

    }

    @Async
    public void findCVmatchJob(Job savedJob) {
        List<CV> listCV = cvRepo.findBySpecialization(savedJob.getSpecialization().getId());
        List<JobSkill> jobSkills = savedJob.getJobSkills();
        Set<Skill> mustHaveSkills = jobSkills.stream()
                .filter(JobSkill::isRequired)
                .map(JobSkill::getSkill)
                .collect(Collectors.toSet());
        // filter list CV by job requirement: must-have & yoe
        listCV = listCV.stream()
                .filter(cv -> { // must-have
                    Set<Skill> tempMustHaveSkills = new HashSet<>(mustHaveSkills);
                    List<Skill> cvSkill = cv.getSkills().stream().map(CvSkill::getSkill).collect(Collectors.toList());
                    cvSkill.forEach(tempMustHaveSkills::remove);
                    return tempMustHaveSkills.isEmpty();
                })
                .filter(cv -> { // yoe
                    Skill mutualSkill;
                    for (CvSkill cvSkill : cv.getSkills()){
                        for (JobSkill jobSkill : jobSkills){
                            if (Objects.equals(cvSkill.getSkill().getId(), jobSkill.getSkill().getId())){
                                mutualSkill = cvSkill.getSkill();
                                if (mustHaveSkills.contains(mutualSkill) && cvSkill.getYoe() < jobSkill.getYoe())
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

        if (hasMatch){
            MailSetting mailSetting = new MailSetting(
                    savedJob.getCompany().getEmail(),
                    "Đã có ứng viên phù hợp với công việc",
                    settingService.getByMailCase(MailCase.MAIL_JOB_HAS_MATCH).getValue(),
                    null,
                    savedJob.getCompany(),
                    savedJob,
                    null);
            mailSender.sendMail(mailSetting);
        }

    }

    public CVwithExtraDataDTO getCvForCompanyView(String cvId, String companyId) {
        boolean isCharged = cvRepo.checkCompanyChargedToViewCv(cvId, companyId);
        CV cv = cvRepo.getById(cvId);
        CVwithExtraDataDTO cvDTO = new CVwithExtraDataDTO();
        cvDTO.setCv(cv);
        cvDTO.setChargedToView(isCharged);

        return cvDTO;
    }

    public void chargeCompanyForViewCv(String cvId, String companyId) {
        CvCompany cvCompany = new CvCompany();
        cvCompany.setCompany(companyService.getById(companyId));
        cvCompany.setCv(getById(cvId).orElseThrow());
        cvCompany.setCreatedAt(new Date());
        cvCompanyRepo.save(cvCompany);

        companyService.updateAccountBalance(companyId, - openjobBusinessService.get().getBaseCvViewPrice());
    }
}
