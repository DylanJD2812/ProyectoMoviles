package data

import Entity.Person
import Interface.PDataManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object PersonGateway : PDataManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val personCollection = db.collection("persons")

    override suspend fun add(person: Person): String {
        try {
            // 1. Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(person.Email, person.Password).await()
            val userId = authResult.user?.uid ?: throw Exception("Failed to create user in authentication")

            // 2. Create user profile with display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("${person.Name} ${person.LastName}")
                .build()
            authResult.user?.updateProfile(profileUpdates)?.await()

            // 3. Store additional user data in Firestore
            person.ID = userId
            val personData = mapOf(
                "ID" to person.ID,
                "Name" to person.Name,
                "LastName" to person.LastName,
                "Phone" to person.Phone,
                "Email" to person.Email,
                "Birthday" to person.Birthday.toString(),
                // Don't store password in Firestore for security
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
                    // Password is not stored in Firestore for security
                    val birthdayStr = document.getString("Birthday") ?: ""
                    if (birthdayStr.isNotEmpty()) {
                        Birthday = java.time.LocalDate.parse(birthdayStr)
                    }
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

            // Update user data in Firestore
            val personData = mapOf(
                "ID" to person.ID,
                "Name" to person.Name,
                "LastName" to person.LastName,
                "Phone" to person.Phone,
                "Email" to person.Email,
                "Birthday" to person.Birthday.toString()
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

            // Delete from Firestore
            personCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Additional authentication methods
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentUser(): Person? {
        return try {
            val user = auth.currentUser
            user?.let { getById(it.uid) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}