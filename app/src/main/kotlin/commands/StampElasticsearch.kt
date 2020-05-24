package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimerNotifier
import domain.usecase.StampData
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class StampElasticsearch @Inject constructor(
    private val getTimerNotifier: GetTimerNotifier,
    private val getElasticsearchData: GetElasticsearchData,
    private val stampData: StampData
): CliktCommand() {

    private val verbose by option().flag("--no-verbose")

    override fun run() {
        getTimerNotifier
            .getObservable()
            .flatMapSingle { getElasticsearchData.getSingle(it) }
            .flatMapCompletable {
                if (it.first.isNotEmpty()) {
                    //TODO log in a file
                    if (verbose) println("Stamping: \n ${it.first} \n File proof: ${it.second}")
                    else println("no verbose")
//                    stampData.getCompletable(it.first.toByteArray(), it.second)
                    Completable.complete()
                } else {
                    //TODO log in a file
                    if (verbose) println("No data to stamp")
                    else println("no verbose")
                    Completable.complete()
                }
            }.onErrorReturn { Completable.complete() }
            .subscribe()
        readLine()
    }
}