package com.klipy.presentation.features.conversation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.klipy.KlipyEvents
import com.klipy.presentation.features.conversation.ConversationViewModel
import com.klipy.presentation.features.conversation.model.MediaType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MediaSelectorContainer(
    modifier: Modifier = Modifier,
    conversationId: String? = null,
) {
    val viewModel: ConversationViewModel = koinViewModel(parameters = { parametersOf(conversationId) })
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchInitialData()
    }

    MediaSelector(
        modifier = modifier,
        isLoading = viewState.isLoading,
        categories = viewState.categories,
        chosenCategory = viewState.chosenCategory,
        mediaTypes = viewState.mediaTypes ?: emptyList(),
        chosenMediaType = viewState.chosenMediaType,
        mediaItems = viewState.mediaItems,
        searchInput = viewState.searchInput,
        onCategoryClicked = { viewModel.onCategoryClicked(it) },
        onMediaTypeClicked = { viewModel.onMediaTypeClicked(it) },
        onLoadMoreItems = { viewModel.onLoadMoreItems() },
        onMediaItemClicked = { mediaItem ->
            KlipyEvents.emitReactionSelected(mediaItem)
            if (mediaItem.mediaType == MediaType.CLIP) {
                viewModel.onMediaItemSelected(mediaItem)
            }
        },
        onMediaItemLongClicked = { mediaItem ->
            viewModel.onMediaItemSelected(mediaItem)
        },
        onSearchInputChanged = { viewModel.onSearchInputEntered(it) },
        onSearchFocused = { /* no-op for now; JS controls layout */ },
        onContainerMeasured = { width, height, screenWidth, screenHeight ->
            viewModel.onScreenMeasured(
                containerWidthDp = width,
                containerHeightDp = height,
                screenWidthPx = screenWidth,
                screenHeightPx = screenHeight,
            )
        },
    )
}
