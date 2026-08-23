package com.gush.security.estate.access.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gush.security.estate.access.data.local.dao.EstateSecurityDao
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
import com.gush.security.estate.access.data.local.entities.MeetingContributionEntity
import com.gush.security.estate.access.data.local.entities.MeetingPollEntity
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentComplaintEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.data.local.entities.SecurityPolicyEntity
import com.gush.security.estate.access.data.local.entities.VisitorPassEntity

@Database(
    entities = [
        VisitorPassEntity::class,
        DeclaredItemEntity::class,
        GateEventEntity::class,
        IncidentEntity::class,
        AuditLogEntity::class,
        SecurityPolicyEntity::class,
        SecurityGateEntity::class,
        ResidentAccountEntity::class,
        GuardAccountEntity::class,
        FamilyMemberEntity::class,
        EstateMessageEntity::class,
        EstateBroadcastEntity::class,
        EstateFeeInvoiceEntity::class,
        EstateMeetingEntity::class,
        MeetingContributionEntity::class,
        MeetingPollEntity::class,
        ResidentComplaintEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class EstateSecurityDatabase : RoomDatabase() {

    abstract fun estateSecurityDao(): EstateSecurityDao

    companion object {
        @Volatile
        private var INSTANCE: EstateSecurityDatabase? = null

        fun getDatabase(context: Context): EstateSecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstateSecurityDatabase::class.java,
                    "gushed_estate_security.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
