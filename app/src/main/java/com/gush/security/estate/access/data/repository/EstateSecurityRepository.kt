package com.gush.security.estate.access.data.repository

import com.gush.security.estate.access.data.local.dao.EstateSecurityDao
import com.gush.security.estate.access.data.local.entities.AuditLogEntity
import com.gush.security.estate.access.data.local.entities.DeclaredItemEntity
import com.gush.security.estate.access.data.local.entities.EstateBroadcastEntity
import com.gush.security.estate.access.data.local.entities.EstateFeeInvoiceEntity
import com.gush.security.estate.access.data.local.entities.FeeStatus
import com.gush.security.estate.access.data.local.entities.EstateMeetingEntity
import com.gush.security.estate.access.data.local.entities.EstateMessageEntity
import com.gush.security.estate.access.data.local.entities.FamilyMemberEntity
import com.gush.security.estate.access.data.local.entities.GateEventEntity
import com.gush.security.estate.access.data.local.entities.GuardAccountEntity
import com.gush.security.estate.access.data.local.entities.IncidentEntity
import com.gush.security.estate.access.data.local.entities.MeetingContributionEntity
import com.gush.security.estate.access.data.local.entities.MeetingPollEntity
import com.gush.security.estate.access.data.local.entities.MessageAttachmentType
import com.gush.security.estate.access.data.local.entities.MessageChannelType
import com.gush.security.estate.access.data.local.entities.PassStatus
import com.gush.security.estate.access.data.local.entities.PassType
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentComplaintEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.data.local.entities.SecurityPolicyEntity
import com.gush.security.estate.access.data.local.entities.VisitorPassEntity
import com.gush.security.estate.access.security.SecurityUtils
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class VerificationResult {
    data object Idle : VerificationResult()
    data class Approved(
        val pass: VisitorPassEntity,
        val declaredItems: List<DeclaredItemEntity>,
        val warnings: List<String> = emptyList()
    ) : VerificationResult()
    data class Denied(
        val reason: String,
        val pass: VisitorPassEntity? = null
    ) : VerificationResult()
}

data class ActiveCallSession(
    val callId: String = UUID.randomUUID().toString(),
    val callerName: String,
    val callerRole: String, // RESIDENT, GUARD, ADMIN
    val callerUnit: String,
    val receiverName: String,
    val receiverRole: String,
    val receiverUnit: String,
    val isVideo: Boolean = false,
    val state: CallState = CallState.RINGING, // RINGING, CONNECTED, ENDED
    val startTimestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isVideoFrontFacing: Boolean = true,
    val gatePostName: String = ""
)

enum class CallState {
    RINGING,
    CONNECTED,
    ENDED
}

class EstateSecurityRepository(private val dao: EstateSecurityDao) {

    val allPasses: Flow<List<VisitorPassEntity>> = dao.getAllPasses()
    val activeInsidePasses: Flow<List<VisitorPassEntity>> = dao.getActiveInsidePasses()
    val recentGateEvents: Flow<List<GateEventEntity>> = dao.getRecentGateEvents()
    val incidents: Flow<List<IncidentEntity>> = dao.getAllIncidents()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAuditLogs()
    val policies: Flow<List<SecurityPolicyEntity>> = dao.getAllPolicies()
    val allSecurityGates: Flow<List<SecurityGateEntity>> = dao.getAllSecurityGates()
    val allResidents: Flow<List<ResidentAccountEntity>> = dao.getAllResidents()
    val allGuards: Flow<List<GuardAccountEntity>> = dao.getAllGuards()

    // New flows
    val allFamilyMembers: Flow<List<FamilyMemberEntity>> = dao.getAllFamilyMembers()
    val allMessages: Flow<List<EstateMessageEntity>> = dao.getAllMessages()
    val allBroadcasts: Flow<List<EstateBroadcastEntity>> = dao.getAllBroadcasts()
    val allInvoices: Flow<List<EstateFeeInvoiceEntity>> = dao.getAllInvoices()
    val allMeetings: Flow<List<EstateMeetingEntity>> = dao.getAllMeetings()
    val allComplaints: Flow<List<ResidentComplaintEntity>> = dao.getAllComplaints()

    fun getItemsForPass(passId: String): Flow<List<DeclaredItemEntity>> {
        return dao.getItemsForPass(passId)
    }

    fun getFamilyMembersForResident(residentId: String): Flow<List<FamilyMemberEntity>> {
        return dao.getFamilyMembersForResident(residentId)
    }

    fun getMessagesForConversation(convId: String, channelType: String): Flow<List<EstateMessageEntity>> {
        return dao.getMessagesForConversation(convId, channelType)
    }

    fun getInvoicesForResident(residentId: String): Flow<List<EstateFeeInvoiceEntity>> {
        return dao.getInvoicesForResident(residentId)
    }

    fun getContributionsForMeeting(meetingId: String): Flow<List<MeetingContributionEntity>> {
        return dao.getContributionsForMeeting(meetingId)
    }

    fun getPollsForMeeting(meetingId: String): Flow<List<MeetingPollEntity>> {
        return dao.getPollsForMeeting(meetingId)
    }

    fun getComplaintsForResident(residentId: String): Flow<List<ResidentComplaintEntity>> {
        return dao.getComplaintsForResident(residentId)
    }

