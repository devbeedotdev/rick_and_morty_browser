package com.example.rickandmortybrowser.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmortybrowser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.character_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            CharacterDetailUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is CharacterDetailUiState.Error -> {
                ErrorView(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding),
                )
            }
            CharacterDetailUiState.OfflineNoCache -> {
                ErrorView(
                    message = stringResource(R.string.offline_not_cached_message),
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding),
                )
            }
            is CharacterDetailUiState.Success -> {
                SuccessContent(
                    uiState = uiState,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: CharacterDetailUiState.Success,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = uiState.character.image,
            contentDescription = uiState.character.name,
            modifier = Modifier
                .fillMaxWidth()
                .size(260.dp),
        )
        Text(
            text = uiState.character.name,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        DetailStatusBadge(status = uiState.character.status)
        Text(
            text = stringResource(R.string.label_species, uiState.character.species),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.label_gender, uiState.character.gender),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.label_origin, uiState.character.origin.name),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.label_location, uiState.character.location.name),
            style = MaterialTheme.typography.bodyLarge,
        )

        Text(
            text = stringResource(R.string.first_episodes_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        if (uiState.isEpisodesLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        } else {
            if (uiState.episodes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_episodes_available),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                uiState.episodes.forEach { episode ->
                    Text(
                        text = stringResource(
                            R.string.episode_item_format,
                            episode.code,
                            episode.name,
                            episode.airDate,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun DetailStatusBadge(status: String, modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.label_status, label),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
