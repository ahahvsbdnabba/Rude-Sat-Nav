# Rude Organic Maps APK builder

This is a phone-friendly GitHub Actions builder for Organic Maps 2026.08.27-18.
It checks out the exact upstream commit, applies `rude-navigation.patch`, and builds an installable Google ARM64 debug APK.

## Build from an Android phone

1. Create a new **public GitHub repository** from the GitHub mobile app or github.com.
2. Upload these files/folders from this package:
   - `rude-navigation.patch`
   - `.github/workflows/build-apk.yml`
   - `README.md` (optional)
3. Open the repository on GitHub.
4. Open **Actions** → **Build Rude Organic Maps APK** → **Run workflow**.
5. Wait for the green checkmark.
6. Open the completed workflow run and scroll to **Artifacts**.
7. Download `rude-organic-maps-google-debug` to your phone and unzip it.
8. Install the APK.

## Important

The APK is a debug-signed build. Because the package name is the same as the official Organic Maps app, Android will normally require you to uninstall the official Organic Maps first before installing this build. Back up/export anything important first.

The current patch adds:
- Rude navigation voice toggle.
- Random sweary English commentary for left/right, recalculation, wrong route, destination, roundabout, keep-left/right and U-turn instructions.
- Optional conversion of spoken English imperial distances from feet to rounded yards.

It does **not yet change the routing engine's notification schedule to exactly 500/300/200/100/50 yards**, and it does not have a native distinct missed-turn event. Those require deeper changes to the routing notification generator.
