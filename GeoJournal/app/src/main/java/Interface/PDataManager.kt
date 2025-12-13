package Interface

import Entity.Person

interface PDataManager {
    suspend fun add(person: Person): String
    suspend fun getById(id: String): Person?
    suspend fun getAll(): List<Person>
    suspend fun update(person: Person): Boolean
    suspend fun remove(id: String): Boolean

    // Authentication methods
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout(): Boolean
    suspend fun getCurrentUser(): Person?
    suspend fun resetPassword(email: String): Boolean
}
