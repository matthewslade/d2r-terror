# Add project specific ProGuard rules here.

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Keep Gson
-keepattributes *Annotation*
-keep class com.d2rterror.data.api.** { *; }
-keep class com.d2rterror.data.model.** { *; }
