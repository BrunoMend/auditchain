package commands

import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetLastStampedTime
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.utility.UI_DATE_FORMAT
import domain.utility.getPreviousTimeInterval
import domain.utility.toDateFormat
import okhttp3.OkHttpClient
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val getLastStampedTime: GetLastStampedTime,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval,
    client: OkHttpClient
) : BaseCommand(client) {

    override fun run() {
        // get the last stamped time or the previous time interval in case of the first stamp
        val startAt: Long =
            getLastStampedTime.getSingle(GetLastStampedTime.Request(Source.ELASTICSEARCH))
                .onErrorReturn {
                    getPreviousTimeInterval(
                        System.currentTimeMillis() - attestationConfiguration.frequencyMillis,
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    )
                }.blockingGet()

        // get the last available time interval to stamp
        val finishIn: Long =
            getPreviousTimeInterval(
                System.currentTimeMillis(),
                attestationConfiguration.frequencyMillis,
                attestationConfiguration.delayMillis
            )

        if (startAt >= finishIn) {
            println("There is no data to stamp now.")
            return
        }

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                stampElasticsearchDataByInterval
                    .getObservable(StampElasticsearchDataByInterval.Request(startAt, finishIn))
                    .doOnSubscribe {
                        printVerbose(
                            "Stamping data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                                    "to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
                        )
                    }
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