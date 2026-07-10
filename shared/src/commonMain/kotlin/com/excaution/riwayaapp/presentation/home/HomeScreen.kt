package com.excaution.riwayaapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.excaution.riwayaapp.presentation.theme.BgDeep
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.LocalThemeIsDark

@Composable
fun ThemeSwitcherRow(modifier: Modifier = Modifier) {
    val isDark = LocalThemeIsDark.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BgDeep)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isDark.value) "Dark Theme" else "Light Theme",
            color = InkTheme.colors.textPrimary
        )

        Switch(
            checked = isDark.value,
            onCheckedChange = { isDark.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = InkTheme.colors.accentLight,
                checkedTrackColor = InkTheme.colors.accentPrimary,
                uncheckedThumbColor = InkTheme.colors.textMuted,
                uncheckedTrackColor = InkTheme.colors.bgBorder
            )
        )
    }
}
