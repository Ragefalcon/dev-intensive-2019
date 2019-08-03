package ru.skillbranch.devintensive.models

data class Profile (
    val firstname: String,
    val lastname: String,
    val about: String,
    val repository: String,
    val rating: Int = 0,
    val respect: Int = 0
){

    val nickname: String = "John Doe"  //TODO implement me
    val rank: String = "Junior Android Developer"

    fun toMap():Map<String,Any> = mapOf(
        "nickName" to nickname,
        "rank" to rank,
        "firstname" to firstname,
        "lastname" to lastname,
        "about" to about,
        "repository" to repository,
        "rating" to rating,
        "respect" to respect
    )
}