package domain.model

import domain.utility.UI_DATE_FORMAT
import domain.utility.toDateFormat
import java.io.Serializable
import java.lang.IllegalArgumentException

data class TimeInterval(val startAt: Long, val finishIn: Long): Serializable {
    init {
        if(startAt >= finishIn) throw IllegalArgumentException("Finish date must be greater than start date")
    }

    override fun toString(): String =
        "${startAt.toDateFormat(UI_DATE_FORMAT)} - ${finishIn.toDateFormat(UI_DATE_FORMAT)}"
}