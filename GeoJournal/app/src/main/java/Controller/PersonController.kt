package Controller

import Entity.Person
import android.content.Context
import com.ACID.geojournal.R
import Interface.PDataManager
import data.PersonGateway

class PersonController(private val context: Context) {

    private val dataManager: PDataManager = PersonGateway

    suspend fun addPerson(person: Person): String {
        try {
            return dataManager.add(person)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgAdd), e)
        }
    }

    suspend fun updatePerson(person: Person) {
        try {
            val success = dataManager.update(person)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgUpdate))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgUpdate), e)
        }
    }

    suspend fun removePerson(id: String) {
        try {
            val result = dataManager.getById(id)
            if (result == null) {
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            val success = dataManager.remove(id)
            if (!success) {
                throw Exception(context.getString(R.string.ErrorMsgRemove))
            }
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgRemove), e)
        }
    }

    suspend fun getById(id: String): Person? {
        try {
            return dataManager.getById(id)
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetById), e)
        }
    }

    suspend fun getAll(): List<Person> {
        try {
            return dataManager.getAll()
        } catch (e: Exception) {
            throw Exception(context.getString(R.string.ErrorMsgGetAll), e)
        }
    }

    // Authentication methods
    suspend fun login(email: String, password: String): Boolean {
        try {
            return dataManager.login(email, password)
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    suspend fun logout(): Boolean {
        try {
            return dataManager.logout()
        } catch (e: Exception) {
            throw Exception("Logout failed: ${e.message}")
        }
    }

    suspend fun getCurrentUser(): Person? {
        try {
            return dataManager.getCurrentUser()
        } catch (e: Exception) {
            throw Exception("Failed to get current user: ${e.message}")
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        try {
            return dataManager.resetPassword(email)
        } catch (e: Exception) {
            throw Exception("Password reset failed: ${e.message}")
        }
    }
}