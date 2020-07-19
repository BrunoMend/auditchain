package commands

import domain.exception.AttestationAlreadyExistsException
import domain.exception.NoDataException
import domain.model.Attestation
import domain.model.AttestationConfiguration
import domain.usecase.GetLastStampedTime
import domain.usecase.ProcessAllElasticsearchStampExceptions
import domain.usecase.StampElasticsearchDataByInterval
import domain.usecase.UpdateAttestationsOtsData
import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    attestationConfiguration: AttestationConfiguration,
    getLastStampedTime: GetLastStampedTime,
    private val updateAttestationsOtsData: UpdateAttestationsOtsData,
    private val processAllElasticsearchStampExceptions: ProcessAllElasticsearchStampExceptions,
    private val stampElasticsearchDataByInterval: StampElasticsearchDataByInterval
) : BaseTimeIntervalCommand(attestationConfiguration, getLastStampedTime) {

    override fun run() {
        super.run()

        updateAttestationsOtsData.getCompletable(Unit)
            .doOnSubscribe { printVerbose("Updating OTS data from previous stamps...") }
            .andThen(
                Observable.concat(
                    processAllElasticsearchStampExceptions.getObservable(Unit)
                        .doOnSubscribe { printVerbose("Checking for stamp exceptions to try again...") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else printStampError(result.exceptionOrNull())
                        },
                    stampElasticsearchDataByInterval
                        .getObservable(StampElasticsearchDataByInterval.Request(startAt, finishIn))
                        .doOnSubscribe { printVerbose("Stamping data from: $uiStartAt to $uiFinishIn") }
                        .doOnNext { result ->
                            if (result.isSuccess) printStampSuccess(result.getOrThrow())
                            else printStampError(result.exceptionOrNull())
                        }
                ))
            .doOnComplete { printProcessCompleted() }
            .doOnError { printProcessError(it) }
            .onErrorComplete()
            .blockingSubscribe()
    }

    private fun printStampSuccess(attestation: Attestation) {
        printVerbose(
            "${attestation.timeInterval.startAt.toDateFormat(UI_DATE_FORMAT)} - " +
                    "${attestation.timeInterval.finishIn.toDateFormat(UI_DATE_FORMAT)} \n" +
                    "Stamped at ${attestation.dateTimestamp.toDateFormat(UI_DATE_FORMAT)} \n" +
                    "ots proof: ${attestation.otsData}"
        )
    }

    private fun printStampError(error: Throwable?) {
        when (error) {
            is NoDataException ->
                printVerbose("No data to stamp at ${error.timeInterval}")
            is HttpException ->
                printVerbose("Http Exception on get data")
            is UnknownHostException ->
                printVerbose("Fail to get data. Verify your internet connection.")
            is AttestationAlreadyExistsException ->
                printVerbose("Data already stamped at ${error.timeInterval}")
            else ->
                printVerbose("Unexpected stamp error - $error")
        }
    }
}