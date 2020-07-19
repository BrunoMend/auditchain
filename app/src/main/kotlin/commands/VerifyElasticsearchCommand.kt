package commands

import domain.exception.className
import domain.model.AttestationConfiguration
import domain.model.BlockchainPublication
import domain.usecase.GetLastStampedTime
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval
) : BaseTimeIntervalCommand(attestationConfiguration, getLastStampedTime) {

    override fun run() {
        verifyElasticsearchDataByInterval
            .getObservable(VerifyElasticsearchDataByInterval.Request(startAt, finishIn))
            .doOnSubscribe{ printVerbose("Verifying data from: $uiStartAt to $uiFinishIn") }
            .doOnError { printProcessError(it) }
            .doOnNext { result ->
                if (result.isSuccess) printVerifySuccess(result.getOrThrow())
                else printVerifyError(result.exceptionOrNull())
            }
            .blockingSubscribe()
    }

    private fun printVerifySuccess(blockchainPublications: List<BlockchainPublication>){
        blockchainPublications.forEach {
            printMsg(
                "${it.blockchain} attests that data exists from " +
                        it.datePublication.toDateFormat(UI_DATE_FORMAT)
            )
        }
    }

    private fun printVerifyError(error: Throwable?) {
        printMsg("Fail to verify: ${error?.className ?: "Unexpected Error"}")
    }
}