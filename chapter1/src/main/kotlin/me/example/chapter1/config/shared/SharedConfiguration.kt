package me.example.chapter1.config.shared

import me.example.chapter1.config.shared.tasklet.SharedStep1Tasklet
import me.example.chapter1.config.shared.tasklet.SharedStep2Tasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
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
 * @since 2023/10/08
 */
@Configuration
class SharedConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val jobLauncher: JobLauncher,
    private val step1Tasklet: SharedStep1Tasklet,
    private val step2Tasklet: SharedStep2Tasklet,
) {
    companion object {
        const val SHARED_JOB_NAME = "shared_job"
        const val SHARED_JOB_STEP1 = "${SHARED_JOB_NAME}_STEP_1"
        const val SHARED_JOB_STEP2 = "${SHARED_JOB_NAME}_STEP_2"
    }

    @Bean
    fun jobStart(): CommandLineRunner {
        return CommandLineRunner {
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()

            jobLauncher.run(sharedJob(), jobParameters)
        }
    }

    @Bean
    fun sharedJob(): Job {
        return JobBuilder(SHARED_JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(sharedStep1())
            .next(sharedStep2())
            .build()
    }

    private fun sharedStep1(): Step {
        return StepBuilder(SHARED_JOB_STEP1, jobRepository)
            .tasklet(step1Tasklet, transactionManager)
            .build()
    }

    private fun sharedStep2(): Step {
        return StepBuilder(SHARED_JOB_STEP2, jobRepository)
            .tasklet(step2Tasklet, transactionManager)
            .build()
    }
}