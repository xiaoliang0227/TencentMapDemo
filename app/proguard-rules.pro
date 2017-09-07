# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zhaoyongliang/tools/android_sdks/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#腾讯地图 2D sdk
-libraryjars libs/TencentMapSDK_*_vx.x.x.x.jar
-keep class com.tencent.mapsdk.**{*;}
-keep class com.tencent.tencentmap.**{*;}

#腾讯地图 3D sdk
-libraryjars libs/TencentMapSDK_*_vx.x.x.x.jar
-keep class com.tencent.tencentmap.**{*;}
-keep class com.tencent.map.**{*;}

#腾讯地图检索sdk
-libraryjars libs/TencentSearch_vx.x.x.x.jar
-keep class com.tencent.lbssearch.**{*;}
-keep class com.google.gson.examples.android.model.** { *; }

#腾讯地图街景sdk
#如果街景混淆报出类似"java.io.IOException: Can't read [*\TencentPanoramaSDKv1.1.0_15232.jar"
#请参考http://bbs.map.qq.com/forum.php?mod=viewthread&tid=21098&extra=page=1&filter=typeid&typeid=95&typeid=95
-libraryjars libs/TencentPanoramaSDK_v.1.2.0_16324.jar
-keep class com.tencent.tencentmap.streetviewsdk.**{*;}
