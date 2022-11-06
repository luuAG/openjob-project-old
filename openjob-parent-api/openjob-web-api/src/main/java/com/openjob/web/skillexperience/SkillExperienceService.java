package com.openjob.web.skillexperience;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.SkillExperience;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillExperienceService {
    private final SkillExperienceRepository skillExperienceRepo;


    public SkillExperience getBySkillAndExperience(String skill, ExperienceValue experience) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<SkillExperience> pageSE = skillExperienceRepo.findBySkillAndExperience(skill, experience, pageable);
        if (pageSE.getContent().size() > 0)
            return pageSE.getContent().get(0);
        return null;
    }

    public SkillExperience saveUpdate(SkillExperience se){
        return skillExperienceRepo.save(se);
    }
}
