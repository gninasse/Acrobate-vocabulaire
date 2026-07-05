package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.VocabularyRepository
import com.example.data.VocabularyWord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

sealed interface Screen {
    object Dashboard : Screen
    data class Learn(val lotId: Int) : Screen
    data class GameSelection(val lotId: Int) : Screen
    data class GamePioche(val lotId: Int) : Screen
    data class GameBataille(val lotId: Int) : Screen
    data class GameMotMystere(val lotId: Int) : Screen
    object GlobalList : Screen
}

data class BatailleItem(
    val id: String, // "F_wordId" or "M_wordId"
    val wordId: Int,
    val text: String,
    val isFrench: Boolean,
    var isSelected: Boolean = false,
    var isMatched: Boolean = false
)

class VocabularyViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: VocabularyRepository
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    // Exposed lists from Room
    val allWords: StateFlow<List<VocabularyWord>>

    // Navigation and main state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // --- Active Study State (Learn Screen) ---
    private val _learnWords = MutableStateFlow<List<VocabularyWord>>(emptyList())
    val learnWords: StateFlow<List<VocabularyWord>> = _learnWords.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    // --- Game: Pioche Rapide ---
    private val _piocheWords = MutableStateFlow<List<VocabularyWord>>(emptyList())
    val piocheWords: StateFlow<List<VocabularyWord>> = _piocheWords.asStateFlow()

    private val _piocheIndex = MutableStateFlow(0)
    val piocheIndex: StateFlow<Int> = _piocheIndex.asStateFlow()

    private val _piocheFlipped = MutableStateFlow(false)
    val piocheFlipped: StateFlow<Boolean> = _piocheFlipped.asStateFlow()

    private val _piocheTimeLeft = MutableStateFlow(30)
    val piocheTimeLeft: StateFlow<Int> = _piocheTimeLeft.asStateFlow()

    private val _piocheScore = MutableStateFlow(0)
    val piocheScore: StateFlow<Int> = _piocheScore.asStateFlow()

    private val _piocheIsOver = MutableStateFlow(false)
    val piocheIsOver: StateFlow<Boolean> = _piocheIsOver.asStateFlow()

    private var piocheTimerJob: Job? = null

    // --- Game: Bataille des Mots ---
    private val _batailleItems = MutableStateFlow<List<BatailleItem>>(emptyList())
    val batailleItems: StateFlow<List<BatailleItem>> = _batailleItems.asStateFlow()

    private val _batailleSelectedId = MutableStateFlow<String?>(null)
    val batailleSelectedId: StateFlow<String?> = _batailleSelectedId.asStateFlow()

    private val _batailleErrors = MutableStateFlow(0)
    val batailleErrors: StateFlow<Int> = _batailleErrors.asStateFlow()

    private val _batailleIsOver = MutableStateFlow(false)
    val batailleIsOver: StateFlow<Boolean> = _batailleIsOver.asStateFlow()

    private val _batailleTimeElapsed = MutableStateFlow(0)
    val batailleTimeElapsed: StateFlow<Int> = _batailleTimeElapsed.asStateFlow()
    private var batailleTimerJob: Job? = null

    // --- Game: Mot Mystère ---
    private val _mystereWords = MutableStateFlow<List<VocabularyWord>>(emptyList())
    val mystereWords: StateFlow<List<VocabularyWord>> = _mystereWords.asStateFlow()

    private val _mystereIndex = MutableStateFlow(0)
    val mystereIndex: StateFlow<Int> = _mystereIndex.asStateFlow()

    private val _mystereTypedAnswer = MutableStateFlow("")
    val mystereTypedAnswer: StateFlow<String> = _mystereTypedAnswer.asStateFlow()

    private val _mystereShowCorrection = MutableStateFlow(false)
    val mystereShowCorrection: StateFlow<Boolean> = _mystereShowCorrection.asStateFlow()

    private val _mystereIsCorrect = MutableStateFlow<Boolean?>(null)
    val mystereIsCorrect: StateFlow<Boolean?> = _mystereIsCorrect.asStateFlow()

    private val _mystereScore = MutableStateFlow(0)
    val mystereScore: StateFlow<Int> = _mystereScore.asStateFlow()

    private val _mystereIsOver = MutableStateFlow(false)
    val mystereIsOver: StateFlow<Boolean> = _mystereIsOver.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VocabularyRepository(db.vocabularyDao())

        // Initial populate if table is empty
        viewModelScope.launch {
            repository.populateIfEmpty()
        }

        allWords = repository.allWords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize TTS for French pronunciation
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("VocabularyViewModel", "Failed to construct TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("VocabularyViewModel", "French language is not supported or missing data")
            } else {
                isTtsInitialized = true
                Log.d("VocabularyViewModel", "TextToSpeech initialized in French successfully.")
            }
        } else {
            Log.e("VocabularyViewModel", "TextToSpeech initialization failed.")
        }
    }

    fun speakFrench(text: String) {
        if (isTtsInitialized) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (e: Exception) {
                Log.e("VocabularyViewModel", "Error in speaking text", e)
            }
        } else {
            Log.w("VocabularyViewModel", "TTS not initialized, can't speak: $text")
        }
    }

    // --- Navigation Functions ---
    fun navigateTo(screen: Screen) {
        // Cancel active timers when switching screens
        stopPiocheTimer()
        stopBatailleTimer()

        _currentScreen.value = screen

        // Reset sub-states based on destination
        when (screen) {
            is Screen.Learn -> {
                _currentWordIndex.value = 0
                _isCardFlipped.value = false
                viewModelScope.launch {
                    allWords.collect { list ->
                        val lotWords = list.filter { it.lotId == screen.lotId }
                        // Leitner review prioritization: Words that are ready or new
                        _learnWords.value = lotWords.sortedWith(
                            compareByDescending<VocabularyWord> { it.isReadyForReview }
                                .thenBy { it.leitnerBox }
                        )
                    }
                }
            }
            is Screen.GamePioche -> {
                initPiocheGame(screen.lotId)
            }
            is Screen.GameBataille -> {
                initBatailleGame(screen.lotId)
            }
            is Screen.GameMotMystere -> {
                initMotMystereGame(screen.lotId)
            }
            else -> {}
        }
    }

    // --- Active Study Action Handles ---
    fun flipLearnCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun recordLearnReview(wasSuccessful: Boolean) {
        val words = _learnWords.value
        val index = _currentWordIndex.value
        if (words.isNotEmpty() && index < words.size) {
            val currentWord = words[index]
            viewModelScope.launch {
                repository.recordReview(currentWord, wasSuccessful)
                
                // Pronounce word if user succeeds to anchor audio memory
                if (wasSuccessful) {
                    speakFrench(currentWord.french)
                }

                // Advance card index
                if (index + 1 < words.size) {
                    _isCardFlipped.value = false
                    delay(200) // smooth card slide transition
                    _currentWordIndex.value = index + 1
                } else {
                    // Finished reviewing the 10 words! Back to dashboard.
                    _currentScreen.value = Screen.Dashboard
                }
            }
        }
    }

    // --- Pioche Rapide Game Engine ---
    private fun initPiocheGame(lotId: Int) {
        val lotWords = allWords.value.filter { it.lotId == lotId }.shuffled()
        _piocheWords.value = lotWords
        _piocheIndex.value = 0
        _piocheFlipped.value = false
        _piocheScore.value = 0
        _piocheTimeLeft.value = 30
        _piocheIsOver.value = false
        startPiocheTimer()
    }

    private fun startPiocheTimer() {
        piocheTimerJob?.cancel()
        piocheTimerJob = viewModelScope.launch {
            while (_piocheTimeLeft.value > 0 && !_piocheIsOver.value) {
                delay(1000)
                _piocheTimeLeft.value -= 1
            }
            _piocheIsOver.value = true
        }
    }

    private fun stopPiocheTimer() {
        piocheTimerJob?.cancel()
        piocheTimerJob = null
    }

    fun flipPiocheCard() {
        _piocheFlipped.value = !_piocheFlipped.value
        if (_piocheFlipped.value) {
            // Speak when revealed
            val words = _piocheWords.value
            val index = _piocheIndex.value
            if (words.isNotEmpty() && index < words.size) {
                speakFrench(words[index].french)
            }
        }
    }

    fun piocheRecordResult(correct: Boolean) {
        if (correct) {
            _piocheScore.value += 10
        }
        val index = _piocheIndex.value
        val words = _piocheWords.value
        if (index + 1 < words.size) {
            _piocheFlipped.value = false
            _piocheIndex.value = index + 1
        } else {
            // Finished all words early!
            _piocheIsOver.value = true
            stopPiocheTimer()
        }
    }

    // --- Bataille des Mots Game Engine ---
    private fun initBatailleGame(lotId: Int) {
        val rawWords = allWords.value.filter { it.lotId == lotId }.shuffled()
        // Take a sub-selection of 5 words to build a 10-card battle grid (5 French + 5 Translations)
        val selectedWords = rawWords.take(5)

        val items = mutableListOf<BatailleItem>()
        selectedWords.forEach { word ->
            items.add(BatailleItem(id = "F_${word.id}", wordId = word.id, text = word.french, isFrench = true))
            items.add(BatailleItem(id = "M_${word.id}", wordId = word.id, text = word.meaning, isFrench = false))
        }
        _batailleItems.value = items.shuffled()
        _batailleSelectedId.value = null
        _batailleErrors.value = 0
        _batailleIsOver.value = false
        _batailleTimeElapsed.value = 0
        startBatailleTimer()
    }

    private fun startBatailleTimer() {
        batailleTimerJob?.cancel()
        batailleTimerJob = viewModelScope.launch {
            while (!_batailleIsOver.value) {
                delay(1000)
                _batailleTimeElapsed.value += 1
            }
        }
    }

    private fun stopBatailleTimer() {
        batailleTimerJob?.cancel()
        batailleTimerJob = null
    }

    fun selectBatailleItem(id: String) {
        val items = _batailleItems.value.map { it.copy() }
        val clickedItem = items.find { it.id == id } ?: return
        if (clickedItem.isMatched) return // Already solved

        val previouslySelectedId = _batailleSelectedId.value

        if (previouslySelectedId == null) {
            // First card of pair selected
            _batailleSelectedId.value = id
            _batailleItems.value = items.map {
                if (it.id == id) it.copy(isSelected = true) else it.copy(isSelected = false)
            }
        } else if (previouslySelectedId == id) {
            // Clicked same card again, deselect it
            _batailleSelectedId.value = null
            _batailleItems.value = items.map { it.copy(isSelected = false) }
        } else {
            // Second card selected, check match
            val previousItem = items.find { it.id == previouslySelectedId } ?: return

            if (previousItem.wordId == clickedItem.wordId && previousItem.isFrench != clickedItem.isFrench) {
                // IT'S A MATCH!
                val updatedItems = items.map {
                    if (it.wordId == clickedItem.wordId) {
                        it.copy(isMatched = true, isSelected = false)
                    } else {
                        it.copy(isSelected = it.id == previousItem.id || it.id == clickedItem.id) // keep selected briefly or reset
                    }
                }
                
                // Speak the matched word
                val matchWord = allWords.value.find { it.id == clickedItem.wordId }
                if (matchWord != null) {
                    speakFrench(matchWord.french)
                }

                _batailleItems.value = updatedItems
                _batailleSelectedId.value = null

                // Check if all matched
                val remaining = updatedItems.filter { !it.isMatched }
                if (remaining.isEmpty()) {
                    _batailleIsOver.value = true
                    stopBatailleTimer()
                }
            } else {
                // MISMATCH
                _batailleErrors.value += 1
                // Highlight red briefly, then reset selection
                _batailleItems.value = items.map {
                    if (it.id == id || it.id == previouslySelectedId) {
                        it.copy(isSelected = true) // visually show mismatch briefly
                    } else {
                        it.copy()
                    }
                }
                viewModelScope.launch {
                    delay(800) // keep visible briefly
                    _batailleSelectedId.value = null
                    _batailleItems.value = _batailleItems.value.map { it.copy(isSelected = false) }
                }
            }
        }
    }

    // --- Mot Mystère Game Engine ---
    private fun initMotMystereGame(lotId: Int) {
        val lotWords = allWords.value.filter { it.lotId == lotId }.shuffled()
        _mystereWords.value = lotWords
        _mystereIndex.value = 0
        _mystereTypedAnswer.value = ""
        _mystereShowCorrection.value = false
        _mystereIsCorrect.value = null
        _mystereScore.value = 0
        _mystereIsOver.value = false
    }

    fun updateMystereAnswer(text: String) {
        _mystereTypedAnswer.value = text
    }

    fun submitMystereAnswer() {
        val words = _mystereWords.value
        val index = _mystereIndex.value
        if (words.isNotEmpty() && index < words.size) {
            val currentWord = words[index]
            val answer = _mystereTypedAnswer.value.trim().lowercase()
            val correctAnswer = currentWord.french.trim().lowercase()

            val correct = answer == correctAnswer
            _mystereIsCorrect.value = correct
            _mystereShowCorrection.value = true

            if (correct) {
                _mystereScore.value += 10
                speakFrench(currentWord.french)
            } else {
                // If wrong, speak the correct answer anyway to help correct memory
                speakFrench(currentWord.french)
            }
        }
    }

    fun nextMystereWord() {
        val index = _mystereIndex.value
        val words = _mystereWords.value
        if (index + 1 < words.size) {
            _mystereIndex.value = index + 1
            _mystereTypedAnswer.value = ""
            _mystereShowCorrection.value = false
            _mystereIsCorrect.value = null
        } else {
            _mystereIsOver.value = true
        }
    }

    // --- Reset Utility Actions ---
    fun resetLotProgress(lotId: Int) {
        viewModelScope.launch {
            repository.resetLotProgress(lotId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("VocabularyViewModel", "Error shutting down TTS", e)
        }
        stopPiocheTimer()
        stopBatailleTimer()
    }
}
