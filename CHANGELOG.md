# Changelog

All notable changes to this project are documented in this file.
本项目所有重要变更都记录在此文件中。

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [1.0.0] - 2026-10-16

### Added

- Initial release. 首个版本。
- Minimal WebView shell optimized for e-ink / low-memory devices (minSdk 19).
- Runtime-configurable server URL persisted via `SharedPreferences`.
- Hidden settings entry: tap the top-left corner 5 times to open the
  "Set Request URL" dialog.
- E-ink friendly full-screen UI (hardware acceleration off, black-on-white).
- Low-memory WebView settings and proper `WebView` destruction on exit.
- Bundled zero-dependency local test server (`kindreader-server/serve.py`) with
  an e-ink optimized test page.
