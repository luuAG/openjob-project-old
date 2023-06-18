package com.openjob.web.cv;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.*;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.job.JobRepository;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.major.MajorService;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.JobCVUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CvService {
    private final CvRepository cvRepo;
    private final SkillRepository skillRepo;
    private final SpecializationService speService;
    private final UserService userService;
    private final JobRepository jobRepo;
    private final JobCvService jobCvService;
    private final MajorService majorService;

    public Optional<CV> getById(String id) {
        return cvRepo.findById(id);
    }

    public Optional<CV> getByUserId(String userId) {
        return cvRepo.findByUserId(userId);
    }

    public CV saveUpdate(CVRequestDTO cvDto) throws InvocationTargetException, IllegalAccessException {
        CV cv = new CV();
        NullAwareBeanUtils mapper = NullAwareBeanUtils.getInstance();
        mapper.copyProperties(cv, cvDto);

        Optional<User> user = userService.get(cvDto.getUserId());
        Optional<Specialization> specialization = speService.getById(cvDto.getSpecializationId());
        Optional<Major> major = majorService.getById(cvDto.getMajorId());

        cv.setUser(user.orElseThrow());
        cv.setSpecialization(specialization.orElseThrow());
        cv.setMajor(major.orElseThrow());
        CV savedCV = cvRepo.save(cv);

        List<CvSkill> realListSkill = new ArrayList<>();

        // detect new skill
        for (int i = 0; i < cvDto.getListSkill().size(); i++) {
            Skill skillFromRequest = cv.getSkills().get(i).getSkill();
            Optional<Skill> skillInDB = skillRepo.findByName(skillFromRequest.getName());

            CvSkill realCvSkill = new CvSkill();
            realCvSkill.setCv(savedCV);
            realCvSkill.setYoe(cvDto.getListSkill().get(i).getYoe());
            if (skillInDB.isEmpty()) { // new skill
                skillFromRequest.setSpecialization(cv.getSpecialization());
                skillFromRequest.setIsVerified(false);
                Skill savedSkill = skillRepo.save(skillFromRequest);
                realCvSkill.setSkill(savedSkill);
            } else {                   // old skill
                realCvSkill.setSkill(skillInDB.get());
            }
        }

        cv.setSkills(realListSkill);
        return cvRepo.save(cv);
    }

    @Async
    public void findJobMatchCV(CV savedCV) {
//        List<Job> listJob = jobRepo.findBySpecialization(savedCV.getSpecialization().getId());
//        for (Job job : listJob) {
//            int matchingPoint = JobCVUtils.checkCVmatchJob(job, savedCV);
//            if (matchingPoint > 0) {
//                Optional<JobCV> existingJobCv =  jobCvService.getByJobIdAndCvId(job.getId(), savedCV.getId());
//                if (existingJobCv.isPresent()){
//                    existingJobCv.get().setIsMatching(true);
//                    existingJobCv.get().setPoint(matchingPoint);
//                    jobCvService.save(existingJobCv.get());
//                }
//                else {
//                    JobCV newJobCv = new JobCV();
//                    newJobCv.setJob(job);
//                    newJobCv.setStatus(CvStatus.NEW);
//                    newJobCv.setIsMatching(true);
//                    newJobCv.setPoint(matchingPoint);
//                    newJobCv.setCv(savedCV);
//                    newJobCv.setIsApplied(false);
//                    jobCvService.save(newJobCv);
//                }
//            }
//        }
    }

    public Page<CV> getByJobId(Integer page, Integer size, String jobId) {
        Pageable pageable = PageRequest.of(page, size);
        return cvRepo.findByJobId(jobId, pageable);
    }

    public Page<CV> getCvAppliedByJobId(Integer page, Integer size, String jobId) {
        Pageable pageable = PageRequest.of(page, size);
        return cvRepo.findCvAppliedByJobId(jobId, pageable);
    }

    public Page<CV> search(Specification<CV> cvSpec, Pageable pageable) {
        return cvRepo.findAll(cvSpec, pageable);
    }

    public List<CvDTO> mapToCvDto(List<CV> listCv) {
        List<CvDTO> toReturn = new ArrayList<>();
        listCv.forEach(cv -> {
            CvDTO cvDTO = new CvDTO();
            try {
                NullAwareBeanUtils.getInstance().copyProperties(cvDTO, cv);
                cvDTO.setUserId(cv.getUser().getId());
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Error while copying CV to CvDTO in CvService");
                throw new RuntimeException("Lỗi hệ thống");
            }
        });
        return toReturn;
    }
}
