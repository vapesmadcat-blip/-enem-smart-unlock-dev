data class Question(
    val title: String,
    val alternatives: List<Alternative>
)

data class Alternative(
    val text: String,
    val isCorrect: Boolean
)
