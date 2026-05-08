package com.example.rickandmortybrowser.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmortybrowser.R

@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CharacterDetailUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is CharacterDetailUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = uiState.message, style = MaterialTheme.typography.bodyLarge)
            }
        }
        is CharacterDetailUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AsyncImage(
                    model = uiState.character.image,
                    contentDescription = uiState.character.name,
                    modifier = Modifier.size(180.dp),
                )
                Text(text = uiState.character.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = uiState.character.species,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = uiState.character.status,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = stringResource(R.string.first_episodes_label),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )

                if (uiState.isEpisodesLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
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
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}
