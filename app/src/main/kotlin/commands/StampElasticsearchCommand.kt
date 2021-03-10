package commands

import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.usecase.GetLastStampedTime
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import okhttp3.OkHttpClient
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    client: OkHttpClient,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval
) : BaseTimeIntervalCommand(client, attestationConfiguration, getLastStampedTime) {
    override val ignoreStartAtIfAlreadyExistsStamps: Boolean
        get() = true

    override fun run() {
        super.run()

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                stampElasticsearchDataByInterval
                    .getObservable(StampElasticsearchDataByInterval.Request(startAt, finishIn))
                    .doOnSubscribe { printVerbose("Stamping data from: $uiStartAt to $uiFinishIn") }
                    .doOnNext { result -> printStampSuccess(result) }
            )
            .doOnComplete {
                releaseResources()
                printProcessCompleted()
            }
            .doOnError { it.printError() }
            .onErrorComplete()
            .blockingSubscribe()
    }

    private fun printStampSuccess(attestation: Attestation) {
        printVerboseSeparatorLine()
        printVerbose(
            "Data from:\n$attestation" +
                    "Stamped at ${attestation.dateTimestamp.toDateFormat(UI_DATE_FORMAT)}"
        )
    }
}