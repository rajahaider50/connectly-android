# Connectly architecture

- `MainActivity.kt`: single Compose host and screens for onboarding-era MVP flows.
- `AppViewModel.kt`: route/state orchestration and optimistic chat message updates.
- `data/AppModels.kt`: domain models and service contracts; demo repositories make the UI runnable before credentials exist.
- `ui/Theme.kt`: premium dark visual system with violet/cyan accents.
- `app/service-config.example.properties`: non-secret configuration template.

The demo repositories are intentionally isolated behind interfaces. Replace them with Firebase-backed implementations after creating the Firebase project and adding `google-services.json`; this keeps UI code independent from service credentials and prevents tokens from being repeated through screens.
