#Building for Android

##Installing Android SDK

You must have Java installed, as well as the Android SDK. The Android SDK can be obtained here:
http://developer.android.com/sdk/index.html

Choose 'Use an existing IDE' and download the SDK tools. Follow the instructions for installing them here:
http://developer.android.com/sdk/installing/index.html

Once the SDK tools are installed, add the `tools` and `platform-tools` directories to your PATH environment variable. Open a console and type `android` to launch the SDK Manager. Use it to install the following packages: Android 4.2 API 17 -> SDK Platform and Extras -> Google Play Services.

Configure an emulator: `android create avd -n <emulator-name> -t android-17`
Take note of the name you chose for the emulator.

##Build Instructions

Now navigate to the android directory:

`cd gea/android/`

Copy the google-play-services_lib from [androidsdk location]/extras/google/google-play-services/libproject to gea/android. 

Prepare the project for building:

Prepare the google-play-services_lib: `android update project -t "android-17" -p google-play-services_lib/`

Prepare the project itself: `android update project -p Gea`

To prepare tests:

`android update test-project -m ../Gea - p GeaTest`

Before building, start the emulator: `emulator "@<emulator-name>"`

To build, `cd gea/android/Gea` and run `ant clean Gea debug`

To run tests:

`cd android/GeaTest/` and run `ant clean debug install test`

To run the application:

cd `android/Gea` and run `ant clean debug install`

The application is now installed on the device, but hasn't been started. To start it, type

`adb shell am start -a android.intent.action.MAIN -n net.kenpowers.gea/.MainActivity`


##Pre-Built APK
There is also a pre-
compiled `apk` available for download to use on Android
[here](https://s3.amazonaws.com/OpenGea/GeaBETA.apk).
