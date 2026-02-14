package com.lovelycatv.sakurachat.core.task

import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

abstract class MessageTaskQueueManager(
    protected val coroutineScope: CoroutineScope
) {
    private val logger = logger()
    private val channelQueues = ConcurrentHashMap<Long, Channel<MessageTask>>()
    private val queueProcessors = ConcurrentHashMap<Long, Boolean>()

    fun enqueueTask(channel: SakuraChatMessageChannel, task: MessageTask) {
        val channelId = channel.channelId
        val queue = channelQueues.computeIfAbsent(channelId) { Channel<MessageTask>(Channel.UNLIMITED) }
        coroutineScope.launch(Dispatchers.IO) {
            queue.send(task)
            logger.info("Enqueued task {} for channel {}", task.uuid, channelId)
        }
        processQueue(channelId, queue)
    }

    private fun processQueue(channelId: Long, queue: Channel<MessageTask>) {
        if (queueProcessors.putIfAbsent(channelId, true) == null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    while (true) {
                        val task = queue.receiveCatching().getOrNull() ?: break
                        logger.info(
                            "+ [Task-{}] Processing task for channel {}",
                            task.uuid,
                            channelId
                        )
                        processTask(task)
                        logger.info(
                            "- [Task-{}] Processed task for channel {}",
                            task.uuid,
                            channelId
                        )
                    }
                } catch (e: Exception) {
                    logger.error("Error processing queue for channel $channelId", e)
                } finally {
                    queueProcessors.remove(channelId)
                }
            }
        }
    }

    protected abstract suspend fun processTask(task: MessageTask)

    fun shutdown() {
        channelQueues.forEach { (_, queue) ->
            queue.close()
        }

        channelQueues.clear()
        queueProcessors.clear()
    }
}
