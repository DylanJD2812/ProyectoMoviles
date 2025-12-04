package Interface

import Entity.History

interface HDataManager {
    suspend fun add(personId: String, history: History): String
    suspend fun getById(personId: String, historyId: String): History?
    suspend fun getAllByPerson(personId: String): List<History>
    suspend fun update(personId: String, history: History): Boolean
    suspend fun remove(personId: String, historyId: String): Boolean
}
