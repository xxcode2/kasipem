# ProGuard rules for KASIPEM Android App

# Keep Kotlin classes
-keepclassmembers class kotlin.Metadata {
    *** *;
}

# Keep Compose
-keep @androidx.compose.runtime.Immutable class * { *; }
-keep @androidx.compose.runtime.Stable class * { *; }

# Keep all Compose recomposers
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep iText
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Keep kotlinx serialization
-keep class kotlinx.serialization.** { *; }
-keepclasseswithmembers class kotlinx.serialization.** {
    *** *;
}

# Keep models
-keep class com.kasipem.data.** { *; }

# Reduce logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# General Android rules
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends androidx.fragment.app.Fragment
-keep class * extends androidx.appcompat.app.AppCompatActivity

-keepattributes Signature
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable
-keepattributes EnclosingMethod

# Remove logging in production
-assumenosideeffects class android.util.Log {
    public static int d(...);
}

-verbose
