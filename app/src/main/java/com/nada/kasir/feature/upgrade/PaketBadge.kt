package com.nada.kasir.feature.upgrade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nada.kasir.core.paket.PaketAplikasi

/**
 * Badge kecil yang menunjukkan paket saat ini & warning jika sudah dekat limit.
 * Gunakan di ProdukScreen atau PenggunaScreen.
 */
@Composable
fun PaketBadge(paket: PaketAplikasi, warning: Boolean = false) {
    Row(
        modifier = Modifier
            .background(
                color = if (warning) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Paket: ${paket.label}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (warning) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onPrimaryContainer
        )
        if (warning) {
            Text(
                text = " ⚠️",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
