package com.openjob.web;

import com.openjob.web.config.AppProperties;
import com.openjob.web.job.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableJpaRepositories(basePackages = "com.openjob.web.*")
@ComponentScan(basePackages = "com.openjob.web.*")
@EntityScan(basePackages = "com.openjob.common.*")
public class WebMain implements CommandLineRunner {
    @Autowired
    private JobService jobService;

    public static void main(String[] args) {
        SpringApplication.run(WebMain.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        jobService.updateStatusExpiredJob();
    }
}
