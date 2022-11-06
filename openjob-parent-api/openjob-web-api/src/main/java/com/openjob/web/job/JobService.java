package com.openjob.web.job;

import com.openjob.common.model.Job;
import com.openjob.common.model.Skill;
import com.openjob.common.model.SkillExperience;
import com.openjob.web.company.CompanyRepository;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.skillexperience.SkillExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepo;
    private final SkillRepository skillRepo;
    private final SkillExperienceService skillExperienceService;
    private final CompanyRepository companyRepo;

    public Page<Job> searchByKeywordAndLocation(Integer size, Integer page, String keyword, String location) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(location) && location.isBlank()){
            if (Objects.isNull(keyword) && keyword.isBlank())
                return jobRepo.findAll(pageable);
            else
                return jobRepo.findByKeyword(keyword, pageable);
        } else {
            if (Objects.isNull(keyword) && keyword.isBlank())
                return jobRepo.findByLocation(location, pageable);
            else
                return jobRepo.findByKeywordAndLocation(keyword, location, pageable);
        }
    }

    public Optional<Job> getById(String id) {
        return jobRepo.findById(id);
    }

    public Job saveNewJob(Job job) throws SQLException {
        List<SkillExperience> realListSkillExperience = new ArrayList<>();

        for (int i = 0; i < job.getListSkillExperience().size(); i++) {
            SkillExperience SEfromRequest = (SkillExperience) ((List) job.getListSkillExperience()).get(i);
            SkillExperience SEinDB =
                    skillExperienceService.getBySkillAndExperience(
                            SEfromRequest.getSkill().getName(), SEfromRequest.getExperience().getValue());
            if (Objects.nonNull(SEinDB)){
                realListSkillExperience.add(SEinDB);
            } else {
                Skill skill = SEfromRequest.getSkill();
                // New skill
                if (Objects.isNull(skill.getId())){
                    skill.setSpecialization(job.getSpecialization());
                    skill.setIsVerified(false);
                    Skill savedSkill = skillRepo.saveAndFlush(skill);
                    SEfromRequest.setSkill(savedSkill);
                    SkillExperience savedSE = skillExperienceService.saveUpdate(SEfromRequest);
                    if (Objects.nonNull(savedSE))
                        realListSkillExperience.add(savedSE);
                    else
                        throw new SQLException("Error when save skill "+skill.getName()+" and experience "
                                + SEfromRequest.getExperience().getValue().name());
                }
            }

        }

        job.setListSkillExperience(realListSkillExperience);

        job.setCreatedAt(new Date());
        return jobRepo.save(job);
    }
}
