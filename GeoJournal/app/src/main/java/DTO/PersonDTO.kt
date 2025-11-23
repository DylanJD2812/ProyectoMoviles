package DTO

data class PersonDTO(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: Int = 0,
    val email: String = "",
    val password: String = "", // Added password field
    val birthday: String = "",        // LocalDate → String
    val photoUrl: String? = null      // Bitmap → URL
)
