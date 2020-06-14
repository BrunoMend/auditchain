package commands

import com.github.ajalt.clikt.core.CliktCommand
import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimeIntervals
import domain.utility.toDateFormat
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val getElasticsearchData: GetElasticsearchData
): CliktCommand() {
    override fun run() {
        TODO("Not yet implemented")
          // get time intervals
          // get elasticsearch data
          // verifyStamp.getSingle(elasticsearchData, otsFileName)
    }
}