package me.example.chapter3.jpaWriter.config

import me.example.chapter3.jpaReader.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/09
 */
@Configuration
class JpaWriterBatchConfiguration(
    private val jobRepository: JobRepository,
    private val jobLauncher: JobLauncher,
    private val entityManagerFactoryBean: AbstractEntityManagerFactoryBean
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val chunkSize = 10

    private val entityManagerFactory = entityManagerFactoryBean.nativeEntityManagerFactory

    // jpa TransactionManager는 따로 선언해서 job을 실행해야함 -> common에 bean을 정의하는 것도 나쁘진않을듯
    private val transactionManager = JpaTransactionManager(entityManagerFactory)

    companion object {
        const val JOB_NAME = "jpa_writer_job"
        const val STEP_NAME = "${JOB_NAME}_STEP"
    }

    @Bean
    fun jpaWriterJob(): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(jpaWriterStep())
            .build()
    }

    private fun jpaWriterStep(): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<Person, Person>(chunkSize, transactionManager)
            .reader(itemReader())
            .writer(jpaItemWriter())
            .build()
    }

    private fun itemReader(): ItemReader<Person> {
        return ListItemReader(getItems())
    }

    private fun jpaItemWriter(): JpaItemWriter<Person> {
        val itemWriter = JpaItemWriterBuilder<Person>()
            .entityManagerFactory(entityManagerFactory)
            .build()

        itemWriter.afterPropertiesSet()

        return itemWriter
    }

    private fun getItems(): MutableList<Person> = MutableList(100) {
        Person("홍길동 $it", "${it + 1}", "서울특별시 신림동 ${it + 1}길")
    }
}