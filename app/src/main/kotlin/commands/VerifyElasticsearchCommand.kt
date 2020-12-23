package commands

import domain.model.AttestationConfiguration
import domain.model.AttestationVerifyResult
import domain.usecase.GetLastStampedTime
import domain.usecase.UpdateAllIncompleteAttestationsOtsData
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    private val updateAllIncompleteAttestationsOtsData: UpdateAllIncompleteAttestationsOtsData,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval
) : BaseTimeIntervalCommand(attestationConfiguration, getLastStampedTime) {

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
            .doOnComplete { printProcessCompleted() }
            .doOnError { it.printError() }
            .onErrorComplete()
            .blockingSubscribe()

        printMsg("Program finished")
    }

    private fun printVerifySuccess(result: AttestationVerifyResult) {
        printSeparatorLine()
        printMsg(
            "Data from:\n${result.attestation}" +
                    "Is attested by: \n" +
                    result.blockchainPublications.joinToString("\n") {
                        "${it.blockchain} since ${it.datePublication.toDateFormat(UI_DATE_FORMAT)}"
                    } + "\n"
        )
    }
}