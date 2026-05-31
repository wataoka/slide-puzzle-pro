package com.wataoka.slidepuzzle

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF2E6BFF))) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF2F4F8)) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }
    val moveSoundId = remember { soundPool.load(context, R.raw.tile_move, 1) }
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    val size = 4
    var state by remember { mutableStateOf(PuzzleState.shuffled(size)) }
    var moves by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }

    // Timer ticks once per second while the game is running.
    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    fun newGame() {
        state = PuzzleState.shuffled(size)
        moves = 0
        seconds = 0
        running = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Slide Puzzle", fontSize = 30.sp, color = Color(0xFF1A2238))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip("Moves", moves.toString())
            StatChip("Time", formatTime(seconds))
        }

        Board(
            state = state,
            onTileTap = { index ->
                if (running && state.canMove(index)) {
                    soundPool.play(moveSoundId, 1f, 1f, 1, 0, 1f)
                    state = state.move(index)
                    moves++
                    if (state.isSolved) running = false
                }
            },
            modifier = Modifier.weight(1f, fill = false)
        )

        Button(
            onClick = { newGame() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6BFF))
        ) {
            Text("New Game", fontSize = 18.sp)
        }
    }

    if (!running && state.isSolved) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { newGame() }) { Text("Play Again") }
            },
            title = { Text("You solved it!") },
            text = { Text("Solved in $moves moves and ${formatTime(seconds)}.") }
        )
    }
}

@Composable
fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 13.sp, color = Color(0xFF7A8194))
        Text(value, fontSize = 22.sp, color = Color(0xFF1A2238))
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}
