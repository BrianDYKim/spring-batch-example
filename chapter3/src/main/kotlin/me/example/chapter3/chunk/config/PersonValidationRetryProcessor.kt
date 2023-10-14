package me.example.chapter3.chunk.config

import com.fasterxml.jackson.core.sym.NameN
import me.example.chapter3.jpaReader.model.Person
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.RetryState
import org.springframework.retry.support.RetryTemplate
import org.springframework.retry.support.RetryTemplateBuilder
import javax.naming.NameNotFoundException

/**
 * @author Doyeop Kim
 * @since 2023/10/14
 */
class PersonValidationRetryProcessor : ItemProcessor<Person, Person> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val retryTemplate: RetryTemplate = RetryTemplateBuilder()
        .maxAttempts(3)
        .retryOn(NotFoundException::class.java)
        .withListener(SavePersonRetryListener())
        .build()

    override fun process(item: Person): Person? {
        val retryCallback: (RetryContext) -> Person = { context ->
            when (item.checkNotEmptyName()) {
                true -> item
                else -> throw NameNotFoundException()
            }
        }

        val recoveryCallback: (RetryContext) -> Person = {
            item.unknownName()
        }

        return this.retryTemplate.execute<Person, NameNotFoundException>(retryCallback, recoveryCallback)
    }

    inner class SavePersonRetryListener : RetryListener {
        override fun <T : Any?, E : Throwable?> open(context: RetryContext?, callback: RetryCallback<T, E>?): Boolean {
            return true
        }

        override fun <T : Any?, E : Throwable?> close(
            context: RetryContext?,
            callback: RetryCallback<T, E>?,
            throwable: Throwable?
        ) {
            logger.info("close")
        }

        override fun <T : Any?, E : Throwable?> onSuccess(
            context: RetryContext?,
            callback: RetryCallback<T, E>?,
            result: T
        ) {
            super.onSuccess(context, callback, result)
        }

        override fun <T : Any?, E : Throwable?> onError(
            context: RetryContext?,
            callback: RetryCallback<T, E>?,
            throwable: Throwable?
        ) {
            logger.info("onError")
        }
    }
}