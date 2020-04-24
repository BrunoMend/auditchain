package common

import common.di.ApplicationComponent
import common.di.ApplicationModule
import common.di.DaggerApplicationComponent
import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimerNotifier
import javax.inject.Inject

class Application @Inject constructor() {

    @Inject
    lateinit var getTimerNotifier: GetTimerNotifier

    @Inject
    lateinit var getElasticsearchData: GetElasticsearchData

    private val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule())
            .build()
    }

    init {
        component.inject(this)

        getTimerNotifier
            .getObservable()
            .flatMapSingle { getElasticsearchData.getSingle(it) }
            .subscribe(
                { println(it) },
                { println(it) }
            )

        readLine()
    }
}