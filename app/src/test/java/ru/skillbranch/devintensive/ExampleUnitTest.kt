package ru.skillbranch.devintensive

import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.devintensive.extensions.TimeUnits
import ru.skillbranch.devintensive.extensions.add
import ru.skillbranch.devintensive.extensions.format
import ru.skillbranch.devintensive.extensions.toUserView
import ru.skillbranch.devintensive.models.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun FirstTestUser() {
        var user0 = User.makeUser("Vasya Pupkin")
        //   var user1 = User.makeUser ("Borya Papkin")
        user0.lastVisit = Date().add(-3, TimeUnits.MINUTE)
        var user1 = user0.copy("33", "Sake", lastVisit = Date().add(3, TimeUnits.HOUR))// .makeUser ("Kolya Petrov")
        var user2 = user0.copy("1233", "Sake", lastVisit = Date())// .makeUser ("Kolya Petrov")

//        var user1 = User("2", "Jonn", "Buba")
//        var user2 = User("3", "Buta", "Rubs", null , lastVisit = Date(), isOnline = true)

        user0.printMe()
        user1.printMe()
        user2.printMe()
        println(
            """
            ${user0.lastVisit?.format()}
            ${user1.lastVisit?.format()}
            ${user2.lastVisit?.format("HH-mm MMMM yy")}
            """.trimIndent()
        )
    }

    @Test
    fun decomposition() {
        var user = User.makeUser("Masha Pupkina")

        fun getUserInfo() = user

        val (id, firstName, lastName) = getUserInfo()

        println("\n$id, $firstName, $lastName\n")
        println("${user.component1()}, ${user.component2()}, ${user.component3()}")
    }

    @Test
    fun test_copy() {
        val user = User.makeUser("Masha Pupkina")
        val user2 = user.copy("123123")
        val user3 = user

//        if (user.equals(user2)) {
        if (user == user2) {
            println(" Объекты равны \n${user.hashCode()} \n$user\n${user2.hashCode()} \n$user2")
        } else {
            println(" Объекты не равны \n${user.hashCode()} \n$user\n${user2.hashCode()} \n$user2")
        }
        if (user === user2) { // === Сравнение ссылок
            println(" Адреса объектов равны \n${System.identityHashCode(user)} \n$user\n${System.identityHashCode(user2)} \n$user2")
        } else {
            println(
                " Адреса объектов не равны \n${System.identityHashCode(user)} \n$user\n${System.identityHashCode(
                    user2
                )} \n$user2"
            )
        }

        if (user === user3) {
            println(" Адреса объектов равны \n${System.identityHashCode(user)} \n$user\n${System.identityHashCode(user3)} \n$user3")
        } else {
            println(
                " Адреса объектов не равны \n${System.identityHashCode(user)} \n$user\n${System.identityHashCode(
                    user3
                )} \n$user3"
            )
        }
    }


    @Test
    fun test_data_mapping() {
        val user = User.makeUser("Masha Pupkina")
        user.printMe()

        val UserV1 = user.toUserView()

        UserV1.printMe()
    }
    @Test
    fun test_abc_factory() {
        val user = User.makeUser("Masha Pupkina")
        val txtMessage = BaseMessage.makeMessage(user, Chat("0"), payload = "any text message", type ="text")
        val imgMessage = BaseMessage.makeMessage(user, Chat("0"), payload = "any image url", type ="image")

        when (imgMessage){
            is TextMessage -> println("This is text message")
            is ImageMessage -> println("This is image  message")
        }

        println(txtMessage.formatMessage())
        println(imgMessage.formatMessage())
    }

}