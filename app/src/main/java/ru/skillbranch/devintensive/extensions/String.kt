package ru.skillbranch.devintensive.extensions

fun String.stripHtml():String {
//    val matchedResults = Regex(pattern = """<|>.*<|>.*""").findAll(input = this)
//    val result = StringBuilder()
//    for (matchedText in matchedResults) {
//        result.append(matchedText.value + " ")
//    }
//    return result.toString() //regex.replace(this,"")

    val regex = "<[^<]+>".toRegex()//setOf(RegexOption.IGNORE_CASE, RegexOption.COMMENTS, RegexOption.UNIX_LINES))
    val regex2 = "&[^&]+;".toRegex()//setOf(RegexOption.IGNORE_CASE, RegexOption.COMMENTS, RegexOption.UNIX_LINES))
    var str1:List<String> = this.split("<")
    var rez:String =""
    for (i in str1) {rez+=i.substringAfter(">") }
////    var str2:List<String> = this.split(">")
    var result:String = regex.replace(this,"")
    result=regex2.replace(result,"")
    return result.replace("\\s* ".toRegex()," ").replace("\'|\"".toRegex(),"")
}
fun String.truncate(dlina: Int = 16):String {

    var str:String = this.take(dlina).trimEnd()
    if (this.trimEnd().length>dlina){str+="..."}

    return str
}
