package com.wataoka.slidepuzzle

/**
 * Immutable representation of a sliding puzzle board.
 *
 * The board is a list of [size]*[size] integers. The value 0 represents the
 * empty (blank) slot. The solved state is 1, 2, ..., n-1, 0.
 */
data class PuzzleState(
    val tiles: List<Int>,
    val size: Int = 4
) {
    val blankIndex: Int get() = tiles.indexOf(0)

    /** True when every tile is in its solved position. */
    val isSolved: Boolean
        get() {
            for (i in 0 until tiles.size - 1) {
                if (tiles[i] != i + 1) return false
            }
            return tiles.last() == 0
        }

    /**
     * Returns a new state with the tile at [index] moved into the blank slot,
     * or the same state if that tile is not orthogonally adjacent to the blank.
     */
    fun move(index: Int): PuzzleState {
        if (!canMove(index)) return this
        val newTiles = tiles.toMutableList()
        val blank = blankIndex
        newTiles[blank] = newTiles[index]
        newTiles[index] = 0
        return copy(tiles = newTiles)
    }

    fun canMove(index: Int): Boolean {
        val blank = blankIndex
        val r1 = index / size; val c1 = index % size
        val r2 = blank / size; val c2 = blank % size
        return (r1 == r2 && kotlin.math.abs(c1 - c2) == 1) ||
               (c1 == c2 && kotlin.math.abs(r1 - r2) == 1)
    }

    companion object {
        fun solved(size: Int = 4): PuzzleState {
            val tiles = (1 until size * size).toMutableList().apply { add(0) }
            return PuzzleState(tiles, size)
        }

        /**
         * Produces a random, guaranteed-solvable, non-trivial shuffle by
         * applying many random legal moves from the solved state. This avoids
         * the parity headaches of shuffling tiles directly.
         */
        fun shuffled(size: Int = 4): PuzzleState {
            var state = solved(size)
            var prevBlank = -1
            val moves = size * size * 60
            repeat(moves) {
                val neighbors = state.neighborsOfBlank().filter { it != prevBlank }
                val pick = neighbors.random()
                prevBlank = state.blankIndex
                state = state.move(pick)
            }
            // Extremely unlikely, but never hand back a solved board.
            return if (state.isSolved) shuffled(size) else state
        }
    }

    private fun neighborsOfBlank(): List<Int> {
        val blank = blankIndex
        val r = blank / size; val c = blank % size
        val result = mutableListOf<Int>()
        if (r > 0) result.add(blank - size)
        if (r < size - 1) result.add(blank + size)
        if (c > 0) result.add(blank - 1)
        if (c < size - 1) result.add(blank + 1)
        return result
    }
}
