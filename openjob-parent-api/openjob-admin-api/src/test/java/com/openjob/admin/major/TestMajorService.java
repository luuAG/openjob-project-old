package com.openjob.admin.major;

import com.openjob.common.model.Major;
import com.openjob.common.model.Skill;
import com.openjob.common.model.Specialization;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMajorService {
    @Autowired
    private MajorRepository majorRepo;

    @Test
    public void insertMajor(){
        List<Skill> skills;
        List<Specialization> specializations;

        Major m1 = new Major();
        m1.setName("Công nghệ phần mềm");

        Specialization sp1 = new Specialization();
        sp1.setName("Lập trình viên Front-end");
        sp1.setMajor(m1);

        Specialization sp2 = new Specialization();
        sp2.setName("Lập trình viên Back-end");
        sp2.setMajor(m1);

        Skill s1 = new Skill();
        s1.setName("HTML, CSS, JS");
        s1.setSpecialization(sp1);
        Skill s2 = new Skill();
        s2.setName("ReactJS");
        s2.setSpecialization(sp1);
        Skill s3 = new Skill();
        s3.setName("Angular");
        s3.setSpecialization(sp1);
        Skill s4 = new Skill();
        s4.setName("VueJS");
        s4.setSpecialization(sp1);
        Skill s5 = new Skill();
        s5.setName("Java");
        s5.setSpecialization(sp2);
        Skill s6 = new Skill();
        s6.setName("Spring framework");
        s6.setSpecialization(sp2);
        Skill s7 = new Skill();
        s7.setName("Hibernate");
        s7.setSpecialization(sp2);
        Skill s8 = new Skill();
        s8.setName("C#");
        s8.setSpecialization(sp2);
        Skill s9 = new Skill();
        s9.setName("ASP.NET");
        s9.setSpecialization(sp2);
        Skill s10 = new Skill();
        s10.setName(".NET Core");
        s10.setSpecialization(sp2);

        sp1.setSkills(List.of(s1, s2, s3, s4));
        sp2.setSkills(List.of(s5, s6, s7, s8, s9, s10));

        m1.setSpecializations(List.of(sp1, sp2));

        Major saved = majorRepo.save(m1);
        Assert.assertNotNull(saved);
        Assert.assertNotNull(saved.getSpecializations());
        Assert.assertNotNull(((Specialization)saved.getSpecializations().toArray()[0]).getSkills().toArray()[0]);
    }
}
