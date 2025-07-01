package com.everybytesystems.dataflow.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

/**
 * Authentication UI Components
 * Login, registration, OTP verification, password reset, and social authentication
 */

// ============================================================================
// 🔐 DATA MODELS
// ============================================================================

data class AuthState(
    val currentScreen: AuthScreen = AuthScreen.LOGIN,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginCredentials: LoginCredentials = LoginCredentials(),
    val registrationData: RegistrationData = RegistrationData(),
    val otpData: OTPData = OTPData(),
    val passwordResetData: PasswordResetData = PasswordResetData(),
    val biometricAvailable: Boolean = false,
    val rememberMe: Boolean = false
)

data class LoginCredentials(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false
)

data class RegistrationData(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val acceptTerms: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

data class OTPData(
    val code: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val method: OTPMethod = OTPMethod.SMS,
    val timeRemaining: Int = 60,
    val canResend: Boolean = false
)

data class PasswordResetData(
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val resetToken: String = "",
    val isPasswordVisible: Boolean = false
)

enum class AuthScreen {
    LOGIN, REGISTER, OTP_VERIFICATION, FORGOT_PASSWORD, RESET_PASSWORD
}

enum class OTPMethod(val displayName: String) {
    SMS("SMS"),
    EMAIL("Email"),
    AUTHENTICATOR("Authenticator App")
}

enum class SocialProvider(val displayName: String, val color: Color) {
    GOOGLE("Google", Color(0xFF4285F4)),
    FACEBOOK("Facebook", Color(0xFF1877F2)),
    APPLE("Apple", Color(0xFF000000)),
    TWITTER("Twitter", Color(0xFF1DA1F2)),
    GITHUB("GitHub", Color(0xFF333333)),
    MICROSOFT("Microsoft", Color(0xFF00A4EF))
}

// ============================================================================
// 🔐 AUTHENTICATION SCREEN
// ============================================================================

@Composable
fun AuthenticationScreen(
    state: AuthState,
    onStateChange: (AuthState) -> Unit,
    onLogin: (LoginCredentials) -> Unit,
    onRegister: (RegistrationData) -> Unit,
    onForgotPassword: (String) -> Unit,
    onOTPVerification: (OTPData) -> Unit,
    onResetPassword: (String, String) -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onBiometricLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AuthHeader(currentScreen = state.currentScreen)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Content based on current screen
            when (state.currentScreen) {
                AuthScreen.LOGIN -> LoginForm(
                    credentials = state.loginCredentials,
                    onCredentialsChange = { credentials ->
                        onStateChange(state.copy(loginCredentials = credentials))
                    },
                    rememberMe = state.rememberMe,
                    onRememberMeChange = { remember ->
                        onStateChange(state.copy(rememberMe = remember))
                    },
                    isLoading = state.isLoading,
                    error = state.error,
                    onLogin = onLogin,
                    onForgotPassword = {
                        onStateChange(state.copy(currentScreen = AuthScreen.FORGOT_PASSWORD))
                    },
                    onRegister = {
                        onStateChange(state.copy(currentScreen = AuthScreen.REGISTER))
                    },
                    onSocialLogin = onSocialLogin,
                    onBiometricLogin = if (state.biometricAvailable) onBiometricLogin else null
                )
                
                AuthScreen.REGISTER -> RegisterForm(
                    data = state.registrationData,
                    onDataChange = { data ->
                        onStateChange(state.copy(registrationData = data))
                    },
                    isLoading = state.isLoading,
                    error = state.error,
                    onRegister = onRegister,
                    onBackToLogin = {
                        onStateChange(state.copy(currentScreen = AuthScreen.LOGIN))
                    }
                )
                
                AuthScreen.OTP_VERIFICATION -> OTPVerificationForm(
                    data = state.otpData,
                    onDataChange = { data ->
                        onStateChange(state.copy(otpData = data))
                    },
                    isLoading = state.isLoading,
                    error = state.error,
                    onVerify = onOTPVerification,
                    onResendOTP = {
                        // Handle OTP resend
                        println("Resend OTP")
                    },
                    onBack = {
                        onStateChange(state.copy(currentScreen = AuthScreen.LOGIN))
                    }
                )
                
                AuthScreen.FORGOT_PASSWORD -> ForgotPasswordForm(
                    email = state.passwordResetData.email,
                    onEmailChange = { email ->
                        onStateChange(
                            state.copy(
                                passwordResetData = state.passwordResetData.copy(email = email)
                            )
                        )
                    },
                    isLoading = state.isLoading,
                    error = state.error,
                    onSubmit = onForgotPassword,
                    onBack = {
                        onStateChange(state.copy(currentScreen = AuthScreen.LOGIN))
                    }
                )
                
                AuthScreen.RESET_PASSWORD -> ResetPasswordForm(
                    data = state.passwordResetData,
                    onDataChange = { data ->
                        onStateChange(state.copy(passwordResetData = data))
                    },
                    isLoading = state.isLoading,
                    error = state.error,
                    onSubmit = { password, confirm ->
                        onResetPassword(password, confirm)
                    },
                    onBack = {
                        onStateChange(state.copy(currentScreen = AuthScreen.LOGIN))
                    }
                )
            }
        }
    }
}

