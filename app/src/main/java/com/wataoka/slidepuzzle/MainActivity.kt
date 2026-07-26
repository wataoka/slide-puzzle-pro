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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
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
    var running by remember { mutableStateOf(true) }

    // Elapsed time is measured from a monotonic start timestamp so we keep full
    // precision (analytics will record more decimals than the UI shows). When the
    // game stops we freeze the final value into [frozenElapsedNanos].
    var startNanos by remember { mutableLongStateOf(System.nanoTime()) }
    var elapsedNanos by remember { mutableLongStateOf(0L) }
    var frozenElapsedNanos by remember { mutableLongStateOf(0L) }

    // Tick frequently while running so the centiseconds display stays smooth.
    LaunchedEffect(running) {
        if (running) {
            startNanos = System.nanoTime() - elapsedNanos
            while (true) {
                elapsedNanos = System.nanoTime() - startNanos
                delay(33)
            }
        }
    }
    val displayNanos = if (running) elapsedNanos else frozenElapsedNanos

    fun newGame() {
        state = PuzzleState.shuffled(size)
        moves = 0
        elapsedNanos = 0
        frozenElapsedNanos = 0
        startNanos = System.nanoTime()
        running = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                StatChip("Time", formatTime(displayNanos))
            }

            Board(
                state = state,
                onTileTap = { index ->
                    if (running && state.canMove(index)) {
                        soundPool.play(moveSoundId, 1f, 1f, 1, 0, 1f)
                        // A straight-line slide counts as one move per tile slid.
                        moves += state.moveDistance(index)
                        state = state.move(index)
                        if (state.isSolved) {
                            frozenElapsedNanos = System.nanoTime() - startNanos
                            running = false
                        }
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

        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }

    if (!running && state.isSolved) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { newGame() }) { Text("Play Again") }
            },
            title = { Text("You solved it!") },
            text = { Text("Solved in $moves moves and ${formatTime(frozenElapsedNanos)} seconds.") }
        )
    }
}

// TODO before release: replace with the real banner ad unit ID from your AdMob account.
// This is Google's public test ad unit ID — ads will be labeled "Test Ad" and earn no revenue.
private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 13.sp, color = Color(0xFF7A8194))
        Text(value, fontSize = 22.sp, color = Color(0xFF1A2238))
    }
}

// Renders elapsed time as seconds with two decimal places, e.g. 83.45.
private fun formatTime(nanos: Long): String {
    val totalCentis = nanos / 10_000_000L  // 1 centisecond = 10ms
    val seconds = totalCentis / 100
    val centis = totalCentis % 100
    return "%d.%02d".format(seconds, centis)
}
