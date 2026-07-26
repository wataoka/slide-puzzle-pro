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
- minSdk: 24, targetSdk: 35 (Play requires ≥35 as of 2026)
- Package: `com.wataoka.slidepuzzle`

## Project Structure
- `app/src/main/java/com/wataoka/slidepuzzle/`
  - `MainActivity.kt` — entry point, game screen UI
  - `PuzzleState.kt` — immutable game state, shuffle/move/solve logic
  - `Board.kt` — Compose grid UI component

## Current Status
**Applied for Production access (Jul 20). Waiting on Google's review.**

> **NEXT ACTION (resume here):**
> 1. Application submitted via Play Console Dashboard on Jul 20, 10:47 PM. Google says review
>    usually takes 7 days or less; they'll email the account owner (wataoka.koki@gmail.com) with
>    an update.
> 2. Check Play Console → Dashboard for status changes if no email arrives in ~7 days.
> 3. After approval, app goes live to all users — no further action needed unless Google asks
>    follow-up questions.

### Closed Testing Progress (as of Jun 3)
- Google Group created: `slide-puzzle-pro-testers@googlegroups.com`
  - Join link: https://groups.google.com/g/slide-puzzle-pro-testers
  - Privacy: **Anyone on the web can join** (confirmed — no approval required)
- Alpha track: **Active · Release 2 (1.1) in review · 177 countries / regions**
- Store listing: saved and confirmed (Save button was greyed out = already saved) ✅
- 14 changes sent for review (Jun 3) ✅
- Opt-in URLs (active):
  - Web: https://play.google.com/apps/testing/com.wataoka.slidepuzzle
  - Android: https://play.google.com/store/apps/details?id=com.wataoka.slidepuzzle
- Tester recruiting: **done** — posted on r/TestMyApp, r/AndroidClosedTesting, r/TestersCommunity, r/AndroidAppTesters ✅

### Tester Recruiting — Communities
| Community | Status |
|---|---|
| r/TestMyApp | ✅ Posted |
| r/AndroidClosedTesting | ✅ Posted |
| r/TestersCommunity | ✅ Posted |
| r/AndroidAppTesters | ✅ Posted |

