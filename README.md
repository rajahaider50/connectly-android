# Connectly Android

A premium Android 9+ social communication app starter written in **Kotlin + Jetpack Compose**. The codebase is structured for real Firebase authentication/realtime data, Cloudinary media uploads, push notifications, and WebRTC call signaling without committing credentials.

## Included

- Email/password and Google sign-in entry points
- Username-based people search using `@username`
- Profile with avatar, bio and about/service fields
- Real-time chat surface with voice, image/video and call action affordances
- Audio/video call permission flow
- Account recovery UI with phone, 13-digit CNIC and 8-digit backup code pathways
- Firebase Auth, Realtime Database and FCM dependency boundaries
- Cloudinary upload configuration template
- Android 9+ minimum SDK (`minSdk = 28`)
- Premium dark UI and generated app icon source asset

## Local setup

1. Install Android Studio Ladybug or newer with Android SDK 35 and JDK 17.
2. Open this folder in Android Studio and let Gradle sync.
3. Create a Firebase project, add the Android app id `com.connectly.app`, and place the downloaded `google-services.json` at `app/google-services.json`.
4. Enable Firebase Auth providers (Email/Password and Google), Realtime Database and Cloud Messaging.
5. Create a restricted Cloudinary unsigned upload preset and put values from `app/service-config.example.properties` in local secrets/CI.
6. Add the Google OAuth web client id to the runtime configuration before enabling Google sign-in.
7. Run `./gradlew assembleDebug` or build from Android Studio, then install the APK on Android 9+.

## Security notes

- No API keys, OAuth client secrets, CNIC values, backup codes or tokens are stored in this repository.
- CNIC and recovery data must be hashed/encrypted server-side before production use; do not treat raw CNIC as a client-side lookup key.
- Call media should use WebRTC with short-lived signaling tokens and Firebase rules that scope reads/writes to conversation members.
- Production moderation, abuse reporting, rate limiting and legal/privacy review are required before release.

## Build limitation in this sandbox

This session does not include Android SDK/Gradle binaries, so the source is prepared for Android Studio/CI but an APK cannot be compiled inside the current sandbox. The repository is intentionally build-ready once the standard Android toolchain and service files are supplied.
