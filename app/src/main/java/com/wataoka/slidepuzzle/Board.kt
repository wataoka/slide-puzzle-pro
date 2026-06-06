package com.wataoka.slidepuzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders the puzzle grid. Tiles are laid out row by row; the blank slot is
 * drawn as an empty recess.
 */
@Composable
fun Board(
    state: PuzzleState,
    onTileTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gap = 6.dp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD9DEE8))
            .padding(gap),
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        for (row in 0 until state.size) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap)
            ) {
                for (col in 0 until state.size) {
                    val index = row * state.size + col
                    val value = state.tiles[index]
                    Tile(
                        value = value,
                        // A tile is "home" when its value matches its solved slot (index + 1).
                        isCorrect = value == index + 1,
                        onClick = { onTileTap(index) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
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
    if (value == 0) {
        Box(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFC7CEDB)))
        return
    }
    // Darker blue when the tile is in its correct position, lighter blue otherwise.
    val tileColor = if (isCorrect) Color(0xFF1E4FD0) else Color(0xFF6B97FF)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tileColor)
            .clickable { onClick() },
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
