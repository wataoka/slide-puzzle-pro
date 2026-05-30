# Slide Puzzle Pro

A 15-puzzle (sliding tile puzzle) for Android.

## Build & Run

**Requirements:** Android Studio installed at `/Applications/Android Studio.app`

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
~/.gradle/wrapper/dists/gradle-8.7-bin/bhs2wmbdwecv87pi65oeuq5iu/gradle-8.7/bin/gradle \
  -p "/path/to/Slide Puzzle Pro" installDebug
```

Then launch via adb:

```bash
~/Library/Android/sdk/platform-tools/adb shell am start -n com.wataoka.slidepuzzle/.MainActivity
```