    suspend fun initializeSeedDataIfEmpty() {
        val latestLog = dao.getLatestAuditLog()
        if (latestLog == null) {
            val now = System.currentTimeMillis()
            val hourMs = 3600000L

            // Genesis Audit Log
            val genesisHash = SecurityUtils.calculateEventHash(
                "0000000000000000000000000000000000000000000000000000000000000000",
                "GENESIS_BLOCK|ESTATE_SECURITY_INITIALIZED|Gushed Systems"
            )
            dao.insertAuditLog(
                AuditLogEntity(
                    eventId = UUID.randomUUID().toString(),
                    actor = "System",
                    role = "SYSTEM",
                    action = "INITIALIZE_SYSTEM",
                    resource = "Pinnock Beach Estate Security Suite",
                    result = "SUCCESS",
                    previousHash = "0000000000000000000000000000000000000000000000000000000000000000",
                    currentHash = genesisHash,
                    details = "Gushed Systems Security operations initialized with SHA-256 tamper-evident chaining."
                )
            )

            // Seed Security Gates
            val defaultGates = listOf(
                SecurityGateEntity(
                    id = "gate-main-01",
                    gateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                    gateCode = "PBE-GT01",
                    estateName = "Pinnock Beach Estate",
                    location = "Pinnock Boulevard Main Access, Lekki",
                    operatingHours = "24 Hours / 7 Days",
                    status = "OPERATIONAL",
                    assignedGuardStaff = "Officer Yakubu Danladi, Officer Sarah Bello",
                    isPrimaryGate = true
                ),
                SecurityGateEntity(
                    id = "gate-north-02",
                    gateName = "Gate 2 - North Access Gate",
                    gateCode = "PBE-GT02",
                    estateName = "Pinnock Beach Estate",
                    location = "North Perimeter Road & Expressway Link",
                    operatingHours = "06:00 - 22:00",
                    status = "OPERATIONAL",
                    assignedGuardStaff = "Officer James Okonjo",
                    isPrimaryGate = false
                ),
                SecurityGateEntity(
                    id = "gate-water-03",
                    gateName = "Gate 3 - Waterfront & Pier Gate",
                    gateCode = "PBE-GT03",
                    estateName = "Pinnock Beach Estate",
                    location = "Lekki Lagoon Shoreline & Jetty Access",
                    operatingHours = "06:00 - 20:00",
                    status = "OPERATIONAL",
                    assignedGuardStaff = "Officer Emmanuel Kalu",
                    isPrimaryGate = false
                ),
                SecurityGateEntity(
                    id = "gate-service-04",
                    gateName = "Gate 4 - Service & Contractor Gate",
                    gateCode = "PBE-GT04",
                    estateName = "Pinnock Beach Estate",
                    location = "Utility Loop & Heavy Equipment Transit",
                    operatingHours = "07:00 - 18:00 (Mon-Sat)",
                    status = "OPERATIONAL",
                    assignedGuardStaff = "Duty Heavy Vehicle Inspection Team",
                    isPrimaryGate = false
                )
            )
            dao.insertSecurityGates(defaultGates)

            // Seed Resident Accounts
            val defaultResidents = listOf(
                ResidentAccountEntity(
                    id = "res-001",
                    fullName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    estateName = "Pinnock Beach Estate",
                    primaryGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                    phone = "+234 803 555 0192",
                    email = "a.balogun@pinnockestate.ng",
                    passcode = "1234",
                    status = "ACTIVE",
                    registeredVehicles = "LAG-849-XY, KJA-992-ZZ",
                    emergencyContact = "+234 802 000 1122"
                ),
                ResidentAccountEntity(
                    id = "res-002",
                    fullName = "Dr. Amina Bello",
                    unitNumber = "Villa 09, Coral Reef Way",
                    estateName = "Pinnock Beach Estate",
                    primaryGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                    phone = "+234 802 444 8811",
                    email = "amina.bello@lagosmed.org",
                    passcode = "1234",
                    status = "ACTIVE",
                    registeredVehicles = "ABJ-501-AA",
                    emergencyContact = "+234 803 999 4433"
                ),
                ResidentAccountEntity(
                    id = "res-003",
                    fullName = "Engr. Tunde Williams",
                    unitNumber = "Penthouse 3B, Lagoon Heights",
                    estateName = "Pinnock Beach Estate",
                    primaryGate = "Gate 2 - North Access Gate",
                    phone = "+234 812 777 3300",
                    email = "tunde.w@techgroup.africa",
                    passcode = "1234",
                    status = "ACTIVE",
                    registeredVehicles = "EKY-402-BC, APP-110-LA",
                    emergencyContact = "+234 809 111 2244"
                ),
                ResidentAccountEntity(
                    id = "res-004",
                    fullName = "Mrs. Folake Craig",
                    unitNumber = "House 07, Jasmine Close",
                    estateName = "Pinnock Beach Estate",
                    primaryGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                    phone = "+234 701 992 3341",
                    email = "folake.craig@craiglaw.com",
                    passcode = "1234",
                    status = "ACTIVE",
                    registeredVehicles = "LSR-773-XD",
                    emergencyContact = "+234 805 333 7788"
                )
            )
            dao.insertResidents(defaultResidents)

            // Seed Security Guard Accounts
            val defaultGuards = listOf(
                GuardAccountEntity(
                    id = "grd-001",
                    fullName = "Officer Yakubu Danladi",
                    badgeId = "GSD-7021",
                    assignedGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                    shift = "Day Shift (06:00 - 18:00)",
                    phone = "+234 809 443 1120",
                    passcode = "0000",
                    status = "ON_DUTY",
                    supervisorName = "CSO Col. Davies (Rtd)"
                ),
                GuardAccountEntity(
                    id = "grd-002",
                    fullName = "Officer Sarah Bello",
                    badgeId = "GSD-8104",
                    assignedGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                    shift = "Night Shift (18:00 - 06:00)",
                    phone = "+234 808 112 9940",
                    passcode = "0000",
                    status = "ON_DUTY",
                    supervisorName = "CSO Col. Davies (Rtd)"
                ),
                GuardAccountEntity(
                    id = "grd-003",
                    fullName = "Officer James Okonjo",
                    badgeId = "GSD-9330",
                    assignedGate = "Gate 2 - North Access Gate",
                    shift = "Day Shift (06:00 - 18:00)",
                    phone = "+234 813 550 7712",
                    passcode = "0000",
                    status = "ON_DUTY",
                    supervisorName = "CSO Col. Davies (Rtd)"
                ),
                GuardAccountEntity(
                    id = "grd-004",
                    fullName = "Officer Emmanuel Kalu",
                    badgeId = "GSD-4412",
                    assignedGate = "Gate 4 - Service & Contractor Gate",
                    shift = "Day Shift (07:00 - 18:00)",
                    phone = "+234 816 772 8831",
                    passcode = "0000",
                    status = "ON_DUTY",
                    supervisorName = "CSO Col. Davies (Rtd)"
                )
            )
            dao.insertGuards(defaultGuards)

            // Seed Initial Policies
            val defaultPolicies = listOf(
                SecurityPolicyEntity(
                    policyKey = "STRICT_PLATE_MATCH",
                    name = "Strict Vehicle Plate Enforcement",
                    description = "Require guard inspection and warning when entered plate differs from pre-registered plate.",
                    isEnabled = true,
                    category = "GATE_CONTROL"
                ),
                SecurityPolicyEntity(
                    policyKey = "REQUIRE_CONTRACTOR_APPROVAL",
                    name = "Contractor Item Verification",
                    description = "Mandatory property and toolset inspection for all service technicians and builders.",
                    isEnabled = true,
                    category = "CONTRACTOR"
                ),
                SecurityPolicyEntity(
                    policyKey = "DELIVERY_HOLDING_OPT",
                    name = "Delivery Gate Verification",
                    description = "Allows dispatch riders to drop packages at security holding area.",
                    isEnabled = true,
                    category = "GATE_CONTROL"
                ),
                SecurityPolicyEntity(
                    policyKey = "PROPERTY_PROTECTION_MODE",
                    name = "Property Protection Mode",
                    description = "Strict chain-of-custody comparison and owner authorization for high-value asset removal.",
                    isEnabled = true,
                    category = "PROPERTY_PROTECTION"
                )
            )
            dao.insertPolicies(defaultPolicies)

            // Seed Family Members for Chief Adebayo Balogun
            val defaultFamily = listOf(
                FamilyMemberEntity(
                    id = "fam-001",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    fullName = "Chief Mrs. Folashade Balogun",
                    relationship = "WIFE",
                    phone = "+234 803 111 8844",
                    email = "folashade.balogun@pinnockestate.ng",
                    accessLevel = "FULL_ACCESS",
                    pinCode = "990142",
                    qrToken = "GSH-FAM-FOLASHADE-01",
                    vehiclePlate = "KJA-992-ZZ"
                ),
                FamilyMemberEntity(
                    id = "fam-002",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    fullName = "Femi Balogun",
                    relationship = "SON",
                    phone = "+234 802 770 1199",
                    email = "femi.balogun@pinnockestate.ng",
                    accessLevel = "FULL_ACCESS",
                    pinCode = "840219",
                    qrToken = "GSH-FAM-FEMI-02",
                    vehiclePlate = "LAG-552-FX"
                ),
                FamilyMemberEntity(
                    id = "fam-003",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    fullName = "Ibrahim Musa",
                    relationship = "DRIVER",
                    phone = "+234 814 330 9922",
                    accessLevel = "GATE_ONLY",
                    pinCode = "102938",
                    qrToken = "GSH-STAFF-MUSA-03",
                    vehiclePlate = "LAG-849-XY"
                )
            )
            dao.insertFamilyMembers(defaultFamily)

            // Seed Broadcasts / Estate Bulletins
            val defaultBroadcasts = listOf(
                EstateBroadcastEntity(
                    id = "bc-001",
                    title = "Perimeter CCTV Upgrade & Automated ALPR Integration",
                    category = "SECURITY_ALERT",
                    priority = "HIGH",
                    authorName = "Col. Davies (Chief Security Officer)",
                    authorRole = "ESTATE_ADMIN",
                    content = "Estate Security has completed the installation of 16 high-definition night-vision cameras along the northern perimeter fence and waterfront jetty. Security guards at Gate 1 and Gate 2 are now testing automated license plate recognition.",
                    targetAudience = "All Residents",
                    timestamp = now - (hourMs * 2),
                    isPinned = true
                ),
                EstateBroadcastEntity(
                    id = "bc-002",
                    title = "Notice: Q3 Estate General Meeting & Security Review",
                    category = "NOTICE",
                    priority = "NORMAL",
                    authorName = "Pinnock Beach Residents Association (PBRA)",
                    authorRole = "ESTATE_ADMIN",
                    content = "All registered residents and property owners are invited to join the upcoming quarterly meeting. Agenda includes review of estate perimeter security, solar lighting project, and approval of 2026 capital budget.",
                    targetAudience = "All Residents",
                    timestamp = now - (hourMs * 6),
                    isPinned = true
                ),
                EstateBroadcastEntity(
                    id = "bc-003",
                    title = "Drainage Desilting & Vector Fogging Notice",
                    category = "MAINTENANCE",
                    priority = "NORMAL",
                    authorName = "Estate Facility Manager",
                    authorRole = "ESTATE_ADMIN",
                    content = "Routine drainage clearing and mosquito fogging will take place this Saturday across Palm Avenue, Coral Reef Way, and Jasmine Close between 08:00 and 12:00. Please keep all pets indoors.",
                    targetAudience = "All Residents",
                    timestamp = now - (hourMs * 18),
                    isPinned = false
                )
            )
            dao.insertBroadcasts(defaultBroadcasts)

            // Seed Estate Dues / Invoices
            val defaultInvoices = listOf(
                EstateFeeInvoiceEntity(
                    id = "inv-001",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    feeTitle = "Q3 2026 Estate Service Charge & Security Levy",
                    category = "SERVICE_CHARGE",
                    amount = 185000.00,
                    period = "Q3 2026 (Jul - Sep)",
                    dueDateEpoch = now + (hourMs * 120),
                    status = "PENDING"
                ),
                EstateFeeInvoiceEntity(
                    id = "inv-002",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    feeTitle = "Estate Solar Streetlight & Power Substation Levy",
                    category = "SPECIAL_PROJECT",
                    amount = 75000.00,
                    period = "Special Capital Fund 2026",
                    dueDateEpoch = now + (hourMs * 240),
                    status = "PENDING"
                ),
                EstateFeeInvoiceEntity(
                    id = "inv-003",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    feeTitle = "Q2 2026 Estate Service Charge & Waste Disposal",
                    category = "SERVICE_CHARGE",
                    amount = 185000.00,
                    period = "Q2 2026 (Apr - Jun)",
                    dueDateEpoch = now - (hourMs * 720),
                    paidDateEpoch = now - (hourMs * 710),
                    status = "PAID",
                    paymentReference = "TXN-PBE-994821",
                    paymentMethod = "CARD",
                    receiptNumber = "RCP-2026-0814"
                )
            )
            dao.insertInvoices(defaultInvoices)

            // Seed Community Meetings
            val defaultMeetings = listOf(
                EstateMeetingEntity(
                    id = "meet-001",
                    title = "Q3 Estate General Assembly & Security Summit",
                    description = "Quarterly townhall meeting for all residents and estate security directorate. Topics: Gate automation, solar streetlights, and speed regulation.",
                    category = "AGM",
                    scheduledEpoch = now, // Currently LIVE
                    durationMinutes = 90,
                    status = "LIVE",
                    meetingRoomCode = "PBE-LIVE-TOWNHALL",
                    hostName = "Chairman Engr. Tunde & CSO Col. Davies",
                    agendaItems = "1. Welcome & Security Operations Overview\n2. Gate 1 & 2 Automated Barrier Systems\n3. Resident Voting: Solar Streetlights\n4. Open Resident Floor & Comments",
                    activeSpeaker = "Col. Davies (Chief Security Officer)",
                    participantCount = 34
                ),
                EstateMeetingEntity(
                    id = "meet-002",
                    title = "Waterfront & Jetty Security Working Group",
                    description = "Special working committee for residents with waterfront access and boat slip owners.",
                    category = "SECURITY_BRIEFING",
                    scheduledEpoch = now + (hourMs * 72),
                    durationMinutes = 45,
                    status = "UPCOMING",
                    meetingRoomCode = "PBE-WATER-ROOM",
                    hostName = "Officer Emmanuel Kalu & Waterfront Rep",
                    agendaItems = "1. Pier entry protocol\n2. Night boating curfew\n3. CCTV coverage",
                    activeSpeaker = "",
                    participantCount = 12
                )
            )
            dao.insertMeetings(defaultMeetings)

            // Seed Meeting Contributions & Poll
            val defaultContributions = listOf(
                MeetingContributionEntity(
                    id = "contrib-001",
                    meetingId = "meet-001",
                    authorName = "Col. Davies",
                    authorRole = "ADMIN",
                    unitNumber = "Security Command",
                    message = "Good evening residents. Gate 1 ALPR testing has achieved a 98.4% detection accuracy over the past week.",
                    timestamp = now - (hourMs / 4)
                ),
                MeetingContributionEntity(
                    id = "contrib-002",
                    meetingId = "meet-001",
                    authorName = "Dr. Amina Bello",
                    authorRole = "RESIDENT",
                    unitNumber = "Villa 09",
                    message = "Can we also ensure the speed limit of 25km/h on Coral Reef Way is enforced?",
                    timestamp = now - (hourMs / 6)
                ),
                MeetingContributionEntity(
                    id = "contrib-003",
                    meetingId = "meet-001",
                    authorName = "Chief Adebayo Balogun",
                    authorRole = "RESIDENT",
                    unitNumber = "Villa 14B",
                    message = "I fully support the solar streetlight project. It will improve night-time visibility significantly.",
                    timestamp = now - (hourMs / 10)
                )
            )
            defaultContributions.forEach { dao.insertContribution(it) }

            val defaultPoll = MeetingPollEntity(
                id = "poll-001",
                meetingId = "meet-001",
                question = "Should the estate proceed with installing 40 Solar LED Streetlights along Palm Avenue & Boulevard?",
                optionA = "Yes, approve immediately",
                optionB = "Yes, but in phased batches",
                optionC = "No, postpone to next year",
                votesA = 28,
                votesB = 5,
                votesC = 1,
                isOpen = true
            )
            dao.insertPoll(defaultPoll)

            // Seed Messages across Channels
            val defaultMessages = listOf(
                EstateMessageEntity(
                    id = "msg-001",
                    channelType = MessageChannelType.DIRECT_GATE.name,
                    conversationId = "gate_res-001",
                    senderId = "grd-001",
                    senderName = "Officer Yakubu Danladi",
                    senderRole = "GUARD",
                    receiverId = "res-001",
                    receiverName = "Chief Adebayo Balogun",
                    content = "Good day Chief, your visitor Engr. David Okonjo has arrived at Gate 1. Should we grant clearance?",
                    timestamp = now - (hourMs / 3)
                ),
                EstateMessageEntity(
                    id = "msg-002",
                    channelType = MessageChannelType.DIRECT_GATE.name,
                    conversationId = "gate_res-001",
                    senderId = "res-001",
                    senderName = "Chief Adebayo Balogun",
                    senderRole = "RESIDENT",
                    senderUnit = "Villa 14B",
                    receiverId = "grd-001",
                    receiverName = "Officer Yakubu Danladi",
                    content = "Yes Officer, he has a pre-registered digital pass. Please grant clearance. Thank you.",
                    timestamp = now - (hourMs / 4)
                ),
                EstateMessageEntity(
                    id = "msg-003",
                    channelType = MessageChannelType.COMMUNITY_GROUP.name,
                    conversationId = "COMMUNITY_FORUM",
                    senderId = "res-004",
                    senderName = "Mrs. Folake Craig",
                    senderRole = "RESIDENT",
                    senderUnit = "House 07",
                    content = "Hello neighbors! Reminder that the estate children's sports tournament is scheduled for next Saturday at the clubhouse.",
                    timestamp = now - (hourMs * 4)
                ),
                EstateMessageEntity(
                    id = "msg-004",
                    channelType = MessageChannelType.SECURITY_WATCH.name,
                    conversationId = "SECURITY_WATCH",
                    senderId = "grd-002",
                    senderName = "Officer Sarah Bello",
                    senderRole = "GUARD",
                    content = "Perimeter night patrol check completed at 02:00. All gates and motion sensors secure.",
                    timestamp = now - (hourMs * 5)
                )
            )
            dao.insertMessages(defaultMessages)

            // Seed Complaints / Service Tickets
            val defaultComplaints = listOf(
                ResidentComplaintEntity(
                    id = "comp-001",
                    ticketCode = "TCK-2026-081",
                    residentId = "res-001",
                    residentName = "Chief Adebayo Balogun",
                    unitNumber = "Villa 14B, Palm Avenue",
                    phone = "+234 803 555 0192",
                    title = "Streetlight flickering at Palm Avenue Junction",
                    category = "FACILITY",
                    severity = "LOW",
                    description = "The third pole after Villa 14B has a loose LED fixture that flickers at night.",
                    status = "IN_PROGRESS",
                    createdTimestamp = now - (hourMs * 12),
                    adminResponse = "Estate electrical team assigned. Replacement bulb ordered.",
                    assignedOfficer = "Engr. Patrick (Facility Team)"
                ),
                ResidentComplaintEntity(
                    id = "comp-002",
                    ticketCode = "TCK-2026-082",
                    residentId = "res-002",
                    residentName = "Dr. Amina Bello",
                    unitNumber = "Villa 09, Coral Reef Way",
                    phone = "+234 802 444 8811",
                    title = "Late night construction noise from Plot 11",
                    category = "NOISE",
                    severity = "MEDIUM",
                    description = "Heavy machinery operating past the 18:00 estate contractor curfew.",
                    status = "RESOLVED",
                    createdTimestamp = now - (hourMs * 36),
                    resolvedTimestamp = now - (hourMs * 24),
                    adminResponse = "Guard dispatched. Site foreman issued caution and work halted immediately.",
                    assignedOfficer = "Officer Yakubu Danladi"
                )
            )
            dao.insertComplaints(defaultComplaints)

            // Seed Sample Passes
            val pass1 = VisitorPassEntity(
                id = UUID.randomUUID().toString(),
                pinCode = "849201",
                qrToken = "GSH-7A9B-4X2C-8812",
                visitorName = "Engr. David Okonjo",
                phone = "+234 803 555 0192",
                hostResidentName = "Chief Adebayo Balogun",
                propertyUnit = "Villa 14B, Palm Avenue",
                visitorType = PassType.GUEST.name,
                visitPurpose = "Executive Consultation & Dinner",
                expectedOccupants = 2,
                vehiclePlate = "LAG-849-XY",
                vehicleMakeModel = "Mercedes-Benz GLE 450",
                vehicleColor = "Obsidian Black",
                driverName = "Musa Ibrahim",
                allowedGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                validFromEpoch = now - (hourMs / 2),
                validUntilEpoch = now + (hourMs * 4),
                status = PassStatus.SCHEDULED.name,
                specialInstructions = "VIP Guest. Grant priority gatehouse access."
            )
            dao.insertPass(pass1)
            dao.insertItems(
                listOf(
                    DeclaredItemEntity(
                        passId = pass1.id,
                        itemName = "MacBook Pro 16\"",
                        category = "Electronics",
                        serialNumber = "C02XYZ8901",
                        quantity = 1
                    ),
                    DeclaredItemEntity(
                        passId = pass1.id,
                        itemName = "Leather Briefcase",
                        category = "Luggage",
                        serialNumber = "N/A",
                        quantity = 1
                    )
                )
            )

            // 2. Currently Inside Pass (Contractor)
            val pass2 = VisitorPassEntity(
                id = UUID.randomUUID().toString(),
                pinCode = "319458",
                qrToken = "GSH-3C8D-91FA-2041",
                visitorName = "Tunde Adeleke (AC Tech)",
                phone = "+234 812 400 9988",
                hostResidentName = "Chief Adebayo Balogun",
                propertyUnit = "Villa 14B, Palm Avenue",
                visitorType = PassType.CONTRACTOR.name,
                visitPurpose = "Central AC Maintenance & Filter Replacement",
                expectedOccupants = 3,
                actualOccupants = 3,
                vehiclePlate = "ABJ-204-TR",
                vehicleMakeModel = "Toyota HiAce Van",
                vehicleColor = "White",
                driverName = "Tunde Adeleke",
                allowedGate = "Gate 4 - Service & Contractor Gate",
                validFromEpoch = now - (hourMs * 2),
                validUntilEpoch = now + (hourMs * 3),
                status = PassStatus.ACTIVE_INSIDE.name,
                entryTimeEpoch = now - (hourMs * 1),
                guardNotes = "Checked in at Gate 4. Equipment inspected."
            )
            dao.insertPass(pass2)
            dao.insertItems(
                listOf(
                    DeclaredItemEntity(
                        passId = pass2.id,
                        itemName = "Industrial Vacuum Pump",
                        category = "Equipment",
                        serialNumber = "VP-99321",
                        quantity = 1
                    ),
                    DeclaredItemEntity(
                        passId = pass2.id,
                        itemName = "Refrigerant Gas Tank R410A",
                        category = "Tools",
                        serialNumber = "TANK-84",
                        quantity = 2
                    )
                )
            )

            // 3. Delivery Pass
            val pass3 = VisitorPassEntity(
                id = UUID.randomUUID().toString(),
                pinCode = "572190",
                qrToken = "GSH-9F12-7B44-5501",
                visitorName = "Samuel Kwesi (DHL Express)",
                phone = "+234 701 992 3341",
                hostResidentName = "Mrs. Folake Craig",
                propertyUnit = "House 07, Jasmine Close",
                visitorType = PassType.DELIVERY.name,
                visitPurpose = "Express Package Delivery",
                expectedOccupants = 1,
                vehiclePlate = "KJA-119-BB",
                vehicleMakeModel = "TVS Motorcycle",
                vehicleColor = "Red",
                driverName = "Samuel Kwesi",
                allowedGate = "Gate 1 - Pinnock Beach Estate Main Gate",
                validFromEpoch = now - (hourMs / 3),
                validUntilEpoch = now + (hourMs * 2),
                status = PassStatus.SCHEDULED.name,
                specialInstructions = "Leave package at Security holding area if recipient unavailable."
            )
            dao.insertPass(pass3)
            dao.insertItems(
                listOf(
                    DeclaredItemEntity(
                        passId = pass3.id,
                        itemName = "Express Parcel (Electronics)",
                        category = "Package",
                        serialNumber = "WAYBILL-DHL-8921",
                        quantity = 2
                    )
                )
            )

            // Record initial gate event
            dao.insertGateEvent(
                GateEventEntity(
                    passId = pass2.id,
                    visitorName = pass2.visitorName,
                    hostResident = pass2.hostResidentName,
                    gateName = "Gate 4 - Service & Contractor Gate",
                    guardName = "Officer Emmanuel Kalu",
                    eventType = "CHECK_IN",
                    timestamp = now - (hourMs * 1),
                    vehiclePlate = "ABJ-204-TR",
                    occupantCount = 3,
                    decisionNote = "Authorized entry. 2 items declared and verified."
                )
            )

            // Seed Initial Incident
            dao.insertIncident(
                IncidentEntity(
                    incidentCode = "INC-2026-001",
                    title = "Attempted Unregistered Vehicle Entry",
                    category = "Vehicle Mismatch",
                    severity = "LOW",
                    guardName = "Officer Yakubu Danladi",
                    gateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                    visitorName = "Unknown Courier",
                    vehiclePlate = "LND-901-AA",
                    description = "Rider arrived claiming delivery for House 12 without valid resident pass. Access was denied in accordance with estate security protocol.",
                    status = "RESOLVED",
                    timestamp = now - (hourMs * 3),
                    resolutionNotes = "Driver redirected to contact host resident for dynamic pass generation."
                )
            )
        }
    }

