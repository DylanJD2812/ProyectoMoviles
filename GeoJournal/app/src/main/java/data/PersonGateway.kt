package data

import Entity.Person
import Interface.PDataManager
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

object PersonGateway : PDataManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val personCollection = db.collection("persons")
    private val storageRef = storage.reference

    override suspend fun add(person: Person): String {
        try {
            // 1. Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(person.Email, person.Password).await()
            val userId = authResult.user?.uid ?: throw Exception("Failed to create user in authentication")

            // 2. Upload photo to Firebase Storage if exists
            var photoUrl: String? = null
            person.Photo?.let { bitmap ->
                photoUrl = uploadImageToStorage(bitmap, userId)
            }

            // 3. Create user profile with display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("${person.Name} ${person.LastName}")
                .build()
            authResult.user?.updateProfile(profileUpdates)?.await()

            // 4. Store additional user data in Firestore
            person.ID = userId
            val personData = mapOf(
                "ID" to person.ID,
                "Name" to person.Name,
                "LastName" to person.LastName,
                "Phone" to person.Phone,
                "Email" to person.Email,
                "Birthday" to person.Birthday.toString(),
                "PhotoUrl" to photoUrl
            )

            personCollection.document(userId).set(personData).await()
            return userId

        } catch (e: Exception) {
            throw Exception("Failed to create user: ${e.message}")
        }
    }

    override suspend fun getById(id: String): Person? {
        return try {
            val document = personCollection.document(id).get().await()
            if (document.exists()) {
                Person().apply {
                    ID = document.getString("ID") ?: ""
                    Name = document.getString("Name") ?: ""
                    LastName = document.getString("LastName") ?: ""
                    Phone = document.getLong("Phone")?.toInt() ?: 0
                    Email = document.getString("Email") ?: ""
                    val birthdayStr = document.getString("Birthday") ?: ""
                    if (birthdayStr.isNotEmpty()) {
                        Birthday = java.time.LocalDate.parse(birthdayStr)
                    }
                    // Note: Photo is not loaded here for performance
                    // You can load it separately if needed
                }
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to get user: ${e.message}")
        }
    }

    override suspend fun getAll(): List<Person> {
        return try {
            personCollection.get().await().documents.mapNotNull { document ->
                Person().apply {
                    ID = document.getString("ID") ?: ""
                    Name = document.getString("Name") ?: ""
                    LastName = document.getString("LastName") ?: ""
                    Phone = document.getLong("Phone")?.toInt() ?: 0
                    Email = document.getString("Email") ?: ""
                    val birthdayStr = document.getString("Birthday") ?: ""
                    if (birthdayStr.isNotEmpty()) {
                        Birthday = java.time.LocalDate.parse(birthdayStr)
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to get users: ${e.message}")
        }
    }

    override suspend fun update(person: Person): Boolean {
        return try {
            // Update user profile in Authentication
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("${person.Name} ${person.LastName}")
                    .build()
                user.updateProfile(profileUpdates)?.await()

                // Update email if changed
                if (user.email != person.Email) {
                    user.updateEmail(person.Email).await()
                }
            }

            // Upload new photo if exists
            var photoUrl: String? = null
            person.Photo?.let { bitmap ->
                photoUrl = uploadImageToStorage(bitmap, person.ID)
            }

            // Update user data in Firestore
            val personData = mapOf(
                "ID" to person.ID,
                "Name" to person.Name,
                "LastName" to person.LastName,
                "Phone" to person.Phone,
                "Email" to person.Email,
                "Birthday" to person.Birthday.toString(),
                "PhotoUrl" to photoUrl
            )

            personCollection.document(person.ID).set(personData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun remove(id: String): Boolean {
        return try {
            // Delete from Authentication
            val user = auth.currentUser
            if (user?.uid == id) {
                user.delete().await()
            }

            // Delete photo from Storage
            deleteImageFromStorage(id)

            // Delete from Firestore
            personCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Upload image to Firebase Storage
    private suspend fun uploadImageToStorage(bitmap: Bitmap, userId: String): String {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            val imageRef = storageRef.child("profile_photos/$userId.jpg")
            val uploadTask = imageRef.putBytes(data).await()

            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.message}")
        }
    }

    // Delete image from Firebase Storage
    private suspend fun deleteImageFromStorage(userId: String) {
        try {
            val imageRef = storageRef.child("profile_photos/$userId.jpg")
            imageRef.delete().await()
        } catch (e: Exception) {
            // Log error but don't throw - it's okay if image doesn't exist
            println("Error deleting image: ${e.message}")
        }
    }

    // Additional authentication methods
    override suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentUser(): Person? {
        return try {
            val user = auth.currentUser
            user?.let { getById(it.uid) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}