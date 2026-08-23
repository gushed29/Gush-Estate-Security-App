package com.gush.security.estate.access.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gush.security.estate.access.data.local.entities.AuditLogEntity
import com.gush.security.estate.access.data.local.entities.DeclaredItemEntity
import com.gush.security.estate.access.data.local.entities.EstateBroadcastEntity
import com.gush.security.estate.access.data.local.entities.EstateFeeInvoiceEntity
import com.gush.security.estate.access.data.local.entities.EstateMeetingEntity
import com.gush.security.estate.access.data.local.entities.EstateMessageEntity
import com.gush.security.estate.access.data.local.entities.FamilyMemberEntity
import com.gush.security.estate.access.data.local.entities.GateEventEntity
import com.gush.security.estate.access.data.local.entities.GuardAccountEntity
import com.gush.security.estate.access.data.local.entities.IncidentEntity
import com.gush.security.estate.access.data.local.entities.MeetingPollEntity
import com.gush.security.estate.access.data.local.entities.MessageAttachmentType
import com.gush.security.estate.access.data.local.entities.MessageChannelType
import com.gush.security.estate.access.data.local.entities.PassType
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentComplaintEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.data.local.entities.SecurityPolicyEntity
import com.gush.security.estate.access.data.local.entities.VisitorPassEntity
import com.gush.security.estate.access.data.repository.ActiveCallSession
import com.gush.security.estate.access.data.repository.CallState
import com.gush.security.estate.access.data.repository.EstateSecurityRepository
import com.gush.security.estate.access.data.repository.VerificationResult
import com.gush.security.estate.access.integration.adapters.DeviceOperationResult
import com.gush.security.estate.access.integration.connectors.AutomationPlatform
import com.gush.security.estate.access.integration.connectors.AutomationRule
import com.gush.security.estate.access.integration.connectors.BridgeSyncResult
import com.gush.security.estate.access.integration.connectors.BridgeTestResult
import com.gush.security.estate.access.integration.connectors.DatabaseBridgeSpec
import com.gush.security.estate.access.integration.gateway.GushSecurityIntegrationHub
import com.gush.security.estate.access.integration.model.AuthType
import com.gush.security.estate.access.integration.model.ConnectionType
import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.GushSecurityEvent
import com.gush.security.estate.access.integration.model.HardwareDeviceProfile
import com.gush.security.estate.access.integration.model.HardwareDeviceType
import com.gush.security.estate.access.integration.model.IntegrationConnectorConfig
import com.gush.security.estate.access.integration.model.IntegrationPermission
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class CurrentPortal {
    data object PortalSelection : CurrentPortal()
    data object AdminPortal : CurrentPortal()
    data object ResidentPortal : CurrentPortal()
    data object GuardPortal : CurrentPortal()
    data object VisitorPassPortal : CurrentPortal()
}

