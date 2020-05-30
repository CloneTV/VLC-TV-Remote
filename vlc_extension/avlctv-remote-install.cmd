@echo off
IF [%1]==[] (
ECHO IP address Android-TV missing, exit
EXIT
)

adb connect %1
adb push atv-home-media-release.apk storage/sdcard/avlctv-remote.apk
rem adb shell pm uninstall ru.ps.vlc.atv.remote
adb shell pm install -r storage/sdcard/avlctv-remote.apk
adb shell rm storage/sdcard/avlctv-remote.apk
adb shell am start -a android.intent.action.MAIN -n ru.ps.vlc.atv.remote/.gui.activity.AppMainActivity
