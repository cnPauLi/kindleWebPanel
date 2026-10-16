# Contributing to kindleWebPanel

Thanks for your interest in contributing! This document explains how to get
started. / 感谢你愿意为本项目做贡献！

## Code of Conduct / 行为准则

Be respectful and constructive. Harassment or abusive behavior will not be
tolerated. / 请保持尊重与建设性，禁止任何骚扰或攻击性行为。

## How to Contribute / 如何贡献

### Reporting Bugs / 报告 Bug

Open an Issue and include:

- Device model and Android version (e.g. Kindle `IMX6SL`, Android 4.4.2)
- Steps to reproduce
- Expected vs. actual behavior
- Relevant `adb logcat` output

### Suggesting Features / 建议新功能

Open an Issue describing the feature and its use case. Keep in mind the
project's goal: **stay lightweight and work on low-memory e-ink hardware**.

### Pull Requests / 提交 PR

1. Fork the repo and create a branch: `git checkout -b feature/your-feature`
2. Keep changes focused and minimal.
3. Test on a real device if possible (e-ink devices are the primary target).
4. Make sure the project builds: `./gradlew :app:assembleDebug`
5. Open a PR with a clear description of *what* and *why*.

## Coding Guidelines / 编码规范

- Target **minSdk 19** — do not use APIs unavailable on Android 4.4 without a
  version guard / reflection fallback.
- Avoid adding heavy dependencies (AndroidX Compose, big libraries). The app is
  intentionally a plain `Activity` + `WebView`.
- Keep the memory footprint low (disable unnecessary WebView features).
- Match the existing code style and comments (bilingual comments are welcome).

## License

By contributing, you agree that your contributions will be licensed under the
[MIT License](./LICENSE).
