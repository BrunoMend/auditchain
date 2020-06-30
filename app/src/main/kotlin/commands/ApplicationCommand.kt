package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import common.di.ApplicationComponent
import common.di.ApplicationModule
import common.di.DaggerApplicationComponent
import javax.inject.Inject

class ApplicationCommand @Inject constructor() : CliktCommand(name = "auditchain") {

    private val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule())
            .build()
    }

    @Inject
    lateinit var stampElasticsearch: StampElasticsearchCommand

    @Inject
    lateinit var verifyElasticsearch: VerifyElasticsearchCommand

    init {
        component.inject(this)
        this.subcommands(stampElasticsearch, verifyElasticsearch)
    }

    override fun run() = Unit

}