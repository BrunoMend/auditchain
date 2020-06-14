package common

import commands.Application

fun main(args: Array<String>) {
    Application().main(listOf("stamp-elasticsearch", "-v")) //"--start-at=2020-05-26 23:00", "--finish-in=2020-05-27 01:40",
}