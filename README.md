<div align="center">
<p><img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="100"></p>

---
# AndroidFileCleanup
### 找出占用你磁盘空间最多的文件，并一键清理！

---

[![license: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](./LICENCE)

</div>

-----------

**AndroidFileCleanup** 是基于开源项目 [disky](https://github.com/newhinton/disky)（作者 newhinton / Felix Nüssee）深度改造而来的安卓存储清理工具。

经过对权限模型、筛选系统、批量管理、性能与界面的全面重构，**本项目已经不再只是原版的简单分支，而是相当于一个全新的独立工具**：定位从"存储空间分析"升级为"分析 + 安全清理"一体化的文件清理应用。

主要特性
--------

- **快速** 即使是较大的设备，也能在几秒钟内完成扫描！
- **精准筛选** 按文件类型、大小、时长、分辨率多维度组合筛选，快速定位目标文件。
- **批量清理** 复选框直接勾选文件或文件夹，底部操作栏一键安全删除。
- **高效刷新** 删除文件后直接更新内存中的文件树并局部刷新界面，无需重新扫描。
- **多存储支持** 支持所有本地文件系统！
- **现代界面** 全新商务风配色方案，各部件辨识度高。

相比原版 disky 的主要改进
--------

### 1. 定位升级与应用更名
- 应用更名为 **AndroidFileCleanup**，从"磁盘空间分析工具"升级为"文件清理工具"。

### 2. 权限精简
- 移除了"使用情况访问"权限（PACKAGE_USAGE_STATS）的申请，应用不再需要该敏感权限。
- 移除了应用扫描相关的谷歌开发者认证警告提示。
- 仅需"所有文件访问"权限（MANAGE_EXTERNAL_STORAGE，Android 11+），权限申请流程更简洁可靠。

### 3. 筛选系统增强
- 文件类型支持多选筛选（音频/视频/图片等），点击即时生效。
- 文件大小筛选加入更多档位：1KB / 10KB / 50KB / 100KB / 500KB。
- 时长筛选加入 10秒 / 20秒 / 30秒 选项。
- 支持分辨率筛选。
- 筛选后自动隐藏不含匹配文件的空文件夹，只显示自身或子文件夹中存在匹配项的目录。
- 应用筛选后，顶部概览栏显示的是**筛选结果的大小**，而非全部文件的总大小。

### 4. 批量选择与安全删除
- 文件/文件夹的复选框始终可见，点击复选框即进入选择模式。
- 只要有任意文件或文件夹被勾选，屏幕底部的操作栏（取消选择/删除选中）即会出现，无需全选。
- 操作栏固定于屏幕底部，不再是悬浮按钮。
- 删除操作均有确认对话框，防止误删。

### 5. 性能优化
- 删除文件后直接从内存中的存储树移除对应节点，并局部刷新列表与概览栏，**不再触发全量重新扫描**，批量删除效率大幅提升。

### 6. 界面重绘
- 全新现代化商务风配色方案（靛蓝主色 + 青绿次色 + 琥珀点缀），界面各部件辨识度高，同时保留 Material You 动态主题能力。

系统要求
--------
- Android 11（API 30）及以上，目标 SDK 36。
- 需要"所有文件访问"权限以扫描和删除文件。

构建
------------

首先，请确保您已克隆此仓库：

```sh
git clone <本仓库地址>
```

然后，您可以通过 Android Studio 或 CLI 正常构建应用：

```sh
# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease
```

致谢与许可
-----------------
### 许可证
本项目沿用上游项目的 [GPLv3 许可证](./LICENCE) 发布。
本项目的诞生离不开原版 [disky](https://github.com/newhinton/disky) 及其作者 Felix Nüssee 的杰出工作，在此致以诚挚感谢。如果您愿意支持原作者，欢迎通过以下链接捐赠：

[Paypal](https://www.paypal.com/paypalme/felixnuesse) | [Liberapay](https://liberapay.com/newhinton) | [Github Sponsor](https://github.com/sponsors/newhinton)

### 依赖库
- [AppIntro](https://github.com/AppIntro/AppIntro) - 这个库负责应用的易用性介绍！
- [Lottie](https://github.com/airbnb/lottie-android) - 这个库使精美的动画成为可能！
- [Glide](https://github.com/bumptech/glide) - 高效的图片加载库，用于文件缩略图预览！
- [Undraw](https://undraw.co/) - 严格来说不是一个库，但没有 Undraw 的图片，这个应用就不会是现在的样子！

感谢所有这些项目！
