package me.example.chapter1.config.shared.tasklet

import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

/**
 * @author Doyeop Kim
 * @since 2023/10/08
 */
@Component
class SharedStep1Tasklet : Tasklet {
    private final val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val stepExecution = contribution.stepExecution
        val stepExecutionContext = stepExecution.executionContext
        stepExecutionContext.putString("stepKey", "step execution context")

        val jobExecution = stepExecution.jobExecution
        val jobInstance = jobExecution.jobInstance
        val jobExecutionContext = jobExecution.executionContext
        jobExecutionContext.putString("jobKey", "job execution context")
        val jobParameters = jobExecution.jobParameters

        logger.info(
            "jobName : {}, stepName : {}, parameter : {}",
            jobInstance.jobName,
            stepExecution.stepName,
            jobParameters.getLong("run.id")
        )

        return RepeatStatus.FINISHED
    }
}