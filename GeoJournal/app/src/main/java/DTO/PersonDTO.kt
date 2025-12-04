package DTO

data class PersonDTO(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val phone: Int = 0,
    val email: String = "",
    val password: String = "",
    val birthday: String = "",        // LocalDate → Strin
    val photoUrl: String? = null      // Bitmap → URL
)
