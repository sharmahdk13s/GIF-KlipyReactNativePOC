package com.klipy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.klipy.KlipyEvents
import com.klipy.di.initKoin
import com.klipy.presentation.features.conversation.ConversationViewModel
import com.klipy.presentation.features.conversation.model.MediaType
import com.klipy.presentation.features.conversation.ui.ConversationScreen
import com.klipy.presentation.features.conversation.ui.MediaSelector
import com.klipy.presentation.theme.KlipyDemoAppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class KlipyMediaPickerActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      // Ensure Koin is started for the Klipy SDK before using koinViewModel
      initKoin(application)

      enableEdgeToEdge()
      setContent {
        KlipyDemoAppTheme(darkTheme = true) {
          ConversationScreen(
            conversationId = null,
            onBackClicked = { finish() }
          )
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      android.util.Log.e("KlipyMediaPickerActivity", "Failed to initialize activity", e)
      finish()
    }
  }
}

@Composable
private fun KlipyMediaPickerContent(onFinished: () -> Unit) {
  val viewModel: ConversationViewModel = koinViewModel(parameters = { parametersOf(null as String?) })
  val viewState by viewModel.viewState.collectAsState()

  LaunchedEffect(Unit) {
    viewModel.fetchInitialData()
  }

  val configuration = LocalConfiguration.current
  val density = LocalDensity.current

  // Current IME (keyboard) bottom inset in pixels
  val imeBottomPx = WindowInsets.ime.getBottom(density)
  val imeBottomDp = with(density) { imeBottomPx.toDp() }

  // Base height: when keyboard is visible, match its height exactly.
  // When keyboard is hidden, use ~35% of screen height as a sensible default.
  val keyboardLikeHeight = if (imeBottomDp > 0.dp) imeBottomDp else (configuration.screenHeightDp * 0.35f).dp

  // Clamp the sheet height between a reasonable min and max so it behaves like a modal.
  val sheetMinHeight = 120.dp
  val sheetMaxHeight = (configuration.screenHeightDp.dp - 32.dp).coerceAtLeast(sheetMinHeight)
  val sheetHeight = keyboardLikeHeight.coerceIn(sheetMinHeight, sheetMaxHeight)

  Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        // When the keyboard opens, push the sheet up so it sits on top of it.
        .padding(bottom = imeBottomDp)
    ) {
      // Full-screen scrim to detect taps outside the bottom sheet and dismiss it.
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable(
            // No ripple/visual feedback for the background
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
          ) {
            onFinished()
          }
      )

      Box(
        modifier = Modifier.align(Alignment.BottomCenter)
      ) {
        MediaSelector(
          modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight),
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
          onFinished()
        },
        onMediaItemLongClicked = { mediaItem ->
          viewModel.onMediaItemSelected(mediaItem)
        },
        onSearchInputChanged = { viewModel.onSearchInputEntered(it) },
        onSearchFocused = { },
        onContainerMeasured = { width, height, screenWidth, screenHeight ->
          viewModel.onScreenMeasured(
            containerWidthDp = width,
            containerHeightDp = height,
            screenWidthPx = screenWidth,
            screenHeightPx = screenHeight
          )
        }
      )
      }
    }
  }
}
