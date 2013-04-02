#Building for Android

##Installing Android SDK

You must have Java installed, as well as the Android SDK. The Android SDK can be obtained here:
http://developer.android.com/sdk/index.html

Choose 'Use an existing IDE' and download the SDK tools. Follow the instructions for installing them here:
http://developer.android.com/sdk/installing/index.html

Once the SDK tools are installed, add the install directory to your PATH environment variable, open a console and type `android` to launch the SDK Manager. Use it to install the following packages: Android 4.2 API 17 -> SDK Platform and Extras -> Google Play Services.

##Build Instructions

Now navigate to the android directory:

`cd gea/android/`

Copy the google-play-services_lib from [androidsdk location]/extras/google/ to gea/android.

Type `android update project --target "android-17" --path Gea --library ../google_play_services`

Prepare the project for building:

`android update project -p Gea`

To prepare tests:

`android update test-project -m ../Gea - p GeaTest`

Now run `ant clean Gea debug`

To run tests:

`ant clean GeaTest debug install test`

To run the application:

`ant clean Gea debug install`

The application is now installed on the device, but hasn't been started. To start it, type

`adb shell am start -a android.intent.action.MAIN -n net.kenpowers.gea/.MainActivity`

##Pre-Built APK
There is also a pre-
compiled `apk` available for download to use on Android
[here](https://s3.amazonaws.com/OpenGea/GeaBETA.apk).
