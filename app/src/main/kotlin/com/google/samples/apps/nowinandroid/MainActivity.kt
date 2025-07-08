// This is a single file, MainActivity.kt. 
// You can copy and paste this entire block into the MainActivity.kt file 
// in a new Android Studio project.

package com.example.ecoquest // Note: Your package name might be different.

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ecoquest.ui.theme.EcoQuestTheme

// Data classes to represent our game's structure
data class Character(
    val id: String,
    val name: String,
    val fact: String,
    val color: Color,
    val image: String,
    val icon: @Composable () -> Unit,
    var isUnlocked: Boolean = false
)

// Main Activity - This is the entry point of our Android app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoQuestTheme {
                EcoQuestApp()
            }
        }
    }
}

// A sealed class to define our different screens for navigation
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Characters : Screen("characters")
    object CharacterDetail : Screen("character_detail/{characterId}") {
        fun createRoute(characterId: String) = "character_detail/$characterId"
    }
    object Devices : Screen("devices")
    object Leaderboard : Screen("leaderboard")
}

// The main composable function that controls the app's navigation
@Composable
fun EcoQuestApp() {
    // State to remember which screen is currently active
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    var selectedCharacterId by remember { mutableStateOf<String?>(null) }

    // Dummy data for our characters
    val characters = remember {
        mutableStateListOf(
            Character("char1", "Capuchin Monkey", "Capuchins are the most intelligent New World monkeys!", Color(0xFFFBBF24), "https://i.ibb.co/RkRRDCtL/Gemini-Generated-Image-altr76altr76altr.png", { Icon(Icons.Default.Pets, null) }, isUnlocked = true),
            Character("char2", "Amazon Milk Frog", "Amazon Milk Frogs get their name from the milky white poison they secrete when threatened.", Color(0xFF34D399), "https://i.ibb.co/Kxqksw57/Gemini-Generated-Image-ebzbh3ebzbh3ebzb.png", { Icon(Icons.Default.Pets, null) }, isUnlocked = true),
            Character("char3", "Jaguar", "Jaguars have a bite so powerful they can pierce shell reptiles.", Color(0xFF8B5CF6), "https://i.ibb.co/RG0ZpgVX/Gemini-Generated-Image-b59o4b59o4b59o4b.png", { Icon(Icons.Default.Pets, null) }, isUnlocked = true)
        )
    }

    // A simple navigation system. It shows a different screen based on the currentScreen state.
    Crossfade(targetState = currentScreen) { screen ->
        when (screen) {
            is Screen.Onboarding -> OnboardingScreen(onStartClick = { currentScreen = Screen.Home })
            is Screen.Home -> HomeScreen(
                onNavigateToCharacters = { currentScreen = Screen.Characters },
                onNavigateToDevices = { currentScreen = Screen.Devices },
                onNavigateToLeaderboard = { currentScreen = Screen.Leaderboard }
            )
            is Screen.Characters -> CharacterCollectionScreen(
                characters = characters,
                onCharacterClick = { characterId ->
                    selectedCharacterId = characterId
                    currentScreen = Screen.CharacterDetail
                },
                onBack = { currentScreen = Screen.Home }
            )
            is Screen.CharacterDetail -> {
                val character = characters.find { it.id == selectedCharacterId }
                if (character != null) {
                    CharacterDetailScreen(character = character, onBack = { currentScreen = Screen.Characters })
                }
            }
            is Screen.Devices -> DevicesScreen(onBack = { currentScreen = Screen.Home })
            is Screen.Leaderboard -> LeaderboardScreen(onBack = { currentScreen = Screen.Home })
        }
    }
}


// --- SCREEN COMPOSABLES ---
// Each function here represents a different screen in our app.

@Composable
fun OnboardingScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://placehold.co/200x200/A7F3D0/333333?text=Eco+Hero"),
            contentDescription = "Narrator Character",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Welcome to EcoQuest!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "I'm Kiko, your guide! Let's make a difference together.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Enter your name...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Let's Go!", modifier = Modifier.padding(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDevices: () -> Unit,
    onNavigateToCharacters: () -> Unit,
    onNavigateToLeaderboard: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EcoQuest") },
                actions = {
                    IconButton(onClick = onNavigateToLeaderboard) { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") }
                    IconButton(onClick = { /* TODO: Settings */ }) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Points: 1,250", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF10B981))
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HomeButton(text = "My Devices", icon = Icons.Default.Devices, onClick = onNavigateToDevices)
                HomeButton(text = "My Friends", icon = Icons.Default.Pets, onClick = onNavigateToCharacters)
            }
            Spacer(modifier = Modifier.height(32.dp))
             Button(onClick = { /* TODO: Get Eco Tip */ }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get an Eco-Tip")
            }
        }
    }
}

@Composable
fun HomeButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCollectionScreen(
    characters: List<Character>,
    onCharacterClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends I've Made") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(characters) { character ->
                CharacterGridItem(character = character, onClick = { onCharacterClick(character.id) })
            }
        }
    }
}

@Composable
fun CharacterGridItem(character: Character, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(character.color.copy(alpha = 0.2f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                 Image(
                    painter = rememberAsyncImagePainter(model = character.image),
                    contentDescription = character.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                )
            }
            Text(character.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(character: Character, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(character.color.copy(alpha = 0.1f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Fun Fact!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(character.fact, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Placeholder screens for features we haven't built yet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(onBack: () -> Unit) {
    Scaffold(
         topBar = {
            TopAppBar(
                title = { Text("My Devices") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("Devices Screen Coming Soon!", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
     Scaffold(
         topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("Leaderboard Screen Coming Soon!", style = MaterialTheme.typography.headlineSmall)
        }
    }
}


// This is the default theme setup that comes with a new Android Studio project.
// You can leave this as it is.
namespace com.example.ecoquest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFF8BC34A),
    tertiary = Color(0xFFCDDC39)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFF8BC34A),
    tertiary = Color(0xFFCDDC39)
)

@Composable
fun EcoQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    EcoQuestTheme {
        EcoQuestApp()
    }
}
