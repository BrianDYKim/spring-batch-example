package me.example.chapter3.listener

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.AfterJob
import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.core.annotation.BeforeJob
import org.springframework.batch.core.annotation.BeforeStep

/**
 * @author Doyeop Kim
 * @since 2023/10/14
 */
sealed class SavePersonListener {
    class SavePersonJobExecutionListener : JobExecutionListener {
        val logger = LoggerFactory.getLogger(this::class.java)

        override fun beforeJob(jobExecution: JobExecution) {
            logger.info("before job")
        }

        override fun afterJob(jobExecution: JobExecution) {
            val sum = jobExecution.stepExecutions.map { it.writeCount }
                .sum()
                .toInt()

            logger.info("after job : $sum")
        }
    }

    class SavePersonAnnotationJobExecutionListener {
        val logger = LoggerFactory.getLogger(this::class.java)

        @BeforeJob
        fun beforeJob(jobExecution: JobExecution) {
            logger.info("annotation before")
        }

        @AfterJob
        fun afterJob(jobExecution: JobExecution) {
            val sum = jobExecution.stepExecutions.map { it.writeCount.toInt() }
                .sum()

            logger.info("annotation after : $sum")
        }
    }

    class SavePersoStepExecutionListener {
        private val logger = LoggerFactory.getLogger(this::class.java)

        @BeforeStep
        fun beforeStep(stepExecution: StepExecution) {
            logger.info("before step")
        }

        @AfterStep
        fun afterStep(stepExecution: StepExecution): ExitStatus {
            logger.info("after step : ${stepExecution.writeCount}")

            return when (stepExecution.writeCount.toInt()) {
                0 -> ExitStatus.FAILED
                else -> ExitStatus.COMPLETED
            }
        }
    }
}