    // --- Admin Gate Management ---
    suspend fun createSecurityGate(
        gateName: String,
        gateCode: String,
        estateName: String,
        location: String,
        operatingHours: String,
        isPrimaryGate: Boolean
    ): SecurityGateEntity {
        val gate = SecurityGateEntity(
            id = UUID.randomUUID().toString(),
            gateName = gateName,
            gateCode = gateCode,
            estateName = estateName,
            location = location,
            operatingHours = operatingHours,
            status = "OPERATIONAL",
            isPrimaryGate = isPrimaryGate
        )
        dao.insertSecurityGate(gate)

        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "GATE_POST_CREATED",
            resource = gate.gateName,
            result = "SUCCESS",
            details = "Provisioned new security gate post [${gate.gateCode}] at ${gate.location}."
        )
        return gate
    }

    suspend fun updateGateStatus(gateId: String, status: String) {
        dao.updateGateStatus(gateId, status)
        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "GATE_STATUS_UPDATED",
            resource = "Gate #$gateId",
            result = "SUCCESS",
            details = "Security Gate status set to $status."
        )
    }

    // --- Admin Resident Management & Registration ---
    suspend fun createResidentAccount(
        fullName: String,
        unitNumber: String,
        estateName: String,
        primaryGate: String,
        phone: String,
        email: String,
        passcode: String,
        registeredVehicles: String
    ): ResidentAccountEntity {
        val resident = ResidentAccountEntity(
            id = UUID.randomUUID().toString(),
            fullName = fullName,
            unitNumber = unitNumber,
            estateName = estateName,
            primaryGate = primaryGate,
            phone = phone,
            email = email,
            passcode = passcode.ifBlank { "1234" },
            status = "ACTIVE",
            registeredVehicles = registeredVehicles
        )
        dao.insertResident(resident)

        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "RESIDENT_ACCESS_PROVISIONED",
            resource = "${resident.fullName} (${resident.unitNumber})",
            result = "SUCCESS",
            details = "Granted estate residence access for ${resident.fullName} at ${resident.unitNumber} with primary gate ${resident.primaryGate}."
        )
        return resident
    }

    suspend fun updateResidentAccount(resident: ResidentAccountEntity) {
        dao.updateResident(resident)
        appendAuditLog(
            actor = resident.fullName,
            role = "RESIDENT",
            action = "PROFILE_UPDATED",
            resource = "${resident.fullName} (${resident.unitNumber})",
            result = "SUCCESS",
            details = "Updated resident registration profile, vehicle list, and emergency contact details."
        )
    }

    suspend fun deleteResidentAccount(resident: ResidentAccountEntity) {
        dao.deleteResident(resident)
        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "RESIDENT_ACCESS_REVOKED",
            resource = "${resident.fullName} (${resident.unitNumber})",
            result = "SUCCESS",
            details = "De-provisioned residence account for ${resident.fullName}."
        )
    }

    // --- Guard Management ---
    suspend fun createGuardAccount(
        fullName: String,
        badgeId: String,
        assignedGate: String,
        shift: String,
        phone: String
    ): GuardAccountEntity {
        val guard = GuardAccountEntity(
            id = UUID.randomUUID().toString(),
            fullName = fullName,
            badgeId = badgeId,
            assignedGate = assignedGate,
            shift = shift,
            phone = phone,
            passcode = "0000",
            status = "ON_DUTY"
        )
        dao.insertGuard(guard)

        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "GUARD_POST_PROVISIONED",
            resource = "${guard.fullName} [${guard.badgeId}]",
            result = "SUCCESS",
            details = "Provisioned security officer access for ${guard.fullName} at ${guard.assignedGate} ($shift)."
        )
        return guard
    }

    suspend fun deleteGuardAccount(guard: GuardAccountEntity) {
        dao.deleteGuard(guard)
        appendAuditLog(
            actor = "Estate Admin",
            role = "ESTATE_ADMIN",
            action = "GUARD_ACCESS_DELETED",
            resource = "${guard.fullName} [${guard.badgeId}]",
            result = "SUCCESS",
            details = "Removed guard access profile for ${guard.fullName}."
        )
    }

    // --- Family Member Access Management ---
    suspend fun addFamilyMember(
        residentId: String,
        residentName: String,
        unitNumber: String,
        fullName: String,
        relationship: String,
        phone: String,
        email: String,
        accessLevel: String,
        vehiclePlate: String
    ): FamilyMemberEntity {
        val pin = SecurityUtils.generateSecurePin()
        val token = "GSH-FAM-${UUID.randomUUID().toString().take(8).uppercase()}"

        val member = FamilyMemberEntity(
            residentId = residentId,
            residentName = residentName,
            unitNumber = unitNumber,
            fullName = fullName,
            relationship = relationship,
            phone = phone,
            email = email,
            accessLevel = accessLevel,
            pinCode = pin,
            qrToken = token,
            vehiclePlate = vehiclePlate.uppercase().trim(),
            isActive = true
        )
        dao.insertFamilyMember(member)

        appendAuditLog(
            actor = residentName,
            role = "RESIDENT",
            action = "FAMILY_ACCESS_GRANTED",
            resource = "$fullName ($relationship)",
            result = "SUCCESS",
            details = "Granted residence access credential to family member $fullName ($relationship) for unit $unitNumber."
        )
        return member
    }

    suspend fun deleteFamilyMember(member: FamilyMemberEntity, actorName: String) {
        dao.deleteFamilyMember(member)
        appendAuditLog(
            actor = actorName,
            role = "RESIDENT",
            action = "FAMILY_ACCESS_REVOKED",
            resource = "${member.fullName} (${member.relationship})",
            result = "SUCCESS",
            details = "Revoked residence badge for family member ${member.fullName}."
        )
    }

    // --- Messaging & Rich File Attachment ---
    suspend fun sendMessage(
        channelType: MessageChannelType,
        conversationId: String,
        senderId: String,
        senderName: String,
        senderRole: String,
        senderUnit: String = "",
        receiverId: String = "",
        receiverName: String = "",
        content: String,
        attachmentType: MessageAttachmentType = MessageAttachmentType.NONE,
        attachmentUrl: String = "",
        attachmentName: String = ""
    ): EstateMessageEntity {
        val message = EstateMessageEntity(
            channelType = channelType.name,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            senderUnit = senderUnit,
            receiverId = receiverId,
            receiverName = receiverName,
            content = content,
            attachmentType = attachmentType.name,
            attachmentUrl = attachmentUrl,
            attachmentName = attachmentName,
            timestamp = System.currentTimeMillis()
        )
        dao.insertMessage(message)
        return message
    }

    // --- Broadcasts & Bulletins ---
    suspend fun publishBroadcast(
        title: String,
        category: String,
        priority: String,
        authorName: String,
        authorRole: String,
        content: String,
        targetAudience: String,
        attachmentUrl: String = "",
        isPinned: Boolean = false
    ): EstateBroadcastEntity {
        val broadcast = EstateBroadcastEntity(
            title = title,
            category = category,
            priority = priority,
            authorName = authorName,
            authorRole = authorRole,
            content = content,
            targetAudience = targetAudience,
            attachmentUrl = attachmentUrl,
            timestamp = System.currentTimeMillis(),
            isPinned = isPinned
        )
        dao.insertBroadcast(broadcast)

        appendAuditLog(
            actor = authorName,
            role = authorRole,
            action = "ESTATE_BROADCAST_PUBLISHED",
            resource = title,
            result = "SUCCESS",
            details = "Dispatched general estate notification [$priority] '$title' to $targetAudience."
        )
        return broadcast
    }

    suspend fun acknowledgeBroadcast(broadcastId: String) {
        dao.acknowledgeBroadcast(broadcastId)
    }

    // --- Fee Collection & Invoices ---
    suspend fun createFeeInvoice(
        residentId: String,
        residentName: String,
        unitNumber: String,
        feeTitle: String,
        category: String,
        amount: Double,
        period: String,
        dueDateEpoch: Long
    ): EstateFeeInvoiceEntity {
        val invoice = EstateFeeInvoiceEntity(
            residentId = residentId,
            residentName = residentName,
            unitNumber = unitNumber,
            feeTitle = feeTitle,
            category = category,
            amount = amount,
            period = period,
            dueDateEpoch = dueDateEpoch,
            status = FeeStatus.PENDING.name
        )
        dao.insertInvoice(invoice)

        appendAuditLog(
            actor = "Estate Finance Admin",
            role = "ESTATE_ADMIN",
            action = "FEE_INVOICE_ISSUED",
            resource = "${invoice.feeTitle} - $unitNumber",
            result = "SUCCESS",
            details = "Issued levy invoice for ₦${"%,.2f".format(amount)} ($feeTitle) to $residentName ($unitNumber)."
        )
        return invoice
    }

    suspend fun payInvoice(
        invoice: EstateFeeInvoiceEntity,
        paymentMethod: String
    ): EstateFeeInvoiceEntity {
        val now = System.currentTimeMillis()
        val ref = "TXN-PBE-${(now % 1000000).toString().padStart(6, '0')}"
        val receipt = "RCP-2026-${(now % 10000).toString().padStart(4, '0')}"

        dao.markInvoicePaid(
            invoiceId = invoice.id,
            paidEpoch = now,
            ref = ref,
            method = paymentMethod,
            receipt = receipt
        )

        appendAuditLog(
            actor = invoice.residentName,
            role = "RESIDENT",
            action = "FEE_PAYMENT_SETTLED",
            resource = "${invoice.feeTitle} ($ref)",
            result = "SUCCESS",
            details = "Processed payment of ₦${"%,.2f".format(invoice.amount)} for ${invoice.feeTitle} via $paymentMethod. Receipt: $receipt."
        )

        return invoice.copy(
            status = FeeStatus.PAID.name,
            paidDateEpoch = now,
            paymentReference = ref,
            paymentMethod = paymentMethod,
            receiptNumber = receipt
        )
    }

    // --- Meetings & Community Contributions ---
    suspend fun createMeeting(
        title: String,
        description: String,
        category: String,
        scheduledEpoch: Long,
        durationMinutes: Int,
        agendaItems: String,
        hostName: String
    ): EstateMeetingEntity {
        val meeting = EstateMeetingEntity(
            title = title,
            description = description,
            category = category,
            scheduledEpoch = scheduledEpoch,
            durationMinutes = durationMinutes,
            status = if (scheduledEpoch <= System.currentTimeMillis()) "LIVE" else "UPCOMING",
            agendaItems = agendaItems,
            hostName = hostName
        )
        dao.insertMeeting(meeting)

        appendAuditLog(
            actor = hostName,
            role = "ESTATE_ADMIN",
            action = "COMMUNITY_MEETING_SCHEDULED",
            resource = title,
            result = "SUCCESS",
            details = "Scheduled estate general meeting '$title' on ${SecurityUtils.formatTimestamp(scheduledEpoch)}."
        )
        return meeting
    }

    suspend fun postMeetingContribution(
        meetingId: String,
        authorName: String,
        authorRole: String,
        unitNumber: String,
        message: String,
        isHandRaised: Boolean = false,
        voteChoice: String = ""
    ): MeetingContributionEntity {
        val contrib = MeetingContributionEntity(
            meetingId = meetingId,
            authorName = authorName,
            authorRole = authorRole,
            unitNumber = unitNumber,
            message = message,
            timestamp = System.currentTimeMillis(),
            isHandRaised = isHandRaised,
            voteChoice = voteChoice
        )
        dao.insertContribution(contrib)
        return contrib
    }

    suspend fun votePoll(poll: MeetingPollEntity, optionIndex: Int, actorName: String) {
        val updated = when (optionIndex) {
            1 -> poll.copy(votesA = poll.votesA + 1, userVotedOption = poll.optionA)
            2 -> poll.copy(votesB = poll.votesB + 1, userVotedOption = poll.optionB)
            3 -> poll.copy(votesC = poll.votesC + 1, userVotedOption = poll.optionC)
            else -> poll
        }
        dao.updatePoll(updated)

        appendAuditLog(
            actor = actorName,
            role = "RESIDENT",
            action = "POLL_VOTE_CAST",
            resource = poll.question.take(30),
            result = "SUCCESS",
            details = "Cast community ballot in meeting poll."
        )
    }

    // --- Resident Complaints / Tickets ---
    suspend fun submitComplaint(
        residentId: String,
        residentName: String,
        unitNumber: String,
        phone: String,
        title: String,
        category: String,
        severity: String,
        description: String,
        imageAttachmentUrl: String = ""
    ): ResidentComplaintEntity {
        val count = (System.currentTimeMillis() % 1000).toString().padStart(3, '0')
        val code = "TCK-2026-$count"

        val complaint = ResidentComplaintEntity(
            ticketCode = code,
            residentId = residentId,
            residentName = residentName,
            unitNumber = unitNumber,
            phone = phone,
            title = title,
            category = category,
            severity = severity,
            description = description,
            imageAttachmentUrl = imageAttachmentUrl,
            status = "OPEN",
            createdTimestamp = System.currentTimeMillis()
        )
        dao.insertComplaint(complaint)

        appendAuditLog(
            actor = residentName,
            role = "RESIDENT",
            action = "COMPLAINT_FILED",
            resource = "$code: $title",
            result = "SUCCESS",
            details = "Logged resident ticket [$category/$severity] '$title' at $unitNumber."
        )
        return complaint
    }

    suspend fun resolveComplaint(
        complaintId: String,
        status: String,
        response: String,
        actorName: String
    ) {
        dao.resolveComplaint(
            complaintId = complaintId,
            status = status,
            response = response,
            resolvedEpoch = System.currentTimeMillis()
        )

        appendAuditLog(
            actor = actorName,
            role = "ESTATE_ADMIN",
            action = "COMPLAINT_UPDATED",
            resource = "Ticket #$complaintId",
            result = "SUCCESS",
            details = "Updated complaint ticket status to $status. Note: $response"
        )
    }

    // --- Visitor Passes ---
    suspend fun createPass(
        visitorName: String,
        phone: String,
        hostResidentName: String,
        propertyUnit: String,
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
        items: List<Pair<String, String>>
    ): VisitorPassEntity {
        val now = System.currentTimeMillis()
        val validUntil = now + (validDurationHours * 3600000L)
        val passId = UUID.randomUUID().toString()
        val pin = SecurityUtils.generateSecurePin()
        val token = SecurityUtils.generateQrToken()

        val pass = VisitorPassEntity(
            id = passId,
            pinCode = pin,
            qrToken = token,
            visitorName = visitorName,
            phone = phone,
            hostResidentName = hostResidentName,
            propertyUnit = propertyUnit,
            visitorType = visitorType.name,
            visitPurpose = visitPurpose,
            expectedOccupants = expectedOccupants,
            vehiclePlate = vehiclePlate.uppercase().trim(),
            vehicleMakeModel = vehicleMakeModel,
            vehicleColor = vehicleColor,
            driverName = driverName,
            allowedGate = allowedGate,
            validFromEpoch = now,
            validUntilEpoch = validUntil,
            status = PassStatus.SCHEDULED.name,
            specialInstructions = specialInstructions
        )

        dao.insertPass(pass)

        if (items.isNotEmpty()) {
            val itemEntities = items.map { (name, category) ->
                DeclaredItemEntity(
                    passId = passId,
                    itemName = name,
                    category = category,
                    quantity = 1
                )
            }
            dao.insertItems(itemEntities)
        }

        appendAuditLog(
            actor = hostResidentName,
            role = "RESIDENT",
            action = "PASS_CREATED",
            resource = "Pass #$pin ($visitorName)",
            result = "SUCCESS",
            details = "Created ${visitorType.name} pass for $visitorName at $propertyUnit via gate: $allowedGate."
        )

        return pass
    }

    suspend fun verifyAccess(code: String, currentGate: String): VerificationResult {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isEmpty()) return VerificationResult.Idle

        val pass = dao.searchPassByAnyCode(cleanCode)
            ?: return VerificationResult.Denied(
                reason = "INVALID CREDENTIAL: No active visitor pass matches '$cleanCode'. Please contact host resident."
            )

        if (pass.isRevoked) {
            appendAuditLog(
                actor = "Security Guard",
                role = "SECURITY_GUARD",
                action = "ACCESS_VERIFIED",
                resource = "Pass #${pass.pinCode}",
                result = "FAILURE",
                details = "Attempted entry on revoked pass: ${pass.revokedReason ?: "Manually revoked by resident"}"
            )
            return VerificationResult.Denied(
                reason = "REVOKED PASS: This pass was revoked by ${pass.hostResidentName}. Reason: ${pass.revokedReason ?: "Access terminated"}.",
                pass = pass
            )
        }

        if (SecurityUtils.isExpired(pass.validUntilEpoch)) {
            appendAuditLog(
                actor = "Security Guard",
                role = "SECURITY_GUARD",
                action = "ACCESS_VERIFIED",
                resource = "Pass #${pass.pinCode}",
                result = "FAILURE",
                details = "Pass expired at ${SecurityUtils.formatTimestamp(pass.validUntilEpoch)}"
            )
            return VerificationResult.Denied(
                reason = "EXPIRED PASS: Validity expired on ${SecurityUtils.formatTimestamp(pass.validUntilEpoch)}. Resident must issue a renewed pass.",
                pass = pass
            )
        }

        if (pass.allowedGate != "All Gates" && !pass.allowedGate.equals(currentGate, ignoreCase = true)) {
            appendAuditLog(
                actor = "Security Guard",
                role = "SECURITY_GUARD",
                action = "ACCESS_VERIFIED",
                resource = "Pass #${pass.pinCode}",
                result = "FAILURE",
                details = "Gate restriction mismatch: restricted to ${pass.allowedGate}, presented at $currentGate"
            )
            return VerificationResult.Denied(
                reason = "GATE MISMATCH: This pass is restricted to '${pass.allowedGate}'. Access at '$currentGate' is unauthorized.",
                pass = pass
            )
        }

        if (pass.status == PassStatus.COMPLETED_EXIT.name) {
            return VerificationResult.Denied(
                reason = "PASS ALREADY USED: This visit has already checked out and the one-time credential is consumed.",
                pass = pass
            )
        }

        val items = dao.getItemsForPassList(pass.id)
        val warnings = mutableListOf<String>()

        if (pass.status == PassStatus.ACTIVE_INSIDE.name) {
            warnings.add("VISITOR CURRENTLY INSIDE: Pass is currently in active visit. Proceed with EXIT verification.")
        }

        return VerificationResult.Approved(
            pass = pass,
            declaredItems = items,
            warnings = warnings
        )
    }

    suspend fun allowEntry(
        passId: String,
        actualOccupants: Int,
        guardName: String,
        gateName: String,
        notes: String,
        signature: String?
    ): Boolean {
        val pass = dao.getPassById(passId) ?: return false
        val now = System.currentTimeMillis()

        dao.recordPassCheckIn(
            passId = passId,
            entryTime = now,
            actualCount = actualOccupants,
            notes = notes,
            sig = signature
        )

        val isDiscrepancy = actualOccupants != pass.expectedOccupants
        val decisionNote = if (isDiscrepancy) {
            "Entry allowed with occupant discrepancy: Expected ${pass.expectedOccupants}, Actual $actualOccupants. Notes: $notes"
        } else {
            "Entry approved by $guardName. $notes"
        }

        dao.insertGateEvent(
            GateEventEntity(
                passId = passId,
                visitorName = pass.visitorName,
                hostResident = pass.hostResidentName,
                gateName = gateName,
                guardName = guardName,
                eventType = "CHECK_IN",
                timestamp = now,
                vehiclePlate = pass.vehiclePlate,
                occupantCount = actualOccupants,
                decisionNote = decisionNote,
                isDiscrepancy = isDiscrepancy
            )
        )

        appendAuditLog(
            actor = guardName,
            role = "SECURITY_GUARD",
            action = "ENTRY_APPROVED",
            resource = "Pass #${pass.pinCode} (${pass.visitorName})",
            result = if (isDiscrepancy) "WARNING" else "SUCCESS",
            details = "Granted estate entry to ${pass.visitorName} ($actualOccupants occupants) at $gateName. Resident: ${pass.hostResidentName} (${pass.propertyUnit})."
        )

        return true
    }

    suspend fun recordExit(
        passId: String,
        updatedItems: List<DeclaredItemEntity>,
        guardName: String,
        gateName: String,
        notes: String,
        signature: String?
    ): Boolean {
        val pass = dao.getPassById(passId) ?: return false
        val now = System.currentTimeMillis()

        updatedItems.forEach { item ->
            dao.updateItem(item)
        }

        val hasItemDiscrepancy = updatedItems.any {
            it.exitInspectionStatus == "ADDED" || it.exitInspectionStatus == "MISSING" || it.exitInspectionStatus == "UNDECLARED"
        }

        dao.recordPassCheckOut(
            passId = passId,
            exitTime = now,
            sig = signature
        )

        val decisionNote = if (hasItemDiscrepancy) {
            "Exit recorded with ITEM DISCREPANCY: ${updatedItems.filter { it.exitInspectionStatus != "MATCHED" }.joinToString { "${it.itemName} (${it.exitInspectionStatus})" }}. Guard: $notes"
        } else {
            "Exit verified with all declared items accounted for. Guard: $notes"
        }

        dao.insertGateEvent(
            GateEventEntity(
                passId = passId,
                visitorName = pass.visitorName,
                hostResident = pass.hostResidentName,
                gateName = gateName,
                guardName = guardName,
                eventType = "CHECK_OUT",
                timestamp = now,
                vehiclePlate = pass.vehiclePlate,
                occupantCount = pass.actualOccupants,
                decisionNote = decisionNote,
                isDiscrepancy = hasItemDiscrepancy
            )
        )

        appendAuditLog(
            actor = guardName,
            role = "SECURITY_GUARD",
            action = "EXIT_RECORDED",
            resource = "Pass #${pass.pinCode} (${pass.visitorName})",
            result = if (hasItemDiscrepancy) "WARNING" else "SUCCESS",
            details = "Recorded departure for ${pass.visitorName} at $gateName. Items checked: ${updatedItems.size}. Discrepancy: $hasItemDiscrepancy."
        )

        return true
    }

    suspend fun revokePass(passId: String, reason: String, actorName: String, role: String) {
        dao.revokePass(passId, reason)
        val pass = dao.getPassById(passId)
        val passCode = pass?.pinCode ?: passId

        appendAuditLog(
            actor = actorName,
            role = role,
            action = "PASS_REVOKED",
            resource = "Pass #$passCode",
            result = "SUCCESS",
            details = "Access credential revoked immediately. Reason: $reason"
        )
    }

    suspend fun reportIncident(
        title: String,
        category: String,
        severity: String,
        guardName: String,
        gateName: String,
        visitorName: String,
        vehiclePlate: String,
        description: String
    ): IncidentEntity {
        val count = System.currentTimeMillis() % 10000
        val code = "INC-2026-${"%04d".format(count)}"

        val incident = IncidentEntity(
            incidentCode = code,
            title = title,
            category = category,
            severity = severity,
            guardName = guardName,
            gateName = gateName,
            visitorName = visitorName,
            vehiclePlate = vehiclePlate.uppercase().trim(),
            description = description,
            status = "OPEN"
        )
        dao.insertIncident(incident)

        appendAuditLog(
            actor = guardName,
            role = "SECURITY_GUARD",
            action = "INCIDENT_LOGGED",
            resource = code,
            result = if (severity == "CRITICAL" || severity == "HIGH") "WARNING" else "SUCCESS",
            details = "Logged security incident [$severity] $title at $gateName. Category: $category."
        )

        return incident
    }

    suspend fun resolveIncident(incident: IncidentEntity, resolutionNotes: String, actorName: String) {
        val updated = incident.copy(
            status = "RESOLVED",
            resolutionNotes = resolutionNotes
        )
        dao.updateIncident(updated)

        appendAuditLog(
            actor = actorName,
            role = "ESTATE_ADMIN",
            action = "INCIDENT_RESOLVED",
            resource = incident.incidentCode,
            result = "SUCCESS",
            details = "Resolved incident ${incident.incidentCode}: $resolutionNotes"
        )
    }

    suspend fun emergencyOverrideGate(
        serviceType: String,
        vehiclePlate: String,
        occupantCount: Int,
        guardName: String,
        gateName: String,
        notes: String
    ): VisitorPassEntity {
        val now = System.currentTimeMillis()
        val passId = UUID.randomUUID().toString()
        val pin = SecurityUtils.generateSecurePin()
        val token = SecurityUtils.generateQrToken("EMG")

        val pass = VisitorPassEntity(
            id = passId,
            pinCode = pin,
            qrToken = token,
            visitorName = "EMERGENCY: $serviceType",
            phone = "112 / EMERGENCY",
            hostResidentName = "ESTATE COMMAND / SUPERVISOR",
            propertyUnit = "ALL ESTATE UNITS",
            visitorType = PassType.EMERGENCY.name,
            visitPurpose = "Emergency First Response - $serviceType",
            expectedOccupants = occupantCount,
            actualOccupants = occupantCount,
            vehiclePlate = vehiclePlate.uppercase().trim(),
            allowedGate = gateName,
            validFromEpoch = now,
            validUntilEpoch = now + 86400000L,
            status = PassStatus.ACTIVE_INSIDE.name,
            entryTimeEpoch = now,
            emergencyOverride = true,
            guardNotes = notes
        )

        dao.insertPass(pass)

        dao.insertGateEvent(
            GateEventEntity(
                passId = passId,
                visitorName = pass.visitorName,
                hostResident = pass.hostResidentName,
                gateName = gateName,
                guardName = guardName,
                eventType = "EMERGENCY_OVERRIDE",
                timestamp = now,
                vehiclePlate = vehiclePlate,
                occupantCount = occupantCount,
                decisionNote = "CRITICAL EMERGENCY OVERRIDE: $serviceType. $notes",
                isDiscrepancy = false
            )
        )

        appendAuditLog(
            actor = guardName,
            role = "SECURITY_GUARD",
            action = "EMERGENCY_OVERRIDE",
            resource = "Pass #$pin ($serviceType)",
            result = "OVERRIDE",
            details = "CRITICAL: Guard executed emergency gate override for $serviceType at $gateName. Plate: $vehiclePlate."
        )

        return pass
    }

    suspend fun updatePolicy(policyKey: String, isEnabled: Boolean, actorName: String) {
        dao.updatePolicyStatus(policyKey, isEnabled)
        appendAuditLog(
            actor = actorName,
            role = "ESTATE_ADMIN",
            action = "POLICY_UPDATED",
            resource = policyKey,
            result = "SUCCESS",
            details = "Updated security policy '$policyKey' to ${if (isEnabled) "ENABLED" else "DISABLED"}."
        )
    }

    private suspend fun appendAuditLog(
        actor: String,
        role: String,
        action: String,
        resource: String,
        result: String,
        details: String
    ) {
        val latestLog = dao.getLatestAuditLog()
        val prevHash = latestLog?.currentHash ?: "0000000000000000000000000000000000000000000000000000000000000000"
        val timestamp = System.currentTimeMillis()
        val eventId = UUID.randomUUID().toString()

        val canonicalData = "$eventId|$timestamp|$actor|$role|$action|$resource|$result|$details"
        val currentHash = SecurityUtils.calculateEventHash(prevHash, canonicalData)

        val entity = AuditLogEntity(
            eventId = eventId,
            timestamp = timestamp,
            actor = actor,
            role = role,
            action = action,
            resource = resource,
            result = result,
            previousHash = prevHash,
            currentHash = currentHash,
            details = details
        )
        dao.insertAuditLog(entity)
    }
}
