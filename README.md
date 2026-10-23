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
   If you see `403 Forbidden` from `repo.maven.apache.org` or `Connection timed out: getsockopt`, this project uses Google + Aliyun mirrors (Huawei is avoided — it hangs on many PK ISPs). Sync again after opening this folder. Still blocked? try a VPN, then Sync. **Do not** set an HTTP proxy in the IDE unless your campus/office network actually requires one.
3. Device Manager → create **Wear OS** virtual device (API 30+).  
   Prefer **Wear OS 3 / API 30** or **Wear OS 4 / API 33** if API 35 crashes on your PC.  
   In AVD settings: **RAM ≥ 1536 MB**, Graphics = **Software – GLES 2.0** (Intel HD GPUs often crash with Automatic/Hardware).  
4. Start the AVD from Device Manager ▶, wait until the watch face appears, then Run `app` once.

### Emulator crashed? (“process … has terminated”)
1. Device Manager → ▼ on the AVD → **Cold Boot Now** (or **Wipe Data**).  
2. If it still dies: edit the AVD → Graphics **Software**, RAM **1536+**.  
3. Avoid clicking Run while the device is still “activating”.  
4. Optional: close other heavy apps — Wear API 35 needs free RAM.

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
