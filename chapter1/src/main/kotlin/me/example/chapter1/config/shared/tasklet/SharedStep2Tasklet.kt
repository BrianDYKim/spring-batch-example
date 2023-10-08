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
class SharedStep2Tasklet : Tasklet {
    private final val logger = LoggerFactory.getLogger(this::class.java)

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val stepExecution = contribution.stepExecution
        val stepExecutionContext = stepExecution.executionContext

        val jobExecution = stepExecution.jobExecution
        val jobExecutionContext = jobExecution.executionContext

        logger.info(
            "jobKey : {}, stepKey: {}",
            jobExecutionContext.getString("jobKey", "emptyJobKey"), // jobKey는 step 간에 공유가 됨
            stepExecutionContext.getString("stepKey", "emptyStepKey") // stepKey는 step 간에 공유가 되지 않음
        )

        return RepeatStatus.FINISHED
    }
}