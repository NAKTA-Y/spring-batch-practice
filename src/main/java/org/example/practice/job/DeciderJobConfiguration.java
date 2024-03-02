package org.example.practice.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Random;

@Slf4j
@Configuration
public class DeciderJobConfiguration {

    @Bean
    public Job deciderJob(JobRepository jobRepository,
                          Step evenStep,
                          Step oddStep) {
        return new JobBuilder("deciderJob", jobRepository)
                    .start(decider())
                    .from(decider())
                    .on("EVEN")
                    .to(evenStep)
                    .from(decider())
                    .on("ODD")
                    .to(oddStep)
                .end()
                .build();
    }

    @Bean
    public Step evenStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager) {

        return new StepBuilder("evenStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> 짝수요");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step oddStep(JobRepository jobRepository,
                         PlatformTransactionManager transactionManager) {

        return new StepBuilder("oddStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> 홀수요");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random rand = new Random();

            int randomNumber = rand.nextInt(50) + 1;
            log.info("랜덤숫자: {}", randomNumber);

            if(randomNumber % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }
        }
    }
}
