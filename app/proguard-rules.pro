# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/chulwoo/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Kakao
-keep class com.kakao.** { *; }
-keepattributes Signature
-keepclassmembers class * {
  public static <fields>;
  public *;
}
-dontwarn android.support.v4.**,org.slf4j.**,com.google.android.gms.**


# Naver
-keep public class com.nhn.android.naverlogin.** {
       public protected *;
}

# IGAWorks
-keep class com.igaworks.** { *; }
-dontwarn com.igaworks.**

-optimizationpasses 5

## Crashlytics는 이미 ProGuard를 사용한 라이브러리들을 다시 ProGuard가 검사할 필요 없게 해서 빌드 시간을 줄이는 팁을 제공하고 있다
#-libraryjars libs
#-keep class com.crashlytics.** { *; }
#-dontwarn com.crashlytics.**

# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception

# Android Support Library는 이미 소스가 공개되어 있기 때문에 코드 난독화가 필요하지 않다
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# 다만 이 코드 때문에 난독화가 덜 되는 것 같다는 생각이 든다면, 파일 이름을 모두 "SourceFile" 문자열로 바꿀 수도 있다:
-renamesourcefileattribute SourceFile

# ProGuard를 이용해 코드 난독화 작업을 거치게 되면, 소스 파일의 줄 번호가 바뀌게 되어 Crashlytics의 스택 트레이스에서 정보를 얻기 어려울 수 있다.
# Obfuscation parameters:
#-dontobfuscate
-useuniqueclassmembernames
-keepattributes SourceFile, LineNumberTable
-allowaccessmodification

# Google Play Services SDK 또한 필요한 클래스가 사라지는 것을 방지하기 위한 ProGuard 규칙을 제공하고 있다
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Retrofit 설정
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# Ignore warnings:
#-dontwarn org.mockito.**
#-dontwarn org.junit.**
#-dontwarn com.robotium.**
#-dontwarn org.joda.convert.**
-dontwarn javax.**
-dontwarn org.apache.**
-dontwarn com.squareup.**
-dontwarn com.sun.**
-dontwarn **retrofit**

-dontwarn okhttp3.**
-dontwarn okio.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Ignore warnings: https://github.com/square/retrofit/issues/435
-dontwarn com.google.appengine.api.urlfetch.**

# Keep the pojos used by GSON or Jackson
-keep class com.futurice.project.models.pojo.** { *; }

# Keep GSON stuff
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Keep Jackson stuff
-keep class org.codehaus.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }

# Keep these for GSON and Jackson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Then add your implementation class to your proguard.cfg/proguard.pro file to allow your module to
# be instantiated via reflection. This will make sure ProGuard and DexGuard is not marking it as unused.
# The performance overhead of reflection is minimal, because each module is instantiated a single time,
# when the first request with Glide is made.
-keep public class * implements com.bumptech.glide.module.GlideModule


# rxjava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

# Jsoup
-keep public class org.jsoup.** {
	public *;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

##---------------Begin: proguard configuration for Igaworks Common  ----------
-keep class com.igaworks.** { *; }
-dontwarn com.igaworks.**
##---------------End: proguard configuration for Igaworks Common   ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.igaworks.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.igaworks.adbrix.model.** { *; }

##---------------End: proguard configuration for Gson  ----------

#TNK Factory
-keep class com.tnkfactory.** { *;}

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

#NAS
-keep class com.nasapps.** { *; }


#Buzzvil
-ignorewarnings
-keep class com.buzzvil.** {
    public private *;
}


########--------Retrofit + RxJava--------#########
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}


# ALSO REMEMBER KEEPING YOUR MODEL CLASSES
-keep class com.apartslide.data.model.** { *; }
-keep class com.apartslide.models.** { *; }