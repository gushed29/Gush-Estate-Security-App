package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.ActiveCallSession
import com.example.data.repository.CallState
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun ActiveCallOverlay(
    callSession: ActiveCallSession,
    onAnswer: () -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCameraFacing: () -> Unit,
    onOpenGate: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val formattedDuration = "%02d:%02d".format(callSession.durationSeconds / 60, callSession.durationSeconds % 60)

    Dialog(
        onDismissRequest = { /* Require explicit hangup button */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60F172A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(28.dp)),
                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Tag
                    Surface(
                        color = if (callSession.isVideo) GushedCobalt.copy(alpha = 0.12f) else GushedEmeraldApproved.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (callSession.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (callSession.isVideo) GushedCobalt else GushedEmeraldApproved,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (callSession.state == CallState.RINGING) "INCOMING 2-WAY INTERCOM" else if (callSession.isVideo) "2-WAY HD VIDEO INTERCOM" else "2-WAY SECURE AUDIO CALL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (callSession.isVideo) GushedCobalt else GushedEmeraldApproved,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Avatar or Video Preview Viewport
                    if (callSession.isVideo && callSession.state == CallState.CONNECTED) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                )
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Video Feed simulation
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = GushedCobalt.copy(alpha = 0.8f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "LIVE INTERCOM FEED: ${callSession.gatePostName.ifEmpty { "Gatehouse Camera" }}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "1080p 60FPS • Encrypted Stream • ${if (callSession.isVideoFrontFacing) "Front Cam" else "Gatehouse Cam"}",
                                    color = Color(0xFF94A3B8),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            // Small self preview PIP in top-right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .size(width = 65.dp, height = 85.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x99000000))
                                    .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "YOU",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Pulsing Ringing Avatar
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(if (callSession.state == CallState.RINGING) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(GushedCobalt.copy(alpha = 0.25f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(76.dp),
                                shape = CircleShape,
                                color = GushedCobalt,
                                shadowElevation = 8.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (callSession.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact Name & Unit info
                    Text(
                        text = callSession.receiverName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${callSession.receiverRole} • ${callSession.receiverUnit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GushedTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Call State or Timer
                    Text(
                        text = if (callSession.state == CallState.RINGING) "Ringing intercom..." else formattedDuration,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (callSession.state == CallState.RINGING) GushedCobalt else GushedEmeraldApproved
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Controls during Connected State
                    if (callSession.state == CallState.CONNECTED) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mute button
                            IconButton(
                                onClick = onToggleMute,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (callSession.isMuted) Color(0xFFEF4444) else Color(0xFFE2E8F0))
                            ) {
                                Icon(
                                    imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute",
                                    tint = if (callSession.isMuted) Color.White else GushedTextPrimary
                                )
                            }

                            // Speaker button
                            IconButton(
                                onClick = onToggleSpeaker,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (callSession.isSpeakerOn) GushedCobalt else Color(0xFFE2E8F0))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speaker",
                                    tint = if (callSession.isSpeakerOn) Color.White else GushedTextPrimary
                                )
                            }

                            // Switch camera if video
                            if (callSession.isVideo) {
                                IconButton(
                                    onClick = onToggleCameraFacing,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cameraswitch,
                                        contentDescription = "Flip Camera",
                                        tint = GushedTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick "Open Gate" action directly from active call
                        Button(
                            onClick = { onOpenGate(callSession.gatePostName.ifEmpty { "Main Gate" }) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GushedEmeraldApproved
                            )
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Barrier Gate Remotely", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Answer or Hang up buttons
                    if (callSession.state == CallState.RINGING) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Decline button
                            Button(
                                onClick = onEndCall,
                                colors = ButtonDefaults.buttonColors(containerColor = GushedCrimsonDenied),
                                shape = CircleShape,
                                modifier = Modifier.size(62.dp)
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White)
                            }

                            // Answer button
                            Button(
                                onClick = onAnswer,
                                colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved),
                                shape = CircleShape,
                                modifier = Modifier.size(62.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Answer", tint = Color.White)
                            }
                        }
                    } else {
                        // End Call Button
                        Button(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(containerColor = GushedCrimsonDenied)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Hang Up",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
