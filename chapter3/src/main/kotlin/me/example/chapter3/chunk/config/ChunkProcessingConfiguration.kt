package me.example.chapter3.chunk.config

import me.example.chapter3.listener.SavePersonListener
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/**
 * @author Doyeop Kim
 * @since 2023/10/08
 */
@Configuration
class ChunkProcessingConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val jobLauncher: JobLauncher,
) {
    private final val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val JOB_NAME = "chunk_job"
        const val TASK_STEP_NAME = "${JOB_NAME}_STEP_TASK"
        const val CHUNK_STEP_NAME = "${JOB_NAME}_CHUNK_TASK"
    }

    @Bean
    fun startJob(): CommandLineRunner {
        return CommandLineRunner {
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .addString("chunkSize", "20")
                .toJobParameters()

            jobLauncher.run(chunkProcessingJob(), jobParameters)
        }
    }

    @Bean
    fun chunkProcessingJob(): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .incrementer(RunIdIncrementer())
            .start(taskBaseStep())
            .listener(SavePersonListener.SavePersonJobExecutionListener())
            .listener(SavePersonListener.SavePersonAnnotationJobExecutionListener())
            .next(chunkBaseStep(null)) // JobScope에서 JobParameter를 읽어와서 초기화함
            .build()
    }

    private fun taskBaseStep(): Step {
        return StepBuilder(TASK_STEP_NAME, jobRepository)
            .tasklet(tasklet(null), transactionManager)
            .build()
    }

    @Bean
    @StepScope
    fun tasklet(@Value("#{jobParameters['chunkSize']}") chunkSize: String?): Tasklet =
        Tasklet { contribution, chunkContext ->
            val items = getItems()
            val inputChunkSize = chunkSize?.let { Integer.parseInt(chunkSize) } ?: 10

            logger.info("task item size : {}, chunk size : {}", items.size, inputChunkSize)

            return@Tasklet RepeatStatus.FINISHED
        }

    @Bean
    @JobScope // Scope에 의해서 Job의 라이프사이클을 따르도록 설정이 되기 때문에 해당 step은 쓰레드 세이프하게 동작함
    fun chunkBaseStep(@Value("#{jobParameters['chunkSize']}") chunkSize: String?): Step {
        // JobParameter의 chunkSize 키를 읽어와서 Integer로 파싱
        val inputChunkSize = chunkSize?.let { Integer.parseInt(chunkSize) } ?: 10

        return StepBuilder(CHUNK_STEP_NAME, jobRepository)
            .chunk<String, String>(inputChunkSize, transactionManager)
            .listener(SavePersonListener.SavePersoStepExecutionListener())
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(itemWriter())
            .build()
    }

    private fun itemReader(): ItemReader<String> = ListItemReader(getItems())

    private fun itemProcessor(): ItemProcessor<String, String> = ItemProcessor { "$it, spring batch!" }

    private fun itemWriter(): ItemWriter<String> = ItemWriter { it ->
        logger.info("chunk item size : {}", it.items.size)
    }

    private fun getItems(): List<String> = List(100) { "hello $it" }
}
