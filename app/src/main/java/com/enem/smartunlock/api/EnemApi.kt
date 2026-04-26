package com.enem.smartunlock.api

import com.enem.smartunlock.data.Question
import retrofit2.http.GET

interface EnemApi {

    @GET("exams/2023/questions")
    suspend fun getQuestions(): List<Question>
}
