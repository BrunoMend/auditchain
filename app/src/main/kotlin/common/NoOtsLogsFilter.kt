package common

import java.util.logging.Filter
import java.util.logging.LogRecord

// used in logconfig.properties
class NoOtsLogsFilter : Filter {
    override fun isLoggable(record: LogRecord): Boolean =
        !record.loggerName.contains("com.eternitywall")
}