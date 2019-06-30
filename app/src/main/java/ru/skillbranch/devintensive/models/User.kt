package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(
    val id:String,
    var firstName:String?,
    var lastName:String?,
    var avatar:String?,
    var rating:Int = 0,
    var respect:Int = 0,
    var lastVisit: Date? = null,
    var isOnline: Boolean = false
    ) {
    constructor(idUser:String, firstName:String?, lastName:String?):this (
        id=idUser,
        firstName=firstName,
        lastName=lastName,
        avatar = null
    )

    constructor(id: String): this (id, "Ivan", "Petrov")

    init {
        println("I'm is Alive!!! \n${if (lastName==="Petrov") "Simple is  $firstName $lastName" else "Best of the best: $firstName $lastName"}\n" +
                "${getIntro()}")
    }

    private fun getIntro() = """
        hhh....
    """.trimIndent()

    fun printMe()= println("""
            id: $id
            firstName: $firstName
            lastName: $lastName
            avatar: $avatar
            rating: $rating
            respect: $respect
            lastVisit: $lastVisit
            isOnline: $isOnline
        """.trimIndent())

    companion object FactoryB {
        private var lastId: Int = -1
        fun makeUser(fullName: String?):User{
            lastId++

            val (fName,lName)=Utils.parseFullName(fullName)
            return User("$lastId",fName,lName)
        }
    }
}





