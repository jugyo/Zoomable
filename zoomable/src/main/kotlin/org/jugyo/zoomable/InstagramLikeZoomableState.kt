package org.jugyo.zoomable

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates

@Composable
fun rememberInstagramLikeZoomableState(): InstagramLikeZoomableState = remember { InstagramLikeZoomableState() }

@Composable
fun rememberInstagramLikeZoomableState(key: Any?): InstagramLikeZoomableState =
    remember(key) { InstagramLikeZoomableState() }

private const val animationDurationMillis = 200

@Stable
class InstagramLikeZoomableState : ZoomableState {
    override var scale by mutableStateOf(1f)
        private set

    override var offset by mutableStateOf(Offset(0f, 0f))
        private set

    override val isZooming by derivedStateOf { scale != 1f }

    private var layoutCoordinates by mutableStateOf<LayoutCoordinates?>(null)

    private var isAnimationRunning: Boolean = false

    override suspend fun onZoom(
        centroid: Offset,
        pan: Offset,
        zoom: Float
    ) {
        val layoutSize = layoutCoordinates?.size ?: return

        if (isAnimationRunning) {
            return
        }

        if (scale == 1f && zoom == 1f) {
            return
        }

        // Handle zoom

        val newScale = scale * zoom
        if (newScale < 1f) {
            scale = 1f
            offset = Offset.Zero
            return
        }

        // Handle centroid

        val offsetAdjustmentForCentroid = if (zoom != 1f) {
            val center = (layoutSize.toOffset() / 2f + offset)
            val distance = center - centroid
            (distance * zoom - distance)
        } else {
            Offset.Zero
        }

        // Handle pan

        val offsetAdjustmentForPan = pan * scale

        // Set new values

        scale = newScale
        offset = offset + offsetAdjustmentForCentroid + offsetAdjustmentForPan
    }

    override suspend fun onRelease() {
        isAnimationRunning = true

        val scaleAnimation = TargetBasedAnimation(
            animationSpec = tween(animationDurationMillis),
            typeConverter = Float.VectorConverter,
            initialValue = scale,
            targetValue = 1f
        )
        val offsetAnimation = TargetBasedAnimation(
            animationSpec = tween(animationDurationMillis),
            typeConverter = Offset.VectorConverter,
            initialValue = offset,
            targetValue = Offset.Zero
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            scale = scaleAnimation.getValueFromNanos(playTime)
            offset = offsetAnimation.getValueFromNanos(playTime)
        } while (scale != 1f || offset != Offset.Zero)

        isAnimationRunning = false
    }

    override fun onGloballyPositioned(layoutCoordinates: LayoutCoordinates) {
        this.layoutCoordinates = layoutCoordinates
    }
}
