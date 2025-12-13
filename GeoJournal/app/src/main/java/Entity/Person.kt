package Entity

import android.graphics.Bitmap
import java.time.LocalDate

class Person {
    var ID: String = ""
    var Name: String = ""
    var LastName: String = ""
    var Phone: Int = 0
    var Email: String = ""
    var Password: String = "" // Added password field
    lateinit var Birthday: LocalDate
    var Photo: Bitmap? = null
    var PhotoUrl: String? = null

    constructor()

    constructor(
        id: String = "",
        name: String,
        lastName: String,
        phone: Int,
        email: String,
        password: String, // Added password parameter
        birthday: LocalDate,
        photo: Bitmap?,
        PhotoUrl: String?
    ) {
        this.ID = id
        this.Name = name
        this.LastName = lastName
        this.Phone = phone
        this.Email = email
        this.Password = password
        this.Birthday = birthday
        this.Photo = photo
        this.PhotoUrl = PhotoUrl
    }

    fun FullName() = "$Name $LastName"
}