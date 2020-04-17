package common

import common.di.ApplicationComponent
import common.di.ApplicationModule
import domain.usecase.GetElasticsearchData
import domain.usecase.GetTimerNotifier

class Main {

//    private val component: ApplicationComponent by lazy {
//        DaggerApplicationComponent.builder()
//            .flowComponent((parentFragment as FlowContainerFragment).component)
//            .movieListModule(MovieListModule(this))
//            .build()
//    }

    companion object {
        fun main(args: Array<String>) {
//            val component: ApplicationComponent by lazy {
//                DaggerApplicationComponent.builder()
//                    .aplicationModule(ApplicationModule())
//                    .build()
//            }
//            component.inject(this)
//
//            GetTimerNotifier()
//                .getObservable()
//                .flatMapSingle { GetElasticsearchData().getSingle(it) }
//                .subscribe(
//                    { println(it) },
//                    { println(it) }
//                )

            readLine()
        }
    }
}