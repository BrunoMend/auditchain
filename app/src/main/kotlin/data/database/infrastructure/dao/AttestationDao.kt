package data.database.infrastructure.dao

import data.database.infrastructure.TableAttestation
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AttestationDao (id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AttestationDao>(TableAttestation)

    var dateStart by TableAttestation.dateStart
    var dateEnd by TableAttestation.dateEnd
    var dataSource by TableAttestation.dataSource
    var dateTimestamp by TableAttestation.dateTimestamp
    var dataSignature by TableAttestation.dataSignature
    var otsData by TableAttestation.otsData
    var isOtsComplete by TableAttestation.isOtsComplete
}