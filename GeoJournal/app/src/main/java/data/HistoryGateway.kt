package data

import Entity.History
import Interface.HDataManager
import java.util.UUID

class HistoryGateway : HDataManager {

    private val store = mutableMapOf<String, MutableMap<String, History>>()

    override suspend fun create(personId: String, history: History): String {
        val id = UUID.randomUUID().toString()
        store.getOrPut(personId) { mutableMapOf() }[id] = history
        return id
    }

    override suspend fun getById(personId: String, id: String) =
        store[personId]?.get(id)

    override suspend fun getAll(personId: String) =
        store[personId]?.values?.toList() ?: emptyList()

    override suspend fun update(personId: String, id: String, history: History): Boolean {
        if (!store.containsKey(personId)) return false
        if (!store[personId]!!.containsKey(id)) return false
        store[personId]!![id] = history
        return true
    }

    override suspend fun delete(personId: String, id: String) =
        store[personId]?.remove(id) != null
}
