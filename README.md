# 与你同行 (HarmonyStride)

<p align="center">
  <img src="logo.png" alt="应用图标" width="100"/>
</p>


<p align="center">
  <b>一个连接残障人士、志愿者与用人单位的社交平台</b>
</p>

---

## 📖 项目简介

**与你同行**（HarmonyStride）是一款专为残障人士、志愿者和用人单位打造的Android社交应用。用户可以在平台上发布帖子（求职、招聘、求助、志愿活动），申请感兴趣的帖子，并通过即时通讯功能进行沟通交流。

- 仓库地址：

  - https://github.com/Yd-Wen/HarmonyStride
  - https://gitee.com/yindong-wen/HarmonyStride
- 功能演示
  - https://oss.yindongwen.top/demo/HarmonyStride_01.mp4
  - https://oss.yindongwen.top/demo/HarmonyStride_02.mp4

### 核心功能

- 📋 **帖子系统**：发布和浏览求职、招聘、求助、志愿等活动信息
- 💬 **即时通讯**：基于融云IM的实时聊天功能
- 👤 **用户认证**：支持残障人士、志愿者、用人单位等多种身份认证
- 📝 **申请管理**：帖子申请与审批流程
- 🔔 **版本更新**：内置应用更新检测与下载功能



---

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| 开发语言 | Java 8 |
| 开发平台 | Android SDK 32 (Android 12) |
| 最低支持 | Android 8.0 (API 26) |
| 构建工具 | Gradle 7.2.2 |
| 网络请求 | OkHttp 3.8.1 |
| 图片加载 | Glide 4.12.0 |
| 本地数据库 | LitePal 3.2.3 |
| 即时通讯 | 融云IM SDK 5.6.10 |
| 短信验证 | Mob SMS SDK |
| 云存储 | 阿里云OSS |
| UI组件 | Material Design, SmartRefreshLayout, Banner |

---

## 📁 项目结构

```
HarmonyStride/
├── app/
│   ├── src/main/java/com/srdp/harmonystride/
│   │   ├── activity/          # Activity页面
│   │   │   ├── WelcomeActivity.java      # 欢迎页
│   │   │   ├── LoginActivity.java        # 登录页
│   │   │   ├── RegisterActivity.java     # 注册页
│   │   │   ├── MainActivity.java         # 主页面（含底部导航）
│   │   │   ├── PostActivity.java         # 帖子详情页
│   │   │   ├── PostingActivity.java      # 发布帖子页
│   │   │   ├── ChatActivity.java         # 聊天页面
│   │   │   ├── ProfileActivity.java      # 个人资料页
│   │   │   └── ...
│   │   ├── adapter/           # RecyclerView适配器
│   │   ├── dialog/            # 自定义对话框
│   │   │   ├── builder/       # 对话框构建器
│   │   │   └── factory/       # 对话框工厂
│   │   ├── entity/            # 数据实体类
│   │   ├── fragment/          # Fragment碎片
│   │   │   ├── HomeFragment.java         # 首页帖子列表
│   │   │   ├── StatusFragment.java       # 动态/个人中心
│   │   │   └── MessageFragment.java      # 消息列表
│   │   └── util/              # 工具类
│   │       ├── HTTPUtil.java             # 网络请求工具
│   │       ├── ImageUtil.java            # 图片处理工具
│   │       ├── DownloadUtil.java         # 下载更新工具
│   │       ├── OSSClientUtil.java        # 阿里云OSS工具
│   │       └── ...
│   ├── src/main/res/          # 资源文件
│   │   ├── layout/            # 布局文件
│   │   ├── drawable/          # 图片资源
│   │   ├── values/            # 字符串、颜色等资源
│   │   └── xml/               # 配置文件
│   └── build.gradle           # 模块级构建配置
├── build.gradle               # 项目级构建配置
├── settings.gradle            # 项目设置
├── gradle.properties          # Gradle属性配置
└── README.md                  # 本文件
```

---

## 📦 项目依赖

### 核心依赖

```gradle
// AndroidX 基础库
implementation 'androidx.appcompat:appcompat:1.3.0'
implementation 'com.google.android.material:material:1.4.0'
implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
```

### 下拉刷新与加载

**SmartRefreshLayout** - 强大的下拉刷新和上拉加载库

- 仓库地址：https://github.com/scwang90/SmartRefreshLayout
- 文档：https://github.com/scwang90/SmartRefreshLayout/blob/main/art/md_property.md

