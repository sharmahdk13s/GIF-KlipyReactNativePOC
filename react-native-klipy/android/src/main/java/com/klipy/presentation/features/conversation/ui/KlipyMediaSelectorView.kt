package com.klipy.presentation.features.conversation.ui

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.platform.ComposeView
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.klipy.di.initKoin
import com.klipy.presentation.theme.KlipyDemoAppTheme

class KlipyMediaSelectorView(context: Context) : FrameLayout(context) {

    private val composeView: ComposeView = ComposeView(context)

    init {
        // Ensure Koin is initialized for the SDK
        val appContext = context.applicationContext
        if (appContext is Application) {
            initKoin(appContext)
        }

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        addView(
            composeView,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        composeView.setContent {
            KlipyDemoAppTheme(darkTheme = true) {
                MediaSelectorContainer()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Ensure any keyboard opened by the internal Compose search field is dismissed
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }
}

class KlipyMediaSelectorViewManager : SimpleViewManager<KlipyMediaSelectorView>() {

    override fun getName(): String = "KlipyMediaSelectorView"

    override fun createViewInstance(reactContext: ThemedReactContext): KlipyMediaSelectorView {
        return KlipyMediaSelectorView(reactContext)
    }
}
