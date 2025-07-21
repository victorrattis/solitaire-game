package com.vhra.game.solitaire

import android.os.Build
import androidx.annotation.RequiresApi
import com.vhra.game.solitaire.gl.CardModel
import kotlin.random.Random

class SolitaireGame {
    var goals: List<CardDetail> = listOf()
    var preview: List<CardDetail> = listOf()
    var pile: List<CardId> = listOf()
    var columns: List<MutableList<CardDetail>> = listOf()

    fun startGame() {
        val deck = generateShuffledDeck()

        val columns: MutableList<MutableList<CardDetail>> = mutableListOf()
        for (i in 0 until 7) {
            val column = mutableListOf<CardDetail>()
            val cardsToDeal = i + 1
            repeat(cardsToDeal) { index ->
                if (deck.isNotEmpty()) {
                    column.add(CardDetail(deck.removeAt(0), index + 1 == cardsToDeal))
                }
            }
            columns.add(column)
        }
        this.columns = columns.toList()

        pile = deck.toList()
    }

    fun getLastCardEachColumn(): Set<CardId> {
        val foundCards = mutableListOf<CardId>()
        columns.forEach { cards ->
            if (cards.isNotEmpty()) {
                foundCards.add(cards.last().id)
            }
        }
        return foundCards.toSet()
    }

    fun isCardDraggable(cardId: CardId): Boolean {
       columns.forEachIndexed { column, cards ->
           if(cards.isNotEmpty() && cards.last().id == cardId) return true
//            cards.filterIndexed { row, card ->
//                card.id == cardId && row == column
//            }.isNotEmpty()
        }

        return false
    }

    private fun generateShuffledDeck(): MutableList<CardId> {
        val random: Random = Random.Default
        val deck = mutableListOf<CardId>()
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                deck.add(CardId(suit, rank))
            }
        }
        deck.shuffle(random)
        return deck
    }

    fun getColumn(card: CardId): Int? {
        columns.forEachIndexed { column, cards ->
            if (cards.isNotEmpty() && cards.last().id == card) {
                return column
            }
        }
        return null
    }

    fun switchColumn(cardId: CardId, nextColumn: Int) {
        columns.forEachIndexed { column, cards ->
            if (cards.isNotEmpty() && cards.last().id == cardId) {
                columns[nextColumn].add(cards.removeAt(cards.size - 1))
            }
        }
    }

    fun getCardColumn(previousColumn: Int): CardDetail? {
        return columns[previousColumn].let {
            if (it.isNotEmpty()) it.last() else null
        }
    }

    fun isCardMatched(base: CardId, top: CardId): Boolean {
        if (top.rank.id + 1 == base.rank.id) {
            return areSuitsOpposite(base.suit, top.suit)
        }
        return false
    }

    private fun areSuitsOpposite(suit1: Suit, suit2: Suit): Boolean = suit1 != suit2 &&
            ((suit1 == Suit.DIAMONDS || suit1 == Suit.HEARTS)
                    != (suit2 == Suit.DIAMONDS || suit2 == Suit.HEARTS))


}

data class CardId(
    val suit: Suit,
    val rank: Rank
)

data class CardDetail(
    val id: CardId,
    var isFaceUp: Boolean
)

enum class Suit(val id: Int) {
    CLUBS(0),    // (♣)
    DIAMONDS(1), // (♦)
    HEARTS(2),   // (♥)
    SPADES(3)    // (♠)
}

enum class Rank(val id: Int) {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13)
}