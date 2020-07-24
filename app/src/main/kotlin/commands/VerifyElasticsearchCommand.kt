package commands

import domain.model.AttestationConfiguration
import domain.model.BlockchainPublication
import domain.model.TimeInterval
import domain.usecase.GetLastStampedTime
import domain.usecase.UpdateAttestationsOtsData
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    private val updateAttestationsOtsData: UpdateAttestationsOtsData,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval
) : BaseTimeIntervalCommand(attestationConfiguration, getLastStampedTime) {

    override fun run() {
        super.run()

        updateAttestationsOtsData.getCompletable(Unit)
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
            .doOnComplete { printProcessCompleted() }
            .doOnError { it.printError() }
            .onErrorComplete()
            .blockingSubscribe()
    }

    private fun printVerifySuccess(result: Pair<TimeInterval, List<BlockchainPublication>>) {
        val timeInterval = result.first
        val blockchainPublications = result.second

        blockchainPublications.forEach {
            printMsg(
                "${it.blockchain} attests that data from interval $timeInterval was stamped at " +
                        it.datePublication.toDateFormat(UI_DATE_FORMAT)
            )
        }
    }
}