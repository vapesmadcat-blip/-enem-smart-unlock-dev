package com.enem.smartunlock

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LocalQuestion(
    val subject: String,
    val text: String,
    val options: List<String>,
    val correct: Int
)

class LockScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            ChallengeScreen {
                finish()
            }
        }
    }
}

@Composable
fun ChallengeScreen(onFinish: () -> Unit) {
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
    var score by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("Acerte 3 seguidas para liberar.") }

    val q = questions[index]
    val progress = score / 3f

    val deepBlue = Color(0xFF0B1A2F)
    val cardBlue = Color(0xFF122B3F)
    val brightBlue = Color(0xFF3F9EF0)

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
                text = "Progresso: $score/3 · Tempo ganho: $minutes min",
                color = Color(0xFFA0C4E2),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = brightBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBlue)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = q.subject,
                        color = Color(0xFFA0C4E2),
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
                            onClick = {
                                if (optionIndex == q.correct) {
                                    score += 1
                                    val gain = score * 5
                                    minutes += gain
                                    feedback = "Correto. +$gain minutos."

                                    if (score >= 3) {
                                        onFinish()
                                    } else {
                                        index = (index + 1) % questions.size
                                    }
                                } else {
                                    score = 0
                                    minutes /= 2
                                    feedback = "Errou. Perda parcial aplicada."
                                    index = (index + 1) % questions.size
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brightBlue)
                        ) {
                            Text(option, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = feedback,
                color = Color(0xFFA0C4E2),
                fontSize = 14.sp
            )
        }
    }
}