```gradle
implementation 'io.github.scwang90:refresh-layout-kernel:2.1.0'      //核心必须依赖
implementation 'io.github.scwang90:refresh-header-classics:2.1.0'    //经典刷新头
implementation 'io.github.scwang90:refresh-header-radar:2.1.0'       //雷达刷新头
implementation 'io.github.scwang90:refresh-header-material:2.1.0'    //谷歌刷新头
implementation 'io.github.scwang90:refresh-footer-classics:2.1.0'    //经典加载
```

### 富文本编辑

**richeditor-android** - Android原生富文本编辑器

- 仓库地址：https://github.com/wasabeef/richeditor-android

```gradle
implementation 'jp.wasabeef:richeditor-android:2.0.0'
```

### 轮播图

**Banner** - Android图片轮播控件

- 仓库地址：https://github.com/youth5201314/banner

```gradle
implementation 'io.github.youth5201314:banner:2.2.3'
```

### 图片加载

**Glide** - 快速高效的Android图片加载库

- 仓库地址：https://github.com/bumptech/glide
- 文档：https://bumptech.github.io/glide/

```gradle
implementation 'com.github.bumptech.glide:glide:4.12.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
```

### 圆形图片

**CircleImageView** - 圆形ImageView

- 仓库地址：https://github.com/hdodenhof/CircleImageView

```gradle
implementation 'de.hdodenhof:circleimageview:3.1.0'
```

### 权限管理

**EasyPermissions** - 简化Android权限处理

- 仓库地址：https://github.com/googlesamples/easypermissions

```gradle
implementation 'pub.devrel:easypermissions:3.0.0'
```

### 数据库

**LitePal** - Android开源数据库框架

- 仓库地址：https://github.com/guolindev/LitePal
- 文档：https://github.com/guolindev/LitePal/tree/master/README.md

```gradle
implementation 'org.litepal.guolindev:core:3.2.3'
```

### 网络请求

**OkHttp** - 高效的HTTP客户端

- 仓库地址：https://github.com/square/okhttp
- 文档：https://square.github.io/okhttp/

```gradle
implementation 'com.squareup.okhttp3:okhttp:3.8.1'
```

**Gson** - JSON解析库

- 仓库地址：https://github.com/google/gson

```gradle
implementation 'com.google.code.gson:gson:2.9.0'
```

### 即时通讯

**融云IM SDK** - 即时通讯云服务

- 官网：https://www.rongcloud.cn/

```gradle
// 即时通讯 UI 基础组件
implementation 'cn.rongcloud.sdk:im_kit:5.6.10'
// 即时通讯基础能力库
implementation 'cn.rongcloud.sdk:im_lib:5.6.10'
```

### 短信验证

**Mob SMS SDK** - 短信验证码服务

- 官网：https://www.mob.com/

```gradle
// 在project的build.gradle中添加插件
id 'com.mob.sdk' version '+' apply false

// 在app的build.gradle中配置
plugins {
    id 'com.mob.sdk'
}
MobSDK {
    appKey "your_app_key"
    appSecret "your_app_secret"
    spEdition "IZNAO"
    SMSSDK {}
}
```

### 阿里云OSS

**阿里云对象存储SDK** - 图片等资源上传

- 文档：https://help.aliyun.com/document_detail/32043.html

```gradle
implementation 'com.aliyun.dpa:oss-android-sdk:2.9.13'
```

### GIF支持

**android-gif-drawable** - 支持GIF动画显示

- 仓库地址：https://github.com/koral--/android-gif-drawable

```gradle
implementation 'pl.droidsonroids.gif:android-gif-drawable:1.2.28'
```

---

## 🚀 部署步骤

### 环境要求

- Android Studio Arctic Fox (2020.3.1) 或更高版本
- JDK 1.8 或更高版本
- Android SDK 32
- Gradle 7.2+

### 本地部署

1. **克隆项目**

```bash
git clone https://github.com/Yd-Wen/HarmonyStride.git
cd HarmonyStride
```

2. **使用Android Studio打开**

   - 打开 Android Studio
   - 选择 `File -> Open`，选择项目根目录
   - 等待Gradle同步完成

3. **配置本地属性**（如需要）

   编辑 `local.properties` 文件，配置SDK路径：

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

4. **构建项目**

   通过 Android Studio 的 `Build -> Make Project` (Ctrl+F9)

   或使用命令行：

```bash
# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

5. **运行应用**

   - 连接Android设备或启动模拟器（API 26+）
   - 点击 `Run -> Run 'app'` (Shift+F10)

### 生成发布版本

```bash
# 构建Release版本
./gradlew assembleRelease

