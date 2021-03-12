package commands

import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetLastStampedTimeOrDefault
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.utility.UI_DATE_FORMAT
import domain.utility.getPreviousTimeInterval
import domain.utility.toDateFormat
import io.reactivex.rxjava3.core.Completable
import okhttp3.OkHttpClient
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val attestationConfiguration: AttestationConfiguration,
    private val getLastStampedTimeOrDefault: GetLastStampedTimeOrDefault,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval,
    client: OkHttpClient
) : BaseCommand(client) {

    override fun run() {
        // get the last stamped time or the previous time interval in case of the first stamp
        val startAt: Long =
            getLastStampedTimeOrDefault.getSingle(GetLastStampedTimeOrDefault.Request(Source.ELASTICSEARCH))
                .blockingGet()

        // get the last available time interval to stamp
        val finishIn: Long =
            getPreviousTimeInterval(
                System.currentTimeMillis(),
                attestationConfiguration.frequencyMillis,
                attestationConfiguration.delayMillis
            )

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                if (startAt >= finishIn)
                    Completable.fromAction { println("There is no data to stamp now.") }
                else
                    stampElasticsearchDataByInterval
                        .getObservable(StampElasticsearchDataByInterval.Request(startAt, finishIn))
                        .doOnSubscribe {
                            printVerbose(
                                "Stamping data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                                        "to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
                            )
                        }
                        .flatMapCompletable { result ->
                            Completable.fromAction { printStampSuccess(result) }
                        }.onErrorComplete()
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