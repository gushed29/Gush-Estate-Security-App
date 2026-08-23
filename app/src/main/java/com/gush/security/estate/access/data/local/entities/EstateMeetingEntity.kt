package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "estate_meetings")
data class EstateMeetingEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: String, // AGM, SECURITY_BRIEFING, ENVIRONMENT_DRAINAGE, RESIDENTS_COUNCIL
    val scheduledEpoch: Long,
    val durationMinutes: Int = 60,
    val status: String = "UPCOMING", // UPCOMING, LIVE, CONCLUDED
    val meetingRoomCode: String = "PBE-ROOM-01",
    val hostName: String = "Estate Chairman & Security Board",
    val agendaItems: String = "1. Security report\n2. Power project update\n3. Resident Q&A",
    val activeSpeaker: String = "",
    val participantCount: Int = 18
)

@Entity(tableName = "meeting_contributions")
data class MeetingContributionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val meetingId: String,
    val authorName: String,
    val authorRole: String, // RESIDENT, ADMIN, SECURITY
    val unitNumber: String = "",
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isHandRaised: Boolean = false,
    val voteChoice: String = ""
)

@Entity(tableName = "meeting_polls")
data class MeetingPollEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val meetingId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String = "",
    val votesA: Int = 0,
    val votesB: Int = 0,
    val votesC: Int = 0,
    val isOpen: Boolean = true,
    val userVotedOption: String? = null
)
