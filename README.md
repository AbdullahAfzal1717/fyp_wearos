# VitalSync Wear OS

Standalone Wear OS app that replaces the Node simulator / phone Demo Stream as a **real wearable data source**.

It uses the **same backend contract** as everything else:

1. `POST /auth/login` → JWT  
2. `POST /vitals/upload` → heartRate, spo2, temperature, activityState, timestamp  

Live updates still go through Socket.IO to the web dashboard and VitalSync phone app.

```
[Wear OS app] ──POST /vitals/upload──► [backend] ──socket──► web + phone
```

---

## What is already built

| Piece | Status |
|--------|--------|
| Email/password login (USER role) | Done |
| Configurable API base URL | Done |
| Start / stop foreground stream | Done |
| Demo vitals (emulator-safe) | Done |
| Real HR via `TYPE_HEART_RATE` when sensor exists | Done (fallback to demo) |
| Offline queue + flush | Done |
| Shows last HR / SpO₂ / temp / risk | Done |

---

## Requirements

1. **Android Studio** (Ladybug / Koala or newer) with **Wear OS** system image  
2. Backend running (`backend/` → `npm run dev`)  
3. Seeded demo user: `patient1@test.com` / `123456`  
4. Optional: physical Wear OS watch on same Wi‑Fi as your PC  

---

## Open & run

1. Open Android Studio → **Open** → select this folder:  
   `Soldier-Health-System/wearos`
2. **File → Sync Project with Gradle Files** (first sync downloads deps).  
   If you see `403 Forbidden` from `repo.maven.apache.org`, this project already uses Google’s Maven Central mirror in `settings.gradle.kts`. Sync again after opening this folder. Still blocked? try a VPN, then Sync.
3. Device Manager → create **Wear OS** virtual device (API 30+).  
4. Run the `app` configuration on the Wear emulator.

### API URL cheat-sheet

| Where the app runs | API base URL |
|--------------------|--------------|
| Wear **emulator** | `http://10.0.2.2:5000` (maps to your PC localhost) |
| Real **watch** on LAN | `http://192.168.x.x:5000` (your PC’s Wi‑Fi IP — same idea as VitalSync `app.json`) |

The login screen defaults to the emulator host. Use **Emulator host** chip or type your LAN IP for a physical watch.

> Windows tip: backend should listen on `0.0.0.0:5000`. Prefer `127.0.0.1` / LAN IP over flaky `localhost` IPv6.

---

## Demo flow (viva)

1. Start Mongo + backend  
2. Open supervisor web dashboard (logged in as `ahmed@test.com`)  
3. Run Wear app → sign in as `patient1@test.com`  
4. Tap **Start stream**  
5. Dashboard patient card / live vitals update every ~4s  
6. Stop stream / log out when done  

---

## Project layout

```
wearos/
├── app/src/main/java/com/vitalsync/wear/
│   ├── data/          # login, upload, offline queue, session
│   ├── sensors/       # demo sampler + optional HR sensor
│   ├── stream/        # foreground service + UI bus
│   └── ui/            # login + stream screens (Wear Compose)
└── README.md
```

---

## Next upgrades (optional)

- Health Services continuous HR (newer watches)  
- Pairing QR from phone instead of typing password on watch  
- Push `activityState` from step/motion APIs only  
- Release signing + Play internal testing track  

Google OAuth is **not** required on Wear for FYP; email login is enough.
