package dev.wdona.burntout.presentation.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme

@Composable
fun FilaTextoPlaceholder(
    modifier: Modifier = Modifier,
    height: Int = 40,
    paddingBottom: Int = 16,
    paddingTop: Int = 0,
    paddingStart: Int = 16,
    paddingEnd: Int = 16,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(bottom = paddingBottom.dp, start = paddingStart.dp, end = paddingEnd.dp, top = paddingTop.dp)
            .alpha(0.1f)
            .background(
                color = BurntOutMaterialTheme.getColorScheme().onSurface,
                shape = RoundedCornerShape(24.dp)
            )
    )
}