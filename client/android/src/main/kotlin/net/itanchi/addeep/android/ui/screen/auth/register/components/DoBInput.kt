package net.itanchi.addeep.android.ui.screen.auth.register.components

import android.app.DatePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DoBInput(
    modifier: Modifier,
    focusManager: FocusManager,
    label: String,
    dob: LocalDate?,
    onDoBChange: (LocalDate) -> Unit,
) {
    val dobInteractionSource = remember { MutableInteractionSource() }

    if (dobInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
        ShowDatePicker(dob, onDoBChange)
    }

    TextField(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        value = dob?.format(DateTimeFormatter.ISO_LOCAL_DATE).orEmpty(),
        onValueChange = { },
        label = { Text(label) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = LocalContentColor.current,
        ),
        singleLine = true,
        readOnly = true,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
        interactionSource = dobInteractionSource,
    )
}

@Composable
private fun ShowDatePicker(
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
) {
    with(selectedDate ?: LocalDate.now()) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                onDateSelect(LocalDate.of(year, month + 1, dayOfMonth))
            },
            year, monthValue - 1, dayOfMonth
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }
}