package org.example.practice.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class StepNextConditionalJobConfiguration {

    @Bean
    public Job stepNextConditionJob(JobRepository jobRepository,
                                    Step conditionStep1,
                                    Step conditionStep2,
                                    Step conditionStep3) {
        return new JobBuilder("stepNextConditionJob", jobRepository)
                .start(conditionStep1)
                    .on("FAILED")
                    .to(conditionStep3)
                    .on("*")
                    .end()
                .from(conditionStep1)
                    .on("*")
                    .to(conditionStep2)
                    .next(conditionStep3)
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step conditionStep1(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> conditionStep1");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step conditionStep2(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> conditionStep2");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step conditionStep3(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionStep3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> conditionStep3");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

}
