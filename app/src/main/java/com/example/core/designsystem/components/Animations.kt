package com.example.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Shared transition specs for screen movements and animated fade-ins.
 */
object CabkaroAnimations {
    val screenTransitionSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
