package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import domain.model.AttestationConfiguration
import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimeIntervals
import domain.usecase.StampData
import domain.utility.getNextTimeInterval
import domain.utility.getPreviousTimeInterval
import domain.utility.toDateFormat
import domain.utility.toDateMillis
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.text.ParseException
import javax.inject.Inject

class StampElasticsearchCommand @Inject constructor(
    private val getTimeIntervals: GetTimeIntervals,
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData,
    attestationConfiguration: AttestationConfiguration
) : CliktCommand() {

    private val startAt: Long
            by option(help = "Start moment to realize stamps")
                .convert("LONG") {
                    try {
                        it.toDateMillis("yyyy-MM-dd HH:mm")
                    } catch (e: ParseException) {
                        fail("Date must be yyyy-MM-dd HH:mm")
                    }
                }.default(getPreviousTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val finishIn: Long
            by option(help = "Finish moment to realize stamps")
                .convert("LONG") {
                    try {
                        it.toDateMillis("yyyy-MM-dd HH:mm")
                    } catch (e: ParseException) {
                        fail("Date must be yyyy-MM-dd HH:mm")
                    }
                }.default(getNextTimeInterval(System.currentTimeMillis(), attestationConfiguration.frequencyMillis))

    private val verbose
            by option("-v", "--verbose").flag()

    //TODO logs in a file
    override fun run() {
        println("startAt: ${startAt.toDateFormat("yyyy-MM-dd HH:mm:ss")} - finishIn: ${finishIn.toDateFormat("yyyy-MM-dd HH:mm:ss")}")
        getTimeIntervals
            .getSingle(startAt, finishIn)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle {
                if (verbose) println("${it.startAt.toDateFormat("yyyy-MM-dd HH:mm:ss")} - ${it.finishIn.toDateFormat("yyyy-MM-dd HH:mm:ss")}")
                Single.just(it)
            }
            .flatMapSingle { getElasticsearchData.getSingle(it) }
            .flatMapCompletable {
                if (it.first.isNotEmpty()) {
                    if (verbose) println("Stamping: \n ${it.first} \n File proof: ${it.second}")
                    stampData.getCompletable(it.first.toByteArray(), it.second)
                } else {
                    if (verbose) println("No data to stamp")
                    Completable.complete()
                }
            }
            .onErrorReturn { Completable.complete() }
            .blockingSubscribe()
    }
}