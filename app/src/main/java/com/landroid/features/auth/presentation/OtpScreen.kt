// app/src/main/java/com/landroid/features/auth/presentation/OtpScreen.kt
package com.landroid.features.auth.presentation

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.navigation.Screen
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.drawBehind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    role: String,
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val otpValues = remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var focusedIndex by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableIntStateOf(42) }

    LaunchedEffect(Unit) {
        while (seconds > 0) {
            delay(1000L)
            seconds--
        }
    }

    LaunchedEffect(state.authState) {
        when (val s = state.authState) {
            is AuthState.Success -> {
                if (role == "consultant") {
                    navController.navigate(Screen.Parcels.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.LandownerDashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(LandroidColors.Surface)) {
        // Top-right decorative blob
        Box(
            modifier = Modifier
                .size(256.dp)
                .align(Alignment.TopEnd)
                .zIndex(-1f)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        paint.asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(120f, 0f, 0f, LandroidColors.PrimaryContainer.copy(alpha = 0.05f).toArgb())
                        }
                        canvas.drawCircle(
                            center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2),
                            radius = size.minDimension / 2,
                            paint = paint
                        )
                    }
                }
                .background(LandroidColors.PrimaryContainer.copy(alpha = 0.05f), CircleShape)
        )

        // Bottom-left decorative blob
        Box(
            modifier = Modifier
                .size(384.dp)
                .align(Alignment.BottomStart)
                .zIndex(-1f)
                .background(LandroidColors.TertiaryContainer.copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = LandroidColors.PrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                val context = androidx.compose.ui.platform.LocalContext.current

                if (state.verificationId.isBlank()) {
                    Text(
                        text = "Enter Your Number",
                        fontFamily = NewsreaderFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = LandroidColors.PrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "We will send an SMS to verify your identity.",
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 14.sp,
                        color = LandroidColors.Secondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    androidx.compose.material3.OutlinedTextField(
                        value = state.phoneNumber,
                        onValueChange = { viewModel.setPhone(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("+91 9876543210") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LandroidColors.PrimaryContainer,
                            unfocusedBorderColor = LandroidColors.OutlineVariant
                        ),
                        shape = LandroidShapes.Button
                    )



                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            var currentContext = context
                            while (currentContext is android.content.ContextWrapper) {
                                if (currentContext is android.app.Activity) break
                                currentContext = currentContext.baseContext
                            }
                            val activity = currentContext as? android.app.Activity
                            
                            if (activity != null && state.phoneNumber.isNotBlank()) {
                                // Add +91 prefix automatically if not present, assuming they type a 10-digit number
                                val finalPhone = if (state.phoneNumber.startsWith("+")) state.phoneNumber else "+91${state.phoneNumber}"
                                viewModel.verifyPhoneNumber(finalPhone, activity, role)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LandroidColors.PrimaryContainer,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Send Code",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.authState is AuthState.Error) {
                        Text(
                            text = (state.authState as AuthState.Error).message,
                            color = LandroidColors.Error,
                            fontSize = 12.sp,
                            fontFamily = PlusJakartaSansFont
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text(
                        text = "Verify Your Number",
                        fontFamily = NewsreaderFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = LandroidColors.PrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter the 6-digit code sent to ${state.phoneNumber}",
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 14.sp,
                        color = LandroidColors.Secondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(6) { index ->
                            var isFocused by remember { mutableStateOf(false) }
                            BasicTextField(
                                value = otpValues.value[index],
                                onValueChange = { newVal ->
                                    if (newVal.length <= 1 && newVal.all { it.isDigit() }) {
                                        val updated = otpValues.value.toMutableList()
                                        updated[index] = newVal
                                        otpValues.value = updated
                                        if (newVal.isNotEmpty() && index < 5) {
                                            focusRequesters[index + 1].requestFocus()
                                        }
                                    } else if (newVal.isEmpty() && index > 0) {
                                        val updated = otpValues.value.toMutableList()
                                        updated[index] = ""
                                        otpValues.value = updated
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .background(LandroidColors.SurfaceContainerLowest, LandroidShapes.OtpBox)
                                    .border(
                                        width = 1.dp,
                                        color = if (isFocused) LandroidColors.PrimaryContainer else LandroidColors.OutlineVariant,
                                        shape = LandroidShapes.OtpBox
                                    )
                                    .focusRequester(focusRequesters[index])
                                    .onFocusChanged { isFocused = it.isFocused },
                                textStyle = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LandroidColors.Primary,
                                    textAlign = TextAlign.Center,
                                    fontFamily = PlusJakartaSansFont
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = if (index < 5) ImeAction.Next else ImeAction.Done
                                ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) { innerTextField() }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countdown timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = LandroidColors.AccentAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (seconds > 0) "Resend in 0:${seconds.toString().padStart(2, '0')}" else "Resend OTP",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = LandroidColors.AccentAmber
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Verify button
                    Button(
                        onClick = {
                            val otp = otpValues.value.joinToString("")
                            if (otp.length == 6) {
                                viewModel.verifyOtp(otp, state.verificationId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LandroidColors.PrimaryContainer,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Verify & Continue",
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
