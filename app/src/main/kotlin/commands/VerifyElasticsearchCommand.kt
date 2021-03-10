package commands

import domain.model.AttestationConfiguration
import domain.model.AttestationVerifyResult
import domain.usecase.GetLastStampedTime
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import okhttp3.OkHttpClient
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    client: OkHttpClient,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval
) : BaseTimeIntervalCommand(client, attestationConfiguration, getLastStampedTime) {
    override val ignoreStartAtIfAlreadyExistsStamps: Boolean
        get() = false

    override fun run() {
        super.run()

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                verifyElasticsearchDataByInterval
                    .getObservable(VerifyElasticsearchDataByInterval.Request(startAt, finishIn))
                    .doOnSubscribe { printVerbose("Verifying data from: $uiStartAt to $uiFinishIn") }
                    .doOnError { it.printError() }
                    .doOnNext { result ->
                        if (result.isSuccess) printVerifySuccess(result.getOrThrow())
                        else result.exceptionOrNull()?.printError()
                    })
            .doOnComplete {
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