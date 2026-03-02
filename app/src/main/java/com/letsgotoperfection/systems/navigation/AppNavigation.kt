package com.letsgotoperfection.systems.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.letsgotoperfection.editor.impl.ui.screen.EditorScreen
import com.letsgotoperfection.journal.JournalScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNewDocument = { navController.navigate("editor/new") },
                onOpenJournal = { navController.navigate("journal") }
            )
        }

        composable("journal") {
            JournalScreen(
                onCreateEntry = { navController.navigate("editor/new") },
                onEntryClick = { documentId -> navController.navigate("editor/$documentId") }
            )
        }

        composable("editor/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            EditorScreen(
                documentId = if (documentId == "new") null else documentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onNewDocument: () -> Unit,
    onOpenJournal: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Systems App",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNewDocument) {
            Text("Create New Document")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOpenJournal) {
            Text("Open Journal")
        }
    }
}