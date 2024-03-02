package org.example.practice.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    @Bean
    public Job simpleJob(JobRepository jobRepository, Step simpleStep1) {
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep1)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            @Value("#{jobParameters[requestDate]}") String requestDate) {

        Tasklet tasklet = (contribution, chunkContext) -> {
            log.info(
                    """
                            myTasklet1
                            ==============================================
                            >> contribution = {}
                            >> chunkContext = {}
                            >> requestDate = {}
                            ==============================================
                            """, contribution, chunkContext, requestDate
            );

            return RepeatStatus.FINISHED;
        };

        return new StepBuilder("simpleStep1", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
