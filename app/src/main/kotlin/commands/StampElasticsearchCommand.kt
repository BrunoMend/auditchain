package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.AttestationAlreadyExistsException
import domain.exception.NoDataException
import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.usecase.ProcessAllElasticsearchStampExceptions
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAttestationsOtsData
import domain.utility.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException
import java.net.UnknownHostException
import java.text.ParseException
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val updateAttestationsOtsData: UpdateAttestationsOtsData,
    private val processAllElasticsearchStampExceptions: ProcessAllElasticsearchStampExceptions,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval,
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
        updateAttestationsOtsData.getCompletable()
            .doOnSubscribe { printStartUpdateAttestationsOtsData() }
            .andThen(
                Observable.concat(
                    processAllElasticsearchStampExceptions.getObservable()
                        .doOnSubscribe { printStartProcessStampExceptions() }
                        .doOnError { error -> println("${error::class.qualifiedName}: ${error.message}") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else printStampError(result.exceptionOrNull())
                        },
                    stampElasticsearchDataByInterval.getObservable(startAt, finishIn)
                        .doOnSubscribe { printStartStamp() }
                        .doOnError { error -> println("${error::class.qualifiedName}: ${error.message}") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else printStampError(result.exceptionOrNull())
                        }
                )).blockingSubscribe()
    }

    private fun printStartUpdateAttestationsOtsData() {
        if (verbose) println(
            "Updating OTS data from previous stamps"
        )
    }

    private fun printStartProcessStampExceptions() {
        if (verbose) println(
            "Checking for stamp exceptions to try again..."
        )
    }

    private fun printStartStamp() {
        if (verbose) println(
            "Stamping data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                    "to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
        )
    }

    private fun printStampSuccess(attestation: Attestation) {
        if (verbose) println(
            "${attestation.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                    "${attestation.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)} \n" +
                    "Stamped at ${attestation.dateTimestamp.toDateFormat(UI_DATE_FORMAT)} \n" +
                    "ots proof: ${attestation.otsData}"
        )
    }

    private fun printStampError(error: Throwable?) {
        if (verbose) when (error) {
            is NoDataException -> println(
                "No data to stamp at ${error.timeInterval}"
            )
            is HttpException -> println(
                "Http Exception on get data"
            )
            is UnknownHostException -> println(
                "Fail to get data. Verify your internet connection."
            )
            is AttestationAlreadyExistsException -> println(
                "Data already stamped at ${error.timeInterval}"
            )
            else -> println("Unexpected error")
        }
    }
}