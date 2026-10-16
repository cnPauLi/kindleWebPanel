# kindleWebPanel

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-19-blue.svg)](#)

> A minimal, memory-friendly Android **WebView shell** designed for **e-ink devices
> such as Kindle**. Point it at your own web server and use it as a lightweight
> dashboard / panel. The remote address is **configurable at runtime** — no
> re-packaging required.

[中文文档](./README_zh.md)

---

## ✨ Features

- **Lightweight WebView shell** — pure `Activity` + `WebView`, no heavy
  frameworks. Optimized for low-memory e-ink hardware.
- **Configurable server URL** — change the target address on-device, no rebuild.
- **Hidden settings entry** — tap the **top-left corner 5 times** in a row to
  open the "Set Request URL" dialog.
- **Persistent config** — the URL is stored in `SharedPreferences` and restored
  on next launch.
- **E-ink friendly UI** — full-screen, hardware acceleration off, pure
  black-on-white rendering to avoid grayscale blur.
- **Low memory footprint** — AppCache/DOM storage/database disabled, cache
  turned off, `WebView` fully destroyed on exit to prevent leaks.

## 📱 Screenshots

_TODO: add screenshots of the panel page and the settings dialog._

## 🚀 Getting Started

### Requirements

| Item | Version |
|------|---------|
| Android SDK (min) | **API 19 (Android 4.4.2)** |
| Android SDK (compile) | API 37 |
| Android Gradle Plugin | 9.4.1 |
| Java | 11 |

### Build

```bash
# Clone
git clone git@github.com:cnPauLi/kindleWebPanel.git
cd kindleWebPanel

# Build debug APK
./gradlew :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Install on your device

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## ⚙️ Configure the server URL

1. Launch the app.
2. **Quickly tap the top-left corner of the screen 5 times** — the
   *"Set Request URL"* dialog appears in the center of the screen.
3. Enter the address (must start with `http://` or `https://`), e.g.
   `http://192.168.1.100:8080/`.
4. Tap **Save** — the page reloads immediately with the new address.

The URL is remembered across launches.

### Tunable constants

Defined at the top of `MainActivity.java`:

| Constant | Default | Description |
|----------|---------|-------------|
| `HIDDEN_TAP_COUNT` | `5` | Number of taps to trigger settings |
| `TAP_INTERVAL_MS` | `1500` | Max interval between taps (ms) |
| `HOTSPOT_WIDTH` / `HOTSPOT_HEIGHT` | `220` | Top-left hotspot size (px) |
| `DEFAULT_SERVER_URL` | `http://192.168.1.100:8080/page` | Fallback address on first launch |

## 🧪 Local test environment

A zero-dependency test server is bundled under [`kindreader-server/`](./kindreader-server)
so you can verify the panel without deploying anything.

```bash
cd kindreader-server
python3 serve.py 8080     # or any free port
```

Then configure the app with `http://<your-lan-ip>:8080/`.
See [`kindreader-server/README.md`](./kindreader-server/README.md) for details.

## 📂 Project structure

```
kindleWebPanel/
├── app/
│   └── src/main/
│       ├── java/com/kindreader/MainActivity.java   # the whole app
│       ├── res/                                     # icons, strings, themes
│       └── AndroidManifest.xml
├── kindreader-server/                               # local test server (dev only)
│   ├── page/index.html
│   ├── serve.py
│   └── README.md
├── LICENSE
├── README.md
└── README_zh.md
```

## 🔒 Permissions

| Permission | Why |
|------------|-----|
| `INTERNET` | Load the remote page |
| `ACCESS_NETWORK_STATE` | Optional network state check (can be removed) |

## 🤝 Contributing

Issues and pull requests are welcome. For major changes, please open an issue
first to discuss what you would like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/foo`)
3. Commit your changes (`git commit -m 'Add foo'`)
4. Push to the branch (`git push origin feature/foo`)
5. Open a Pull Request

## 📄 License

Released under the [MIT License](./LICENSE).

## ⚠️ Disclaimer

This project is an independent, third-party open-source tool. It is **not
affiliated with, endorsed by, or sponsored by Amazon or the Kindle brand**.
"Kindle" is a trademark of its respective owner and is used here only to
describe the target device category (e-ink readers).
