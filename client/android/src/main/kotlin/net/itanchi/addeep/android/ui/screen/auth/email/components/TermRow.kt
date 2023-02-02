package net.itanchi.addeep.android.ui.screen.auth.email.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R

@Composable
fun TermRow(
    modifier: Modifier,
    termTitle: String,
    termDescription: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    onClicked: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            TermCheckBox(modifier = Modifier.padding(end = 8.dp), checked = checked, onChecked = onChecked)

            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    Text(
                        termTitle,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (onClicked != null) {
                        Icon(
                            painterResource(R.drawable.ic_chevron_right),
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp).size(24.dp)
                        )
                    }
                }
                Text(
                    termDescription,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TermCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onChecked: ((Boolean) -> Unit) = {},
) {
    val checkboxId = if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
    Icon(
        painterResource(checkboxId),
        contentDescription = null,
        modifier = modifier.size(24.dp).clickable { onChecked(!checked) }
    )
}