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
App builds and runs on emulator (Pixel 8a, API 35, `emulator-5554`). Game logic and UI confirmed working.

**Next step: Play Store release**
1. Generate a signing keystore
2. Build a signed release AAB
3. Register Google Play Developer account ($25 one-time fee)
4. Create store listing (512×512 icon, screenshots, description, privacy policy)
5. Submit for review

## Build Notes
No system Java or Gradle installed. Use these when building from terminal:
- `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
- Gradle binary: `~/.gradle/wrapper/dists/gradle-8.7-bin/bhs2wmbdwecv87pi65oeuq5iu/gradle-8.7/bin/gradle`
- Project flag: `-p "/Users/wataoka/Documents/Claude/Projects/Slide Puzzle Pro"`

## Feature Tiers
- **v1 (MVP):** Working 4×4 15 puzzle, move counter, timer, new game button, solve detection
- **Post-launch:** Additional grid sizes, image puzzles, leaderboard, animations, etc.
