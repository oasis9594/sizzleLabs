package com.example.dell.testappkotlin

class MovieObject {
    var id: Long=0
    var original_title: String?=null
    var overview:String?=null
    var language:String?=null
    var posterPath: String?=null
    var release_date: String?=null
    var rating: Double = 0.toDouble()//vote_average in api
    var popularity: Double = 0.toDouble()
    var vote_count: Int = 0
    var isAdult: Boolean = false
}