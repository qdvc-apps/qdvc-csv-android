package qdvc.csv.android.app

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CellWidth: Dp = 160.dp
private val RowNumberWidth: Dp = 56.dp
private val CellHorizontalPadding: Dp = 12.dp
private val CellVerticalPadding: Dp = 10.dp

/**
 * Renders a CSV as a scrollable grid. The header row stays pinned to the top
 * while the body scrolls vertically; both header and body share a single
 * horizontal scroll so columns stay aligned.
 */
@Composable
fun CsvTable(
    header: List<String>,
    rows: List<List<String>>,
    columnCount: Int,
    modifier: Modifier = Modifier,
) {
    val horizontalScroll = rememberScrollState()
    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        // Sticky header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .horizontalScroll(horizontalScroll),
        ) {
            HeaderCell(text = "#", width = RowNumberWidth)
            for (col in 0 until columnCount) {
                HeaderCell(
                    text = header.getOrNull(col).orEmpty(),
                    width = CellWidth,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary)

        // Scrollable body
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                count = rows.size,
                key = { it },
            ) { index ->
                val row = rows[index]
                val striped = index % 2 == 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (striped) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.surface
                        )
                        .horizontalScroll(horizontalScroll),
                ) {
                    BodyCell(
                        text = (index + 1).toString(),
                        width = RowNumberWidth,
                        emphasize = true,
                    )
                    for (col in 0 until columnCount) {
                        BodyCell(
                            text = row.getOrNull(col).orEmpty(),
                            width = CellWidth,
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                )
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = CellHorizontalPadding, vertical = CellVerticalPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BodyCell(text: String, width: Dp, emphasize: Boolean = false) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = CellHorizontalPadding, vertical = CellVerticalPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            color = if (emphasize)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurface,
            fontWeight = if (emphasize) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
