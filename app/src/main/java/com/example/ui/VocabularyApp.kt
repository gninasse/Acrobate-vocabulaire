package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.VocabularyWord
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyApp(viewModel: VocabularyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎈 Acrobate Vocab",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (currentScreen != Screen.Dashboard) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Dashboard) },
                            modifier = Modifier.testTag("back_to_home_button")
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Accueil")
                        }
                    }
                    IconButton(
                        onClick = {
                            if (currentScreen == Screen.GlobalList) {
                                viewModel.navigateTo(Screen.Dashboard)
                            } else {
                                viewModel.navigateTo(Screen.GlobalList)
                            }
                        },
                        modifier = Modifier.testTag("dictionary_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (currentScreen == Screen.GlobalList) Icons.Default.GridOn else Icons.Default.Book,
                            contentDescription = "Dictionnaire"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Dashboard -> DashboardScreen(viewModel = viewModel, words = allWords)
                is Screen.Learn -> LearnScreen(viewModel = viewModel, lotId = screen.lotId)
                is Screen.GameSelection -> GameSelectionScreen(viewModel = viewModel, lotId = screen.lotId)
                is Screen.GamePioche -> GamePiocheScreen(viewModel = viewModel, lotId = screen.lotId)
                is Screen.GameBataille -> GameBatailleScreen(viewModel = viewModel, lotId = screen.lotId)
                is Screen.GameMotMystere -> GameMotMystereScreen(viewModel = viewModel, lotId = screen.lotId)
                is Screen.GlobalList -> GlobalListScreen(viewModel = viewModel, words = allWords)
            }
        }
    }
}

// --- UTILITY FOR LOT METADATA ---
fun getLotTitle(lotId: Int): String {
    return when (lotId) {
        1 -> "Les salutations et besoins"
        2 -> "La famille et sentiments"
        3 -> "Les actions quotidiennes"
        4 -> "Animaux et nature"
        5 -> "Couleurs et nombres"
        6 -> "Mon école et mes objets"
        7 -> "La délicieuse nourriture"
        8 -> "Mon corps et mes habits"
        9 -> "Le temps et les lieux"
        10 -> "Petites descriptions"
        else -> "Vocabulaire essentiel"
    }
}

fun getLotIcon(lotId: Int): String {
    return when (lotId) {
        1 -> "👋"
        2 -> "❤️"
        3 -> "🏃"
        4 -> "🐱"
        5 -> "🎨"
        6 -> "🎒"
        7 -> "🍎"
        8 -> "👕"
        9 -> "🏡"
        10 -> "🌟"
        else -> "📝"
    }
}

fun isLotUnlocked(lotId: Int, words: List<VocabularyWord>): Boolean {
    if (lotId == 1) return true
    val prevLotWords = words.filter { it.lotId == lotId - 1 }
    if (prevLotWords.isEmpty()) return false
    val mastered = prevLotWords.count { it.status == "MASTERED" }
    return mastered >= 8 // Unlock N if N-1 has >= 8 mastered words
}

