package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.NoDataException
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetTimeIntervals
import domain.usecase.SaveEmptyAttestation
import domain.usecase.StampElasticsearchData
import domain.utility.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException
import java.net.UnknownHostException
import java.text.ParseException
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val stampElasticsearchData: StampElasticsearchData,
    private val saveEmptyAttestation: SaveEmptyAttestation,
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

    private val finishIn: Long
            by option(help = "Finish moment to realize stamps")
                .convert("LONG") {
                    try {
                        val result = getNextTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                        if (startAt > result) fail("Finish date must be greater than start date")
                        else result
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getNextTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val verbose
            by option("-v", "--verbose").flag()

    override fun run() {
        if (verbose) println(
            "Stamping data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                    "to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
        )
        getTimeIntervals.getSingle(startAt, finishIn)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { timeInterval ->
                Observable.just(timeInterval)
                    .flatMapSingle { stampElasticsearchData.getSingle(timeInterval) }
                    .onErrorResumeNext { error ->
                        if (verbose) when (error) {
                            is NoDataException -> println(
                                "No data to stamp at ${error.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                                        error.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)
                            )
                            is HttpException -> println(
                                "Http Exception on get data"
                            )
                            is UnknownHostException -> println(
                                "Fail to get data. Verify your internet connection."
                            )
                            else -> println("Unexpected error")
                        }
                        saveEmptyAttestation.getCompletable(
                            timeInterval,
                            Source.ELASTICSEARCH,
                            (error is NoDataException)
                        ).andThen(Observable.empty())
                    }
            }
            .blockingSubscribe(
                { attestation ->
                    if (verbose) println(
                        "${attestation.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                                "${attestation.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)} \n" +
                                "Stamped at ${attestation.dateTimestamp?.toDateFormat(UI_DATE_FORMAT)} \n" +
                                "ots proof: ${attestation.otsData}"
                    )
                },
                { error -> println("${error::class.qualifiedName}: ${error.message}") }
            )
    }
}