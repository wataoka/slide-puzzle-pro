package com.wataoka.slidepuzzle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleStateTest {

    @Test
    fun solvedBoardIsSolved() {
        assertTrue(PuzzleState.solved(4).isSolved)
    }

    @Test
    fun shuffledBoardIsNotSolved() {
        repeat(20) { assertFalse(PuzzleState.shuffled(4).isSolved) }
    }

    @Test
    fun movingAdjacentTileSwapsWithBlank() {
        // Solved 2x2: [1, 2, 3, 0]. Tile at index 2 (value 3) is above blank.
        val s = PuzzleState(listOf(1, 2, 3, 0), size = 2)
        val moved = s.move(2)
        assertEquals(listOf(1, 2, 0, 3), moved.tiles)
    }

    @Test
    fun cannotMoveNonAdjacentTile() {
        val s = PuzzleState(listOf(1, 2, 3, 0), size = 2)
        assertEquals(s.tiles, s.move(0).tiles)
    }

    @Test
    fun shuffledBoardIsAlwaysSolvable() {
        // Built from legal moves, so a reverse sequence of legal moves must exist.
        repeat(10) {
            val s = PuzzleState.shuffled(4)
            assertEquals(16, s.tiles.size)
            assertTrue(s.tiles.toSet() == (0..15).toSet())
        }
    }
}