// --- SCREEN: DASHBOARD ---
@Composable
fun DashboardScreen(viewModel: VocabularyViewModel, words: List<VocabularyWord>) {
    val totalMastered = words.count { it.status == "MASTERED" }
    val totalLearning = words.count { it.status == "LEARNING" }
    val progressPercent = if (words.isEmpty()) 0 else (totalMastered * 100) / words.size

    var lotToReset by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Overall Mascot / Progress Banner
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🦊",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bonjour l'artiste !",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tu as maîtrisé $totalMastered mots sur 100. Super !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Progress bar
                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = MintGreen,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$progressPercent% Réussi",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Mes Lots d'Apprentissage",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // List of 10 Lots
        items(10) { index ->
            val lotId = index + 1
            val lotWords = words.filter { it.lotId == lotId }
            val unlocked = isLotUnlocked(lotId, words)
            val masteredCount = lotWords.count { it.status == "MASTERED" }
            val learningCount = lotWords.count { it.status == "LEARNING" }
            
            // Check scheduled cards
            val readyCount = lotWords.count { it.isReadyForReview }

            LotItemCard(
                lotId = lotId,
                title = getLotTitle(lotId),
                emoji = getLotIcon(lotId),
                unlocked = unlocked,
                masteredCount = masteredCount,
                learningCount = learningCount,
                readyCount = readyCount,
                onLearnClick = { viewModel.navigateTo(Screen.Learn(lotId)) },
                onPlayClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) },
                onResetClick = { lotToReset = lotId }
            )
        }
    }

    // Reset Confirmation Dialog
    lotToReset?.let { lotId ->
        AlertDialog(
            onDismissRequest = { lotToReset = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetLotProgress(lotId)
                        lotToReset = null
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Oui, recommencer", color = CoralRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { lotToReset = null }) {
                    Text("Annuler")
                }
            },
            title = { Text("Recommencer ce Lot ?") },
            text = { Text("Toutes tes cartes du Lot $lotId redeviendront nouvelles. Tu es sûr ?") },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun LotItemCard(
    lotId: Int,
    title: String,
    emoji: String,
    unlocked: Boolean,
    masteredCount: Int,
    learningCount: Int,
    readyCount: Int,
    onLearnClick: () -> Unit,
    onPlayClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, if (readyCount > 0 && unlocked) SunOrange else CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lot_card_$lotId")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (unlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        )
                ) {
                    Text(text = if (unlocked) emoji else "🔒", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lot $lotId : $title",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (unlocked) {
                        Text(
                            text = "$masteredCount maîtrisé(s) • $learningCount en cours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Maîtrise au moins 8/10 mots du Lot ${lotId - 1} pour débloquer !",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = CoralRed
                        )
                    }
                }

                if (unlocked && (masteredCount > 0 || learningCount > 0)) {
                    IconButton(
                        onClick = onResetClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("reset_lot_${lotId}_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Réinitialiser",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (unlocked) {
                Spacer(modifier = Modifier.height(14.dp))
                
                // Spaced Repetition Alert pill
                if (readyCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .background(SunOrange.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Alarm,
                                contentDescription = "Révision disponible",
                                tint = Color(0xFFD35400),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$readyCount mot(s) prêt(s) pour la révision !",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFD35400)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Practice Button
                    Button(
                        onClick = onLearnClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (readyCount > 0) SunOrange else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(40.dp)
                            .testTag("learn_lot_${lotId}_button")
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (readyCount > 0) "Réviser ($readyCount)" else "Apprendre",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Games Button
                    FilledTonalButton(
                        onClick = onPlayClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("games_lot_${lotId}_button")
                    ) {
                        Icon(
                            Icons.Default.Games,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Jeux", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- SCREEN: ACTIVE CARD RECALL (LEARN) ---
@Composable
fun LearnScreen(viewModel: VocabularyViewModel, lotId: Int) {
    val words by viewModel.learnWords.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentWordIndex.collectAsStateWithLifecycle()
    val isFlipped by viewModel.isCardFlipped.collectAsStateWithLifecycle()

    if (words.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentWord = words.getOrNull(currentIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                modifier = Modifier.testTag("close_learn_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Fermer")
            }
            Text(
                text = "Cartes ${currentIndex + 1} / ${words.size}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Box(
                modifier = Modifier
                    .background(FriendlyBlue.copy(alpha = 0.2f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Lot $lotId",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Horizontal visual step bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in words.indices) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (i <= currentIndex) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Beautiful Interactive 3D Flip Card
        Flashcard3D(
            word = currentWord,
            isFlipped = isFlipped,
            onCardClick = { viewModel.flipLearnCard() },
            onSpeak = { viewModel.speakFrench(currentWord.french) }
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Instructions
        Text(
            text = if (!isFlipped) "Essaie de deviner le sens, puis touche la carte pour vérifier !"
                   else "Alors, as-tu retrouvé le bon mot ?",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Leitner Action Decisions
        AnimatedVisibility(
            visible = !isFlipped,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = { viewModel.flipLearnCard() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("reveal_answer_button")
            ) {
                Icon(Icons.Default.RemoveRedEye, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vérifier ma réponse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        AnimatedVisibility(
            visible = isFlipped,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Incorrect Recall (Keep studying)
                FilledTonalButton(
                    onClick = { viewModel.recordLearnReview(wasSuccessful = false) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CoralRed.copy(alpha = 0.2f),
                        contentColor = Color(0xFFC0392B)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("recall_failed_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("À revoir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Pas encore", fontSize = 10.sp)
                    }
                }

                // Successful Recall
                Button(
                    onClick = { viewModel.recordLearnReview(wasSuccessful = true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen,
                        contentColor = TextDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(56.dp)
                        .testTag("recall_success_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Je savais !", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Gagné !", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- 3D INTERACTIVE FLIP CARD ---
@Composable
fun Flashcard3D(
    word: VocabularyWord,
    isFlipped: Boolean,
    onCardClick: () -> Unit,
    onSpeak: () -> Unit
) {
    // Rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    Card(
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(3.dp, if (isFlipped) MintGreen else FriendlyBlue),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { onCardClick() }
            .testTag("flash_card_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isFlipped) {
                            listOf(Color.White, Color(0xFFF1FDF9))
                        } else {
                            listOf(Color.White, Color(0xFFF2F9FF))
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // --- RECTO : FRENCH WORD ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "En français :",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = word.french,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("recto_french_word")
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Tap indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Touche pour retourner",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // --- VERSO : MEANING & EXAMPLE ---
                // Flip Content horizontally so it doesn't appear mirrored due to the Y rotation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(24.dp)
                        .graphicsLayer { rotationY = 180f }
                ) {
                    // Title and Speak Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = word.french,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { onSpeak() },
                            modifier = Modifier
                                .background(FriendlyBlue.copy(alpha = 0.2f), CircleShape)
                                .size(36.dp)
                                .testTag("speak_word_button")
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Prononcer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = CardBorder
                    )

                    // English Translation
                    Text(
                        text = "Signification :",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Mental Association Image Description
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WarmYellow.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "💡", fontSize = 20.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = word.mentalImage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                ),
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Example Sentence
                    Text(
                        text = "Exemple :",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = word.exampleFr,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = word.exampleTr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// --- SCREEN: GAMES SELECTION ---
@Composable
fun GameSelectionScreen(viewModel: VocabularyViewModel, lotId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "Jeux du Lot $lotId",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
        }

        Text(
            text = "Sélectionne un jeu rigolo pour t'entraîner de façon active !",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GAME 1: Pioche Rapide
        GameCardOption(
            title = "⚡ La Pioche Rapide",
            description = "Devine un maximum de cartes en 30 secondes chrono ! Un sprint d'apprentissage.",
            color = SunOrange,
            onClick = { viewModel.navigateTo(Screen.GamePioche(lotId)) },
            testTag = "game_pioche_card"
        )

        // GAME 2: Bataille des Mots
        GameCardOption(
            title = "🧩 La Bataille des Mots",
            description = "Retrouve les paires ! Associe chaque mot français à sa traduction anglaise.",
            color = FriendlyBlue,
            onClick = { viewModel.navigateTo(Screen.GameBataille(lotId)) },
            testTag = "game_bataille_card"
        )

        // GAME 3: Le Mot Mystère
        GameCardOption(
            title = "🕵️ Le Mot Mystère",
            description = "Devine le mot français caché dans la phrase d'exemple ! Trouve l'énigme.",
            color = WarmYellow,
            onClick = { viewModel.navigateTo(Screen.GameMotMystere(lotId)) },
            testTag = "game_mystere_card"
        )
    }
}

@Composable
fun GameCardOption(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, color),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// --- GAME SCREEN 1: PIOCHE RAPIDE (Sprint 30s) ---
@Composable
fun GamePiocheScreen(viewModel: VocabularyViewModel, lotId: Int) {
    val words by viewModel.piocheWords.collectAsStateWithLifecycle()
    val index by viewModel.piocheIndex.collectAsStateWithLifecycle()
    val flipped by viewModel.piocheFlipped.collectAsStateWithLifecycle()
    val timeLeft by viewModel.piocheTimeLeft.collectAsStateWithLifecycle()
    val score by viewModel.piocheScore.collectAsStateWithLifecycle()
    val isOver by viewModel.piocheIsOver.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            
            // Timer Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (timeLeft <= 10) CoralRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Temps restant",
                        tint = if (timeLeft <= 10) CoralRed else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$timeLeft s",
                        fontWeight = FontWeight.Black,
                        color = if (timeLeft <= 10) CoralRed else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Score Card
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmYellow.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🏆 Score: $score",
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isOver && words.isNotEmpty() && index < words.size) {
            val word = words[index]

            Text(
                text = "⚡ Sprint Pioche !",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SunOrange
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Game Flashcard Box
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(2.dp, SunOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clickable { viewModel.flipPiocheCard() }
                    .testTag("pioche_card_touch")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (!flipped) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = word.french,
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Touche pour retourner 🔍",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Anglais :",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = word.meaning,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "💡 Image :",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = word.mentalImage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Action buttons
            if (flipped) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.piocheRecordResult(correct = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("pioche_wrong_button")
                    ) {
                        Text("Perdu ❌", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.piocheRecordResult(correct = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen, contentColor = TextDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp)
                            .testTag("pioche_correct_button")
                    ) {
                        Text("Trouvé ! ✅", fontWeight = FontWeight.ExtraBold)
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.flipPiocheCard() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Révéler la réponse 🔍")
                }
            }

        } else {
            // GAME OVER STATE
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "👑", fontSize = 64.sp)
            Text(
                text = "Temps Écoulé !",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = SunOrange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ton Score Final : $score points",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Bien joué ! Chaque partie renforce ta mémoire active.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Choisir un autre jeu")
                }

                Button(
                    onClick = { viewModel.navigateTo(Screen.GamePioche(lotId)) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("replay_pioche_button")
                ) {
                    Text("Rejouer 🔄")
                }
            }
        }
    }
}

// --- GAME SCREEN 2: BATAILLE DES MOTS (Pair Matching) ---
@Composable
fun GameBatailleScreen(viewModel: VocabularyViewModel, lotId: Int) {
    val items by viewModel.batailleItems.collectAsStateWithLifecycle()
    val selectedId by viewModel.batailleSelectedId.collectAsStateWithLifecycle()
    val errors by viewModel.batailleErrors.collectAsStateWithLifecycle()
    val isOver by viewModel.batailleIsOver.collectAsStateWithLifecycle()
    val timeElapsed by viewModel.batailleTimeElapsed.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "⏱️ $timeElapsed s",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "❌ Erreurs: $errors",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CoralRed,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "🧩 La Bataille des Paires",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = FriendlyBlue
        )
        Text(
            text = "Associe chaque carte française à son équivalent !",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isOver) {
            // Vertical grid of matching items
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    BatailleGridCard(
                        item = item,
                        isSelected = item.id == selectedId || item.isSelected,
                        onClick = { viewModel.selectBatailleItem(item.id) }
                    )
                }
            }
        } else {
            // VICTORY SCREEN
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "🎉🏆🎉", fontSize = 64.sp)
            Text(
                text = "Victoire !",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = FriendlyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu as terminé la bataille en $timeElapsed secondes !",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Erreurs commises : $errors",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) }) {
                    Text("Autre jeu")
                }
                Button(
                    onClick = { viewModel.navigateTo(Screen.GameBataille(lotId)) },
                    modifier = Modifier.testTag("replay_bataille_button")
                ) {
                    Text("Rejouer 🔄")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BatailleGridCard(
    item: BatailleItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardBg = when {
        item.isMatched -> Color.LightGray.copy(alpha = 0.2f)
        isSelected -> if (item.isFrench) FriendlyBlue.copy(alpha = 0.3f) else WarmYellow.copy(alpha = 0.3f)
        else -> Color.White
    }

    val borderCol = when {
        item.isMatched -> Color.Transparent
        isSelected -> if (item.isFrench) FriendlyBlue else WarmYellow
        else -> CardBorder
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderCol),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = !item.isMatched) { onClick() }
            .testTag("bataille_item_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (item.isMatched) "✓" else item.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (item.isMatched) FontWeight.Bold else FontWeight.ExtraBold,
                    fontSize = if (item.isMatched) 24.sp else 14.sp
                ),
                color = if (item.isMatched) Color.Gray else TextDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- GAME SCREEN 3: LE MOT MYSTÈRE (Sentence riddles with blank & multiple choices) ---
@Composable
fun GameMotMystereScreen(viewModel: VocabularyViewModel, lotId: Int) {
    val words by viewModel.mystereWords.collectAsStateWithLifecycle()
    val index by viewModel.mystereIndex.collectAsStateWithLifecycle()
    val typedAnswer by viewModel.mystereTypedAnswer.collectAsStateWithLifecycle()
    val showCorrection by viewModel.mystereShowCorrection.collectAsStateWithLifecycle()
    val isCorrect by viewModel.mystereIsCorrect.collectAsStateWithLifecycle()
    val score by viewModel.mystereScore.collectAsStateWithLifecycle()
    val isOver by viewModel.mystereIsOver.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "Énigme ${index + 1} / ${words.size}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = WarmYellow.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🏆 Score: $score",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isOver && words.isNotEmpty() && index < words.size) {
            val word = words[index]

            // Dynamic choices generator (correct option + 2 distractors from same lot)
            val options = remember(word) {
                val distractors = words.filter { it.id != word.id }.shuffled().take(2)
                (distractors + word).shuffled()
            }

            // Clean French example sentence replacing the word with blanks
            val puzzleSentence = remember(word) {
                val regex = Regex(word.french, RegexOption.IGNORE_CASE)
                word.exampleFr.replace(regex, "✍️ ________")
            }

            Text(
                text = "🕵️ Trouve le Mot Mystère !",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = WarmYellow
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Puzzle Clue Box
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, WarmYellow),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Exemple d'indice :",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = puzzleSentence,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "En anglais : \"${word.exampleTr}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

                    // Mental image help
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡 Image mentale : ", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = word.mentalImage, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!showCorrection) {
                // Text input
                OutlinedTextField(
                    value = typedAnswer,
                    onValueChange = { viewModel.updateMystereAnswer(it) },
                    placeholder = { Text("Tape le mot français ici") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.submitMystereAnswer()
                    }),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mystere_answer_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "OU choisis parmi les propositions :", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                // Multiple choice buttons (Perfect child-friendly fallback!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { option ->
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                viewModel.updateMystereAnswer(option.french)
                                viewModel.submitMystereAnswer()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("mystere_option_${option.french}")
                        ) {
                            Text(text = option.french, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.submitMystereAnswer()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Valider mon choix 🕵️", fontWeight = FontWeight.Bold)
                }

            } else {
                // CORRECTION DISPLAY STATE
                val correct = isCorrect == true

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (correct) MintGreen.copy(alpha = 0.2f) else CoralRed.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (correct) "Bravo ! C'est tout à fait ça ! 🎉" else "Oups, ce n'est pas tout à fait ça... 😉",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (correct) Color(0xFF27AE60) else Color(0xFFC0392B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Le mot était : \"${word.french}\"",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = TextDark
                        )
                        Text(
                            text = "Signification : ${word.meaning}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.nextMystereWord() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("mystere_next_button")
                ) {
                    Text("Continuer ➡️", fontWeight = FontWeight.Bold)
                }
            }

        } else {
            // GAME OVER STATE
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "🏅", fontSize = 64.sp)
            Text(
                text = "Partie Terminée !",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = WarmYellow
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Score : $score / ${words.size * 10} points",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(onClick = { viewModel.navigateTo(Screen.GameSelection(lotId)) }) {
                    Text("Autre jeu")
                }
                Button(
                    onClick = { viewModel.navigateTo(Screen.GameMotMystere(lotId)) },
                    modifier = Modifier.testTag("replay_mystere_button")
                ) {
                    Text("Rejouer 🔄")
                }
            }
        }
    }
}

// --- SCREEN: GLOBAL LIST (DICTIONARY) ---
@Composable
fun GlobalListScreen(viewModel: VocabularyViewModel, words: List<VocabularyWord>) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredWords = remember(searchQuery, words) {
        if (searchQuery.isBlank()) {
            words
        } else {
            words.filter {
                it.french.contains(searchQuery, ignoreCase = true) ||
                it.meaning.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Accueil")
            }
            Text(
                text = "Dictionnaire & Suivi",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
        }

        Text(
            text = "Suis la progression de tes 100 mots essentiels de façon globale.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un mot (ex: Bonjour, Pain)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("dictionary_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grid/List of vocabulary words
        if (filteredWords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aucun mot trouvé !",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredWords) { word ->
                    DictionaryWordRow(word = word, onSpeak = { viewModel.speakFrench(word.french) })
                }
            }
        }
    }
}

@Composable
fun DictionaryWordRow(word: VocabularyWord, onSpeak: () -> Unit) {
    val statusColor = when (word.status) {
        "MASTERED" -> MintGreen
        "LEARNING" -> WarmYellow
        else -> Color.LightGray.copy(alpha = 0.5f)
    }

    val statusLabel = when (word.status) {
        "MASTERED" -> "Maîtrisé"
        "LEARNING" -> "En Cours"
        else -> "Nouveau"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("word_row_${word.french}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leitner box representation / status color bubble
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text column
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.french,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Lot ${word.lotId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = word.meaning,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (word.status != "NEW" && word.nextReviewTime > 0) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateStr = sdf.format(Date(word.nextReviewTime))
                    Text(
                        text = "Prochaine révision : $dateStr (Boîte ${word.leitnerBox})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status label
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (word.status == "NEW") Color.Gray else TextDark,
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Prononcer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
