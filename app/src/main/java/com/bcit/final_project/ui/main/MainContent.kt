package com.bcit.final_project.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bcit.final_project.data.Recipe

@Composable
fun MainContent(recipeState: RecipeState) {
    val navController = rememberNavController()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                NavBar(navController = navController)
            }
        }
    ) { sPadding ->
        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier.padding(sPadding)
        ) {
            composable("bookmarks") {
                Bookmarks(navController = navController, recipeState = recipeState)
            }
            composable("search") {
                Search(navController = navController, recipeState = recipeState)
            }
            composable(
                route = "details/{recipeID}",
                arguments = listOf(navArgument("recipeID") { type = NavType.StringType })
            ) { backStackEntry ->
                RecipeDescription(
                    recipeId = backStackEntry.arguments?.getString("recipeID") ?: "",
                    recipeState = recipeState
                )
            }
        }
    }
}

@Composable
private fun PageHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp)
            .semantics { heading() }
    )
}

@Composable
fun Bookmarks(navController: NavController, recipeState: RecipeState) {
    val favourites by recipeState.favourites.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeading("Favourites")
        if (favourites.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text(
                    text = "No favourites saved yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favourites, key = { recipe -> recipe.id }) { recipe ->
                    ItemCard(
                        recipe = recipe,
                        isFavourite = true,
                        onToggleFavourite = { recipeState.toggleFavourite(recipe) },
                        onClick = { navController.navigate("details/${recipe.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun Search(navController: NavController, recipeState: RecipeState) {
    val favourites by recipeState.favourites.collectAsState(initial = emptyList())
    val favouriteIds = favourites.map { recipe -> recipe.id }.toSet()
    val focusManager = LocalFocusManager.current
    val visibleRecipes = if (recipeState.activeSearchQuery == null) {
        recipeState.randomRecipes
    } else {
        recipeState.searchResults
    }

    LaunchedEffect(Unit) {
        if (recipeState.randomRecipes.isEmpty()) {
            recipeState.loadRandomRecipes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeading("Recipes")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = recipeState.searchInput,
                onValueChange = recipeState::updateSearchInput,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Search recipes" },
                singleLine = true,
                placeholder = { Text("Search recipes") },
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        recipeState.submitSearch()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    if (recipeState.searchInput.isNotEmpty() || recipeState.activeSearchQuery != null) {
                        IconButton(onClick = recipeState::clearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
            FilledIconButton(
                onClick = {
                    recipeState.submitSearch()
                    focusManager.clearFocus()
                },
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        }

        when {
            recipeState.isLoading && visibleRecipes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            recipeState.errorMessage != null && visibleRecipes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text(
                        text = recipeState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            recipeState.activeSearchQuery != null && visibleRecipes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text(
                        text = "No recipes found for \"${recipeState.activeSearchQuery}\".",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleRecipes, key = { recipe -> recipe.id }) { recipe ->
                        ItemCard(
                            recipe = recipe,
                            isFavourite = recipe.id in favouriteIds,
                            onToggleFavourite = { recipeState.toggleFavourite(recipe) },
                            onClick = { navController.navigate("details/${recipe.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    recipe: Recipe,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recipe.name ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium
                )
                if (!recipe.category.isNullOrBlank()) {
                    Text(
                        text = recipe.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onToggleFavourite) {
                Icon(
                    imageVector = if (isFavourite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavourite) "Remove favourite" else "Add favourite",
                    tint = if (isFavourite) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeDescription(recipeId: String, recipeState: RecipeState) {
    LaunchedEffect(recipeId) {
        recipeState.loadRecipe(recipeId)
    }

    when {
        recipeState.selectedRecipe != null -> {
            val recipe = recipeState.selectedRecipe!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = recipe.name ?: "Untitled",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                }
                item {
                    Text(
                        text = "Category: ${recipe.category ?: "None"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp).semantics { heading() }
                    )
                }
                item {
                    Text(
                        text = recipe.instructions ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                item {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp).semantics { heading() }
                    )
                }
                items(recipe.ingredientsList.entries.toList()) { (ingredient, measure) ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = ingredient,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (measure.isNotBlank()) {
                                Text(
                                    text = measure,
                                    modifier = Modifier.weight(0.6f),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        recipeState.isRecipeDetailLoading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
