package me.example.chapter3.jpaProcessor.config

import me.example.chapter3.jpaReader.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean
import org.springframework.orm.jpa.JpaTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/09
 */
@Configuration
class JpaProcessorBatchConfig(
    private val jobRepository: JobRepository,
    private val jobLauncher: JobLauncher,
    private val entityManagerFactoryBean: AbstractEntityManagerFactoryBean
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val entityManagerFactory = entityManagerFactoryBean.nativeEntityManagerFactory
    private val transactionManager = JpaTransactionManager(entityManagerFactory)

    companion object {
        const val JOB_NAME = "jpa_processor_job"
        const val STEP_NAME = "${JOB_NAME}_STEP"
    }



    private fun getItems(): MutableList<Person> = MutableList(100) {
        Person("홍길동 $it", "${it + 1}", "서울특별시 신림동 ${it + 1}길")
    }
}