// ============================================================================
// 📝 LOGIN FORM
// ============================================================================

@Composable
fun LoginForm(
    credentials: LoginCredentials,
    onCredentialsChange: (LoginCredentials) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    isLoading: Boolean,
    error: String?,
    onLogin: (LoginCredentials) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onBiometricLogin: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Username field
        OutlinedTextField(
            value = credentials.username,
            onValueChange = { username ->
                onCredentialsChange(credentials.copy(username = username))
            },
            label = { Text("Username or Email") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        
        // Password field
        OutlinedTextField(
            value = credentials.password,
            onValueChange = { password ->
                onCredentialsChange(credentials.copy(password = password))
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onCredentialsChange(
                            credentials.copy(isPasswordVisible = !credentials.isPasswordVisible)
                        )
                    }
                ) {
                    Icon(
                        if (credentials.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (credentials.isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (credentials.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        
        // Remember me and forgot password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = onRememberMeChange
                )
                Text(
                    text = "Remember me",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            TextButton(onClick = onForgotPassword) {
                Text("Forgot Password?")
            }
        }
        
        // Error message
        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Login button
        Button(
            onClick = { onLogin(credentials) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && credentials.username.isNotBlank() && credentials.password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Sign In")
        }
        
        // Biometric login
        if (onBiometricLogin != null) {
            OutlinedButton(
                onClick = onBiometricLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Biometric")
            }
        }
        
        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        // Social login
        SocialLoginSection(onSocialLogin = onSocialLogin)
        
        // Register link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onRegister) {
                Text("Sign Up")
            }
        }
    }
}

// ============================================================================
// 📝 REGISTER FORM
// ============================================================================

@Composable
fun RegisterForm(
    data: RegistrationData,
    onDataChange: (RegistrationData) -> Unit,
    isLoading: Boolean,
    error: String?,
    onRegister: (RegistrationData) -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First name and last name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = data.firstName,
                onValueChange = { firstName ->
                    onDataChange(data.copy(firstName = firstName))
                },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            OutlinedTextField(
                value = data.lastName,
                onValueChange = { lastName ->
                    onDataChange(data.copy(lastName = lastName))
                },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        
        // Username
        OutlinedTextField(
            value = data.username,
            onValueChange = { username ->
                onDataChange(data.copy(username = username))
            },
            label = { Text("Username") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Email
        OutlinedTextField(
            value = data.email,
            onValueChange = { email ->
                onDataChange(data.copy(email = email))
            },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        // Password
        OutlinedTextField(
            value = data.password,
            onValueChange = { password ->
                onDataChange(data.copy(password = password))
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onDataChange(data.copy(isPasswordVisible = !data.isPasswordVisible))
                    }
                ) {
                    Icon(
                        if (data.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (data.isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (data.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        // Confirm password
        OutlinedTextField(
            value = data.confirmPassword,
            onValueChange = { confirmPassword ->
                onDataChange(data.copy(confirmPassword = confirmPassword))
            },
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onDataChange(data.copy(isConfirmPasswordVisible = !data.isConfirmPasswordVisible))
                    }
                ) {
                    Icon(
                        if (data.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (data.isConfirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (data.isConfirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = data.confirmPassword.isNotEmpty() && data.password != data.confirmPassword
        )
        
        // Terms and conditions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = data.acceptTerms,
                onCheckedChange = { accept ->
                    onDataChange(data.copy(acceptTerms = accept))
                }
            )
            Text(
                text = "I agree to the Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Error message
        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Register button
        Button(
            onClick = { onRegister(data) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && 
                    data.firstName.isNotBlank() &&
                    data.lastName.isNotBlank() &&
                    data.username.isNotBlank() &&
                    data.email.isNotBlank() &&
                    data.password.isNotBlank() &&
                    data.password == data.confirmPassword &&
                    data.acceptTerms
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Create Account")
        }
        
        // Back to login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = onBackToLogin) {
                Text("Sign In")
            }
        }
    }
}

// ============================================================================
// 🔢 OTP VERIFICATION FORM
// ============================================================================

@Composable
fun OTPVerificationForm(
    data: OTPData,
    onDataChange: (OTPData) -> Unit,
    isLoading: Boolean,
    error: String?,
    onVerify: (OTPData) -> Unit,
    onResendOTP: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter the verification code sent to your ${data.method.displayName.lowercase()}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // OTP input
        OTPInputField(
            value = data.code,
            onValueChange = { code ->
                onDataChange(data.copy(code = code))
            },
            length = 6
        )
        
        // Timer and resend
        if (data.timeRemaining > 0) {
            Text(
                text = "Resend code in ${data.timeRemaining}s",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TextButton(onClick = onResendOTP) {
                Text("Resend Code")
            }
        }
        
        // Error message
        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Verify button
        Button(
            onClick = { onVerify(data) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && data.code.length == 6
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Verify")
        }
        
        // Back button
        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

// ============================================================================
// 🔑 FORGOT PASSWORD FORM
// ============================================================================

@Composable
fun ForgotPasswordForm(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSubmit: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter your email address and we'll send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        // Error message
        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        Button(
            onClick = { onSubmit(email) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Send Reset Link")
        }
        
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In")
        }
    }
}

// ============================================================================
// 🔄 RESET PASSWORD FORM
// ============================================================================

@Composable
fun ResetPasswordForm(
    data: PasswordResetData,
    onDataChange: (PasswordResetData) -> Unit,
    isLoading: Boolean,
    error: String?,
    onSubmit: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter your new password",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        OutlinedTextField(
            value = data.newPassword,
            onValueChange = { password ->
                onDataChange(data.copy(newPassword = password))
            },
            label = { Text("New Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onDataChange(data.copy(isPasswordVisible = !data.isPasswordVisible))
                    }
                ) {
                    Icon(
                        if (data.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (data.isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (data.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        OutlinedTextField(
            value = data.confirmPassword,
            onValueChange = { confirmPassword ->
                onDataChange(data.copy(confirmPassword = confirmPassword))
            },
            label = { Text("Confirm New Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = data.confirmPassword.isNotEmpty() && data.newPassword != data.confirmPassword
        )
        
        // Error message
        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        Button(
            onClick = { onSubmit(data.newPassword, data.confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && 
                    data.newPassword.isNotBlank() && 
                    data.newPassword == data.confirmPassword
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Reset Password")
        }
        
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In")
        }
    }
}

// ============================================================================
// 🌐 SOCIAL LOGIN SECTION
// ============================================================================

@Composable
fun SocialLoginSection(
    onSocialLogin: (SocialProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val providers = listOf(
            SocialProvider.GOOGLE,
            SocialProvider.FACEBOOK,
            SocialProvider.APPLE
        )
        
        providers.forEach { provider ->
            OutlinedButton(
                onClick = { onSocialLogin(provider) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = provider.color
                )
            ) {
                // In a real implementation, you would use proper brand icons
                Icon(
                    when (provider) {
                        SocialProvider.GOOGLE -> Icons.Default.AccountCircle
                        SocialProvider.FACEBOOK -> Icons.Default.AccountCircle
                        SocialProvider.APPLE -> Icons.Default.AccountCircle
                        else -> Icons.Default.AccountCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with ${provider.displayName}")
            }
        }
    }
}

// ============================================================================
// 🔢 OTP INPUT FIELD
// ============================================================================

@Composable
fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(length) { index ->
            val char = if (index < value.length) value[index].toString() else ""
            val isFocused = index == value.length
            
            OutlinedTextField(
                value = char,
                onValueChange = { newChar ->
                    if (newChar.length <= 1 && newChar.all { it.isDigit() }) {
                        val newValue = value.take(index) + newChar + value.drop(index + 1)
                        onValueChange(newValue.take(length))
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isFocused) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

// ============================================================================
// 📱 AUTH HEADER
// ============================================================================

@Composable
fun AuthHeader(
    currentScreen: AuthScreen,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = when (currentScreen) {
                AuthScreen.LOGIN -> "Welcome Back"
                AuthScreen.REGISTER -> "Create Account"
                AuthScreen.OTP_VERIFICATION -> "Verify Your Account"
                AuthScreen.FORGOT_PASSWORD -> "Reset Password"
                AuthScreen.RESET_PASSWORD -> "New Password"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = when (currentScreen) {
                AuthScreen.LOGIN -> "Sign in to your account"
                AuthScreen.REGISTER -> "Join us today"
                AuthScreen.OTP_VERIFICATION -> "Enter the verification code"
                AuthScreen.FORGOT_PASSWORD -> "We'll help you reset it"
                AuthScreen.RESET_PASSWORD -> "Choose a strong password"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}