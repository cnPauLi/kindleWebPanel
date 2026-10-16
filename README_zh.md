# kindleWebPanel

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-19-blue.svg)](#)

> 一个面向**墨水屏设备（如 Kindle）**的极简、省内存 Android **WebView 外壳**。
> 将它指向你自己的 Web 服务器，即可当作一个轻量级的**面板 / 仪表盘**使用。
> 请求地址**运行时即可配置**，无需重新打包。

[English](./README.md)

---

## ✨ 特性

- **轻量 WebView 外壳** —— 纯 `Activity` + `WebView`，不依赖重型框架，
  针对低内存墨水屏硬件优化。
- **请求地址可配置** —— 在设备上直接修改目标地址，无需重新编译打包。
- **隐藏设置入口** —— **连续点击屏幕左上角 5 次**，弹出「设置请求地址」对话框。
- **配置持久化** —— 地址保存在 `SharedPreferences` 中，下次启动自动恢复。
- **墨水屏友好界面** —— 全屏、关闭硬件加速、纯黑字白底渲染，避免灰阶模糊。
- **低内存占用** —— 关闭 AppCache / DOM Storage / 数据库、禁用缓存，
  退出时彻底销毁 `WebView` 防止内存泄漏。

## 📱 截图

_待补充：面板页面与设置对话框的截图。_

## 🚀 快速开始

### 环境要求

| 项目 | 版本 |
|------|------|
| Android SDK (min) | **API 19 (Android 4.4.2)** |
| Android SDK (compile) | API 37 |
| Android Gradle Plugin | 9.4.1 |
| Java | 11 |

### 构建

```bash
# 克隆
git clone git@github.com:cnPauLi/kindleWebPanel.git
cd kindleWebPanel

# 构建 debug 版 APK
./gradlew :app:assembleDebug
# 产物路径：app/build/outputs/apk/debug/app-debug.apk
```

### 安装到设备

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## ⚙️ 配置请求地址

1. 打开 App；
2. **快速连点屏幕左上角 5 次** —— 屏幕中央弹出「设置请求地址」对话框；
3. 输入地址（必须以 `http://` 或 `https://` 开头），例如
   `http://192.168.1.100:8080/`；
4. 点击「保存」—— 页面立即用新地址重新加载。

地址会被记住，下次启动直接生效。

### 可调参数

定义在 `MainActivity.java` 文件顶部：

| 常量 | 默认值 | 说明 |
|------|--------|------|
| `HIDDEN_TAP_COUNT` | `5` | 触发设置所需的连点次数 |
| `TAP_INTERVAL_MS` | `1500` | 两次点击的最大间隔（毫秒） |
| `HOTSPOT_WIDTH` / `HOTSPOT_HEIGHT` | `220` | 左上角热区大小（像素） |
| `DEFAULT_SERVER_URL` | `http://192.168.1.100:8080/page` | 首次启动的默认地址 |

## 🧪 本地测试环境

项目在 [`kindreader-server/`](./kindreader-server) 下自带一个**零依赖**的测试服务器，
无需部署任何东西即可验证面板：

```bash
cd kindreader-server
python3 serve.py 8080     # 或任意空闲端口
```

然后在 App 里配置 `http://<你的局域网IP>:8080/`。
详细说明见 [`kindreader-server/README.md`](./kindreader-server/README.md)。

## 📂 项目结构

```
kindleWebPanel/
├── app/
│   └── src/main/
│       ├── java/com/kindreader/MainActivity.java   # 全部逻辑
│       ├── res/                                     # 图标、字符串、主题
│       └── AndroidManifest.xml
├── kindreader-server/                               # 本地测试服务器（仅开发用）
│   ├── page/index.html
│   ├── serve.py
│   └── README.md
├── LICENSE
├── README.md
└── README_zh.md
```

## 🔒 权限说明

| 权限 | 用途 |
|------|------|
| `INTERNET` | 加载远程页面 |
| `ACCESS_NETWORK_STATE` | 可选的网络状态检测（可删除） |

## 🤝 贡献

欢迎提交 Issue 和 Pull Request。对于较大的改动，请先开 Issue 讨论你的想法。

1. Fork 本仓库
2. 新建特性分支（`git checkout -b feature/foo`）
3. 提交改动（`git commit -m 'Add foo'`）
4. 推送到分支（`git push origin feature/foo`）
5. 发起 Pull Request

## 📄 许可证

本项目基于 [MIT 协议](./LICENSE) 发布。

## ⚠️ 免责声明

本项目是独立的第三方开源工具，**与 Amazon 或 Kindle 品牌无任何隶属、认可或赞助关系**。
「Kindle」为其各自所有者的商标，此处仅用于描述目标设备类别（墨水屏阅读器）。
