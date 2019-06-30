package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?,String?> {
        val parts:List<String>?=fullName?.split(" ")

        val fName=parts?.getOrNull(0)
        val lName=parts?.getOrNull(1)

//       return Pair(fName,lName)
        return fName to lName
    }

    fun transliteration(payload: String, divider:String = " "): String {
        return "sdfa"
    }

    fun toInitials (firstName:String?, lastName:String?):String?{
        return "AA"
    }
}