package commands

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import domain.exception.TimeShorterThanCurrentWithDelayException
import domain.model.AttestationConfiguration
import domain.model.Source
import domain.usecase.GetLastStampedTime
import domain.utility.*
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import java.text.ParseException

abstract class BaseTimeIntervalCommand(
    client: OkHttpClient,
    private val attestationConfiguration: AttestationConfiguration,
    private val getLastStampedTime: GetLastStampedTime
) : BaseCommand(client) {

    protected val uiStartAt: String by lazy { startAt.toDateFormat(UI_DATE_FORMAT) }
    protected val uiFinishIn: String by lazy { finishIn.toDateFormat(UI_DATE_FORMAT) }

    protected abstract val ignoreStartAtIfAlreadyExistsStamps: Boolean

    protected val startAt: Long
            by option(help = "Start moment to realize stamps")
                .convert("LONG") { passedStartDate ->
                    var startDate: Long? = null

                    if (ignoreStartAtIfAlreadyExistsStamps)
                        startDate = getStartDateBasedOnPreviousStamp()
                            .doOnSuccess {
                                if (passedStartDate != "")
                                    printVerbose(
                                        "There is already a previous stamp, " +
                                                "so the informed start date will be ignored "
                                    )
                            }
                            .onErrorReturn { null }
                            .blockingGet()

                    if (startDate == null) {
                        val formattedDate = try {
                            passedStartDate.toDateMillis(UI_DATE_FORMAT)
                        } catch (e: ParseException) {
                            fail("Date must be $UI_DATE_FORMAT")
                        }

                        startDate = try {
                            getPreviousTimeInterval(
                                formattedDate,
                                attestationConfiguration.frequencyMillis,
                                attestationConfiguration.delayMillis,
                                false
                            )
                        } catch (e: TimeShorterThanCurrentWithDelayException) {
                            fail(e.message!!)
                        }
                    }

                    startDate ?: fail("An error occurred with the start date")
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
        getStartDateBasedOnPreviousStamp()
            .onErrorReturn {
                getPreviousTimeInterval(
                    System.currentTimeMillis() - attestationConfiguration.frequencyMillis,
                    attestationConfiguration.frequencyMillis,
                    attestationConfiguration.delayMillis
                )
            }
            .blockingGet()

    private fun getStartDateBasedOnPreviousStamp(): Single<Long> =
        getLastStampedTime.getSingle(GetLastStampedTime.Request(Source.ELASTICSEARCH))

    override fun run() {
        if (startAt >= finishIn) {
            println("There is no data to stamp now.")
            return
        }
    }
}