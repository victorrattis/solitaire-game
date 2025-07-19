package com.vhra.game.solitaire.gl.utils

enum class Suit {
    CLUBS,     //(♣)
    DIAMONDS, // (♦)
    HEARTS,   // (♥)
    SPADES    // (♠)
}

enum class Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
    JACK, QUEEN, KING
}

private data class Coord(val row: Int, val column: Int)

object CardTextureMapper {
    private val map: Map<Suit, Map<Rank, Coord>> = mapOf(
        Suit.CLUBS to (mapOf(
            Rank.ACE to Coord(0, 3),
            Rank.TWO to Coord(1, 4),
            Rank.THREE to Coord(2, 5),
            Rank.FOUR to Coord(3, 6),
            Rank.FIVE to Coord(0, 2),
            Rank.SIX to Coord(1, 3),
            Rank.SEVEN to Coord(2, 4),
            Rank.EIGHT to Coord(3, 5),
            Rank.NINE to Coord(4, 6),
            Rank.TEN to Coord(0, 1),
            Rank.JACK to Coord(1, 2),
            Rank.QUEEN to Coord(2, 3),
            Rank.KING to Coord(3, 4)
        )),
        Suit.DIAMONDS to (mapOf(
            Rank.ACE to Coord(4, 5),
            Rank.TWO to Coord(5, 6),
            Rank.THREE to Coord(0, 0),
            Rank.FOUR to Coord(1, 1),
            Rank.FIVE to Coord(2, 2),
            Rank.SIX to Coord(3, 3),
            Rank.SEVEN to Coord(4, 4),
            Rank.EIGHT to Coord(5, 5),
            Rank.NINE to Coord(6, 6),
            Rank.TEN to Coord(1, 0),
            Rank.JACK to Coord(2, 1),
            Rank.QUEEN to Coord(3, 2),
            Rank.KING to Coord(4, 3)
        )),
        Suit.HEARTS to (mapOf(
            Rank.ACE to Coord(5, 4),
            Rank.TWO to Coord(6, 5),
            Rank.THREE to Coord(7, 6),
            Rank.FOUR to Coord(2, 0),
            Rank.FIVE to Coord(3, 1),
            Rank.SIX to Coord(4, 2),
            Rank.SEVEN to Coord(5, 3),
            Rank.EIGHT to Coord(6, 4),
            Rank.NINE to Coord(7, 5),
            Rank.TEN to Coord(8, 6),
            Rank.JACK to Coord(3, 0),
            Rank.QUEEN to Coord(4, 1),
            Rank.KING to Coord(5, 2)
        )),
        Suit.SPADES to (mapOf(
            Rank.ACE to Coord(6, 3),
            Rank.TWO to Coord(6, 4),
            Rank.THREE to Coord(8, 5),
            Rank.FOUR to Coord(9, 6),
            Rank.FIVE to Coord(4, 0),
            Rank.SIX to Coord(5, 1),
            Rank.SEVEN to Coord(6, 2),
            Rank.EIGHT to Coord(7, 3),
            Rank.NINE to Coord(8, 4),
            Rank.TEN to Coord(9, 5),
            Rank.JACK to Coord(5, 0),
            Rank.QUEEN to Coord(5, 1),
            Rank.KING to Coord(5, 2)
        ))
    )
    private val calculator = CardSpriteSheetCalculator(192, 269, 2015, 1948)

    fun getBackCardTextureCoordinate(): FloatArray = calculator.getCardCoordinate(6, 0)

    fun getTextureCoordinate(cardRank: Rank, cardSuit: Suit): FloatArray {
        val coord: Coord? = map[cardSuit]?.get(cardRank)
        return coord?.let { calculator.getCardCoordinate(coord.row, coord.column) } ?: floatArrayOf()
    }
}


class CardSpriteSheetCalculator (
    private val cardWidth: Int,
    private val cardHeight: Int,
    private val spriteSheetWidth: Int,
    private val spriteSheetHeight: Int
) {
    fun getCardCoordinate(row: Int, column: Int): FloatArray {
        val x = 2 + (row * (10 + cardWidth))
        val y = 3 + (column * (10 + cardHeight))
        return floatArrayOf(
            x / spriteSheetWidth.toFloat(),
            y / spriteSheetHeight.toFloat(),
            (x + cardWidth) / spriteSheetWidth.toFloat(),
            (y + cardHeight) / spriteSheetHeight.toFloat()
        )
    }
}
