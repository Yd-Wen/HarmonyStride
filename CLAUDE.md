# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HarmonyStride (与你同行) is an Android social platform app targeting users with disabilities, volunteers, and employers. It enables users to create posts (job listings, help requests, volunteer opportunities), apply to posts, and chat via instant messaging.

## Build Commands

This project uses the Gradle wrapper for builds:

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build artifacts
./gradlew clean

# Install debug build on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Project Architecture

### App Structure

The app follows a standard Android architecture pattern with Activities and Fragments:

- **Entry Point**: `WelcomeActivity` → `LoginActivity`/`RegisterActivity` → `MainActivity`
- **Main Navigation**: `MainActivity` hosts three Fragments via BottomNavigationView:
  - `HomeFragment` - Post feed with filtering/sorting
  - `StatusFragment` - User profile and personal posts
  - `MessageFragment` - Chat conversation list (RongCloud IM)

### Package Organization

```
com.srdp.harmonystride/
├── activity/       # All Activities (screens)
├── adapter/        # RecyclerView adapters for lists
├── dialog/         # Custom dialogs with builder/factory pattern
│   ├── builder/
│   └── factory/
├── entity/         # Data models (User, Post, Certification, etc.)
├── fragment/       # Main screen Fragments
└── util/           # Utility classes
```

### Key Dependencies

- **UI**: Material Design, SmartRefreshLayout (pull-to-refresh), Banner (carousel)
- **Image Loading**: Glide (with circle crop transformation for avatars)
- **Database**: LitePal (ORM for local SQLite storage)
- **Networking**: OkHttp (REST API calls via `HTTPUtil`)
- **IM**: RongCloud SDK (real-time messaging)
- **SMS**: Mob SMS SDK (verification codes)
- **Storage**: Aliyun OSS (image uploads)
- **Rich Text**: richeditor-android (post content editing)

### Backend API

The app communicates with a Spring Boot backend at `http://yindongwen.top:8080`. See `HTTPUtil.java` for available endpoints:
- User: `/user/register`, `/user/verify`, `/user/find`, `/user/update`
- Post: `/post/*` endpoints
- Certification: `/certification/*` for identity verification
- App Update: `/app/check` for version checking

### Instant Messaging (RongCloud)

- App Key is configured in `MyApplication.java`
- User info provider fetches from backend via `HTTPUtil.getUserByAccount()`
- Avatar images are displayed as circles using Glide transformations
- Conversation list is integrated into the main UI via `RouteUtils.registerActivity()`

### Local Database (LitePal)

Configured in `assets/litepal.xml`. Currently only stores the `User` entity locally. Call `LitePal.initialize(context)` before use (done in `MyApplication`).

### Important Implementation Details

- **BaseActivity**: All activities extend this; provides toast helper, navigation methods, and ActivityResultLauncher setup
- **Image Upload Flow**: Local image → Aliyun OSS → get URL → send to backend
- **Version Update**: `DownloadUtil` checks for updates, downloads APK, and triggers install via FileProvider
- **Permission Handling**: Uses EasyPermissions library for runtime permissions
- **Fragment Management**: MainActivity uses add/hide/show pattern rather than replace for performance

## Development Notes

- Compile SDK: 32, Min SDK: 26, Target SDK: 32
- Java 8 compatibility
- AndroidX enabled with Jetifier
- Uses Aliyun Maven mirrors for faster builds in China
- ProGuard is disabled (`minifyEnabled false`)
