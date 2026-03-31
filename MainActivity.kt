package com.example.socialspark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialspark.ui.theme.AppTheme
import com.example.socialspark.ui.theme.SocialSparkTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(AppTheme.BlackWhite) }
            var showSplash by remember { mutableStateOf(true) }

            SocialSparkTheme(appTheme = currentTheme) {
                if (showSplash) {
                    SplashScreen { showSplash = false }
                } else {
                    MainScreen(
                        onThemeToggle = {
                            currentTheme = if (currentTheme == AppTheme.BlackWhite) AppTheme.Spark else AppTheme.BlackWhite
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )
    val alphaValue by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoAlpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SS",
            fontSize = 100.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale).alpha(alphaValue)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onThemeToggle: () -> Unit) {
    var timeInput by remember { mutableStateOf("") }
    var suggestion by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SOCIAL SPARK", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    TextButton(onClick = onThemeToggle) {
                        Text(
                            text = "Switch Theme",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "When is it?",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = timeInput,
                onValueChange = { 
                    timeInput = it
                    errorMessage = "" 
                },
                label = { Text("e.g. Morning, Afternoon, Night") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val result = getSocialSpark(timeInput)
                        if (result.startsWith("Error:")) {
                            errorMessage = result.removePrefix("Error: ")
                            suggestion = ""
                        } else {
                            suggestion = result
                            errorMessage = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Get Spark")
                }

                IconButton(
                    onClick = {
                        timeInput = ""
                        suggestion = ""
                        errorMessage = ""
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = suggestion.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = suggestion,
                        modifier = Modifier.padding(24.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            AnimatedVisibility(
                visible = errorMessage.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getSocialSpark(input: String): String {
    val time = input.trim().lowercase()
    return when {
        time.contains("morning") && !time.contains("mid") -> 
            "Send a \"Good morning\" text to a family member. It starts the day with love!"
        time.contains("mid-morning") || (time.contains("mid") && time.contains("morning")) -> 
            "Reach out to a colleague with a quick \"Thank you.\" Appreciation builds great teams!"
        time.contains("afternoon") && !time.contains("snack") -> 
            "Share a funny meme or interesting link with a friend. Laughter is the best bridge!"
        time.contains("snack") || (time.contains("afternoon") && time.contains("snack")) -> 
            "Send a quick \"thinking of you\" message. A small spark can brighten someone's whole day."
        time.contains("dinner") -> 
            "Call a friend or relative for a 5-minute catch-up. Real voices create real connections."
        time.contains("night") || time.contains("evening") || time.contains("after dinner") -> 
            "Leave a thoughtful comment on a friend's post. Meaningful words matter more than likes."
        time.isEmpty() -> 
            "Error: Please enter a time of day to find your social spark!"
        else -> 
            "Error: Oops! That's not a time we recognize yet. Try something like 'Morning' or 'Dinner' to find your spark and spread some joy!"
    }
}
