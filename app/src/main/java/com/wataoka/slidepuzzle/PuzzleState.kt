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
     * Returns a new state for a tap on [index]. If [index] shares the blank's
     * row or column, every tile between them slides one step toward the blank
     * and the blank ends up where the tapped tile was. Returns the same state
     * if the tap is not on the blank's row or column.
     */
    fun move(index: Int): PuzzleState {
        if (!canMove(index)) return this
        val newTiles = tiles.toMutableList()
        var blank = blankIndex
        val r1 = index / size; val c1 = index % size
        val r2 = blank / size; val c2 = blank % size
        // Unit step (in flat-index space) from the blank toward the tapped tile.
        val stepIndex = when {
            r1 == r2 -> if (c1 > c2) 1 else -1        // same row: step right / left
            else -> if (r1 > r2) size else -size      // same column: step down / up
        }
        // Walk the blank one cell at a time toward [index], shifting tiles over.
        while (blank != index) {
            val next = blank + stepIndex
            newTiles[blank] = newTiles[next]
            newTiles[next] = 0
            blank = next
        }
        return copy(tiles = newTiles)
    }

    /** A tap is legal when the tile shares the blank's row or column (and isn't the blank). */
    fun canMove(index: Int): Boolean {
        if (index == blankIndex) return false
        val blank = blankIndex
        val r1 = index / size; val c1 = index % size
        val r2 = blank / size; val c2 = blank % size
        return r1 == r2 || c1 == c2
    }

    /**
     * Number of tiles a tap on [index] would slide — i.e. the cell distance to
     * the blank along their shared row or column. 0 if the tap is illegal. Used
     * for the move counter: a multi-tile slide counts as one move per tile slid.
     */
    fun moveDistance(index: Int): Int {
        if (!canMove(index)) return 0
        val blank = blankIndex
        val r1 = index / size; val c1 = index % size
        val r2 = blank / size; val c2 = blank % size
        return if (r1 == r2) kotlin.math.abs(c1 - c2) else kotlin.math.abs(r1 - r2)
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
