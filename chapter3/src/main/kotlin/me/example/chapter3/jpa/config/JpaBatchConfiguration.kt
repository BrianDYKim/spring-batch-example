package me.example.chapter3.jpa.config

import me.example.chapter3.jpa.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/09
 */
@Configuration
class JpaBatchConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val jobLauncher: JobLauncher,
    private val entityManagerFactoryBean: AbstractEntityManagerFactoryBean
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // entityManagerFactory 자체는 Bean 으로 등록이 되어있지 않음
    private val entityManagerFactory = entityManagerFactoryBean.nativeEntityManagerFactory

    companion object {
        const val JOB_NAME = "jpa_job"
        const val STEP_NAME = "jpa_job_step"
    }

    @Bean
    fun startJpaJob(): CommandLineRunner = CommandLineRunner {
        val jobParameters = JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()

        jobLauncher.run(customJpaJob(), jobParameters)
    }

    @Bean
    fun customJpaJob(): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(customJpaJobStep())
            .build()
    }

    private fun customJpaJobStep(): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<Person, Person>(10, transactionManager)
            .reader(jpaCursorItemReader())
            .writer(itemWriter())
            .build()
    }

    private fun jpaCursorItemReader(): JpaCursorItemReader<Person> {
        val itemReader = JpaCursorItemReaderBuilder<Person>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select p from Person p") // jpql 문법을 따름
            .build()

        itemReader.afterPropertiesSet()

        return itemReader
    }

    private fun itemWriter(): ItemWriter<Person> = ItemWriter {
        it.items.forEach {
            logger.info(
                "id : {}, name : {}, age : {}, address : {}",
                it.id,
                it.name,
                it.age,
                it.address
            )
        }
    }
}