package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.DeclaredItemEntity
import com.example.data.local.entities.EstateBroadcastEntity
import com.example.data.local.entities.EstateFeeInvoiceEntity
import com.example.data.local.entities.EstateMeetingEntity
import com.example.data.local.entities.EstateMessageEntity
import com.example.data.local.entities.FamilyMemberEntity
import com.example.data.local.entities.GateEventEntity
import com.example.data.local.entities.GuardAccountEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.MeetingContributionEntity
import com.example.data.local.entities.MeetingPollEntity
import com.example.data.local.entities.ResidentAccountEntity
import com.example.data.local.entities.ResidentComplaintEntity
import com.example.data.local.entities.SecurityGateEntity
import com.example.data.local.entities.SecurityPolicyEntity
import com.example.data.local.entities.VisitorPassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstateSecurityDao {

    // --- Visitor Passes ---
    @Query("SELECT * FROM visitor_passes ORDER BY createdTimestamp DESC")
    fun getAllPasses(): Flow<List<VisitorPassEntity>>

    @Query("SELECT * FROM visitor_passes WHERE status = 'ACTIVE_INSIDE' ORDER BY entryTimeEpoch DESC")
    fun getActiveInsidePasses(): Flow<List<VisitorPassEntity>>

    @Query("SELECT * FROM visitor_passes WHERE id = :passId LIMIT 1")
    suspend fun getPassById(passId: String): VisitorPassEntity?

    @Query("SELECT * FROM visitor_passes WHERE pinCode = :pin OR qrToken = :token LIMIT 1")
    suspend fun findPassByPinOrToken(pin: String, token: String): VisitorPassEntity?

    @Query("SELECT * FROM visitor_passes WHERE pinCode = :code OR qrToken = :code OR vehiclePlate = :code OR phone = :code LIMIT 1")
    suspend fun searchPassByAnyCode(code: String): VisitorPassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPass(pass: VisitorPassEntity)

    @Update
    suspend fun updatePass(pass: VisitorPassEntity)

    @Query("UPDATE visitor_passes SET isRevoked = 1, status = 'REVOKED', revokedReason = :reason WHERE id = :passId")
    suspend fun revokePass(passId: String, reason: String)

    @Query("UPDATE visitor_passes SET status = 'ACTIVE_INSIDE', entryTimeEpoch = :entryTime, actualOccupants = :actualCount, guardNotes = :notes, entrySignature = :sig WHERE id = :passId")
    suspend fun recordPassCheckIn(passId: String, entryTime: Long, actualCount: Int, notes: String, sig: String?)

    @Query("UPDATE visitor_passes SET status = 'COMPLETED_EXIT', exitTimeEpoch = :exitTime, exitSignature = :sig WHERE id = :passId")
    suspend fun recordPassCheckOut(passId: String, exitTime: Long, sig: String?)

    // --- Declared Items ---
    @Query("SELECT * FROM declared_items WHERE passId = :passId ORDER BY id ASC")
    fun getItemsForPass(passId: String): Flow<List<DeclaredItemEntity>>

    @Query("SELECT * FROM declared_items WHERE passId = :passId ORDER BY id ASC")
    suspend fun getItemsForPassList(passId: String): List<DeclaredItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<DeclaredItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: DeclaredItemEntity)

    @Update
    suspend fun updateItem(item: DeclaredItemEntity)

    // --- Gate Events ---
    @Query("SELECT * FROM gate_events ORDER BY timestamp DESC LIMIT 100")
    fun getRecentGateEvents(): Flow<List<GateEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGateEvent(event: GateEventEntity)

    // --- Incidents ---
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    // --- Audit Logs (Tamper-evident chain) ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 150")
    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAuditLog(): AuditLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // --- Security Policies ---
    @Query("SELECT * FROM security_policies")
    fun getAllPolicies(): Flow<List<SecurityPolicyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicies(policies: List<SecurityPolicyEntity>)

    @Query("UPDATE security_policies SET isEnabled = :isEnabled WHERE policyKey = :key")
    suspend fun updatePolicyStatus(key: String, isEnabled: Boolean)

    // --- Security Gates Management ---
    @Query("SELECT * FROM security_gates ORDER BY isPrimaryGate DESC, gateName ASC")
    fun getAllSecurityGates(): Flow<List<SecurityGateEntity>>

    @Query("SELECT * FROM security_gates WHERE id = :gateId LIMIT 1")
    suspend fun getSecurityGateById(gateId: String): SecurityGateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityGate(gate: SecurityGateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecurityGates(gates: List<SecurityGateEntity>)

    @Update
    suspend fun updateSecurityGate(gate: SecurityGateEntity)

    @Query("UPDATE security_gates SET status = :status WHERE id = :gateId")
    suspend fun updateGateStatus(gateId: String, status: String)

    @Delete
    suspend fun deleteSecurityGate(gate: SecurityGateEntity)

    // --- Resident Accounts Management ---
    @Query("SELECT * FROM resident_accounts ORDER BY fullName ASC")
    fun getAllResidents(): Flow<List<ResidentAccountEntity>>

    @Query("SELECT * FROM resident_accounts WHERE id = :residentId LIMIT 1")
    suspend fun getResidentById(residentId: String): ResidentAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResident(resident: ResidentAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResidents(residents: List<ResidentAccountEntity>)

    @Update
    suspend fun updateResident(resident: ResidentAccountEntity)

    @Delete
    suspend fun deleteResident(resident: ResidentAccountEntity)

    // --- Guard Accounts Management ---
    @Query("SELECT * FROM guard_accounts ORDER BY fullName ASC")
    fun getAllGuards(): Flow<List<GuardAccountEntity>>

    @Query("SELECT * FROM guard_accounts WHERE id = :guardId LIMIT 1")
    suspend fun getGuardById(guardId: String): GuardAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuard(guard: GuardAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuards(guards: List<GuardAccountEntity>)

    @Update
    suspend fun updateGuard(guard: GuardAccountEntity)

    @Delete
    suspend fun deleteGuard(guard: GuardAccountEntity)

    // --- Family Members Management ---
    @Query("SELECT * FROM family_members WHERE residentId = :residentId ORDER BY createdTimestamp DESC")
    fun getFamilyMembersForResident(residentId: String): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members ORDER BY createdTimestamp DESC")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMembers(members: List<FamilyMemberEntity>)

    @Update
    suspend fun updateFamilyMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMemberEntity)

    // --- Messages & Rich Communications ---
    @Query("SELECT * FROM estate_messages WHERE conversationId = :conversationId OR channelType = :channelType ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String, channelType: String): Flow<List<EstateMessageEntity>>

    @Query("SELECT * FROM estate_messages ORDER BY timestamp DESC LIMIT 200")
    fun getAllMessages(): Flow<List<EstateMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: EstateMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<EstateMessageEntity>)

    // --- Broadcasts / General Security Bulletin ---
    @Query("SELECT * FROM estate_broadcasts ORDER BY isPinned DESC, timestamp DESC")
    fun getAllBroadcasts(): Flow<List<EstateBroadcastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcast(broadcast: EstateBroadcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcasts(broadcasts: List<EstateBroadcastEntity>)

    @Update
    suspend fun updateBroadcast(broadcast: EstateBroadcastEntity)

    @Query("UPDATE estate_broadcasts SET isAcknowledged = 1 WHERE id = :broadcastId")
    suspend fun acknowledgeBroadcast(broadcastId: String)

    // --- Estate Fee Invoices & Dues ---
    @Query("SELECT * FROM estate_fee_invoices WHERE residentId = :residentId ORDER BY dueDateEpoch ASC")
    fun getInvoicesForResident(residentId: String): Flow<List<EstateFeeInvoiceEntity>>

    @Query("SELECT * FROM estate_fee_invoices ORDER BY dueDateEpoch ASC")
    fun getAllInvoices(): Flow<List<EstateFeeInvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: EstateFeeInvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<EstateFeeInvoiceEntity>)

    @Query("UPDATE estate_fee_invoices SET status = 'PAID', paidDateEpoch = :paidEpoch, paymentReference = :ref, paymentMethod = :method, receiptNumber = :receipt WHERE id = :invoiceId")
    suspend fun markInvoicePaid(invoiceId: String, paidEpoch: Long, ref: String, method: String, receipt: String)

    // --- Estate Meetings & Contributions ---
    @Query("SELECT * FROM estate_meetings ORDER BY scheduledEpoch ASC")
    fun getAllMeetings(): Flow<List<EstateMeetingEntity>>

    @Query("SELECT * FROM estate_meetings WHERE id = :meetingId LIMIT 1")
    suspend fun getMeetingById(meetingId: String): EstateMeetingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: EstateMeetingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<EstateMeetingEntity>)

    @Update
    suspend fun updateMeeting(meeting: EstateMeetingEntity)

    @Query("SELECT * FROM meeting_contributions WHERE meetingId = :meetingId ORDER BY timestamp ASC")
    fun getContributionsForMeeting(meetingId: String): Flow<List<MeetingContributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: MeetingContributionEntity)

    @Query("SELECT * FROM meeting_polls WHERE meetingId = :meetingId ORDER BY id ASC")
    fun getPollsForMeeting(meetingId: String): Flow<List<MeetingPollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: MeetingPollEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolls(polls: List<MeetingPollEntity>)

    @Update
    suspend fun updatePoll(poll: MeetingPollEntity)

    // --- Resident Complaints / Tickets ---
    @Query("SELECT * FROM resident_complaints WHERE residentId = :residentId ORDER BY createdTimestamp DESC")
    fun getComplaintsForResident(residentId: String): Flow<List<ResidentComplaintEntity>>

    @Query("SELECT * FROM resident_complaints ORDER BY createdTimestamp DESC")
    fun getAllComplaints(): Flow<List<ResidentComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ResidentComplaintEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<ResidentComplaintEntity>)

    @Update
    suspend fun updateComplaint(complaint: ResidentComplaintEntity)

    @Query("UPDATE resident_complaints SET status = :status, adminResponse = :response, resolvedTimestamp = :resolvedEpoch WHERE id = :complaintId")
    suspend fun resolveComplaint(complaintId: String, status: String, response: String, resolvedEpoch: Long)
}
