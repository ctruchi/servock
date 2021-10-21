package io.servock

import mu.KotlinLogging
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

private val logger = KotlinLogging.logger {}

private val watchService = FileSystems.getDefault().newWatchService()

fun Path.watch(block: (Path) -> Unit) {
    val watchKey = toAbsolutePath().parent.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)
    logger.debug("Watching $this for changes")

    while (true) {
        val key = watchService.take()
        logger.debug("$this changed")
        if (key.pollEvents().any { it.context() as Path == this }) {
            block(this)
        }

        if (!key.reset()) {
            logger.debug("Stop wathing $this for changes")
            key.cancel()
            watchService.close()
            break
        }
    }

    watchKey.cancel()
}