class EstateSecurityViewModel(private val repository: EstateSecurityRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    // Portal Navigation State
    private val _currentPortal = MutableStateFlow<CurrentPortal>(CurrentPortal.PortalSelection)
    val currentPortal: StateFlow<CurrentPortal> = _currentPortal.asStateFlow()

    // Active Authenticated Profiles
    private val _activeResident = MutableStateFlow<ResidentAccountEntity?>(null)
    val activeResident: StateFlow<ResidentAccountEntity?> = _activeResident.asStateFlow()

    private val _activeGuard = MutableStateFlow<GuardAccountEntity?>(null)
    val activeGuard: StateFlow<GuardAccountEntity?> = _activeGuard.asStateFlow()

    private val _selectedGate = MutableStateFlow("Gate 1 - Pinnock Beach Estate Main Gate")
    val selectedGate: StateFlow<String> = _selectedGate.asStateFlow()

    // Scanner & Verification State
    private val _scannerInput = MutableStateFlow("")
    val scannerInput: StateFlow<String> = _scannerInput.asStateFlow()

    private val _verificationResult = MutableStateFlow<VerificationResult>(VerificationResult.Idle)
    val verificationResult: StateFlow<VerificationResult> = _verificationResult.asStateFlow()

    private val _selectedPass = MutableStateFlow<VisitorPassEntity?>(null)
    val selectedPass: StateFlow<VisitorPassEntity?> = _selectedPass.asStateFlow()

    // UI Feedback Banner
    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    // 2-Way Calling & Intercom State
    private val _activeCall = MutableStateFlow<ActiveCallSession?>(null)
    val activeCall: StateFlow<ActiveCallSession?> = _activeCall.asStateFlow()
    val activeCallSession: StateFlow<ActiveCallSession?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Data Streams from Repository
    val allPasses: StateFlow<List<VisitorPassEntity>> = repository.allPasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeInsidePasses: StateFlow<List<VisitorPassEntity>> = repository.activeInsidePasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentGateEvents: StateFlow<List<GateEventEntity>> = repository.recentGateEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidents: StateFlow<List<IncidentEntity>> = repository.incidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val policies: StateFlow<List<SecurityPolicyEntity>> = repository.policies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSecurityGates: StateFlow<List<SecurityGateEntity>> = repository.allSecurityGates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResidents: StateFlow<List<ResidentAccountEntity>> = repository.allResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGuards: StateFlow<List<GuardAccountEntity>> = repository.allGuards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFamilyMembers: StateFlow<List<FamilyMemberEntity>> = repository.allFamilyMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val familyMembers: StateFlow<List<FamilyMemberEntity>> = allFamilyMembers

    val allMessages: StateFlow<List<EstateMessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val messages: StateFlow<List<EstateMessageEntity>> = allMessages

    val allBroadcasts: StateFlow<List<EstateBroadcastEntity>> = repository.allBroadcasts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val broadcasts: StateFlow<List<EstateBroadcastEntity>> = allBroadcasts

    val allInvoices: StateFlow<List<EstateFeeInvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val invoices: StateFlow<List<EstateFeeInvoiceEntity>> = allInvoices

    val allMeetings: StateFlow<List<EstateMeetingEntity>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val meetings: StateFlow<List<EstateMeetingEntity>> = allMeetings

    val allComplaints: StateFlow<List<ResidentComplaintEntity>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val complaints: StateFlow<List<ResidentComplaintEntity>> = allComplaints

    // --- Authentication & Portal Navigation ---
    fun loginAsAdmin() {
        _currentPortal.value = CurrentPortal.AdminPortal
        _bannerMessage.value = "👑 Authenticated: Estate Security Operations & Command"
    }

    fun loginAsResident(resident: ResidentAccountEntity) {
        _activeResident.value = resident
        _currentPortal.value = CurrentPortal.ResidentPortal
        _bannerMessage.value = "🏡 Welcome, ${resident.fullName} (${resident.unitNumber})"
    }

    fun loginAsGuard(guard: GuardAccountEntity, gateName: String) {
        _activeGuard.value = guard
        _selectedGate.value = gateName
        _currentPortal.value = CurrentPortal.GuardPortal
        _bannerMessage.value = "🛡️ Guard Terminal Active: ${guard.fullName} at $gateName"
    }

    fun selectGate(gateName: String) {
        _selectedGate.value = gateName
        _bannerMessage.value = "📍 Shift Location: $gateName"
    }

    fun logoutToPortalSelection() {
        _currentPortal.value = CurrentPortal.PortalSelection
        _activeResident.value = null
        _activeGuard.value = null
        _selectedPass.value = null
        _verificationResult.value = VerificationResult.Idle
        _scannerInput.value = ""
        _bannerMessage.value = "🔒 Logged out of active portal"
    }

    fun selectPassForDetail(pass: VisitorPassEntity) {
        _selectedPass.value = pass
        _currentPortal.value = CurrentPortal.VisitorPassPortal
    }

    fun backToResidentPortal() {
        _currentPortal.value = CurrentPortal.ResidentPortal
        _selectedPass.value = null
    }

    fun dismissBanner() {
        _bannerMessage.value = null
    }

    // --- 2-Way Calling & Intercom ---
    fun startCall(
        receiverName: String,
        receiverRole: String,
        receiverUnit: String,
        isVideo: Boolean,
        gatePostName: String = ""
    ) {
        val callerName = _activeResident.value?.fullName ?: _activeGuard.value?.fullName ?: "Estate Directorate Admin"
        val callerRole = when {
            _activeResident.value != null -> "RESIDENT"
            _activeGuard.value != null -> "GUARD"
            else -> "ADMIN"
        }
        val callerUnit = _activeResident.value?.unitNumber ?: _selectedGate.value

        val session = ActiveCallSession(
            callerName = callerName,
            callerRole = callerRole,
            callerUnit = callerUnit,
            receiverName = receiverName,
            receiverRole = receiverRole,
            receiverUnit = receiverUnit,
            isVideo = isVideo,
            state = CallState.RINGING,
            gatePostName = gatePostName.ifEmpty { _selectedGate.value }
        )
        _activeCall.value = session

        // Auto connect after 2 seconds simulation
        viewModelScope.launch {
            delay(2000)
            if (_activeCall.value?.state == CallState.RINGING) {
                _activeCall.value = _activeCall.value?.copy(state = CallState.CONNECTED)
                startCallTimer()
            }
        }
    }

    fun simulateIncomingGateCall(resident: ResidentAccountEntity, gateName: String, guardName: String) {
        val session = ActiveCallSession(
            callerName = guardName,
            callerRole = "GUARD",
            callerUnit = gateName,
            receiverName = resident.fullName,
            receiverRole = "RESIDENT",
            receiverUnit = resident.unitNumber,
            isVideo = true,
            state = CallState.RINGING,
            gatePostName = gateName
        )
        _activeCall.value = session
    }

    fun answerCall() {
        _activeCall.value = _activeCall.value?.copy(state = CallState.CONNECTED)
        startCallTimer()
    }

    fun endCall() {
        callTimerJob?.cancel()
        val duration = _activeCall.value?.durationSeconds ?: 0
        val target = _activeCall.value?.receiverName ?: "Call"
        _activeCall.value = null
        _bannerMessage.value = "📞 Call ended ($duration seconds) with $target"
    }

    fun toggleCallMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleCallCameraFacing() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideoFrontFacing = !it.isVideoFrontFacing) }
    }

    fun toggleCallVideo() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideo = !it.isVideo) }
    }

    fun triggerGateOpenFromCall(gateName: String) {
        viewModelScope.launch {
            _bannerMessage.value = "🔓 Intercom Remote Override: Barrier gate opened at $gateName"
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    // --- Family Member Access ---
    fun addFamilyMember(
        fullName: String,
        relationship: String,
        phone: String,
        email: String,
        accessLevel: String,
        vehiclePlate: String
    ) {
        val res = _activeResident.value ?: return
        viewModelScope.launch {
            val member = repository.addFamilyMember(
                residentId = res.id,
                residentName = res.fullName,
                unitNumber = res.unitNumber,
                fullName = fullName,
                relationship = relationship,
                phone = phone,
                email = email,
                accessLevel = accessLevel,
                vehiclePlate = vehiclePlate
            )
            _bannerMessage.value = "🔑 Family access pass issued for ${member.fullName} (${member.relationship}) with PIN #${member.pinCode}"
        }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        val actor = _activeResident.value?.fullName ?: "Estate Admin"
        viewModelScope.launch {
            repository.deleteFamilyMember(member, actor)
            _bannerMessage.value = "🗑️ Revoked access pass for ${member.fullName}"
        }
    }

    // --- Messaging & Rich Attachments ---
    fun sendMessage(
        channelType: MessageChannelType,
        conversationId: String,
        text: String,
        attachmentType: MessageAttachmentType = MessageAttachmentType.NONE,
        attachmentUrl: String = "",
        attachmentName: String = ""
    ) {
        val senderName = _activeResident.value?.fullName ?: _activeGuard.value?.fullName ?: "Estate Admin"
        val senderId = _activeResident.value?.id ?: _activeGuard.value?.id ?: "admin"
        val senderRole = when {
            _activeResident.value != null -> "RESIDENT"
            _activeGuard.value != null -> "GUARD"
            else -> "ADMIN"
        }
        val senderUnit = _activeResident.value?.unitNumber ?: _selectedGate.value

        viewModelScope.launch {
            repository.sendMessage(
                channelType = channelType,
                conversationId = conversationId,
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                senderUnit = senderUnit,
                content = text,
                attachmentType = attachmentType,
                attachmentUrl = attachmentUrl,
                attachmentName = attachmentName
            )
        }
    }

    // --- Broadcasts & Bulletins ---
    fun publishBroadcast(
        title: String,
        category: String,
        priority: String,
        content: String,
        targetAudience: String,
        isPinned: Boolean = false
    ) {
        val author = _activeResident.value?.fullName ?: "Col. Davies (CSO Command)"
        val role = if (_activeResident.value != null) "RESIDENT" else "ESTATE_ADMIN"

        viewModelScope.launch {
            repository.publishBroadcast(
                title = title,
                category = category,
                priority = priority,
                authorName = author,
                authorRole = role,
                content = content,
                targetAudience = targetAudience,
                isPinned = isPinned
            )
            _bannerMessage.value = "📢 Estate notification [$priority] broadcasted to $targetAudience"
        }
    }

    fun acknowledgeBroadcast(broadcastId: String) {
        viewModelScope.launch {
            repository.acknowledgeBroadcast(broadcastId)
        }
    }

    // --- Fee Collection / Invoices ---
    fun recordInvoicePayment(invoice: EstateFeeInvoiceEntity, method: String) {
        viewModelScope.launch {
            val updated = repository.payInvoice(invoice, method)
            _bannerMessage.value = "💳 Settled ₦${"%,.2f".format(invoice.amount)} for ${invoice.feeTitle}. Receipt: ${updated.receiptNumber}"
        }
    }

    fun createInvoice(
        resident: ResidentAccountEntity,
        title: String,
        category: String,
        amount: Double,
        period: String,
        dueDateDays: Int
    ) {
        viewModelScope.launch {
            val dueEpoch = System.currentTimeMillis() + (dueDateDays * 86400000L)
            repository.createFeeInvoice(
                residentId = resident.id,
                residentName = resident.fullName,
                unitNumber = resident.unitNumber,
                feeTitle = title,
                category = category,
                amount = amount,
                period = period,
                dueDateEpoch = dueEpoch
            )
            _bannerMessage.value = "🧾 Levy invoice of ₦${"%,.2f".format(amount)} issued to ${resident.fullName} (${resident.unitNumber})"
        }
    }

    // --- Meetings & Community Hub ---
    fun scheduleMeeting(
        title: String,
        type: String,
        agenda: String,
        scheduledEpoch: Long,
        durationMinutes: Int,
        location: String
    ) {
        viewModelScope.launch {
            val host = _activeResident.value?.fullName ?: "Estate Security Directorate"
            repository.createMeeting(
                title = title,
                description = location,
                category = type,
                scheduledEpoch = scheduledEpoch,
                durationMinutes = durationMinutes,
                agendaItems = agenda,
                hostName = host
            )
            _bannerMessage.value = "🗓️ Meeting scheduled: '$title'"
        }
    }

    fun postMeetingContribution(
        meetingId: String,
        message: String,
        isHandRaised: Boolean = false,
        voteChoice: String = ""
    ) {
        val authorName = _activeResident.value?.fullName ?: _activeGuard.value?.fullName ?: "Estate Admin"
        val authorRole = when {
            _activeResident.value != null -> "RESIDENT"
            _activeGuard.value != null -> "GUARD"
            else -> "ADMIN"
        }
        val unit = _activeResident.value?.unitNumber ?: "HQ"

        viewModelScope.launch {
            repository.postMeetingContribution(
                meetingId = meetingId,
                authorName = authorName,
                authorRole = authorRole,
                unitNumber = unit,
                message = message,
                isHandRaised = isHandRaised,
                voteChoice = voteChoice
            )
        }
    }

    fun voteMeetingPoll(poll: MeetingPollEntity, optionIndex: Int) {
        val actor = _activeResident.value?.fullName ?: "Resident"
        viewModelScope.launch {
            repository.votePoll(poll, optionIndex, actor)
            _bannerMessage.value = "🗳️ Vote successfully recorded for '${poll.question.take(30)}...'"
        }
    }

    // --- Complaints & Service Tickets ---
    fun submitComplaint(
        title: String,
        category: String,
        severity: String,
        description: String,
        imageAttachmentUrl: String = ""
    ) {
        val res = _activeResident.value
        val resId = res?.id ?: "admin_ticket"
        val resName = res?.fullName ?: "Resident"
        val resUnit = res?.unitNumber ?: "General"
        val resPhone = res?.phone ?: ""

        viewModelScope.launch {
            val ticket = repository.submitComplaint(
                residentId = resId,
                residentName = resName,
                unitNumber = resUnit,
                phone = resPhone,
                title = title,
                category = category,
                severity = severity,
                description = description,
                imageAttachmentUrl = imageAttachmentUrl
            )
            _bannerMessage.value = "🎫 Issue ticket ${ticket.ticketCode} submitted. Estate team notified."
        }
    }

    fun resolveComplaint(complaintId: String, status: String, response: String) {
        val actor = _activeResident.value?.fullName ?: "Estate Operations Lead"
        viewModelScope.launch {
            repository.resolveComplaint(complaintId, status, response, actor)
            _bannerMessage.value = "✅ Ticket status updated to $status"
        }
    }

    // --- Resident Profile Update ---
    fun updateResidentProfile(
        resident: ResidentAccountEntity?,
        fullName: String,
        unitNumber: String,
        phone: String,
        email: String,
        registeredVehicles: String,
        emergencyContact: String
    ) {
        val target = resident ?: _activeResident.value ?: return
        viewModelScope.launch {
            val updated = target.copy(
                fullName = fullName,
                unitNumber = unitNumber,
                phone = phone,
                email = email,
                registeredVehicles = registeredVehicles,
                emergencyContact = emergencyContact
            )
            repository.updateResidentAccount(updated)
            if (_activeResident.value?.id == target.id) {
                _activeResident.value = updated
            }
            _bannerMessage.value = "👤 Resident profile updated successfully for $fullName"
        }
    }

    // --- Guard Scanner & Keypad ---
    fun setScannerInput(input: String) {
        _scannerInput.value = input
    }

    fun appendKeypad(char: Char) {
        if (_scannerInput.value.length < 16) {
            _scannerInput.value += char
        }
    }

    fun appendKeypad(digit: String) {
        if (_scannerInput.value.length < 16) {
            _scannerInput.value += digit
        }
    }

    fun backspaceKeypad() {
        if (_scannerInput.value.isNotEmpty()) {
            _scannerInput.value = _scannerInput.value.dropLast(1)
        }
    }

    fun clearScanner() {
        _scannerInput.value = ""
        _verificationResult.value = VerificationResult.Idle
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            _verificationResult.value = repository.verifyAccess(code, _selectedGate.value)
        }
    }

    fun approveEntry(
        pass: VisitorPassEntity,
        actualOccupants: Int,
        notes: String,
        signature: String?
    ) {
        viewModelScope.launch {
            val guardName = _activeGuard.value?.fullName ?: "Officer Duty"
            val success = repository.allowEntry(
                passId = pass.id,
                actualOccupants = actualOccupants,
                guardName = guardName,
                gateName = _selectedGate.value,
                notes = notes,
                signature = signature
            )
            if (success) {
                _bannerMessage.value = "✅ ENTRY APPROVED for ${pass.visitorName} ($actualOccupants occupants)"
                clearScanner()
            }
        }
    }

    fun recordExit(
        pass: VisitorPassEntity,
        updatedItems: List<DeclaredItemEntity>,
        notes: String,
        signature: String?
    ) {
        viewModelScope.launch {
            val guardName = _activeGuard.value?.fullName ?: "Officer Duty"
            val success = repository.recordExit(
                passId = pass.id,
                updatedItems = updatedItems,
                guardName = guardName,
                gateName = _selectedGate.value,
                notes = notes,
                signature = signature
            )
            if (success) {
                _bannerMessage.value = "🏁 EXIT RECORDED for ${pass.visitorName}. Visit finalized."
                clearScanner()
            }
        }
    }

    fun denyAccessManually(reason: String) {
        _verificationResult.value = VerificationResult.Denied(reason = reason)
        _bannerMessage.value = "⛔ ACCESS DENIED: $reason"
    }

    fun revokePass(passId: String, reason: String) {
        val actor = _activeResident.value?.fullName ?: _activeGuard.value?.fullName ?: "Estate Admin"
        val role = when {
            _activeResident.value != null -> "RESIDENT"
            _activeGuard.value != null -> "SECURITY_GUARD"
            else -> "ESTATE_ADMIN"
        }
        viewModelScope.launch {
            repository.revokePass(passId, reason, actor, role)
            _bannerMessage.value = "🚫 Pass revoked: $reason"
        }
    }

    fun createVisitorPass(
        visitorName: String,
        phone: String,
        visitorType: PassType,
        visitPurpose: String,
        expectedOccupants: Int,
        vehiclePlate: String,
        vehicleMakeModel: String,
        vehicleColor: String,
        driverName: String,
        allowedGate: String,
        validDurationHours: Int,
        specialInstructions: String,
        declaredItems: List<Pair<String, String>>
    ) {
        val hostName = _activeResident.value?.fullName ?: "Resident Host"
        val propertyUnit = _activeResident.value?.unitNumber ?: "Villa 14B"

        viewModelScope.launch {
            val pass = repository.createPass(
                visitorName = visitorName,
                phone = phone,
                hostResidentName = hostName,
                propertyUnit = propertyUnit,
                visitorType = visitorType,
                visitPurpose = visitPurpose,
                expectedOccupants = expectedOccupants,
                vehiclePlate = vehiclePlate,
                vehicleMakeModel = vehicleMakeModel,
                vehicleColor = vehicleColor,
                driverName = driverName,
                allowedGate = allowedGate,
                validDurationHours = validDurationHours,
                specialInstructions = specialInstructions,
                items = declaredItems
            )
            _selectedPass.value = pass
            _currentPortal.value = CurrentPortal.VisitorPassPortal
            _bannerMessage.value = "✨ Digital Pass #${pass.pinCode} generated for ${pass.visitorName}"
        }
    }

    fun submitIncident(
        title: String,
        category: String,
        severity: String,
        visitorName: String,
        vehiclePlate: String,
        description: String
    ) {
        val guardName = _activeGuard.value?.fullName ?: "Security Officer"
        viewModelScope.launch {
            val incident = repository.reportIncident(
                title = title,
                category = category,
                severity = severity,
                guardName = guardName,
                gateName = _selectedGate.value,
                visitorName = visitorName,
                vehiclePlate = vehiclePlate,
                description = description
            )
            _bannerMessage.value = "⚠️ Incident logged [${incident.incidentCode}]: $title"
        }
    }

    fun resolveIncident(incident: IncidentEntity, resolutionNotes: String) {
        val adminName = "Estate Admin"
        viewModelScope.launch {
            repository.resolveIncident(incident, resolutionNotes, adminName)
            _bannerMessage.value = "✅ Incident ${incident.incidentCode} marked as resolved."
        }
    }

    fun executeEmergencyOverride(
        serviceType: String,
        vehiclePlate: String,
        occupantCount: Int,
        notes: String
    ) {
        val guardName = _activeGuard.value?.fullName ?: "Gate Officer"
        viewModelScope.launch {
            val pass = repository.emergencyOverrideGate(
                serviceType = serviceType,
                vehiclePlate = vehiclePlate,
                occupantCount = occupantCount,
                guardName = guardName,
                gateName = _selectedGate.value,
                notes = notes
            )
            _bannerMessage.value = "🚨 EMERGENCY OVERRIDE: Gate raised for ${pass.visitorName}"
        }
    }

    fun togglePolicy(policyKey: String, isEnabled: Boolean) {
        val admin = "Estate Admin"
        viewModelScope.launch {
            repository.updatePolicy(policyKey, isEnabled, admin)
            _bannerMessage.value = "⚙️ Security policy '$policyKey' updated."
        }
    }

    fun createSecurityGate(
        gateName: String,
        gateCode: String,
        estateName: String,
        location: String,
        operatingHours: String,
        isPrimary: Boolean
    ) {
        viewModelScope.launch {
            val gate = repository.createSecurityGate(gateName, gateCode, estateName, location, operatingHours, isPrimary)
            _bannerMessage.value = "🚪 Provisioned new security gate post: ${gate.gateName}"
        }
    }

    fun toggleGateStatus(gateId: String, currentStatus: String) {
        val newStatus = if (currentStatus == "OPERATIONAL") "RESTRICTED" else "OPERATIONAL"
        viewModelScope.launch {
            repository.updateGateStatus(gateId, newStatus)
            _bannerMessage.value = "🚪 Gate status changed to $newStatus"
        }
    }

    fun createResidentAccount(
        fullName: String,
        unitNumber: String,
        estateName: String,
        primaryGate: String,
        phone: String,
        email: String,
        passcode: String,
        registeredVehicles: String
    ) {
        viewModelScope.launch {
            val res = repository.createResidentAccount(
                fullName = fullName,
                unitNumber = unitNumber,
                estateName = estateName,
                primaryGate = primaryGate,
                phone = phone,
                email = email,
                passcode = passcode,
                registeredVehicles = registeredVehicles
            )
            _bannerMessage.value = "🏡 Provisioned residence access for ${res.fullName} (${res.unitNumber})"
        }
    }

    fun deleteResidentAccount(resident: ResidentAccountEntity) {
        viewModelScope.launch {
            repository.deleteResidentAccount(resident)
            _bannerMessage.value = "🗑️ Deleted resident account for ${resident.fullName}"
        }
    }

    fun createGuardAccount(
        fullName: String,
        badgeId: String,
        gate: String,
        shift: String,
        phone: String
    ) {
        viewModelScope.launch {
            val guard = repository.createGuardAccount(fullName, badgeId, gate, shift, phone)
            _bannerMessage.value = "🛡️ Provisioned officer ${guard.fullName} [${guard.badgeId}] at $gate"
        }
    }

    fun deleteGuardAccount(guard: GuardAccountEntity) {
        viewModelScope.launch {
            repository.deleteGuardAccount(guard)
            _bannerMessage.value = "🗑️ Removed guard profile for ${guard.fullName}"
        }
    }

    // =========================================================================
    // GUSH CONNECT — UNIVERSAL ACCESS CONTROL INTEGRATION GATEWAY & HUB
    // =========================================================================
    val integrationHub = GushSecurityIntegrationHub()
    val integrationConnectors: StateFlow<List<IntegrationConnectorConfig>> = integrationHub.connectors
    val registeredDevices: StateFlow<List<HardwareDeviceProfile>> = integrationHub.devices
    val databaseBridges: StateFlow<List<DatabaseBridgeSpec>> = integrationHub.dbBridges
    val automationRules: StateFlow<List<AutomationRule>> = integrationHub.automationRules
    val liveIntegrationEvents: StateFlow<List<GushSecurityEvent>> = integrationHub.eventBus.recentEvents
    val commandExecutionLog: StateFlow<List<GushSecurityCommand>> = integrationHub.commandBus.recentCommands

    private val _integrationStatusMessage = MutableStateFlow<String?>(null)
    val integrationStatusMessage: StateFlow<String?> = _integrationStatusMessage.asStateFlow()

    fun dismissIntegrationStatus() {
        _integrationStatusMessage.value = null
    }

    fun toggleConnectorState(connectorId: String) {
        integrationHub.toggleConnector(connectorId)
    }

    fun testConnectorConnection(connectorId: String) {
        viewModelScope.launch {
            val probeResult = integrationHub.testConnectorConnection(connectorId)
            _integrationStatusMessage.value = "🔍 Connector Probe: $probeResult"
            _bannerMessage.value = "⚡ Connector verified successfully"
        }
    }

    fun testDeviceConnection(deviceId: String) {
        viewModelScope.launch {
            val result = integrationHub.testDevice(deviceId)
            _integrationStatusMessage.value = if (result.isSuccess) {
                "✅ [Device Probe OK] ${result.message} (${result.responseTimeMs}ms)"
            } else {
                "⚠️ [Device Offline/Error] ${result.message}"
            }
            _bannerMessage.value = if (result.isSuccess) "📡 Hardware device online" else "⚠️ Device unreachable"
        }
    }

    fun executeRemoteHardwareCommand(
        commandType: String,
        targetDeviceId: String,
        targetGateName: String,
        parameters: Map<String, String> = emptyMap()
    ) {
        viewModelScope.launch {
            val result = integrationHub.executeCommand(
                commandType = commandType,
                targetDeviceId = targetDeviceId,
                targetGateName = targetGateName,
                actorId = _activeGuard.value?.badgeId ?: _activeResident.value?.fullName ?: "ADMIN_OP",
                actorRole = if (_activeGuard.value != null) "GUARD_OFFICER" else "ADMIN_SUPERVISOR",
                parameters = parameters
            )
            _integrationStatusMessage.value = if (result.isSuccess) {
                "⚡ [Command Executed] ${result.message} (${result.responseTimeMs}ms)"
            } else {
                "🛑 [Command Rejected] ${result.message}"
            }
            _bannerMessage.value = if (result.isSuccess) "Gate barrier actuated" else "Command rejected by Security Policy"
        }
    }

    fun testDatabaseBridge(spec: DatabaseBridgeSpec) {
        viewModelScope.launch {
            val result = integrationHub.dbBridgeService.testBridgeConnection(spec)
            _integrationStatusMessage.value = if (result.isSuccess) {
                "🗄️ [DB Bridge OK] ${result.message} (${result.latencyMs}ms)"
            } else {
                "🛑 [DB Bridge Failed] Unable to connect to ${spec.hostAddress}"
            }
        }
    }

    fun triggerDatabaseSync(spec: DatabaseBridgeSpec) {
        viewModelScope.launch {
            val sync = integrationHub.dbBridgeService.triggerManualSync(spec)
            _integrationStatusMessage.value = "🔄 [DB Synced] Ingested: ${sync.recordsIngested}, Exported: ${sync.recordsExported} (${sync.durationMs}ms)"
            _bannerMessage.value = "Synced ${sync.recordsIngested} passes from external DB"
        }
    }

    fun testAutomationRule(rule: AutomationRule) {
        viewModelScope.launch {
            val result = integrationHub.automationService.testAutomationRule(rule)
            _integrationStatusMessage.value = "⚡ [Automation Webhook] ${result.message}"
            _bannerMessage.value = "Dispatched test trigger to ${rule.platform.platformName}"
        }
    }

    fun registerNewConnector(
        name: String,
        connectionType: ConnectionType,
        endpointUrl: String,
        authType: AuthType,
        permissions: Set<IntegrationPermission>
    ) {
        integrationHub.addConnector(name, connectionType, endpointUrl, authType, permissions)
        _bannerMessage.value = "🔌 Registered new connector: $name"
    }

    fun updateConnectorEndpoint(connectorId: String, newEndpointUrl: String, reason: String = "Admin update") {
        val success = integrationHub.updateConnectorEndpoint(
            connectorId = connectorId,
            newEndpointUrl = newEndpointUrl,
            actor = _activeGuard.value?.badgeId ?: _activeResident.value?.fullName ?: "ADMIN",
            reason = reason
        )
        if (success) {
            _integrationStatusMessage.value = "🌐 [Endpoint Updated] Switched to $newEndpointUrl"
            _bannerMessage.value = "Updated connector endpoint URL"
        }
    }

    fun rotateConnectorCredentials(connectorId: String) {
        val newKey = integrationHub.rotateConnectorCredentials(
            connectorId = connectorId,
            actor = _activeGuard.value?.badgeId ?: _activeResident.value?.fullName ?: "ADMIN"
        )
        _integrationStatusMessage.value = "🔑 [Credentials Rotated] New key generated: ${newKey.take(14)}..."
        _bannerMessage.value = "Rotated API key and HMAC secret"
    }

    fun deleteConnector(connectorId: String) {
        val success = integrationHub.deleteConnector(
            connectorId = connectorId,
            actor = _activeGuard.value?.badgeId ?: _activeResident.value?.fullName ?: "ADMIN"
        )
        if (success) {
            _integrationStatusMessage.value = "🗑️ [Connector Removed] Integration disconnected"
            _bannerMessage.value = "Connector removed from Hub"
        }
    }

    fun registerNewDevice(
        name: String,
        deviceType: HardwareDeviceType,
        manufacturer: String,
        modelNumber: String,
        ipAddress: String,
        port: Int,
        location: String,
        assignedGateName: String
    ) {
        integrationHub.addDevice(name, deviceType, manufacturer, modelNumber, ipAddress, port, location, assignedGateName)
        _bannerMessage.value = "🛰️ Enrolled hardware device: $name"
    }
}

class EstateSecurityViewModelFactory(private val repository: EstateSecurityRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstateSecurityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstateSecurityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
