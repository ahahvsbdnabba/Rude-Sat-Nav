# Rude-Sat-Nav

A custom Android build of Organic Maps with optional rude/random voice commentary.

## Features

- Random rude commentary for navigation instructions
- Different phrases for left and right turns
- Recalculation commentary
- Wrong-route commentary
- Destination-arrival commentary
- U-turn commentary
- Roundabout commentary
- Keep-left and keep-right commentary
- Optional conversion of spoken feet to yards
- Driver-name support
- Optional speeding commentary
- Optional harsh-braking commentary
- Optional fast-corner commentary
- Uses the Android system Text-to-Speech engine
- ARM64 Google Debug APK build
- Preserves the Android Auto support provided by the selected Organic Maps revision

## Important

Rude commentary is optional. Normal Organic Maps navigation remains available when the feature is disabled.

The custom voice text is passed through Android's normal Text-to-Speech system. This project does not bundle or redistribute Google's proprietary Google Maps navigation voice.

## Build

The GitHub Actions workflow builds the selected Organic Maps revision and applies the custom patch.

The workflow is:

`.github/workflows/build-apk.yml`

Run it from:

**GitHub → Actions → Build Rude Organic Maps APK → Run workflow**

The resulting APK is uploaded as a GitHub Actions artifact.

## Architecture

The current workflow builds:

```text
arm64-v8a
