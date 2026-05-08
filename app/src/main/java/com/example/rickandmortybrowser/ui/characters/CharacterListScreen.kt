package com.example.rickandmortybrowser.ui.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmortybrowser.R
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.util.AppConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    uiState: CharacterListUiState,
    isOffline: Boolean,
    query: String,
    selectedStatus: String?,
    selectedSpecies: String?,
    onRetry: () -> Unit,
    onClearSearch: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onStatusSelected: (String?) -> Unit,
    onSpeciesSelected: (String?) -> Unit,
    onCharacterClick: (Int) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val offlineMessage = stringResource(R.string.offline_cached_message)

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = offlineMessage,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            SearchBar(
                query = query,
                onQueryChange = onQueryChanged,
                onSearch = {},
                active = false,
                onActiveChange = {},
                placeholder = { Text(stringResource(R.string.search_characters_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {}

            FilterSection(
                selectedStatus = selectedStatus,
                selectedSpecies = selectedSpecies,
                onStatusSelected = onStatusSelected,
                onSpeciesSelected = onSpeciesSelected,
            )

            when (uiState) {
                CharacterListUiState.Loading -> LoadingState()
                CharacterListUiState.Empty -> EmptyState(onRetry = onRetry)
                is CharacterListUiState.SearchEmpty -> SearchEmptyState(
                    query = uiState.query,
                    onClearSearch = onClearSearch,
                )
                is CharacterListUiState.Error -> ErrorState(
                    message = uiState.message,
                    onRetry = onRetry,
                )
                is CharacterListUiState.Success -> CharacterListContent(
                    characters = uiState.characters,
                    isAppending = uiState.isAppending,
                    appendErrorMessage = uiState.appendErrorMessage,
                    onCharacterClick = onCharacterClick,
                    onLoadNextPage = onLoadNextPage,
                    onRetryLoadNextPage = onRetryLoadNextPage,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    selectedStatus: String?,
    selectedSpecies: String?,
    onStatusSelected: (String?) -> Unit,
    onSpeciesSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = stringResource(R.string.status_filter_label), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(
                label = stringResource(R.string.filter_status_alive),
                value = AppConstants.Filters.STATUS_ALIVE,
                selected = selectedStatus == AppConstants.Filters.STATUS_ALIVE,
                onStatusSelected = onStatusSelected,
            )
            StatusChip(
                label = stringResource(R.string.filter_status_dead),
                value = AppConstants.Filters.STATUS_DEAD,
                selected = selectedStatus == AppConstants.Filters.STATUS_DEAD,
                onStatusSelected = onStatusSelected,
            )
            StatusChip(
                label = stringResource(R.string.filter_status_unknown),
                value = AppConstants.Filters.STATUS_UNKNOWN,
                selected = selectedStatus == AppConstants.Filters.STATUS_UNKNOWN,
                onStatusSelected = onStatusSelected,
            )
        }

        Text(text = stringResource(R.string.species_filter_label), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SpeciesChip(
                label = stringResource(R.string.filter_species_human),
                value = AppConstants.Filters.SPECIES_HUMAN,
                selected = selectedSpecies == AppConstants.Filters.SPECIES_HUMAN,
                onSpeciesSelected = onSpeciesSelected,
            )
            SpeciesChip(
                label = stringResource(R.string.filter_species_alien),
                value = AppConstants.Filters.SPECIES_ALIEN,
                selected = selectedSpecies == AppConstants.Filters.SPECIES_ALIEN,
                onSpeciesSelected = onSpeciesSelected,
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    value: String,
    selected: Boolean,
    onStatusSelected: (String?) -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = { onStatusSelected(if (selected) null else value) },
        label = { Text(label) },
    )
}

@Composable
private fun SpeciesChip(
    label: String,
    value: String,
    selected: Boolean,
    onSpeciesSelected: (String?) -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = { onSpeciesSelected(if (selected) null else value) },
        label = { Text(label) },
    )
}

@Composable
private fun SearchEmptyState(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.search_empty_characters_message, query))
        Button(onClick = onClearSearch, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.clear_search))
        }
    }
}

@Composable
private fun CharacterListContent(
    characters: List<Character>,
    isAppending: Boolean,
    appendErrorMessage: String?,
    onCharacterClick: (Int) -> Unit,
    onLoadNextPage: () -> Unit,
    onRetryLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    ObservePaginationThreshold(
        listState = listState,
        totalItems = characters.size,
        onLoadNextPage = onLoadNextPage,
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(characters, key = { it.id }) { character ->
            CharacterRow(
                character = character,
                onClick = { onCharacterClick(character.id) },
            )
        }
        if (isAppending) {
            item(key = "append_loading") {
                BottomLoadingIndicator()
            }
        }
        if (appendErrorMessage != null) {
            item(key = "append_error") {
                AppendErrorView(
                    message = appendErrorMessage,
                    onRetry = onRetryLoadNextPage,
                )
            }
        }
    }
}

@Composable
private fun ObservePaginationThreshold(
    listState: LazyListState,
    totalItems: Int,
    onLoadNextPage: () -> Unit,
) {
    LaunchedEffect(listState, totalItems) {
        snapshotFlow {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 3
        }.collect { shouldLoad ->
            if (shouldLoad) {
                onLoadNextPage()
            }
        }
    }
}

@Composable
private fun CharacterRow(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier.size(64.dp),
        )
        Column {
            Text(text = character.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = character.species,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.size(6.dp))
            StatusBadge(status = character.status)
        }
    }
}

@Composable
private fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (badgeColor, label) = when (status.lowercase()) {
        "alive" -> Color(0xFF2E7D32) to stringResource(R.string.status_alive)
        "dead" -> Color(0xFFC62828) to stringResource(R.string.status_dead)
        else -> Color(0xFF757575) to stringResource(R.string.status_unknown)
    }

    Surface(
        color = badgeColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.empty_characters_message))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun BottomLoadingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun AppendErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}
