# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room annotations
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Retrofit interfaces
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson TypeAdapter
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.TypeAdapter

# Keep data classes
-keep class com.suspension.analyzer.data.** { *; }
-keep class com.suspension.analyzer.network.** { *; }
