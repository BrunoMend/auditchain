package commands

import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimeIntervals
import domain.utility.toDateFormat
import javax.inject.Inject

class VerifyElasticsearch @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val getElasticsearchData: GetElasticsearchData
): Runnable {

    override fun run() {
        println("Provide the initial moment to verify (yyyy-MM-dd'T'HH:mm:ss)")
        val startAt = readLine() ?: throw NullPointerException()
        println("Provide the final moment to verify (yyyy-MM-dd'T'HH:mm:ss)")
        val finishIn = readLine() ?: throw NullPointerException()

        getTimeIntervals.getSingle(startAt, finishIn)
            .subscribe(
                {
                    println(it.first().startAt.toDateFormat())
                    println(it.first().finishIn.toDateFormat())
                },
                {}
            )

        //TODO
        // get time interval from a moment
        // get elasticsearch data
        // verifyStamp.getSingle(elasticsearchData, otsFileName)
    }
}