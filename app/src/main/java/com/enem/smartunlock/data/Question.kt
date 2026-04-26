package com.enem.smartunlock.data

data class Question(
    val title: String = "",
    val alternatives: List<Alternative> = emptyList()
)

data class Alternative(
    val text: String = "",
    val isCorrect: Boolean = false
)
