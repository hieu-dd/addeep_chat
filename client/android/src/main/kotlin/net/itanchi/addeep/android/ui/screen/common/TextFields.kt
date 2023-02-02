package net.itanchi.addeep.android.ui.screen.common

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    label: String,
    email: String,
    onEmailChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = modifier,
        label = { Text(label) },
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
    )
}

@Preview
@Composable
fun EmailTextFieldPreview() {
    EmailTextField(
        label = "Email",
        email = "abc@abc.com",
        onEmailChange = {}
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier,
        label = { Text(label) },
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                Icon(
                    imageVector = if (passwordHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
    )
}

@Preview
@Composable
fun PasswordTextFieldPreview() {
    PasswordTextField(
        label = "Password",
        password = "abc@abc.com",
        onPasswordChange = {}
    )
}