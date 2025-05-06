package com.noveleta.sabongbetting.widgets

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*                                    // Material3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.border

object EditTextWidgets {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun editTextOneLine(
        placeHolderTitle: String,
        textValue: String,
        onTextValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        isPasswordField: Boolean = false,
        isError: Boolean = false
    ) {
        val focusManager = LocalFocusManager.current
        var isFocused by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        // small animated top padding
        val topPadding by animateDpAsState(
            targetValue = if (isFocused) 4.dp else 0.dp,
            animationSpec = tween(200)
        )

      Box(
         modifier = modifier
             .fillMaxWidth()
             .wrapContentHeight()
             .background(
                 color = Color(0xFF171F2D),
                 shape = RoundedCornerShape(20.dp)
             )
             .border(
                 width = if (isError) 1.dp else 0.dp,
                 color = if (isError) Color.Red else Color.Transparent,
                 shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp)
) {
    Column {
        Spacer(modifier = Modifier.height(topPadding))

        OutlinedTextField(
            value = textValue,
            onValueChange = onTextValueChange,
            singleLine = true,
            textStyle = TextStyle(color = Color(0xFF7D97FF)),
            label = {
                Text(
                    text = placeHolderTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isFocused) 12.sp else 13.sp,
                    color = if (isFocused) Color.LightGray else Color.White
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPasswordField) KeyboardType.Password else KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            visualTransformation = if (isPasswordField && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            trailingIcon = {
                if (isPasswordField) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Smaller height
                .onFocusChanged { isFocused = it.isFocused }
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

    }
}