### Play Store Submission Checklist
- [x] 1. Generate signing keystore
- [x] 2. App icons (mipmap + 512×512 for Play Store)
- [x] 3. Build signed release AAB
- [x] 4. Privacy policy (GitHub Pages)
- [x] 5. Store listing copy
- [x] 6. Screenshots
- [x] 7. Google identity verification (cleared Jun 2)
- [x] 8. Upload AAB + release to internal testing track
- [x] 9. All 10 App content declarations completed (Data safety, Content ratings, Target audience, Ads, Privacy policy, App access, Advertising ID, Government apps, Financial features, Health apps)
- [x] 10. Save store listing + publish closed test release (14 changes sent for review Jun 3)
- [x] 11. Recruit 12+ testers, run 14-day closed test (confirmed done in Play Console Jul 20 — all 3 requirement checks are green)
- [x] 12. Promote to Production + submit for review (application submitted Jul 20, 10:47 PM — awaiting Google's review, usually ≤7 days)

### Store Listing Copy
- **Title:** Slide Puzzle Pro
- **Short description:** The classic 15-puzzle. Slide the tiles. Beat your time.
- **Full description** (⚠️ **stale — says "No ads", but ads are now being added; rewrite before next release**):
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
- `ic_launcher_512.png` — Play Store icon (512×512)
- `feature_graphic.png` — Play Store feature graphic (1024×500)
- `screenshot1.png` — in-game screenshot
- `screenshot2.png` — in-game screenshot
- `screenshot_title.png` — branded title card
- `privacy_policy.html` — source for GitHub Pages

### AAB Location
`app/build/outputs/bundle/release/app-release.aab` (1.2 MB, signed)

### Versioning Gotcha (learned Jun 2)
- Play rejects re-uploads with a used `versionCode`. **Bump `versionCode` in `app/build.gradle.kts`
  for every upload** (current: 2). Editing it elsewhere/by hand can silently revert — always confirm
  the change stuck by grepping the merged manifest after building:
  `grep versionCode app/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml`
- If Play shows errors against an old version code, an old AAB is still attached to the release —
  remove it in the Play Console "App bundles" section so only the newest remains.

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

## Planned Features (requested 2026-06-06)
User-requested batch, in priority order. Analytics is deferred (heavy, needs design discussion).

1. ✅ **Tile color change** (DONE — commit `e804a39`, verified on emulator) — tiles in the correct
   position render a *darker* blue; tiles in the wrong position render a *lighter* blue.
   (Correct position for value `v` is board index `v-1`.)
2. ✅ **Touch sensitivity** (DONE — commit `405f3d8`, verified) — tile moves on touch *down*, not on
   finger release. Replaced `.clickable {}` with a `pointerInput`/`awaitFirstDown` press-down gesture
   (guarded by `rememberUpdatedState` so it never fires with stale state).
3. ✅ **Decimal timer** (DONE — commit `23980e4`, verified) — elapsed time shows two decimals (`SS.CC`).
   - Backed by a monotonic `System.nanoTime()` start stamp + ~33 ms UI tick (replaced the 1-second loop).
   - Elapsed value kept in **nanoseconds** so upcoming analytics can record finish times at higher
     precision (≥5 dp) than the 2-dp display.
4. ✅ **Disable automatic rotation** (DONE — commit `778a96c`, verified on emulator) — locked to
   portrait via `android:screenOrientation="portrait"` on `MainActivity` in `AndroidManifest.xml`.
   Also prevents the rotation-triggered game reset.
5. **Analytics (DEFERRED — design discussion required, not first step)** — log each finish
   (finish time, finish steps) and show statistics: scatter of finish times, average finish time,
   minimal finish steps, scatter of step-count vs finish-time, etc. Needs local persistence
   (likely Room or a simple file/DataStore) and a stats/visualization screen. Discuss schema +
   UI before building.

### Additional requests (2026-06-06, second batch)
6. ✅ **Smooth move (tile slide animation)** (DONE — commit `51e0a82`, verified mid-slide on emulator) —
   `Board` rearchitected from nested `Column`/`Row` cells to one composable per tile *value*,
   absolutely positioned by an `offset` animated with a 110 ms tween (`animateDpAsState`). Static
   recesses are drawn as a background grid so the blank still reads as a hole.
7. ✅ **Multiple tile move (straight-line slide)** (DONE — commit `ddcacc3`, verified on emulator) —
   a tap on a tile 2+ cells from the blank in the *same row/column* slides the whole line; the blank
   ends at the tapped tile's old spot. `canMove` accepts any same-row/column tile, `move` walks the
   blank one cell at a time, and `moveDistance` reports how many tiles slid.
   - Move counter adds **N moves** (one per tile slid), NOT +1 per tap. (Decided 2026-06-06.)
   - With #6's per-value tiles, all shifted tiles animate at once.

## Feature Tiers
- **v1 (MVP):** Working 4×4 15 puzzle, move counter, timer, new game button, solve detection
- **Post-launch:** Additional grid sizes, image puzzles, leaderboard, animations, etc.

## Implemented Since MVP
- **Tile-move sound** (in signed release AAB as of 2026-05-31): plays a short click on each valid move.
  - Sound file: `app/src/main/res/raw/tile_move.wav` (~6.6 KB, generated locally; confirmed bundled at
    `base/res/raw/tile_move.wav` inside the release AAB).
  - Played via `SoundPool` in `MainActivity.kt` (`GameScreen`). Chosen over `AudioManager.playSoundEffect`
    because the latter only plays when the system "Touch sounds" setting is on — many users disable it.
  - `SoundPool` is created with `remember` and released in a `DisposableEffect(Unit) { onDispose { ... } }`.

## In Progress: Ads (started 2026-07-26)
Decision: **Google AdMob, banner only** (no interstitial — chosen to stay simple and less
intrusive, per project strategy of keeping things releasable).

- Dependency added: `com.google.android.gms:play-services-ads:23.6.0` in `app/build.gradle.kts`.
- `AndroidManifest.xml`: added `INTERNET` permission (app was offline-only before) and the
  `com.google.android.gms.ads.APPLICATION_ID` meta-data tag.
- `MainActivity.kt`: `MobileAds.initialize(this)` called in `onCreate`; new `BannerAdView`
  composable (uses `AndroidView` to host a classic `AdView`/`AdSize.BANNER`), placed at the
  bottom of `GameScreen`, below the game content and respecting `WindowInsets.navigationBars`.
- Debug build (`assembleDebug`) compiles and installs cleanly.

⚠️ **Currently using Google's public test IDs** (`ca-app-pub-3940256099942544~3347511713` for
the app ID, `ca-app-pub-3940256099942544/6300978111` for the banner ad unit). These only ever
show ads labeled "Test Ad" and earn no revenue — both are marked with `TODO` comments in the
code (`AndroidManifest.xml` and `MainActivity.kt`).

### Remaining before this can ship
1. Create an AdMob account (if not already done) and register the app to get a real
   App ID + banner ad unit ID. Swap them in for the two test IDs above.
2. Rewrite the store listing full description — it currently says "No ads" (see
   [Store Listing Copy](#store-listing-copy) above).
3. Update Play Console → App content declarations: **Ads** (now Yes) and **Advertising ID**
   (now used) — these were filled in as part of checklist item 9 before ads existed.
4. Update the privacy policy (`store_assets/privacy_policy.html`, published via GitHub Pages)
   to disclose AdMob's data collection.
5. Bump `versionCode` in `app/build.gradle.kts` before building the release AAB (see
   [Versioning Gotcha](#versioning-gotcha-learned-jun-2)).
6. Build a signed release AAB and submit as a new Play Console release/update — this ships
   *after* the current production review (submitted Jul 20) resolves, not inside it.
