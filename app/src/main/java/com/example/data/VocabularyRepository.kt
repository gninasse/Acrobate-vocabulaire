package com.example.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {

    val allWords: Flow<List<VocabularyWord>> = vocabularyDao.getAllWords()

    fun getWordsByLot(lotId: Int): Flow<List<VocabularyWord>> {
        return vocabularyDao.getWordsByLot(lotId)
    }

    fun getReadyReviews(currentTime: Long): Flow<List<VocabularyWord>> {
        return vocabularyDao.getWordsReadyForReview(currentTime)
    }

    suspend fun populateIfEmpty() {
        try {
            val count = vocabularyDao.getWordCount()
            if (count == 0) {
                val initialList = InitialVocabulary.getWords()
                vocabularyDao.insertWords(initialList)
                Log.d("VocabularyRepository", "Prepopulated database with ${initialList.size} words.")
            } else {
                Log.d("VocabularyRepository", "Database already has $count words.")
            }
        } catch (e: Exception) {
            Log.e("VocabularyRepository", "Error populating database", e)
        }
    }

    suspend fun updateWord(word: VocabularyWord) {
        vocabularyDao.updateWord(word)
    }

    /**
     * Spaced Repetition (Leitner Box) Scheduling Logic
     * @param word The word that was reviewed
     * @param wasSuccessful True if the child correctly recalled the word
     */
    suspend fun recordReview(word: VocabularyWord, wasSuccessful: Boolean) {
        val currentTime = System.currentTimeMillis()
        val nextBox: Int
        val nextStatus: String
        val nextConsecutiveSuccesses: Int

        if (wasSuccessful) {
            nextConsecutiveSuccesses = word.successfulRecalls + 1
            // Advance Leitner Box (Max 5)
            nextBox = (word.leitnerBox + 1).coerceAtMost(5)
            
            // Mark as MASTERED if in high boxes (e.g., Box >= 4) or consecutive successes >= 2
            nextStatus = if (nextBox >= 4 || nextConsecutiveSuccesses >= 2) {
                "MASTERED"
            } else {
                "LEARNING"
            }
        } else {
            // "L'erreur fait partie du système : un mot mal rappelé ne descend pas d'un niveau brutalement"
            // Keep in current box or at least box 1 if it was box 1, but reset consecutive successes
            nextConsecutiveSuccesses = 0
            nextBox = (word.leitnerBox - 1).coerceAtAtLeast(1) // Doesn't drop brutally, just goes down 1 box max
            nextStatus = "LEARNING"
        }

        // Intervals based on Leitner Box:
        // Box 1: 1 day
        // Box 2: 3 days
        // Box 3: 7 days
        // Box 4: 14 days
        // Box 5: 30 days
        val intervalMs = when (nextBox) {
            1 -> 24L * 60 * 60 * 1000 // 1 Day
            2 -> 3L * 24 * 60 * 60 * 1000 // 3 Days
            3 -> 7L * 24 * 60 * 60 * 1000 // 7 Days
            4 -> 14L * 24 * 60 * 60 * 1000 // 14 Days
            5 -> 30L * 24 * 60 * 60 * 1000 // 30 Days
            else -> 24L * 60 * 60 * 1000
        }

        val updatedWord = word.copy(
            status = nextStatus,
            leitnerBox = nextBox,
            lastReviewedTime = currentTime,
            nextReviewTime = if (wasSuccessful) currentTime + intervalMs else 0L, // 0 means review again today
            successfulRecalls = nextConsecutiveSuccesses,
            totalAttempts = word.totalAttempts + 1
        )

        vocabularyDao.updateWord(updatedWord)
        Log.d("VocabularyRepository", "Updated word: ${word.french}, success=$wasSuccessful, nextBox=$nextBox, status=$nextStatus")
    }

    /**
     * Resets progress for a specific word
     */
    suspend fun resetWordProgress(word: VocabularyWord) {
        val resetWord = word.copy(
            status = "NEW",
            leitnerBox = 1,
            lastReviewedTime = 0L,
            nextReviewTime = 0L,
            successfulRecalls = 0,
            totalAttempts = 0
        )
        vocabularyDao.updateWord(resetWord)
    }

    /**
     * Resets progress for a whole lot of 10 words
     */
    suspend fun resetLotProgress(lotId: Int) {
        val words = vocabularyDao.getWordsByLot(lotId).first()
        for (word in words) {
            resetWordProgress(word)
        }
    }
}

// Extension to handle min value for coerceAtLeast (standard Kotlin function is coerceAtLeast)
fun Int.coerceAtAtLeast(minimumValue: Int): Int {
    return if (this < minimumValue) minimumValue else this
}
