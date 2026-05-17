package dev.wdona.burntout.presentation.ui.components.formulario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.common.FilaTextoPlaceholder

@Composable
fun SkeletonPregunta(nLineasTitulo: Int = 1, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        repeat(nLineasTitulo) {
            FilaTextoPlaceholder(modifier = Modifier.padding(horizontal = 32.dp))
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            (0..6).forEach { _ ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = false, onClick = null)
                    FilaTextoPlaceholder(paddingEnd = 32)
                }
            }
        }
    }
}