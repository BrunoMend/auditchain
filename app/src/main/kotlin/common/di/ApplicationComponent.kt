package common.di

import common.Application
import dagger.Component
import domain.datarepository.ElasticsearchDataRepository
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import domain.utility.Logger
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Singleton

@Component(modules = [(ApplicationModule::class)])
@Singleton
interface ApplicationComponent {
    @IOScheduler
    fun ioScheduler(): Scheduler
    @ComputationScheduler
    fun computationScheduler(): Scheduler
    fun logger(): Logger
    fun elasticsearchDataRepository(): ElasticsearchDataRepository
    fun inject(application: Application)
}