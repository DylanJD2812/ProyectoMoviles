package data

import Entity.History
import Interface.HDataManager
import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object HistoryGateway : HDataManager {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override suspend fun add(personId: String, history: History): String {
        try {
            // 1. Upload photo to Firebase Storage if exists
            var photoUrl: String? = null
            history.Photo?.let { bitmap ->
                photoUrl = uploadHistoryImageToStorage(bitmap, personId, history.Id)
            }

            // 2. Create document reference with auto-generated ID
            val documentReference = getHistoryCollection(personId).document()
            history.Id = documentReference.id

            // 3. Prepare history data for Firestore
            val historyData = mapOf(
                "id" to history.Id,
                "title" to history.Title,
                "comment" to history.Comment,
                "location" to history.Location,
                "personId" to personId,
                "photoUrl" to photoUrl,
                "createdAt" to history.CreatedAt.toString()
            )

            // 4. Save to Firestore
            documentReference.set(historyData).await()
            return documentReference.id

        } catch (e: Exception) {
            throw Exception("Failed to create history: ${e.message}")
        }
    }

    override suspend fun getById(personId: String, historyId: String): History? {
        return try {
            val document = getHistoryCollection(personId).document(historyId).get().await()
            if (document.exists()) {
                History().apply {
                    Id = document.getString("id") ?: ""
                    Title = document.getString("title") ?: ""
                    Comment = document.getString("comment") ?: ""
                    Location = document.getString("location") ?: ""
                    PersonId = document.getString("personId") ?: ""
                    // Note: Photo would need to be loaded separately from URL if needed
                    val createdAtStr = document.getString("createdAt") ?: ""
                    if (createdAtStr.isNotEmpty()) {
                        CreatedAt = java.time.LocalDateTime.parse(createdAtStr)
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to get history: ${e.message}")
        }
    }

    override suspend fun getAllByPerson(personId: String): List<History> {
        return try {
            getHistoryCollection(personId).get().await().documents.mapNotNull { document ->
                History().apply {
                    Id = document.getString("id") ?: ""
                    Title = document.getString("title") ?: ""
                    Comment = document.getString("comment") ?: ""
                    Location = document.getString("location") ?: ""
                    PersonId = document.getString("personId") ?: ""
                    val createdAtStr = document.getString("createdAt") ?: ""
                    if (createdAtStr.isNotEmpty()) {
                        CreatedAt = java.time.LocalDateTime.parse(createdAtStr)
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to get histories: ${e.message}")
        }
    }

    override suspend fun update(personId: String, history: History): Boolean {
        return try {
            // Upload new photo if exists
            var photoUrl: String? = null
            history.Photo?.let { bitmap ->
                photoUrl = uploadHistoryImageToStorage(bitmap, personId, history.Id)
            }

            // Update history data in Firestore
            val historyData = mapOf(
                "id" to history.Id,
                "title" to history.Title,
                "comment" to history.Comment,
                "location" to history.Location,
                "personId" to personId,
                "photoUrl" to photoUrl,
                "createdAt" to history.CreatedAt.toString()
            )

            getHistoryCollection(personId).document(history.Id).set(historyData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun remove(personId: String, historyId: String): Boolean {
        return try {
            // Delete photo from Storage
            deleteHistoryImageFromStorage(personId, historyId)

            // Delete from Firestore
            getHistoryCollection(personId).document(historyId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Helper method to get the histories collection for a specific person
    private fun getHistoryCollection(personId: String) =
        db.collection("persons").document(personId).collection("histories")

    // Upload history image to Firebase Storage
    private suspend fun uploadHistoryImageToStorage(bitmap: Bitmap, personId: String, historyId: String): String {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            val imageRef = storageRef.child("history_photos/$personId/$historyId.jpg")
            val uploadTask = imageRef.putBytes(data).await()

            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload history image: ${e.message}")
        }
    }

    // Delete history image from Firebase Storage
    private suspend fun deleteHistoryImageFromStorage(personId: String, historyId: String) {
        try {
            val imageRef = storageRef.child("history_photos/$personId/$historyId.jpg")
            imageRef.delete().await()
        } catch (e: Exception) {
            // Log error but don't throw - it's okay if image doesn't exist
            println("Error deleting history image: ${e.message}")
        }
    }
}