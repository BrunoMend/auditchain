package common.di

import commands.ApplicationCommand
import dagger.Component
import domain.datarepository.ConfigurationDataRepository
import domain.datarepository.ElasticsearchDataRepository
import domain.datarepository.FileDataRepository
import domain.datarepository.TimestampDataRepository
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
    fun elasticsearchRepository(): ElasticsearchDataRepository
    fun configurationRepository(): ConfigurationDataRepository
    fun timestampRepository(): TimestampDataRepository
    fun fileRepository(): FileDataRepository
    fun inject(applicationCommand: ApplicationCommand)
}