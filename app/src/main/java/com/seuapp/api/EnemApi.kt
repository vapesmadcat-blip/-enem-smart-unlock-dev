interface EnemApi {

    @GET("exams/2023/questions")
    suspend fun getQuestions(): List<Question>
}
