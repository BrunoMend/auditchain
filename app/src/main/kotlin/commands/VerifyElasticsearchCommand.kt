package commands

import com.github.ajalt.clikt.parameters.options.*
import domain.exception.TimeShorterThanCurrentWithDelayException
import domain.model.AttestationConfiguration
import domain.model.AttestationVerifyResult
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.utility.*
import okhttp3.OkHttpClient
import java.text.ParseException
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    client: OkHttpClient,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval
) : BaseCommand(client) {

    private val startAt: Long
            by option(help = "Start moment to realize stamps")
                .convert("LONG") { passedStartDate ->

                    val formattedDate = try {
                        passedStartDate.toDateMillis(UI_DATE_FORMAT)
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }

                    val previousInterval = try {
                        getPreviousTimeInterval(
                            formattedDate,
                            attestationConfiguration.frequencyMillis,
                            attestationConfiguration.delayMillis,
                            false
                        )
                    } catch (e: TimeShorterThanCurrentWithDelayException) {
                        fail(e.message!!)
                    }

                    previousInterval
                }.required()

    private val finishIn: Long
            by option(help = "Finish moment to verify stamps")
                .convert("LONG") {
                    val formattedDate = try {
                        it.toDateMillis(UI_DATE_FORMAT)
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }

                    val nextInterval = try {
                        getNextTimeInterval(
                            formattedDate,
                            attestationConfiguration.frequencyMillis,
                            attestationConfiguration.delayMillis,
                            false
                        )
                    } catch (e: TimeShorterThanCurrentWithDelayException) {
                        fail(e.message!!)
                    }

                    if (startAt >= nextInterval) fail("Finish date must be greater than start date")
                    nextInterval
                }.defaultLazy {
                    getPreviousTimeInterval(
                        System.currentTimeMillis(),
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    )
                }

    override fun run() {

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                verifyElasticsearchDataByInterval
                    .getObservable(VerifyElasticsearchDataByInterval.Request(startAt, finishIn))
                    .doOnSubscribe {
                        printVerbose(
                            "Verifying data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                                    "to ${finishIn.toDateFormat(UI_DATE_FORMAT)} \n"
                        )
                    }
                    .doOnNext { result ->
                        if (result.isSuccess) printVerifySuccess(result.getOrThrow())
                        else result.exceptionOrNull()?.printError()
                    }
                    .doOnError { it.printError() }
                    .onErrorComplete()
            ).doOnComplete {
                releaseResources()
                printProcessCompleted()
            }
            .doOnError { it.printError() }
            .onErrorComplete()
            .blockingSubscribe()
    }

    private fun printVerifySuccess(result: AttestationVerifyResult) {
        printSeparatorLine()
        printMsg(
            "Data from:\n${result.attestation}" +
                    "Is attested by: \n" +
                    result.blockchainPublications.joinToString("\n") {
                        "${it.blockchain} since ${it.datePublication.toDateFormat(UI_DATE_FORMAT)}"
                    } + "\n" +
                    "Latency time from this attestation: ${
                        result.attestation.latencyMillis?.div(1000)?.toInt() ?: -1
                    } seconds" + "\n"
        )
    }
}