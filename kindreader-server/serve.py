#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KindReader 本地测试服务器
=========================

零依赖，只用 Python 标准库。用于把 kindreader-server/page/ 下的页面
提供给 App 内的 WebView 加载测试。

用法：
    cd kindreader-server
    python3 serve.py            # 默认端口 8080
    python3 serve.py 9000       # 指定端口 9000

然后在 Kindle 上：
    1. 打开 App
    2. 快速连点屏幕左上角 5 次
    3. 填入本机地址，例如：http://192.168.1.100:8080/page/
       （把 192.168.1.100 换成运行本脚本的电脑的局域网 IP）
    4. 点击「保存」

本机局域网 IP 可用下面命令查看（macOS）：
    ipconfig getifaddr en0
"""

import os
import sys
import socket
import http.server
import socketserver

# 要提供服务的目录（页面根目录）
PAGE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "page")
# 默认端口
DEFAULT_PORT = 8080


def get_local_ip():
    """尽量探测本机在局域网中的 IP，仅用于友好提示。"""
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        # 不会真的发包，只是让系统选出默认出口网卡的 IP
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"


class Handler(http.server.SimpleHTTPRequestHandler):
    """在 PAGE_DIR 目录下提供静态文件；额外禁用缓存，方便反复调试。"""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=PAGE_DIR, **kwargs)

    def end_headers(self):
        # 调试期间禁用缓存，改完页面刷新即可见
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate")
        self.send_header("Pragma", "no-cache")
        self.send_header("Expires", "0")
        super().end_headers()

    def log_message(self, fmt, *args):
        # 打印带时间前缀的访问日志，便于观察 App 是否请求到了服务器
        sys.stderr.write("[%s] %s\n" % (self.log_date_time_string(), fmt % args))


class ReusableTCPServer(socketserver.ThreadingTCPServer):
    allow_reuse_address = True
    daemon_threads = True


def main():
    port = DEFAULT_PORT
    if len(sys.argv) > 1:
        try:
            port = int(sys.argv[1])
        except ValueError:
            print("端口必须是数字，例如：python3 serve.py 8080")
            sys.exit(1)

    if not os.path.isdir(PAGE_DIR):
        print("找不到页面目录：%s" % PAGE_DIR)
        sys.exit(1)

    handler = Handler
    with ReusableTCPServer(("0.0.0.0", port), handler) as httpd:
        ip = get_local_ip()
        print("=" * 56)
        print("KindReader 测试服务器已启动")
        print("-" * 56)
        print("服务目录 : %s" % PAGE_DIR)
        print("本机访问 : http://127.0.0.1:%d/" % port)
        print("局域网   : http://%s:%d/" % (ip, port))
        print("-" * 56)
        print("在 App 的「设置请求地址」里填入上面的【局域网】地址")
        print("按 Ctrl+C 停止服务器")
        print("=" * 56)
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\n已停止服务器。")


if __name__ == "__main__":
    main()
