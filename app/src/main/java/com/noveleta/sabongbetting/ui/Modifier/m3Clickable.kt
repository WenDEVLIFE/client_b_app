package com.noveleta.sabongbetting.ui.Modifier

import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import com.noveleta.sabongbetting.ui.theme.*

fun Modifier.m3Clickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = Role.Button,
    onClick: () -> Unit
) = composed {
    clickable(
        enabled = enabled,
        role = role,
        onClickLabel = onClickLabel,
        interactionSource = remember { MutableInteractionSource() },
        indication = androidx.compose.material.ripple.rememberRipple(
            bounded = true,
            color = MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    )
}