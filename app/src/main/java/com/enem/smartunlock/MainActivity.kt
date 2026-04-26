package com.enem.smartunlock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Button(
                onClick = {
                    startActivity(Intent(this, LockScreenActivity::class.java))
                }
            ) {
                Text("Iniciar ENEM")
            }
        }
    }
}
