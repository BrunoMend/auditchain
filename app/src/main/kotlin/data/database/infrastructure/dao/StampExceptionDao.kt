package data.database.infrastructure.dao

import data.database.infrastructure.TableStampException
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class StampExceptionDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StampExceptionDao>(TableStampException)

    var dateStart by TableStampException.dateStart
    var dateEnd by TableStampException.dateEnd
    var dataSource by TableStampException.dataSource
    var exception by TableStampException.exception
    var dateException by TableStampException.dateException
}