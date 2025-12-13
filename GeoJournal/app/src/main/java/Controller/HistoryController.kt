package Controller

import Entity.History
import android.content.Context
import com.ACID.geojournal.R
import Interface.HDataManager
import data.HistoryGateway

class HistoryController(private val context: Context) {

    private val dataManager: HDataManager = HistoryGateway

    suspend fun addHistory(personId: String, history: History): String {
        try {
            return dataManager.add(personId, history)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd), e)
        }
    }

    suspend fun updateHistory(personId: String, history: History) {
        try {
            val success = dataManager.update(personId, history)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgUpdate))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdate), e)
        }
    }

    suspend fun removeHistory(personId: String, historyId: String) {
        try {
            val result = dataManager.getById(personId, historyId)
            if (result == null) {
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            val success = dataManager.remove(personId, historyId)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgRemove))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgRemove), e)
        }
    }

    suspend fun getHistoryById(personId: String, historyId: String): History? {
        try {
            return dataManager.getById(personId, historyId)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById), e)
        }
    }

    suspend fun getAllHistoriesByPerson(personId: String): List<History> {
        try {
            return dataManager.getAllByPerson(personId)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetAll), e)
        }
    }
}