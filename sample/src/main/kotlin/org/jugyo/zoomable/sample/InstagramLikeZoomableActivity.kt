package org.jugyo.zoomable.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jugyo.zoomable.Zoomable
import org.jugyo.zoomable.rememberInstagramLikeZoomableState

class InstagramLikeZoomableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Screen()
            }
        }
    }
}

@Preview
@Composable
private fun Screen() {
    val listState = rememberLazyListState()
    val images = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3,
        R.drawable.sample4,
    )
    val zoomableStates = List(images.size) { index ->
        rememberInstagramLikeZoomableState(index)
    }

    MaterialTheme {
        LazyColumn(
            state = listState,
            userScrollEnabled = zoomableStates.none { it.isZooming }
        ) {
            itemsIndexed(images) { index, item ->
                Zoomable(
                    state = zoomableStates[index],
                    modifier = Modifier
                        .padding(20.dp)
                        .zIndex(if (zoomableStates[index].isZooming) 1f else 0f)
                        .aspectRatio(1f)
                ) {
                    Image(
                        painter = painterResource(item),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentDescription = "Photo $index"
                    )
                }
            }
        }
    }
}
