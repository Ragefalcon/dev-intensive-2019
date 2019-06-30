package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?,String?> {
        val parts:List<String>?=fullName?.split(" ")

        var fName=parts?.getOrNull(0)
        var lName=parts?.getOrNull(1)
        if (fName=="")fName=null
        if (lName=="")lName=null
//       return Pair(fName,lName)
        return fName to lName
    }

    fun transliteration(payload: String, divider:String = " "): String {
        var rez:String=""
        for (i in 0..payload.lastIndex){
            when (payload[i].toString()) {
                "а" -> rez+="a"
                "б" -> rez+="b"
                "в" -> rez+="v"
                "г" -> rez+="g"
                "д" -> rez+="d"
                "е" -> rez+="e"
                "ё" -> rez+="e"
                "ж" -> rez+="zh"
                "з" -> rez+="z"
                "и" -> rez+="i"
                "й" -> rez+="i"
                "к" -> rez+="k"
                "л" -> rez+="l"
                "м" -> rez+="m"
                "н" -> rez+="n"
                "о" -> rez+="o"
                "п" -> rez+="p"
                "р" -> rez+="r"
                "с" -> rez+="s"
                "т" -> rez+="t"
                "у" -> rez+="u"
                "ф" -> rez+="f"
                "х" -> rez+="h"
                "ц" -> rez+="c"
                "ч" -> rez+="ch"
                "ш" -> rez+="sh"
                "щ" -> rez+="sh'"
                "ъ" -> rez+=""
                "ы" -> rez+="i"
                "ь" -> rez+=""
                "э" -> rez+="e"
                "ю" -> rez+="yu"
                "я" -> rez+="ya"
                "А" -> rez+="a".toUpperCase()
                "Б" -> rez+="b".toUpperCase()
                "В" -> rez+="v".toUpperCase()
                "Г" -> rez+="g".toUpperCase()
                "Д" -> rez+="d".toUpperCase()
                "Е" -> rez+="e".toUpperCase()
                "Ё" -> rez+="e".toUpperCase()
                "Ж" -> rez+="Zh"
                "З" -> rez+="z".toUpperCase()
                "И" -> rez+="i".toUpperCase()
                "Й" -> rez+="i".toUpperCase()
                "К" -> rez+="k".toUpperCase()
                "Л" -> rez+="l".toUpperCase()
                "М" -> rez+="m".toUpperCase()
                "Н" -> rez+="n".toUpperCase()
                "О" -> rez+="o".toUpperCase()
                "П" -> rez+="p".toUpperCase()
                "Р" -> rez+="r".toUpperCase()
                "С" -> rez+="s".toUpperCase()
                "Т" -> rez+="t".toUpperCase()
                "У" -> rez+="u".toUpperCase()
                "Ф" -> rez+="f".toUpperCase()
                "Х" -> rez+="h".toUpperCase()
                "Ц" -> rez+="c".toUpperCase()
                "Ч" -> rez+="Ch"
                "Ш" -> rez+="Sh"
                "Щ" -> rez+="Sh'"
                "Ъ" -> rez+="".toUpperCase()
                "Ы" -> rez+="i".toUpperCase()
                "Ь" -> rez+="".toUpperCase()
                "Э" -> rez+="e".toUpperCase()
                "Ю" -> rez+="Yu"
                "Я" -> rez+="Ya"
                " " -> rez+=divider
                    else -> rez+=payload[i].toString()
            }
        }
        return rez
    }

    fun toInitials (firstName:String?, lastName:String?):String?{
        var rez:String? = null
        if (firstName!=null) {
            if (firstName.length>0 && firstName?.get(0).toString()!=" ") {
                rez=firstName?.get(0).toString().toUpperCase()
            } else null
        } else null
        if (lastName!=null) {
            if (lastName.length>0 && lastName?.get(0).toString()!=" ") {
                if (rez!=null)
                    rez+=lastName?.get(0).toString().toUpperCase()
                else
                    rez=lastName?.get(0).toString().toUpperCase()
            } else null
        } else null

//        val fIn = firstName?.get(0)
//        val lIn=if (lastName!=null) { if (lastName.length>0 ) {
//            lastName?.get(0)
//        } else null } else null
//        if (fIn!=null && fIn.toString()!=" ") rez=fIn.toString()
//
//        if (lIn!=null && lIn.toString()!=" ") {
//            if (rez!=null)
//                rez+=lIn.toString()
//            else
//                rez=lIn.toString()
//        }

        return rez
    }
}