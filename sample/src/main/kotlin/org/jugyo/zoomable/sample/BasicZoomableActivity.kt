package org.jugyo.zoomable.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import org.jugyo.zoomable.Zoomable
import org.jugyo.zoomable.rememberBasicZoomableState

class BasicZoomableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Screen()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun Screen() {
    val pagerState = rememberPagerState(initialPage = 0)

    val images = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3,
        R.drawable.sample4,
    )

    val zoomableStates = List(images.size) { index ->
        rememberBasicZoomableState(index)
    }

    HorizontalPager(
        count = images.size,
        state = pagerState,
        userScrollEnabled = !zoomableStates[pagerState.currentPage].isZooming
    ) { index ->
        Box(modifier = Modifier.fillMaxSize()) {
            Zoomable(
                modifier = Modifier
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                state = zoomableStates[index],
            ) {
                Image(
                    painter = painterResource(images[index]),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentDescription = "Photo 1"
                )
            }
        }
    }
}
