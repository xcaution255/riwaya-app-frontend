package com.excaution.riwayaapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.theme.InkTheme

@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InkTheme.colors.bgSurface)
            .border(1.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Icon(
            imageVector        = Icons.Rounded.Search,
            contentDescription = null,
            tint               = InkTheme.colors.textFaint,
            modifier           = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value         = query,
            onValueChange = { query = it },
            textStyle = LocalTextStyle.current.copy(
                color    = InkTheme.colors.textPrimary,
                fontSize = 14.sp,
            ),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search...", style = InkTheme.typography.bodyMedium, color = InkTheme.colors.textFaint)
                }
                inner()
            },
            modifier = Modifier.weight(1f),
        )
    }
}