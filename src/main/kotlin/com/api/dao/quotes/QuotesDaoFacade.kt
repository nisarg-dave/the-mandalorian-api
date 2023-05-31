package com.api.dao.quotes

import com.api.models.*

interface QuotesDaoFacade{
    suspend fun randomQuote():Quote?
    suspend fun addQuote(quote: String, season: Int, chapter: String, character: String):Quote?
    suspend fun quotesByCharacter(character: String): List<Quote>
    suspend fun quotesFromSeason(season: Int): List<Quote>
}