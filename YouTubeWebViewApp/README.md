# YouTube WebView App

A full-screen Android WebView application that loads YouTube and automatically removes Shorts-related content.

## Features

- **Full-screen YouTube experience** - Loads the official YouTube website in an immersive WebView
- **Shorts URL redirection** - Automatically redirects `/shorts/VIDEO_ID` URLs to standard `watch?v=VIDEO_ID` format
- **Shorts UI removal** - JavaScript injection to hide Shorts shelves, navigation tabs, and related UI elements
- **Minimal permissions** - Only requires `INTERNET` permission
- **Hardware acceleration** - Optimized for smooth video playback
- **Back navigation support** - Proper WebView navigation handling

## Technical Specifications

- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Language**: Java
- **Build System**: Gradle with Android Gradle Plugin 8.1.2

## Project Structure

```
YouTubeWebViewApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/youtubewebview/app/
│   │   │   └── MainActivity.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   └── styles.xml
│   │   │   ├── xml/
│   │   │   │   ├── backup_rules.xml
│   │   │   │   └── data_extraction_rules.xml
│   │   │   └── mipmap-*/
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Installation

1. **Import into Android Studio**:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to and select the `YouTubeWebViewApp` folder

2. **Build and Run**:
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift+F10
   - Grant any required permissions when prompted

3. **Alternative APK Installation**:
   - Build the project in Android Studio
   - Install the generated APK on your device

## Key Implementation Details

### URL Interception
The app intercepts navigation in `shouldOverrideUrlLoading()` and converts Shorts URLs:
- `https://www.youtube.com/shorts/ABC123` → `https://www.youtube.com/watch?v=ABC123`

### JavaScript Injection
Comprehensive script injection removes Shorts elements:
- Shorts shelf sections
- Navigation tabs
- Suggestion links
- Mobile buttons
- Dynamic content via MutationObserver

### WebView Configuration
Optimized settings for YouTube:
- Desktop user agent for full experience
- Hardware acceleration enabled
- JavaScript and DOM storage enabled
- Media playback without user gesture
- Zoom controls with hidden UI

## Permissions

- `android.permission.INTERNET` - Required for loading YouTube
- `android.permission.ACCESS_NETWORK_STATE` - For network connectivity checks

## Compatibility

- **Android 5.0+** (API level 21 and above)
- **WebView-enabled devices** (standard on all Android devices)
- **Internet connection required**

## Troubleshooting

1. **YouTube not loading**: Check internet connection and ensure the device has a modern WebView implementation
2. **Videos not playing**: Ensure hardware acceleration is enabled and the device supports modern web standards
3. **Shorts still appearing**: The JavaScript injection runs continuously, but some elements may appear briefly before being hidden

## Development Notes

- The app uses immersive full-screen mode for the best viewing experience
- JavaScript injection runs on page load and continuously monitors for new content
- WebView is configured with desktop user agent to access full YouTube features
- ProGuard rules preserve JavaScript interface methods for release builds

## License

This project is for educational and personal use. Please respect YouTube's Terms of Service when using this application.
