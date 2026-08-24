package com.gush.security.estate.access.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gush.security.estate.access.data.local.entities.GuardAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassBorderMuted
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.FrostedGlassSurfaceElevated
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedCrimsonDenied
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedEmeraldDark
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary
import com.gush.security.estate.access.ui.viewmodel.EstateSecurityViewModel

@Composable
fun RoleSelectionScreen(
    residents: List<ResidentAccountEntity> = emptyList(),
    guards: List<GuardAccountEntity> = emptyList(),
    gates: List<SecurityGateEntity> = emptyList(),
    viewModel: EstateSecurityViewModel? = null,
    onLoginAdmin: () -> Unit = {},
    onLoginResident: (ResidentAccountEntity) -> Unit = {},
    onLoginGuard: (GuardAccountEntity, String) -> Unit = { _, _ -> },
    onEnrollCode: (String) -> Unit = { code -> viewModel?.enrollWithJoinCode(code) }
) {
    var showJoinDialog by remember { mutableStateOf(false) }
    var showActivatorDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Brand Hero Shield Badge
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.6f)
                        )
                    )
                )
                .border(2.dp, Color.White, CircleShape)
                .shadow(12.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GushedCobalt, Color(0xFF312E81))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Gush Estate Security Shield",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Gush Estate Security",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = GushedTextPrimary,
            letterSpacing = (-0.5).sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Welcome",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = GushedCobalt,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Enter your estate credentials or activate a new estate to access your synchronized security terminal.",
            fontSize = 13.sp,
            color = GushedTextSecondary,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Primary Action 1: [ Join Estate ]
        Button(
            onClick = { showJoinDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GushedCobalt,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Join Estate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Action 2: [ Estate Activator ]
        OutlinedButton(
            onClick = { showActivatorDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White.copy(alpha = 0.7f),
                contentColor = GushedTextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FrostedGlassBorder)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CorporateFare,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = GushedCobalt
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Estate Activator",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushedTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Underneath Link
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { showHelpDialog = true }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Don’t have an access code?",
                fontSize = 13.sp,
                color = GushedTextMuted,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Learn how to activate or join an estate.",
                fontSize = 13.sp,
                color = GushedCobalt,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal 1: Join Estate Dialog
    if (showJoinDialog) {
        JoinEstateCodeDialog(
            viewModel = viewModel,
            onDismiss = { showJoinDialog = false },
            onCodeSubmitted = { code ->
                if (viewModel != null) {
                    viewModel.enrollWithJoinCode(code) { success, _ ->
                        if (success) {
                            showJoinDialog = false
                        }
                    }
                } else {
                    onEnrollCode(code)
                    showJoinDialog = false
                }
            }
        )
    }

    // Modal 2: Estate Activator Portal Info
    if (showActivatorDialog) {
        EstateActivatorInfoDialog(
            viewModel = viewModel,
            onDismiss = { showActivatorDialog = false },
            onDemoActivate = {
                viewModel?.activateDemoEstate("Pinnock Beach Estate", "ENTERPRISE")
                showActivatorDialog = false
            }
        )
    }

    // Modal 3: Learn How to Join or Activate
    if (showHelpDialog) {
        EstateHelpDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
fun JoinEstateCodeDialog(
    viewModel: EstateSecurityViewModel?,
    onDismiss: () -> Unit,
    onCodeSubmitted: (String) -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    val isEnrolling = viewModel?.isEnrolling?.value ?: false
    val errorMsg = viewModel?.enrollmentError?.value

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .imePadding()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GushedCobalt.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = GushedCobalt,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Join Estate",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "Enter your assigned Access Code",
                                fontSize = 11.sp,
                                color = GushedTextMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it.uppercase() },
                    label = { Text("Access Code / Join ID") },
                    placeholder = { Text("e.g. GST-ADM-D45472") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (inputCode.isNotBlank()) onCodeSubmitted(inputCode)
                        }
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = GushedCobalt)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GushedCobalt,
                        unfocusedBorderColor = FrostedGlassBorder
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        color = GushedCrimsonDenied,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onCodeSubmitted(inputCode) },
                    enabled = inputCode.isNotBlank() && !isEnrolling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    if (isEnrolling) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authorizing...", fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = "Authorize & Enter",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast Presets for Instant Verification
                Text(
                    text = "VERIFIED ACCESS CONTROL CODE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GushedTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("GST-ADM-D45472", "Master Admin (Full Access Control)", GushedCobalt)
                    ).forEach { (code, label, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FrostedGlassSurface)
                                .border(1.dp, FrostedGlassBorder, RoundedCornerShape(12.dp))
                                .clickable { inputCode = code }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = code,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = color
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = GushedTextSecondary
                                )
                            }
                            Text(
                                text = "USE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedCobalt
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstateActivatorInfoDialog(
    viewModel: EstateSecurityViewModel?,
    onDismiss: () -> Unit,
    onDemoActivate: () -> Unit
) {
    val context = LocalContext.current
    val activatorUrl = "https://api.sstore.ng/dashboard/gsecurity/activator/"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .imePadding()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GushedCobalt.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CorporateFare,
                                contentDescription = null,
                                tint = GushedCobalt,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Estate Activator",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "Commercial Onboarding Portal",
                                fontSize = 11.sp,
                                color = GushedTextMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GushedCobalt.copy(alpha = 0.06f))
                        .border(1.dp, GushedCobalt.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "OFFICIAL ONBOARDING GATEWAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GushedCobalt,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "api.sstore.ng/dashboard/gsecurity/activator/",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To deploy Gush Security for your residential community, industrial park, or gated estate, representative administrators complete the online activation protocol:",
                    fontSize = 12.sp,
                    color = GushedTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Steps Breakdown
                listOf(
                    Triple("1. Estate Information", "Estate Name, Address, Country, State, City, Contact, Email, Phone", Icons.Default.Home),
                    Triple("2. Account Creation", "Admin Full Name, Email, Official Phone, Secure Master Password", Icons.Default.AdminPanelSettings),
                    Triple("3. Package Tier", "FREE (Essential Gates), PREMIUM, or ENTERPRISE (Full Biometrics & Hub)", Icons.Default.AutoAwesome),
                    Triple("4. Instant Provisioning", "Issues permanent Estate ID (GST-EST-XXXX) + initial Admin Join ID (GST-ADM-XXXX)", Icons.Default.CheckCircle)
                ).forEach { (stepTitle, stepDesc, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(FrostedGlassSurface)
                                .border(1.dp, FrostedGlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(stepTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                            Text(stepDesc, fontSize = 11.sp, color = GushedTextMuted, lineHeight = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(activatorUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Web Activator Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDemoActivate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedEmeraldDark)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = GushedEmeraldDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate Live Demo Estate Activation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun EstateHelpDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .imePadding()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Access Codes & Enrollment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GushedTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    Triple(
                        "Are you an Estate Administrator?",
                        "Use your Master Access Control code (GST-ADM-D45472) to access the full estate command console, manage gates, audit devices, and issue resident access passes.",
                        Icons.Default.CorporateFare
                    ),
                    Triple(
                        "Are you Security Personnel?",
                        "Security officers enter the gate-bound access invitation code provisioned by Estate Command to unlock the real-time scanner, gate controls, and intercom terminal.",
                        Icons.Default.Security
                    ),
                    Triple(
                        "Are you a Resident?",
                        "Residents enter their unit invitation code generated by Estate Command to manage visitor passes, invite family, view gate arrivals, and settle estate dues.",
                        Icons.Default.Home
                    )
                ).forEach { (title, description, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GushedCobalt.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(description, fontSize = 12.sp, color = GushedTextSecondary, lineHeight = 17.sp)
                        }
                    }
                    HorizontalDivider(color = FrostedGlassBorderMuted)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
