package com.wataoka.slidepuzzle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tile slide duration — fast enough to never get in the user's way.
private const val SLIDE_MS = 110

/**
 * Renders the puzzle grid. Each numbered tile is a single composable placed by
 * an animated offset, so when the board state changes a tile *slides* to its new
 * cell instead of snapping. The empty recesses are drawn once as a static
 * background grid so the blank slot still reads as a hole.
 */
@Composable
fun Board(
    state: PuzzleState,
    onTileTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gap = 6.dp
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD9DEE8))
            .padding(gap)
    ) {
        val size = state.size
        // Square cell size derived from the available (post-padding) width.
        val cellSize = (maxWidth - gap * (size - 1)) / size
        val step = cellSize + gap

        // Static recesses for every cell so empty slots look like holes.
        for (row in 0 until size) {
            for (col in 0 until size) {
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .offset(x = step * col, y = step * row)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFC7CEDB))
                )
            }
        }

        // One movable tile per value; its offset animates as the value moves cells.
        for (value in 1 until size * size) {
            key(value) {
                val index = state.tiles.indexOf(value)
                val row = index / size
                val col = index % size
                val animatedX by animateDpAsState(
                    targetValue = step * col,
                    animationSpec = tween(SLIDE_MS),
                    label = "tileX"
                )
                val animatedY by animateDpAsState(
                    targetValue = step * row,
                    animationSpec = tween(SLIDE_MS),
                    label = "tileY"
                )
                Tile(
                    value = value,
                    // A tile is "home" when its value matches its solved slot (index + 1).
                    isCorrect = index == value - 1,
                    onClick = { onTileTap(index) },
                    modifier = Modifier
                        .size(cellSize)
                        .offset(x = animatedX, y = animatedY)
                )
            }
        }
    }
}

@Composable
private fun Tile(
    value: Int,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Darker blue when the tile is in its correct position, lighter blue otherwise.
    val tileColor = if (isCorrect) Color(0xFF1E4FD0) else Color(0xFF6B97FF)
    // Keep the latest onClick so the touch-down gesture never fires with stale game state.
    val currentOnClick by rememberUpdatedState(onClick)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tileColor)
            // Fire on touch-down (first pointer press) rather than on release.
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    currentOnClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
