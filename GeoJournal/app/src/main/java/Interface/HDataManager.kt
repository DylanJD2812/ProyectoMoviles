package Interface

import Entity.History

interface HDataManager {
    suspend fun create(personId: String, history: History): String
    suspend fun getById(personId: String, id: String): History?
    suspend fun getAll(personId: String): List<History>
    suspend fun update(personId: String, id: String, history: History): Boolean
    suspend fun delete(personId: String, id: String): Boolean
}
