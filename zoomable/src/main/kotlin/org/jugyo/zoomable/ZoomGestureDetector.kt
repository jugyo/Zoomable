package org.jugyo.zoomable

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged
import kotlin.math.PI
import kotlin.math.abs

suspend fun PointerInputScope.detectZoomGestures(
    onZoom: (centroid: Offset, pan: Offset, zoom: Float) -> Unit,
    onRelease: () -> Unit
) {
    forEachGesture {
        awaitPointerEventScope {
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop

            awaitFirstDown(requireUnconsumed = false)

            do {
                val event = awaitPointerEvent()
                val canceled = event.changes.any { it.isConsumed }

                if (!canceled) {
                    val pressed = event.changes.any { it.pressed }

                    if (pressed) {
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()

                        if (!pastTouchSlop) {
                            zoom *= zoomChange
                            pan += panChange

                            val centroidSize = event.calculateCentroidSize(useCurrent = false)
                            val zoomMotion = abs(1 - zoom) * centroidSize
                            val panMotion = pan.getDistance()

                            if (zoomMotion > touchSlop ||
                                panMotion > touchSlop
                            ) {
                                pastTouchSlop = true
                            }
                        }

                        if (pastTouchSlop) {
                            val centroid = event.calculateCentroid(useCurrent = false)
                            if (zoomChange != 1f ||
                                panChange != Offset.Zero
                            ) {
                                onZoom(centroid, panChange, zoomChange)
                            }
                            event.changes.forEach {
                                if (zoom != 1f && it.positionChanged()) {
                                    it.consume()
                                }
                            }
                        }
                    } else {
                        if (pastTouchSlop) {
                            onRelease()
                        }
                    }
                }
            } while (!canceled && event.changes.any { it.pressed })
        }
    }
}
