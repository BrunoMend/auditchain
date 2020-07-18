package common

import commands.ApplicationCommand
import java.util.logging.LogManager

fun main(args: Array<String>) {
    LogManager
        .getLogManager()
        .readConfiguration(
            Thread.currentThread().contextClassLoader.getResourceAsStream("logconfig.properties")
        )

    ApplicationCommand().main(args)
}