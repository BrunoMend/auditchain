package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.className
import domain.model.AttestationConfiguration
import domain.usecase.VerifyElasticsearchDataByInterval
import domain.usecase.GetTimeIntervals
import domain.utility.*
import io.reactivex.rxjava3.core.Completable
import java.text.ParseException
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchDataByInterval: VerifyElasticsearchDataByInterval,
    attestationConfiguration: AttestationConfiguration
) : CliktCommand() {

    private val startAt: Long
            by option(help = "Start moment to verify stamps")
                .convert("LONG") {
                    try {
                        getPreviousTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getPreviousTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val finishIn: Long
            by option(help = "Finish moment to verify stamps")
                .convert("LONG") {
                    try {
                        val result = getNextTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                        if (startAt > result) fail("Finish date must be greater than start date")
                        else result
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getNextTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val verbose
            by option("-v", "--verbose").flag()

    override fun run() {
        if (verbose) println(
            "Verifying data from: ${startAt.toDateFormat(UI_DATE_FORMAT)} " +
                    " to ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
        )
        verifyElasticsearchDataByInterval
            .getObservable(VerifyElasticsearchDataByInterval.Request(startAt, finishIn))
            .doOnError { error -> println("${error::class.qualifiedName}: ${error.message}") }
            .doOnNext { result ->
                if (result.isSuccess)
                    result.getOrNull()?.let { blockchainPublications ->
                        blockchainPublications.forEach {
                            println(
                                "${it.blockchain} attests that data exists from " +
                                        it.datePublication.toDateFormat(UI_DATE_FORMAT)
                            )
                        }
                    }
                else
                    result.exceptionOrNull()?.let {
                        println("Fail to verify: ${it.className}")
                    }
            }
            .blockingSubscribe()
    }
}