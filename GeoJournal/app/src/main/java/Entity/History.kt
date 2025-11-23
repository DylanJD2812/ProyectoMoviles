package Entity

import android.graphics.Bitmap

class History{

    private var _id: Int = 0
    private var _title: String = ""
    private var _comment: String = ""
    private var _photo: Bitmap? = null

    constructor()
    // Primary constructor
    constructor(id: Int, title: String, comment: String, photo: Bitmap?){
        this._id = id
        this._title = title
        this._comment = comment
        this._photo = photo
    }

    // Getter / Setter for id
    var Id: Int
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
}