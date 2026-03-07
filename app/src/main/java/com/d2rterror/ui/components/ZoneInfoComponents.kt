package com.d2rterror.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d2rterror.data.model.Element
import com.d2rterror.data.model.Tier
import com.d2rterror.ui.theme.*

@Composable
fun ImmunityDot(
    element: Element,
    size: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    val color = element.color()
    val label = element.shortLabel()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(0.5.dp, color.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Bold,
            color = if (element == Element.LIGHTNING || element == Element.POISON) Color.Black else Color.White,
            lineHeight = (size.value * 0.6f).sp
        )
    }
}

@Composable
fun ImmunityRow(
    immunities: Set<Element>,
    dotSize: Dp = 14.dp,
    spacing: Dp = 3.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fixed display order
        val displayOrder = listOf(
            Element.PHYSICAL, Element.MAGIC,
            Element.FIRE, Element.COLD, Element.LIGHTNING, Element.POISON
        )
        displayOrder.filter { it in immunities }.forEach { element ->
            ImmunityDot(element = element, size = dotSize)
        }
    }
}

@Composable
fun TierBadge(
    tier: Tier,
    modifier: Modifier = Modifier
) {
    val color = tier.color()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tier.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun KeyIcon(
    size: Dp = 18.dp,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Default.VpnKey,
        contentDescription = "Key drop",
        tint = D2RGold,
        modifier = modifier.size(size)
    )
}

@Composable
fun BossIcon(
    size: Dp = 18.dp,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Default.Shield,
        contentDescription = "Act boss",
        tint = D2RRed,
        modifier = modifier.size(size)
    )
}

fun Element.color(): Color = when (this) {
    Element.FIRE -> D2RFire
    Element.COLD -> D2RCold
    Element.LIGHTNING -> D2RLightning
    Element.POISON -> D2RPoison
    Element.PHYSICAL -> D2RPhysical
    Element.MAGIC -> D2RMagic
}

fun Element.shortLabel(): String = when (this) {
    Element.FIRE -> "F"
    Element.COLD -> "C"
    Element.LIGHTNING -> "L"
    Element.POISON -> "P"
    Element.PHYSICAL -> "Ph"
    Element.MAGIC -> "M"
}

fun Element.displayName(): String = when (this) {
    Element.FIRE -> "Fire"
    Element.COLD -> "Cold"
    Element.LIGHTNING -> "Lightning"
    Element.POISON -> "Poison"
    Element.PHYSICAL -> "Physical"
    Element.MAGIC -> "Magic"
}

fun Tier.color(): Color = when (this) {
    Tier.S -> TierS
    Tier.A -> TierA
    Tier.B -> TierB
    Tier.C -> TierC
    Tier.D -> TierD
    Tier.F -> TierF
}