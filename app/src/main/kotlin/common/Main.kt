package common

import common.di.ApplicationComponent
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
        @JvmStatic
        fun main(args: Array<String>) {

//            DaggerApplicationComponent.create()

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