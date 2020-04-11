package common

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

        val filePath: String
            get() = configFile.getProperty("filePath")

        val instants: List<Long> by lazy {
            val dayMinutes: Long = 1440
            val initializeInstantList = mutableListOf<Long>()
            for (temp in frequency until dayMinutes step frequency) {
                initializeInstantList.add(temp)
            }
            initializeInstantList.add(dayMinutes)

            initializeInstantList
        }
    }
}