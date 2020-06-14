package data.repository

import data.remote.ElasticsearchRemoteDataSource
import domain.datarepository.AttestationDataRepository
import domain.datarepository.ElasticsearchDataRepository
import domain.exception.NoDataException
import domain.model.Attestation
import domain.model.Source
import domain.model.TimeInterval
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class ElasticsearchRepository @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val timestampRepository: TimestampRepository,
    private val attestationDataRepository: AttestationDataRepository,
    private val elasticsearchRemoteDataSource: ElasticsearchRemoteDataSource
) : ElasticsearchDataRepository {

    private fun getElasticsearchData(timeInterval: TimeInterval): Single<ByteArray> =
        configurationRepository.getElasticsearchConfiguration()
            .flatMap {
                elasticsearchRemoteDataSource.getLogs(
                    it.indexPattern,
                    it.getDefaultQuery(timeInterval),
                    it.resultMaxSize
                )
            }.map {
                val initLogs = "\"hits\":["
                val result = it.substring((it.indexOf(initLogs) + initLogs.length - 1), it.lastIndexOf("]") + 1)
                if (result.isNotEmpty() && result != "[]") result.toByteArray() else throw NoDataException(timeInterval)
            }

    override fun verifyElasticsearchData(intervals: List<TimeInterval>): Completable =
        attestationDataRepository.getAttestation(intervals.first(), Source.ELASTICSEARCH)
            .flatMapCompletable {
                println("Attestation: \n" +
                        "${it.timeInterval.startAt}, " +
                        "${it.timeInterval.finishIn}, " +
                        "${it.source}, " +
                        "${it.dateTimestamp}, " +
                        "${it.otsData}")
                Completable.complete()
            }
    
    override fun stampElasticsearchData(intervals: List<TimeInterval>): Observable<Attestation> =
            Observable.fromIterable(intervals)
                .flatMap { timeInterval ->
                    getElasticsearchData(timeInterval)
                        .flatMapObservable { data ->
                            timestampRepository.stampData(data)
                                .flatMapObservable { otsData ->
                                    val attestation =
                                        Attestation(timeInterval, Source.ELASTICSEARCH, Date().time, otsData)
                                    attestationDataRepository.saveAttestation(attestation)
                                        .andThen (Observable.just(attestation))
                                }.onErrorComplete()
                        }.onErrorComplete()
                }.onErrorComplete()
}