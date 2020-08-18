package commands

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetLastStampedTime
import domain.utility.*
import java.text.ParseException

abstract class BaseTimeIntervalCommand(
    private val attestationConfiguration: AttestationConfiguration,
    private val getLastStampedTime: GetLastStampedTime
) : BaseCommand() {

    protected val uiStartAt: String by lazy { startAt.toDateFormat(UI_DATE_FORMAT) }
    protected val uiFinishIn: String by lazy { finishIn.toDateFormat(UI_DATE_FORMAT) }

    protected val startAt: Long
            by option(help = "Start moment to realize stamps")
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
                }.default(getDefaultStartDate())

    protected val finishIn: Long
            by option(help = "Finish moment to realize stamps")
                .convert("LONG") {
                    try {
                        val result = getNextTimeInterval(
                            it.toDateMillis(UI_DATE_FORMAT),
                            attestationConfiguration.frequencyMillis,
                            false
                        )
                        if (startAt >= result) fail("Finish date must be greater than start date")
                        if (result + attestationConfiguration.delayMillis > System.currentTimeMillis())
                            fail(
                                "Stamp data from finish in ${result.toDateFormat(UI_DATE_FORMAT)} must be called after " +
                                        (result + attestationConfiguration.delayMillis).toDateFormat(UI_DATE_FORMAT)
                            )
                        else result
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }
                }.default(getPreviousTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private fun getDefaultStartDate(): Long =
        getLastStampedTime.getSingle(GetLastStampedTime.Request(Source.ELASTICSEARCH))
            .onErrorReturn {
                getPreviousTimeInterval(
                    System.currentTimeMillis() - attestationConfiguration.frequencyMillis,
                    attestationConfiguration.frequencyMillis
                )
            }
            .blockingGet()

    override fun run() {
        if (startAt >= finishIn) {
            println("There is no data to stamp now.")
            return
        }
    }
}