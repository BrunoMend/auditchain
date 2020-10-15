package commands

import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.usecase.GetLastStampedTime
import domain.usecase.ProcessAllElasticsearchStampExceptions
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val processAllElasticsearchStampExceptions: ProcessAllElasticsearchStampExceptions,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval
) : BaseTimeIntervalCommand(attestationConfiguration, getLastStampedTime) {

    override fun run() {
        super.run()

        updateAllIncompleteAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                Observable.concat(
                    processAllElasticsearchStampExceptions.getObservable(Unit)
                        .doOnSubscribe { printVerbose("Checking for stamp exceptions to try again...") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else result.exceptionOrNull()?.printError()
                        },
                    stampElasticsearchDataByInterval
                        .getObservable(StampElasticsearchDataByInterval.Request(startAt, finishIn))
                        .doOnSubscribe { printVerbose("Stamping data from: $uiStartAt to $uiFinishIn") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else result.exceptionOrNull()?.printError()
                        }
                ))
            .doOnComplete { printProcessCompleted() }
            .doOnError { it.printError() }
            .onErrorComplete()
            .blockingSubscribe()
    }

    private fun printStampSuccess(attestation: Attestation) {
        printVerboseSeparatorLine()
        printVerbose(
            "Data from: " +
                    "${attestation.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                    "${attestation.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)} \n" +
                    attestation.sourceParams?.map { "${it.key} : ${it.value}" }?.joinToString("\n") + "\n" +
                    "Stamped at ${attestation.dateTimestamp.toDateFormat(UI_DATE_FORMAT)}"
        )
    }
}