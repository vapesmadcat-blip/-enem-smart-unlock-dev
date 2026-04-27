package com.enem.smartunlock

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class LocalQuestion(
    val subject: String,
    val text: String,
    val options: List<String>,
    val correct: Int
)

class LockScreenActivity : ComponentActivity() {

    private val prefs by lazy {
        getSharedPreferences("enem_unlock", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val unlockedUntil = prefs.getLong("unlocked_until", 0L)
        if (System.currentTimeMillis() < unlockedUntil) {
            finish()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContent {
            ChallengeScreen(
                onUnlocked = { minutes ->
                    val unlockedUntilTime =
                        System.currentTimeMillis() + minutes * 60_000L

                    prefs.edit()
                        .putLong("unlocked_until", unlockedUntilTime)
                        .apply()

                    finish()
                }
            )
        }
    }
}

@Composable
fun ChallengeScreen(onUnlocked: (Int) -> Unit) {
    val questions = remember {
        listOf(
            LocalQuestion(
                subject = "Física",
                text = "Energia hidrelétrica converte principalmente qual tipo de energia?",
                options = listOf(
                    "Energia térmica",
                    "Energia potencial gravitacional",
                    "Energia química",
                    "Energia solar"
                ),
                correct = 1
            ),
            LocalQuestion(
                subject = "História",
                text = "A mecanização agrícola e o cercamento dos campos contribuíram para:",
                options = listOf(
                    "Êxodo rural",
                    "Redução urbana",
                    "Feudalismo",
                    "Isolamento comercial"
                ),
                correct = 0
            ),
            LocalQuestion(
                subject = "Química",
                text = "O aumento do efeito estufa está ligado principalmente ao:",
                options = listOf(
                    "Oxigênio",
                    "Nitrogênio",
                    "Dióxido de carbono",
                    "Argônio"
                ),
                correct = 2
            )
        )
    }

    var index by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var locked by remember { mutableStateOf(false) }
    var shouldUnlock by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("Faltam 3 acertos seguidos.") }

    val required = 3
    val remaining = required - streak
    val q = questions[index]

    val deepBlue = Color(0xFF0B1A2F)
    val cardBlue = Color(0xFF122B3F)
    val brightBlue = Color(0xFF3F9EF0)
    val softBlue = Color(0xFFA0C4E2)
    val green = Color(0xFF21B573)
    val red = Color(0xFFD94F5C)

    LaunchedEffect(shouldUnlock) {
        if (shouldUnlock) {
            delay(900)
            onUnlocked(minutes)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBlue),
        color = deepBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Modo Estudo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (streak >= required) {
                    "Desbloqueando..."
                } else {
                    "Faltam $remaining · Tempo ganho: $minutes min"
                },
                color = softBlue,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { streak / required.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = green,
                trackColor = Color(0xFF1A2E44)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = cardBlue)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = q.subject,
                        color = softBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = q.text,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    q.options.forEachIndexed { optionIndex, option ->
                        Button(
                            enabled = !locked && !shouldUnlock,
                            onClick = {
                                locked = true

                                if (optionIndex == q.correct) {
                                    val newStreak = streak + 1
                                    streak = newStreak

                                    val gain = newStreak * 5
                                    minutes += gain

                                    if (newStreak >= required) {
                                        feedback = "✅ 3/3 concluído. Desbloqueando..."
                                        shouldUnlock = true
                                    } else {
                                        feedback = "✅ Correto. Faltam ${required - newStreak}."
                                        index = (index + 1) % questions.size
                                        locked = false
                                    }
                                } else {
                                    streak = 0
                                    minutes /= 2
                                    feedback = "❌ Errou. Voltou para 3."
                                    index = (index + 1) % questions.size
                                    locked = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = brightBlue,
                                disabledContainerColor = brightBlue.copy(alpha = 0.45f)
                            )
                        ) {
                            Text(option, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = feedback,
                color = if (feedback.startsWith("❌")) red else softBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