# APK输出路径
app/release/app-release.apk
```

---

## ⚙️ 后端服务

本项目需要配合后端服务使用，后端API默认地址配置在 `HTTPUtil.java` 中：

```java
public static final String IP = "your-backend-address";
```

后端技术栈：
- **框架**：Spring Boot
- **数据库**：MySQL
- **文件存储**：阿里云OSS
- **即时通讯**：融云IM

---

## 📝 更新日志

### [Unreleased] - 2026-04-01
- 完善项目README文档
- 添加Apache License 2.0开源协议
- 添加NOTICE文件声明第三方依赖归属
- 添加CLAUDE.md文件用于AI辅助开发

### [1.2.4] - 2024-05-30
- 新增轮播图功能
- 新增版本更新检测与下载功能

### [1.2.3] - 2024-04-29
- 修复底部菜单文字显示问题

### [1.2.2] - 2024-04-13
- 优化整体UI界面
- 优化业务处理逻辑

### [1.2.1] - 2024-04-12
- 整体功能优化和调整

### [1.2.0] - 2024-04-06
- 优化聊天功能体验
- 优化融云IM注册和登录流程

### [1.1.0] - 2024-03-24
- 新增评论功能

### [1.0.9] - 2024-03-18
- 完成申请提交、查看与管理功能
- 完成账号安全设置
- 完成帖子设置功能

### [1.0.8] - 2024-03-17
- 新增设置页面
- 新增个人帖子管理功能
- 新增申请管理功能

### [1.0.7] - 2024-03-15
- 新增搜索页面功能

### [1.0.6] - 2024-03-14
- 优化Fragment布局结构

### [1.0.5] - 2024-03-10
- 新增帖子详情页
- 用户主页新增帖子列表展示

### [1.0.4] - 2024-03-08
- 优化用户认证流程
- 新增发布帖子功能
- 新增浏览帖子功能

### [1.0.3] - 2024-03-05
- 优化ButtonDialog/EditTextDialog对话框
- 优化图片上传操作
- 完成认证页上传和修改功能

### [1.0.2] - 2024-03-03
- 优化Radio Button Dialog
- 新增用户认证页面

### [1.0.1] - 2024-03-01
- 修复聊天页面数据加载问题

### [1.0.0] - 2024-02-28
- 项目初始版本
- 实现基础框架搭建
- 实现用户登录注册
- 实现基础即时通讯功能

---

## 🤝 开源协议

本项目采用 [Apache License 2.0](LICENSE) 协议开源。

### 许可证文件说明

根据 Apache License 2.0 要求，本项目包含以下许可证相关文件：

| 文件 | 说明 |
|------|------|
| [LICENSE](LICENSE) | Apache License 2.0 完整许可证文本 |
| [NOTICE](NOTICE) | 版权声明和第三方依赖声明 |

### 快速参考

```
Copyright 2024 HarmonyStride Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### 使用须知

1. **保留声明**：分发本软件或其衍生作品时，必须保留 LICENSE 和 NOTICE 文件
2. **修改声明**：修改文件时需添加显著声明，说明文件已被更改
3. **归属声明**：必须在源代码形式中保留所有版权、专利、商标和归属声明

---

## 👨‍💻 作者

**HarmonyStride Team**

- 主要开发者：[Yd Wen]
- 联系邮箱：[wenyindong@163.com]
- 项目主页：
  - https://github.com/Yd-Wen/HarmonyStride
  - https://gitee.com/yindong-wen/HarmonyStride

---

## 🙏 致谢

感谢以下开源项目和库：

### AndroidX 官方库
- [AndroidX AppCompat](https://github.com/androidx/androidx)
- [AndroidX ConstraintLayout](https://github.com/androidx/constraintlayout)
- [AndroidX SwipeRefreshLayout](https://github.com/androidx/androidx)
- [AndroidX CardView](https://github.com/androidx/androidx)
- [AndroidX RecyclerView](https://github.com/androidx/androidx)
- [AndroidX Room](https://github.com/androidx/androidx)

### Material Design
- [Material Components for Android](https://github.com/material-components/material-components-android)

### 网络与数据
- [OkHttp](https://github.com/square/okhttp)
- [Gson](https://github.com/google/gson)
- [LitePal](https://github.com/guolindev/LitePal)

### 图片处理
- [Glide](https://github.com/bumptech/glide)
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)
- [Android GIF Drawable](https://github.com/koral--/android-gif-drawable)

### UI 组件
- [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)
- [Banner](https://github.com/youth5201314/banner)
- [RichEditor Android](https://github.com/wasabeef/richeditor-android)

### 权限管理
- [EasyPermissions](https://github.com/googlesamples/easypermissions)

### 云服务
- [阿里云 OSS Android SDK](https://github.com/aliyun/aliyun-oss-android-sdk)
- [融云 IM SDK](https://www.rongcloud.cn/)
- [Mob SMS SDK](https://www.mob.com/)

---

<p align="center">
  Made with ❤️ for a more inclusive world !
</p>
