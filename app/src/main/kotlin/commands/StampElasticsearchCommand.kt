package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.NoDataToStampException
import domain.model.AttestationConfiguration
import domain.usecase.GetTimeIntervals
import domain.usecase.StampElasticsearchData
import domain.utility.*
import java.text.ParseException
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val stampElasticsearchData: StampElasticsearchData,
    attestationConfiguration: AttestationConfiguration
) : CliktCommand() {

    private val startAt: Long
            by option(help = "Start moment to realize stamps")
                .convert("LONG") {
                    try {
                        getPreviousTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getPreviousTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    //TODO validate if it is greater than start date
    private val finishIn: Long
            by option(help = "Finish moment to realize stamps")
                .convert("LONG") {
                    try {
                        getNextTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getNextTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val verbose
            by option("-v", "--verbose").flag()

    override fun run() {
        if (verbose) println(
            "Stamping data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                    " to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
        )
        getTimeIntervals.getSingle(startAt, finishIn)
            .flatMapObservable { stampElasticsearchData.getObservable(it) }
            .blockingSubscribe(
                { attestation ->
                    if (verbose) {
                        println(
                            "${attestation.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                                    attestation.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)
                        )
                        println(
                            "Stamped at ${attestation.dateTimestamp.toDateFormat(UI_DATE_FORMAT)} \n" +
                                    "ots proof: ${attestation.otsData}"
                        )
                    }
                },
                { error ->
                    when (error) {
                        is NoDataToStampException -> if (verbose) println("No data to stamp")
                        else -> println("${error::class.qualifiedName}: ${error.message}")
                    }
                }
            )
    }
}