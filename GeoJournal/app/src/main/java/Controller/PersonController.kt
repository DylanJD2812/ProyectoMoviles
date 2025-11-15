package Controller

import Entity.Person
import android.content.Context
import com.ACID.geojournal.R
import data.IDataManager
import data.MemoryDataManager

class PersonController {
    private var dataManager: IDataManager = MemoryDataManager
    private lateinit var context: Context

    constructor(context: Context){
        this.context = context
    }

    fun addPerson(person: Person){
        try {
            dataManager.add(person)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgAdd))
        }
    }
    fun updatePerson(person: Person){
        try {
            dataManager.update(person)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgUpdate))
        }
    }
    fun removePerson(id: String){
        try {
            val result = dataManager.getById(id)
            if (result == null){
                throw Exception(context.getString(R.string.MsgDataNotFound))
            }
            dataManager.remove(id)
        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgRemove))
        }
    }
    fun GetById(id: String): Person? {
        try {
            return  dataManager.getById(id)

        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }
    fun GetByFullName(fullName: String): Person? {
        try {
            return dataManager.getByFullName(fullName)

        }catch (e: Exception){
            throw Exception(context.getString(R.string.ErrorMsgGetById))
        }
    }
}