package com.api.dao.quotes

import com.api.models.*

// Creates an interface to abstract the necessary operations for updating quotes.
interface QuotesDaoFacade{
    suspend fun randomQuote():Quote?
    suspend fun quotesByCharacter(character: String): List<Quote>
    suspend fun quotesByShow(show: String, season: Int?): List<Quote>
    suspend fun addQuote(show: String, season: Int, episode: String, character: String, quote: String):Quote?
    suspend fun removeQuote(id: Int): Boolean
    suspend fun editQuote(id: Int, show: String, season: Int, episode: String, character: String, quote: String): Boolean
}