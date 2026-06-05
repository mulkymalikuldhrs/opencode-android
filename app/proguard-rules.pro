# =============================================================================
# OpenCode Android — ProGuard Rules
# =============================================================================

# ── OkHttp ──────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── OkHttp SSE ──────────────────────────────────────────────────────────
-keep class okhttp3.sse.** { *; }

# ── Java-WebSocket ──────────────────────────────────────────────────────
-keep class org.java_websocket.** { *; }
-keepclassmembers class org.java_websocket.** { *; }

# ── org.json ────────────────────────────────────────────────────────────
-keep class org.json.** { *; }

# ── AndroidX / Material ────────────────────────────────────────────────
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# ── Kotlin Coroutines ───────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ── App Models (keep for JSON serialization) ────────────────────────────
-keep class ai.opencode.mobile.model.** { *; }

# ── App API Client (keep for reflection) ───────────────────────────────
-keep class ai.opencode.mobile.api.** { *; }

# ── App Service ─────────────────────────────────────────────────────────
-keep class ai.opencode.mobile.OpenCodeService { *; }

# ── Prevent stripping BuildConfig ──────────────────────────────────────
-keep class ai.opencode.mobile.BuildConfig { *; }

# ── Generic safe rules ─────────────────────────────────────────────────
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Remove logs in release ─────────────────────────────────────────────
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
