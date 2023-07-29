package wedding.backend.app.workers

import aws.sdk.kotlin.services.sqs.model.Message
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import wedding.backend.app.aws.SqsService
import wedding.backend.app.configuration.UserUploadWorkerConfiguration
import wedding.backend.app.model.Role
import wedding.backend.app.model.UserUploadMessage
import wedding.backend.app.services.UserService
import wedding.backend.app.util.repeatUntilCancelled
import kotlin.coroutines.CoroutineContext

@Component
class UserUploadWorker(
    private val sqsService: SqsService,
    private val userService: UserService,
    private val config: UserUploadWorkerConfiguration,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) : CoroutineScope {

    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + supervisorJob

    @EventListener(classes = [ContextRefreshedEvent::class])
    @Order(2)
    fun start() = launch {
        logger.info("Launching workers")
        val messageChannel = Channel<Message>()
        repeat(config.numWorkers) { launchWorker(messageChannel) }
        launchMsgReceiver(messageChannel)
    }



    private fun CoroutineScope.launchMsgReceiver(channel: SendChannel<Message>) = launch {
        repeatUntilCancelled {
            val messages =
                sqsService.getMessages(config.sqsUrl, config.waitTime, config.maxMessages, config.visibilityTimeout)

            messages.forEach {
                channel.send(it)
            }
        }
    }

    private fun CoroutineScope.launchWorker(channel: ReceiveChannel<Message>) = launch {
        repeatUntilCancelled {
            for (msg in channel) {
                try {
                    processMessage(msg)
                    sqsService.deleteMessage(config.sqsUrl, msg)
                } catch (ex: Exception) {
                    logger.error("${Thread.currentThread().name} exception trying to process msg", ex)
                    sqsService.changeVisibility(config.sqsUrl, msg, config.visibilityTimeout)
                }
            }
        }
    }

    private suspend fun processMessage(msg: Message) {
        val userDetails = objectMapper.readValue<UserUploadMessage>(msg.body ?: throw IllegalStateException("Empty msg body"))

        userService.addUser(userDetails.username, "", userDetails.password, "", "", listOf(Role.ROLE_USER))
    }
}