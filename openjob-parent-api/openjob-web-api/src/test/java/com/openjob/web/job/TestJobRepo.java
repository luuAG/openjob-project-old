package com.openjob.web.job;

import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.*;
import com.openjob.web.company.CompanyRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJobRepo {
    @Autowired
    private JobRepository jobRepo;
    @Autowired
    private CompanyRepository companyRepo;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void insertJob(){
        Job job = new Job();
        Company company = companyRepo.getById("297eaef983de8b5d0183dec8a57c0003");
        List<SkillExperience> listSe =  new ArrayList<>();
        listSe.add(entityManager.find(SkillExperience.class, 1));
        listSe.add(entityManager.find(SkillExperience.class, 2));

        job.setCompany(company);
        job.setTitle("Test title");
        job.setDescription("Test Description");
        job.setSalary("10tr-20tr");
        job.setCreatedAt(new Date());
        job.setWorkPlace(WorkPlace.HYBRID);
        job.setMajor(entityManager.find(Major.class, 2));
        job.setSpecialization(entityManager.find(Specialization.class, 1));
        job.setListSkillExperience(listSe);

        Job saved = jobRepo.save(job);
        Assert.assertNotNull(saved);
    }
}
