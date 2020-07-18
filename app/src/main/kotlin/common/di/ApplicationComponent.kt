package common.di

import commands.ApplicationCommand
import dagger.Component
import domain.datarepository.*
import domain.di.ComputationScheduler
import domain.di.IOScheduler
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Singleton

@Component(modules = [(ApplicationModule::class)])
@Singleton
interface ApplicationComponent {
    @IOScheduler
    fun ioScheduler(): Scheduler
    @ComputationScheduler
    fun computationScheduler(): Scheduler
    fun elasticsearchRepository(): ElasticsearchDataRepository
    fun configurationRepository(): ConfigurationDataRepository
    fun timestampRepository(): TimestampDataRepository
    fun fileRepository(): FileDataRepository
    fun attestationRepository(): AttestationDataRepository
    fun inject(applicationCommand: ApplicationCommand)
}