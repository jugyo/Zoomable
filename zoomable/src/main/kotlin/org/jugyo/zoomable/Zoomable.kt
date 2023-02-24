package org.jugyo.zoomable

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

@Composable
fun Zoomable(
    modifier: Modifier = Modifier,
    state: ZoomableState = rememberInstagramLikeZoomableState(),
    content: @Composable BoxScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = state.scale
                    scaleY = state.scale
                    translationX = state.offset.x
                    translationY = state.offset.y
                }
                .pointerInput(Unit) {
                    detectZoomGestures(
                        onZoom = { centroid, pan, zoom ->
                            scope.launch {
                                state.onZoom(
                                    centroid = centroid,
                                    pan = pan,
                                    zoom = zoom
                                )
                            }
                        },
                        onRelease = {
                            scope.launch {
                                state.onRelease()
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        scope.launch {
                            state.onDoubleTap(it)
                        }
                    })
                }
                .onGloballyPositioned(state::onGloballyPositioned),
            content = content
        )
    }
}

interface ZoomableState {
    val offset: Offset
    val scale: Float
    val isZooming: Boolean
    suspend fun onZoom(centroid: Offset, pan: Offset, zoom: Float)
    suspend fun onRelease() {}
    suspend fun onDoubleTap(position: Offset) {}
    fun onGloballyPositioned(layoutCoordinates: LayoutCoordinates)
}

internal fun IntSize.toOffset() = Offset(width.toFloat(), height.toFloat())

