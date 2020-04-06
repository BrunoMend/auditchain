package br.ufscar.auditchain.common

import java.io.FileInputStream
import java.util.*

class Config {
    companion object {
        private val configFile: Properties = Properties()

        init {
            try {
                configFile.load(FileInputStream("C:\\ots\\config.properties"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val elasticHost: String
            get() = configFile.getProperty("elasticHost")

        val elasticUser: String
            get() = configFile.getProperty("elasticUser")

        val elasticPwds: String
            get() = configFile.getProperty("elasticPwds")

        val frequency: Long
            get() = configFile.getProperty("frequency").toLong()

        val delay: Long
            get() = configFile.getProperty("delay").toLong()

        val indexPattern: String
            get() = configFile.getProperty("indexPattern")

        val rangeParameter: String
            get() = configFile.getProperty("rangeParameter")

        val resultMaxSize: Int
            get() = configFile.getProperty("resultMaxSize").toInt()

        override fun toString(): String =
            "elasticHost: $elasticHost\n" +
                    "elasticUser: $elasticUser\n" +
                    "elasticPwds: $elasticPwds\n" +
                    "frequency: $frequency\n" +
                    "delay: $delay\n" +
                    "indexPattern: $indexPattern\n" +
                    "rangeParameter: $rangeParameter\n" +
                    "resultMaxSize: $resultMaxSize"
    }
}