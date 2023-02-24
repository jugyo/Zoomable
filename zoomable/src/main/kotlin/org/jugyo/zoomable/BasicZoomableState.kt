package org.jugyo.zoomable

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.IntSize
import kotlin.math.min

@Composable
fun rememberBasicZoomableState(): BasicZoomableState = remember { BasicZoomableState() }

@Composable
fun rememberBasicZoomableState(key: Any?): BasicZoomableState =
    remember(key) { BasicZoomableState() }

private const val animationDurationMillis = 200
private const val maxScale = 4f

@Stable
class BasicZoomableState : ZoomableState {
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

        val newScale = min(scale * zoom, maxScale)
        val actualZoom = newScale / scale
        if (newScale < 1f) {
            scale = 1f
            offset = Offset.Zero
            return
        }

        // Handle centroid

        val offsetAdjustmentForCentroid = if (actualZoom != 1f) {
            calculateOffsetAdjustmentForCentroid(centroid, actualZoom, layoutSize)
        } else {
            Offset.Zero
        }

        // Handle pan

        val offsetAdjustmentForPan = pan * scale

        // Set new values

        scale = newScale
        offset = offset + offsetAdjustmentForCentroid + offsetAdjustmentForPan
    }

    override suspend fun onDoubleTap(position: Offset) {
        if (isZooming) {
            reset()
        } else {
            zoomInTo(position)
        }
    }

    private suspend fun zoomInTo(position: Offset, zoom: Float = 2f) {
        val layoutSize = layoutCoordinates?.size ?: return
        val targetScale = scale * zoom
        val offsetAdjustmentForCentroid = calculateOffsetAdjustmentForCentroid(position, zoom, layoutSize)
        val targetOffset = offset + offsetAdjustmentForCentroid

        runAnimation(
            initialScale = scale,
            targetScale = targetScale,
            initialOffset = offset,
            targetOffset = targetOffset
        )
    }

    private suspend fun reset() {
        runAnimation(
            initialScale = scale,
            targetScale = 1f,
            initialOffset = offset,
            targetOffset = Offset.Zero
        )
    }

    private suspend fun runAnimation(
        initialScale: Float,
        targetScale: Float,
        initialOffset: Offset,
        targetOffset: Offset
    ) {
        isAnimationRunning = true

        val scaleAnimation = TargetBasedAnimation(
            animationSpec = tween(animationDurationMillis),
            typeConverter = Float.VectorConverter,
            initialValue = initialScale,
            targetValue = targetScale
        )
        val offsetAnimation = TargetBasedAnimation(
            animationSpec = tween(animationDurationMillis),
            typeConverter = Offset.VectorConverter,
            initialValue = initialOffset,
            targetValue = targetOffset
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            scale = scaleAnimation.getValueFromNanos(playTime)
            offset = offsetAnimation.getValueFromNanos(playTime)
        } while (scale != targetScale || offset != targetOffset)

        isAnimationRunning = false
    }

    override fun onGloballyPositioned(layoutCoordinates: LayoutCoordinates) {
        this.layoutCoordinates = layoutCoordinates
    }

    private fun calculateOffsetAdjustmentForCentroid(
        centroid: Offset,
        zoom: Float,
        layoutSize: IntSize,
    ): Offset {
        val center = (layoutSize.toOffset() / 2f + offset)
        val distance = center - centroid
        return (distance * zoom - distance)
    }
}
