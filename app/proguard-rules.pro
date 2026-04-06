# ============================================================
# Landroid ProGuard Rules
# ============================================================

# --- Keep data classes (Gson/Supabase serialization) ---
-keepclassmembers class com.landroid.shared.models.** { *; }
-keepclassmembers class com.landroid.features.**.data.** { *; }
-keepclassmembers class com.landroid.core.network.** { *; }

# --- Kotlin serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.landroid.**$$serializer { *; }

# --- Hilt / Dagger ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers @androidx.room.Dao class * { *; }

# --- Retrofit + Gson ---
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }

# --- OkHttp ---
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# --- MapLibre ---
-keep class org.maplibre.** { *; }
-dontwarn org.maplibre.**

# --- OpenCV ---
-keep class org.opencv.** { *; }
-dontwarn org.opencv.**

# --- Supabase ---
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- Coil ---
-keep class coil.** { *; }
-dontwarn coil.**

# --- Vico charts ---
-keep class com.patrykandpatrick.vico.** { *; }

# --- General Kotlin ---
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# --- Enums ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Parcelable ---
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Obfuscate everything else
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
