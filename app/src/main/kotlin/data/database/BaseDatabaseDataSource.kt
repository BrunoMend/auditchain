package data.database

import data.database.infrastructure.DATABASE_DRIVER
import data.database.infrastructure.TableAttestation
import data.database.infrastructure.TableStampException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.Semaphore

abstract class BaseDatabaseDataSource {
    companion object {
        init {
            // set information to get connections
            Database.connect(System.getenv("DATABASE_URL_PATH"), DATABASE_DRIVER)
            transaction {
                // create tables if not exists
                SchemaUtils.create(TableAttestation, TableStampException)
            }
        }
        @JvmStatic
        protected var databaseSemaphore: Semaphore = Semaphore(1, true)
    }
}