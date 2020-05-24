package domain.usecase

import domain.datarepository.ConfigurationDataRepository
import domain.di.ComputationScheduler
import domain.model.TimeInterval
import domain.utility.toDateMillis
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class GetTimeIntervals @Inject constructor(
    private val configurationDataRepository: ConfigurationDataRepository,
    @ComputationScheduler private val executorScheduler: Scheduler
) {
    fun getSingle(startAt: String, finishIn: String): Single<List<TimeInterval>> {
//        val localNow = LocalDateTime.parse()
//        val currentZone = ZoneId.systemDefault()
//        val zonedNow = ZonedDateTime.of(localNow, currentZone)

        //TODO
        // get all time intervals between start and finish moment
        // get a list of time intervals from all day long
        //

        val attestationConfiguration = configurationDataRepository.getAttestationConfiguration().blockingGet()
        val frequencyInterval = attestationConfiguration.frequency * 60000

        val startDate = startAt.take(10).toDateMillis("yyyy-MM-dd")
        val startDateTime = startAt.toDateMillis()
        val firstInterval = startDateTime - startDateTime.rem(frequencyInterval) + startDate

        val finishDate = finishIn.take(10).toDateMillis("yyyy-MM-dd")
        val finishDateTime = startAt.toDateMillis()
        val lastInterval = finishDateTime - finishDateTime.rem(frequencyInterval) + finishDate + frequencyInterval

        val intervalList: MutableList<TimeInterval> = mutableListOf(TimeInterval(firstInterval, lastInterval))

        for (interval in firstInterval..lastInterval) {

            //todo
            // list of days in datemillis?
            // increment intervalList considering midnight
        }

        return Single.just(intervalList)
    }
}