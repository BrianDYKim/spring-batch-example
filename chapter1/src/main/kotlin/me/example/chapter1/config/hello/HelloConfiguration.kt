package me.example.chapter1.config.hello

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/07
 */
@Configuration
class HelloConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val tasklet: HelloTasklet,
    private val jobLauncher: JobLauncher
) {
    companion object {
        const val JOB_NAME = "helloJob"
        const val JOB_STEP = "${JOB_NAME}_STEP"
    }

    @Bean
    fun jobStartUp(): CommandLineRunner {
        return CommandLineRunner {
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()

            jobLauncher.run(job(), jobParameters)
        }
    }

    @Bean
    fun job(): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step())
            .build()
    }

    @Bean
    fun step(): Step {
        return StepBuilder(JOB_STEP, jobRepository)
            .tasklet(tasklet, transactionManager)
            .build()
    }
}