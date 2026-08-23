package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.EstateSecurityDao
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
