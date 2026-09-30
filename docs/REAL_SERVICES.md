# Real service configuration

## Firebase

The connected Firebase project is `connectly-android-20261001` with project number `628266690806`. The registered Android application is `com.connectly.app`, app ID `1:628266690806:android:87c5c6f7cf2ca7ab1064d7`.

Email/password and Google sign-in providers were initialized, and Realtime Database rules were created in `database.rules.json`. The local `app/google-services.json` is intentionally ignored from GitHub; regenerate/download it from the Firebase Console for a fresh checkout.

## Cloudinary

The connected Cloudinary product environment is available on the current account. A dedicated `connectly` folder was created for app media. The client source keeps Cloudinary values outside Git through `app/service-config.example.properties`; use a restricted unsigned upload preset or a server-side signing endpoint before enabling user-generated uploads in production. The Cloudinary MCP account reported a free plan with 25 credits and existing media resources.

## APK

The debug build was compiled successfully with Android SDK 35, Gradle 8.7 and OpenJDK 21. Build command:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export ANDROID_HOME=$HOME/android-sdk
$HOME/gradle-8.7/bin/gradle --no-daemon clean assembleDebug
```

The generated APK is distributed as the GitHub Release asset rather than committed to the source repository.
