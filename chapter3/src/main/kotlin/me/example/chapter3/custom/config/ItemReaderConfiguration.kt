package me.example.chapter3.custom.config

import me.example.chapter3.custom.model.Person
import me.example.chapter3.custom.reader.CustomItemReader
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
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/08
 */
@Configuration
class ItemReaderConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val jobLauncher: JobLauncher
) {
    private final val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val JOB_NAME = "item_reader"
        const val STEP_NAME = "item_reader_step"
    }

    @Bean
    fun jobStart(): CommandLineRunner {
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
            .start(customItemReaderStep())
            .build()
    }

    @Bean
    fun customItemReaderStep(): Step {
        val chunkSize = 10

        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<Person, Person>(chunkSize, transactionManager)
            .reader(CustomItemReader(getItems()))
            .writer(itemWriter())
            .build()
    }

    private fun getItems(): MutableList<Person> =
        MutableList(100) { it -> Person(it + 1, "test name $it", "test age $it", "test address") }

    private fun itemWriter(): ItemWriter<Person> = ItemWriter { chunk ->
        chunk.items.map { it.name }
            .reduce { acc, name -> "$acc, $name" }
            .let { logger.info(it) }
    }
}