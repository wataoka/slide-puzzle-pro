# Slide Puzzle Pro

## Goal
Publish a 15-puzzle Android app to Google Play Store.

## Strategy
Iterative launch cycle:
1. Develop a simple, working 15 puzzle
2. Launch to Google Play Store (MVP)
3. Improve features and design
4. Re-launch / update
5. Repeat

Always prioritize getting something releasable over adding features. Keep Play Store requirements in mind at every step.

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose
- Build: Gradle (KTS)
- minSdk: 24, targetSdk: 34
- Package: `com.wataoka.slidepuzzle`

## Project Structure
- `app/src/main/java/com/wataoka/slidepuzzle/`
  - `MainActivity.kt` — entry point, game screen UI
  - `PuzzleState.kt` — immutable game state, shuffle/move/solve logic
  - `Board.kt` — Compose grid UI component

## Current Status
**Play Store submission in progress — waiting for Google identity verification.**

### Play Store Submission Checklist
- [x] 1. Generate signing keystore
- [x] 2. App icons (mipmap + 512×512 for Play Store)
- [x] 3. Build signed release AAB
- [x] 4. Privacy policy (GitHub Pages)
- [x] 5. Store listing copy
- [x] 6. Screenshots
- [ ] 7. Google identity verification (submitted — pending)
- [ ] 8. Upload AAB + submit for review

### Store Listing Copy
- **Title:** Slide Puzzle Pro
- **Short description:** The classic 15-puzzle. Slide the tiles. Beat your time.
- **Full description:**
  > A clean, no-nonsense 15-puzzle with nothing in the way.
  >
  > No ads, no accounts, and no internet required — just you and the puzzle.
  > Pick it up anytime, put it down anytime. It's always ready when you are.
- **Category:** Puzzle / Brain & Puzzle Games
- **Privacy policy URL:** https://wataoka.github.io/slide-puzzle-pro/privacy_policy.html

### Signing
- Keystore: `~/keystores/slidepuzzle.jks` (alias: `slidepuzzle`, validity: 10,000 days)
- Credentials stored in `keystore.properties` (gitignored — never commit)
- ⚠️ Back up the keystore file externally — losing it means you can never update the app

### Release Assets (in `store_assets/`)
- `ic_launcher_512.png` — Play Store icon
- `screenshot1.png` — in-game screenshot
- `screenshot_title.png` — branded title card
- `privacy_policy.html` — source for GitHub Pages

### AAB Location
`app/build/outputs/bundle/release/app-release.aab` (1.2 MB, signed)

## Build Notes
No system Java or Gradle installed. The `./gradlew` script has an issue — use this instead:

```bash
cd "/Users/wataoka/Documents/Claude/Projects/Slide Puzzle Pro"
"/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java" \
  -classpath gradle/wrapper/gradle-wrapper.jar \
  org.gradle.wrapper.GradleWrapperMain \
  bundleRelease
```

Common tasks:
- `bundleRelease` — build signed release AAB
- `assembleDebug` — build debug APK for emulator testing

## Feature Tiers
- **v1 (MVP):** Working 4×4 15 puzzle, move counter, timer, new game button, solve detection
- **Post-launch:** Additional grid sizes, image puzzles, leaderboard, animations, etc.
