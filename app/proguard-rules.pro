#############################################
# Firebase
#############################################
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

#############################################
# Hilt / Dagger
#############################################
-keep class dagger.hilt.** { *; }
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends androidx.lifecycle.ViewModel
-dontwarn dagger.hilt.**

#############################################
# Kotlin Coroutines
#############################################
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

#############################################
# Jetpack Compose
#############################################
-keep class androidx.compose.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.compose.**

#############################################
# Retrofit + OkHttp + Gson
#############################################
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep class retrofit2.** { *; }
-keep interface com.sanjey.codestride.data.remote.** { *; }   # ✅ keep API interfaces
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

#############################################
# Firestore & Data Models
#############################################
-keep class com.sanjey.codestride.data.model.** { *; }
-keep class com.sanjey.codestride.data.model.ai.** { *; }

#############################################
# YouTube API Models
#############################################
-keep class com.sanjey.codestride.data.remote.YouTubeResponse { *; }
-keep class com.sanjey.codestride.data.remote.YouTubeVideoItem { *; }
-keep class com.sanjey.codestride.data.remote.VideoId { *; }

# Keep Retrofit generics
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Coroutines continuation for suspend functions
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep AI models for Gson
-keep class com.sanjey.codestride.data.model.ai.** { *; }
