package com.openjob.web.cv;

import com.openjob.common.model.*;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.job.JobRepository;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.major.MajorService;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.AuthenticationUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CvSkillRepository cvSkillRepo;
    private final AuthenticationUtils authenticationUtils;

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
}
