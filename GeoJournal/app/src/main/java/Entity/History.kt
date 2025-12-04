package Entity

import android.graphics.Bitmap
import java.time.LocalDateTime

class History {
    private var _id: String = ""
    private var _title: String = ""
    private var _comment: String = ""
    private var _photo: Bitmap? = null
    private var _createdAt: LocalDateTime = LocalDateTime.now()
    private var _personId: String = "" // Reference to the person who owns this history

    constructor()

    // Primary constructor
    constructor(id: String, title: String, comment: String, photo: Bitmap?, personId: String, createdAt: LocalDateTime = LocalDateTime.now()) {
        this._id = id
        this._title = title
        this._comment = comment
        this._photo = photo
        this._personId = personId
        this._createdAt = createdAt
    }

    // Getter / Setter for id
    var Id: String
        get() = this._id
        set(value) { this._id = value }

    // Getter / Setter for title
    var Title: String
        get() = this._title
        set(value) { this._title = value }

    // Getter / Setter for comment
    var Comment: String
        get() = this._comment
        set(value) { this._comment = value }

    // Getter / Setter for photo
    var Photo: Bitmap?
        get() = this._photo
        set(value) { this._photo = value }

    // Getter / Setter for personId
    var PersonId: String
        get() = this._personId
        set(value) { this._personId = value }

    // Getter / Setter for createdAt
    var CreatedAt: LocalDateTime
        get() = this._createdAt
        set(value) { this._createdAt = value }
}