# Gush Security - Production ProGuard / R8 Rules

# Preserve line numbers and source attributes for meaningful crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room Database rules
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# Moshi & JSON serialization rules
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Gush Security Data Entities & Domain Models
-keep class com.gush.security.estate.access.data.local.entities.** { *; }
-keep class com.gush.security.estate.access.data.repository.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

