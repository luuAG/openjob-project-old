package com.openjob.web.job;

import com.openjob.common.model.Job;
import com.openjob.common.model.Skill;
import com.openjob.common.model.SkillExperience;
import com.openjob.web.company.CompanyRepository;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.skillexperience.SkillExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
                    Skill savedSkill = skillRepo.save(skill);
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

    public Page<Job> searchByKeywordAndLocationAndCompany(Integer size, Integer page, String keyword, String location, String companyId) {
        Page<Job> pageJob;
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(location) || location.isBlank()){
            if (Objects.isNull(keyword) || keyword.isBlank())
                pageJob = jobRepo.findAll(pageable);
            else
                pageJob = jobRepo.findByKeyword(keyword, pageable);
        } else {
            if (Objects.isNull(keyword) || keyword.isBlank())
                pageJob = jobRepo.findByLocation(location, pageable);
            else
                pageJob = jobRepo.findByKeywordAndLocation(keyword, location, pageable);
        }
        if (Objects.nonNull(companyId) && !companyId.isBlank()){
            List<Job> jobs = new ArrayList<>(pageJob.getContent());
            for (int i=0; i< jobs.size(); i++)
                if ( ! jobs.get(i).getCompany().getId().equals(companyId))
                    jobs.remove(jobs.get(i));

            return new PageImpl<>(jobs);
        }
        return pageJob;
    }

    public void deleteById(String jobId){
        jobRepo.deleteById(jobId);
    }
}
