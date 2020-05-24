package common

import commands.Application

fun main(args: Array<String>) {
    Application().main(listOf("stamp-elasticsearch", "--no-verbose"))
}