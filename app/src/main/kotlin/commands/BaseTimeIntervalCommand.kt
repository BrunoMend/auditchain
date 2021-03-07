package commands

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.TimeShorterThanCurrentWithDelayException
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetLastStampedTime
import domain.utility.*
import okhttp3.OkHttpClient
import java.text.ParseException

abstract class BaseTimeIntervalCommand(
    client: OkHttpClient,
    private val attestationConfiguration: AttestationConfiguration,
    private val getLastStampedTime: GetLastStampedTime
) : BaseCommand(client) {

    protected val uiStartAt: String by lazy { startAt.toDateFormat(UI_DATE_FORMAT) }
    protected val uiFinishIn: String by lazy { finishIn.toDateFormat(UI_DATE_FORMAT) }

    protected val startAt: Long
            by option(help = "Start moment to realize stamps")
                .convert("LONG") {
                    val formattedDate = try {
                        it.toDateMillis(UI_DATE_FORMAT)
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }

                    val previousInterval = try {
                        getPreviousTimeInterval(
                            formattedDate,
                            attestationConfiguration.frequencyMillis,
                            attestationConfiguration.delayMillis,
                            false
                        )
                    } catch (e: TimeShorterThanCurrentWithDelayException) {
                        fail(e.message!!)
                    }

                    previousInterval
                }.default(getDefaultStartDate())

    protected val finishIn: Long
            by option(help = "Finish moment to realize stamps")
                .convert("LONG") {
                    val formattedDate = try {
                        it.toDateMillis(UI_DATE_FORMAT)
                    } catch (e: ParseException) {
                        fail("Date must be $UI_DATE_FORMAT")
                    }

                    val nextInterval = try {
                        getNextTimeInterval(
                            formattedDate,
                            attestationConfiguration.frequencyMillis,
                            attestationConfiguration.delayMillis,
                            false
                        )
                    } catch (e: TimeShorterThanCurrentWithDelayException) {
                        fail(e.message!!)
                    }

                    if (startAt >= nextInterval) fail("Finish date must be greater than start date")
                    nextInterval
                }.default(
                    getPreviousTimeInterval(
                        System.currentTimeMillis(),
                        attestationConfiguration.frequencyMillis,
                        attestationConfiguration.delayMillis
                    )
                )

    private fun getDefaultStartDate(): Long =
        getLastStampedTime.getSingle(GetLastStampedTime.Request(Source.ELASTICSEARCH))
            .onErrorReturn {
                getPreviousTimeInterval(
                    System.currentTimeMillis() - attestationConfiguration.frequencyMillis,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis
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