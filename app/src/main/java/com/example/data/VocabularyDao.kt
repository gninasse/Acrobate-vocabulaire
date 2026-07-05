package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_words ORDER BY lotId ASC, id ASC")
    fun getAllWords(): Flow<List<VocabularyWord>>

    @Query("SELECT * FROM vocabulary_words WHERE lotId = :lotId ORDER BY id ASC")
    fun getWordsByLot(lotId: Int): Flow<List<VocabularyWord>>

    @Query("SELECT * FROM vocabulary_words WHERE id = :wordId LIMIT 1")
    suspend fun getWordById(wordId: Int): VocabularyWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<VocabularyWord>)

    @Update
    suspend fun updateWord(word: VocabularyWord)

    @Query("SELECT COUNT(*) FROM vocabulary_words")
    suspend fun getWordCount(): Int

    @Query("SELECT * FROM vocabulary_words WHERE nextReviewTime <= :currentTime OR status = 'NEW'")
    fun getWordsReadyForReview(currentTime: Long): Flow<List<VocabularyWord>>
}
