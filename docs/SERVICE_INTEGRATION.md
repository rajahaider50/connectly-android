# Production service integration

## Firebase

- Add `google-services.json` under `app/` locally; it is ignored by Git.
- Enable Email/Password and Google providers in Firebase Authentication.
- Use Realtime Database rules that scope `/users/$uid`, `/chats/$chatId` and `/calls/$callId` to authenticated members.
- Enable FCM and register the device token after sign-in; send push notifications from a trusted server/Cloud Function, not directly from the client.

## Cloudinary

Use an unsigned upload preset restricted to the `connectly/` folder, allowed image/video/audio formats and a strict file-size limit. The client should upload only to that preset and persist the returned `secure_url` in Firebase. For sensitive media, use signed uploads through a server endpoint.

## Calls

The UI currently exposes audio/video entry points and runtime permission requests. Production calls should use a WebRTC SDK, with Firebase only for short-lived offer/answer/ICE signaling. Do not store microphone/video frames in Realtime Database.

## Account recovery

The UI offers phone, 13-digit CNIC and 8-digit backup-code pathways because they were requested. In production, never use raw CNIC as a database key and never compare raw backup codes. Store salted hashes server-side, rate-limit attempts, require explicit consent, and add an abuse lockout/audit trail. Phone recovery should use Firebase Phone Auth or another verified OTP provider.

## Supabase

Supabase is not required for the core Firebase path. If it is used for an admin or analytics extension, keep the anon key in build-time configuration and put all privileged operations behind server-side policies.
