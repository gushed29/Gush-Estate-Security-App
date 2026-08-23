package com.gush.security.estate.access

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gush.security.estate.access.data.local.EstateSecurityDatabase
import com.gush.security.estate.access.data.repository.EstateSecurityRepository
import com.gush.security.estate.access.ui.components.ActiveCallOverlay
import com.gush.security.estate.access.ui.components.GushHeader
import com.gush.security.estate.access.ui.screens.AdminOpsScreen
import com.gush.security.estate.access.ui.screens.CreatePassDialog
import com.gush.security.estate.access.ui.screens.EmergencyDialog
import com.gush.security.estate.access.ui.screens.GuardScannerScreen
import com.gush.security.estate.access.ui.screens.IncidentDialog
import com.gush.security.estate.access.ui.screens.ResidentDashboardScreen
import com.gush.security.estate.access.ui.screens.RoleSelectionScreen
import com.gush.security.estate.access.ui.screens.VisitorPassViewScreen
import com.gush.security.estate.access.ui.theme.FrostedAmbientBlue
import com.gush.security.estate.access.ui.theme.FrostedAmbientIndigo
import com.gush.security.estate.access.ui.theme.FrostedBackground
import com.gush.security.estate.access.ui.theme.GushSecurityTheme
import com.gush.security.estate.access.ui.viewmodel.CurrentPortal
import com.gush.security.estate.access.ui.viewmodel.EstateSecurityViewModel
import com.gush.security.estate.access.ui.viewmodel.EstateSecurityViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: EstateSecurityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = EstateSecurityDatabase.getDatabase(this)
        val repository = EstateSecurityRepository(database.estateSecurityDao())
        val factory = EstateSecurityViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EstateSecurityViewModel::class.java]

        setContent {
            GushSecurityTheme {
                EstateSecurityApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EstateSecurityApp(viewModel: EstateSecurityViewModel) {
    val currentPortal by viewModel.currentPortal.collectAsStateWithLifecycle()
    val activeResident by viewModel.activeResident.collectAsStateWithLifecycle()
    val activeGuard by viewModel.activeGuard.collectAsStateWithLifecycle()
    val selectedGate by viewModel.selectedGate.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()

    val scannerInput by viewModel.scannerInput.collectAsStateWithLifecycle()
    val verificationResult by viewModel.verificationResult.collectAsStateWithLifecycle()
    val selectedPass by viewModel.selectedPass.collectAsStateWithLifecycle()

    val allPasses by viewModel.allPasses.collectAsStateWithLifecycle()
    val recentGateEvents by viewModel.recentGateEvents.collectAsStateWithLifecycle()
    val incidents by viewModel.incidents.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val policies by viewModel.policies.collectAsStateWithLifecycle()

    val securityGates by viewModel.allSecurityGates.collectAsStateWithLifecycle()
    val residents by viewModel.allResidents.collectAsStateWithLifecycle()
    val guards by viewModel.allGuards.collectAsStateWithLifecycle()

    // New Modules StateFlows
    val familyMembers by viewModel.familyMembers.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val broadcasts by viewModel.broadcasts.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val activeCallSession by viewModel.activeCallSession.collectAsStateWithLifecycle()

    var showCreatePassDialog by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showIncidentDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = FrostedBackground)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(FrostedAmbientBlue.copy(alpha = 0.8f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(0f, 0f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(FrostedAmbientIndigo, Color.Transparent),
                        center = Offset(size.width, size.height * 0.45f),
                        radius = size.width * 0.7f
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width, size.height * 0.45f)
                )
            },
        containerColor = Color.Transparent,
        topBar = {
            GushHeader(
                currentPortal = currentPortal,
                selectedGate = selectedGate,
                activeResident = activeResident,
                activeGuard = activeGuard,
                availableGates = securityGates,
                onGateSelected = { viewModel.selectGate(it) },
                onLogoutToSelection = { viewModel.logoutToPortalSelection() },
                onBackFromVisitorPass = { viewModel.backToResidentPortal() },
                bannerMessage = bannerMessage,
                onDismissBanner = { viewModel.dismissBanner() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentPortal) {
                CurrentPortal.PortalSelection -> {
                    RoleSelectionScreen(
                        residents = residents,
                        guards = guards,
                        gates = securityGates,
                        onLoginAdmin = { viewModel.loginAsAdmin() },
                        onLoginResident = { resident -> viewModel.loginAsResident(resident) },
                        onLoginGuard = { guard, gateName -> viewModel.loginAsGuard(guard, gateName) }
                    )
                }

                CurrentPortal.AdminPortal -> {
                    AdminOpsScreen(
                        gateEvents = recentGateEvents,
                        incidents = incidents,
                        auditLogs = auditLogs,
                        policies = policies,
                        allPasses = allPasses,
                        securityGates = securityGates,
                        residents = residents,
                        guards = guards,
                        broadcasts = broadcasts,
                        invoices = invoices,
                        meetings = meetings,
                        complaints = complaints,
                        messages = messages,
                        viewModel = viewModel,
                        onTogglePolicy = { key, isEnabled ->
                            viewModel.togglePolicy(key, isEnabled)
                        },
                        onResolveIncident = { incident, notes ->
                            viewModel.resolveIncident(incident, notes)
                        },
                        onOpenIncidentDialog = { showIncidentDialog = true },
                        onCreateGate = { name, code, estate, location, hours, isPrimary ->
                            viewModel.createSecurityGate(name, code, estate, location, hours, isPrimary)
                        },
                        onToggleGateStatus = { gateId, status ->
                            viewModel.toggleGateStatus(gateId, status)
                        },
                        onCreateResident = { fullName, unitNumber, estate, primaryGate, phone, email, passcode, vehicles ->
                            viewModel.createResidentAccount(fullName, unitNumber, estate, primaryGate, phone, email, passcode, vehicles)
                        },
                        onDeleteResident = { res ->
                            viewModel.deleteResidentAccount(res)
                        },
                        onCreateGuard = { fullName, badgeId, gate, shift, phone ->
                            viewModel.createGuardAccount(fullName, badgeId, gate, shift, phone)
                        },
                        onDeleteGuard = { guard ->
                            viewModel.deleteGuardAccount(guard)
                        },
                        onPublishBroadcast = { title, category, priority, content, audience, pinned ->
                            viewModel.publishBroadcast(title, category, priority, content, audience, pinned)
                        },
                        onAcknowledgeBroadcast = { broadcastId ->
                            viewModel.acknowledgeBroadcast(broadcastId)
                        },
                        onCreateInvoice = { resident, title, category, amount, period, dueDays ->
                            viewModel.createInvoice(resident, title, category, amount, period, dueDays)
                        },
                        onPayInvoice = { invoice, method ->
                            viewModel.recordInvoicePayment(invoice, method)
                        },
                        onScheduleMeeting = { title, type, agenda, dateEpoch, duration, location ->
                            viewModel.scheduleMeeting(title, type, agenda, dateEpoch, duration, location)
                        },
                        onPostMeetingContribution = { meetingId, msg, isHand, vote ->
                            viewModel.postMeetingContribution(meetingId, msg, isHand, vote)
                        },
                        onVoteMeetingPoll = { poll, optIndex ->
                            viewModel.voteMeetingPoll(poll, optIndex)
                        },
                        onSubmitComplaint = { title, cat, sev, desc, img ->
                            viewModel.submitComplaint(title, cat, sev, desc, img)
                        },
                        onResolveComplaint = { complaintId, status, notes ->
                            viewModel.resolveComplaint(complaintId, status, notes)
                        },
                        onSendMessage = { channelType, convId, text, attachType, url, filename ->
                            viewModel.sendMessage(channelType, convId, text, attachType, url, filename)
                        },
                        onStartCall = { receiverName, receiverRole, receiverUnit, isVideo, gatePost ->
                            viewModel.startCall(receiverName, receiverRole, receiverUnit, isVideo, gatePost)
                        }
                    )
                }

                CurrentPortal.ResidentPortal -> {
                    ResidentDashboardScreen(
                        activeResident = activeResident,
                        passes = allPasses.filter {
                            activeResident == null || it.hostResidentName == activeResident?.fullName || it.propertyUnit.contains(activeResident?.unitNumber ?: "")
                        }.ifEmpty { allPasses },
                        familyMembers = familyMembers,
                        messages = messages,
                        broadcasts = broadcasts,
                        invoices = invoices,
                        meetings = meetings,
                        complaints = complaints,
                        securityGates = securityGates,
                        onSelectPassForDetail = { pass ->
                            viewModel.selectPassForDetail(pass)
                        },
                        onRevokePass = { passId, reason ->
                            viewModel.revokePass(passId, reason)
                        },
                        onOpenCreatePassDialog = { showCreatePassDialog = true },
                        onAddFamilyMember = { name, rel, phone, email, access, plate ->
                            viewModel.addFamilyMember(name, rel, phone, email, access, plate)
                        },
                        onDeleteFamilyMember = { member ->
                            viewModel.deleteFamilyMember(member)
                        },
                        onStartCall = { receiverName, receiverRole, receiverUnit, isVideo, gatePost ->
                            viewModel.startCall(receiverName, receiverRole, receiverUnit, isVideo, gatePost)
                        },
                        onSendMessage = { channelType, convId, text, attachType, url, filename ->
                            viewModel.sendMessage(channelType, convId, text, attachType, url, filename)
                        },
                        onAcknowledgeBroadcast = { broadcastId ->
                            viewModel.acknowledgeBroadcast(broadcastId)
                        },
                        onPayInvoice = { invoice, method ->
                            viewModel.recordInvoicePayment(invoice, method)
                        },
                        onPostMeetingContribution = { meetingId, msg, isHand, vote ->
                            viewModel.postMeetingContribution(meetingId, msg, isHand, vote)
                        },
                        onVoteMeetingPoll = { poll, optIndex ->
                            viewModel.voteMeetingPoll(poll, optIndex)
                        },
                        onSubmitComplaint = { title, cat, sev, desc, img ->
                            viewModel.submitComplaint(title, cat, sev, desc, img)
                        },
                        onUpdateResidentProfile = { name, unit, phone, email, vehicles, emergency ->
                            viewModel.updateResidentProfile(activeResident, name, unit, phone, email, vehicles, emergency)
                        }
                    )
                }

                CurrentPortal.GuardPortal -> {
                    GuardScannerScreen(
                        scannerInput = scannerInput,
                        verificationResult = verificationResult,
                        selectedGate = selectedGate,
                        onInputChange = { viewModel.setScannerInput(it) },
                        onAppendKeypad = { viewModel.appendKeypad(it) },
                        onBackspaceKeypad = { viewModel.backspaceKeypad() },
                        onClearInput = { viewModel.clearScanner() },
                        onVerifyCode = { viewModel.verifyCode(it) },
                        onApproveEntry = { pass, occupants, notes, sig ->
                            viewModel.approveEntry(pass, occupants, notes, sig)
                        },
                        onRecordExit = { pass, items, notes, sig ->
                            viewModel.recordExit(pass, items, notes, sig)
                        },
                        onDenyAccess = { reason ->
                            viewModel.denyAccessManually(reason)
                        },
                        onOpenEmergencyDialog = { showEmergencyDialog = true },
                        onOpenIncidentDialog = { showIncidentDialog = true },
                        onStartCall = { receiverName, receiverRole, receiverUnit, isVideo, gatePost ->
                            viewModel.startCall(receiverName, receiverRole, receiverUnit, isVideo, gatePost)
                        }
                    )
                }

                CurrentPortal.VisitorPassPortal -> {
                    VisitorPassViewScreen(
                        pass = selectedPass,
                        allPasses = allPasses,
                        onSelectPass = { viewModel.selectPassForDetail(it) },
                        onBack = { viewModel.backToResidentPortal() }
                    )
                }
            }

            // 2-Way Calling & Intercom Active Session Overlay
            activeCallSession?.let { session ->
                ActiveCallOverlay(
                    callSession = session,
                    onAnswer = { viewModel.answerCall() },
                    onEndCall = { viewModel.endCall() },
                    onToggleMute = { viewModel.toggleCallMute() },
                    onToggleSpeaker = { viewModel.toggleCallSpeaker() },
                    onToggleCameraFacing = { viewModel.toggleCallCameraFacing() },
                    onOpenGate = { gateName -> viewModel.triggerGateOpenFromCall(gateName) }
                )
            }
        }

        // --- Modals & Dialogs ---
        if (showCreatePassDialog) {
            val resName = activeResident?.fullName ?: "Resident Host"
            val resUnit = "${activeResident?.unitNumber ?: "Villa 14B"} (${activeResident?.estateName ?: "Pinnock Beach Estate"})"

            CreatePassDialog(
                residentName = resName,
                propertyUnit = resUnit,
                availableGates = securityGates,
                onDismiss = { showCreatePassDialog = false },
                onCreatePass = { name, phone, type, purpose, occupants, plate, make, color, driver, gate, duration, special, items ->
                    viewModel.createVisitorPass(
                        visitorName = name,
                        phone = phone,
                        visitorType = type,
                        visitPurpose = purpose,
                        expectedOccupants = occupants,
                        vehiclePlate = plate,
                        vehicleMakeModel = make,
                        vehicleColor = color,
                        driverName = driver,
                        allowedGate = gate,
                        validDurationHours = duration,
                        specialInstructions = special,
                        declaredItems = items
                    )
                    showCreatePassDialog = false
                }
            )
        }

        if (showEmergencyDialog) {
            EmergencyDialog(
                gateName = selectedGate,
                onDismiss = { showEmergencyDialog = false },
                onExecuteOverride = { service, plate, occupants, notes ->
                    viewModel.executeEmergencyOverride(service, plate, occupants, notes)
                    showEmergencyDialog = false
                }
            )
        }

        if (showIncidentDialog) {
            IncidentDialog(
                gateName = selectedGate,
                onDismiss = { showIncidentDialog = false },
                onSubmitIncident = { title, category, severity, visitor, plate, desc ->
                    viewModel.submitIncident(title, category, severity, visitor, plate, desc)
                    showIncidentDialog = false
                }
            )
        }
    }
}
