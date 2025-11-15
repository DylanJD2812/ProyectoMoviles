package Entity

import android.graphics.Bitmap
import java.time.LocalDate

class Person {
    private var id: String= ""
    private var name: String= ""
    private var fLastName: String=""
    private var sLastName: String=""
    private var phone: Int=0
    private var email: String=""
    private lateinit var birthday: LocalDate
    private var photo: Bitmap? = null

    constructor()

    constructor(id: String,name: String,fLastName: String,
                sLastName: String,phone: Int, email: String,
                birthday: LocalDate,photo: Bitmap?){
        this.id=id
        this.name=name
        this.fLastName=fLastName
        this.sLastName=sLastName
        this.phone=phone
        this.email=email
        this.birthday=birthday
        this.photo=photo
    }

    var ID: String
        get() = this.id
        set(value){this.id=value}

    var Name: String
        get() = this.name
        set(value){this.name=value}

    var FLastName: String
        get() = this.fLastName
        set(value){this.fLastName=value}

    var SLastName: String
        get() = this.sLastName
        set(value){this.sLastName=value}

    var Phone: Int
        get() = this.phone
        set(value){this.phone=value}

    var Email: String
        get() = this.email
        set(value){this.email=value}

    var Birthday: LocalDate
        get() = this.birthday
        set(value){this.birthday=value}

    var Photo: Bitmap?
        get() = this.photo
        set(value){this.photo=value}

    fun FullName() = "$this.name $this.fLastName $this.sLastName"

}