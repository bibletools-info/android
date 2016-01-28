# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/tinashe/Documents/Android/Android-Studio/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#Realm
-keepnames public class * extends io.realm.RealmObject
-keep class io.realm.** { *; }
-dontwarn javax.**
-dontwarn io.realm.**

#ABC
# Need to keep classes unobfuscated from v7.**, not v7.appcompat.**, because other packages directly under
# v7.** get obfuscated. eg. v7.internal.** and v7.widget.** cause errors if obfuscated.
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

#To revise
-keep class com.google.android.gms.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

#Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature

#OkHttp
-dontwarn com.squareup.okhttp.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#nineoldandroids
-keep class com.nineoldandroids.** { *; }
-keep interface com.nineoldandroids.** { *; }

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#To remove debug logs:
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
}

#To keep line numbers for crash reports
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable

-dontwarn uk.co.senab.**