package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.model.AttestationConfiguration
import domain.usecase.VerifyElasticsearchData
import domain.usecase.GetTimeIntervals
import domain.utility.*
import java.text.ParseException
import javax.inject.Inject

class VerifyElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val verifyElasticsearchData: VerifyElasticsearchData,
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
                        if(startAt > result) fail("Finish date must be greater than start date")
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
        getTimeIntervals.getSingle(startAt, finishIn)
            .flatMapCompletable { verifyElasticsearchData.getCompletable(it) }
            .blockingSubscribe(
                { println("finish with success") },
                { println("finish with error: ${it::class.qualifiedName}: ${it.message}") }
            )
    }
}