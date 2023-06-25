package com.openjob.admin.job;

import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.MailCase;
import com.openjob.common.enums.MailTemplateVariable;
import com.openjob.common.model.Job;
import com.openjob.common.model.MailSetting;
import com.openjob.common.model.SalaryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepo;
    private final CustomJavaMailSender mailSender;
    private final SettingService settingService;

    public Optional<Job> getById(String jobId) {
        return jobRepo.findById(jobId);
    }

    public Page<Job> search(Specification<Job> jobSpec, Pageable pageable) {
        return jobRepo.findAll(jobSpec, pageable);
    }

    public void approve(List<Job> jobs) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.approveByIds(ids);
        // TODO: send mail notification
        for (Job job : jobs){
            MailSetting mailSetting = new MailSetting(
                    job.getCompany().getEmail(),
                    "Công việc đã được duyệt",
                    settingService.getByName(MailCase.MAIL_JOB_APPROVED.name()).orElseThrow().getValue(),
                    null,
                    job.getCompany(),
                    job,
                    null);
            mailSender.sendMail(mailSetting); // async
        }
    }

    public void reject(List<Job> jobs, List<String> rejectReasons) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.rejectByIds(ids);
        // send mail about the reason and refund
        for (int i=0; i<jobs.size(); i++){
            // refund


            // send mail
            Map<String, String> extraData = new HashMap<>();
            extraData.put(MailTemplateVariable.REASON.getTemplateVariable(), rejectReasons.get(i) == null ? "" : rejectReasons.get(i));

            MailSetting mailSetting = new MailSetting(
                    jobs.get(i).getCompany().getEmail(),
                    "Công việc không được duyệt",
                    settingService.getByName(MailCase.MAIL_JOB_REJECTED.name()).orElseThrow().getValue(),
                    null,
                    jobs.get(i).getCompany(),
                    jobs.get(i),
                    extraData);
            mailSender.sendMail(mailSetting); // async
        }
    }


}
