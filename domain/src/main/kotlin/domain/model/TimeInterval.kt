package domain.model

import java.lang.IllegalArgumentException

data class TimeInterval(val startAt: Long, val finishIn: Long) {
    init {
        if(startAt > finishIn) throw IllegalArgumentException("Finish date must be greater than start date")
    }
}