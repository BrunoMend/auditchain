package common

import commands.ApplicationCommand

fun main(args: Array<String>) {
    ApplicationCommand().main(listOf("stamp-elasticsearch", "-v", "--start-at=2020-06-05 01:00", "--finish-in=2020-06-05 01:40")) //
}