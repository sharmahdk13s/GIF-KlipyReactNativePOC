package com.klipy.presentation.features.mediaitempreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Actions(
    modifier: Modifier = Modifier,
    mediaType: String,
    showHideFromRecentButton: Boolean,
    onSent: () -> Unit,
    onReport: (reason: String) -> Unit,
    onHideFromRecent: () -> Unit
) {
    val boxShape = RoundedCornerShape(10.dp)
    var reportReasonsVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(
                shape = boxShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6F),
            )
            .clip(
                shape = boxShape
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (reportReasonsVisible.not()) {
            GeneralActions(
                mediaType = mediaType,
                showHideFromRecentButton = showHideFromRecentButton,
                onSent = onSent,
                onReportClicked = {
                    reportReasonsVisible = true
                },
                onHideFromRecent = onHideFromRecent
            )
        } else {
            ReportReasons(
                onReport = onReport,
                onBackClicked = {
                    reportReasonsVisible = false
                }
            )
        }
    }
}

@Composable
private fun GeneralActions(
    mediaType: String,
    showHideFromRecentButton: Boolean,
    onSent: () -> Unit,
    onReportClicked: () -> Unit,
    onHideFromRecent: () -> Unit
) {
    Action(
        modifier = Modifier.wrapContentSize(),
        text = "Send $mediaType",
        onAction = onSent
    )
    if (showHideFromRecentButton) {
        Action(
            modifier = Modifier.wrapContentSize(),
            text = "Hide from \"Recents\"",
            onAction = onHideFromRecent
        )
    }
    Action(
        modifier = Modifier.wrapContentSize(),
        text = "Report",
        onAction = onReportClicked
    )
}

@Composable
private fun ReportReasons(
    reasons: List<String> = reportReasonsList,
    onReport: (reason: String) -> Unit,
    onBackClicked: () -> Unit
) {
    reasons.forEach {
        Action(
            modifier = Modifier.wrapContentSize(),
            text = it,
            onAction = {
                onReport.invoke(it)
            }
        )
    }
    Action(
        modifier = Modifier.wrapContentSize(),
        text = "Back",
        onAction = onBackClicked
    )
}

@Composable
private fun Action(
    modifier: Modifier = Modifier,
    text: String,
    onAction: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable {
                onAction.invoke()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private val reportReasonsList = listOf(
    "Violence",
    "Pornography",
    "Child Abuse",
    "Copyright",
    "Other",
)