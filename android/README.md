#Release binary
##Pre-Built APK
There is a pre-compiled `apk` available for download to use on Android [here](https://s3.amazonaws.com/OpenGea/Gea.apk).

#Building for Android from source

##Installing Android SDK

You must have Java installed, as well as the Android SDK. The Android SDK can be obtained [here](
http://developer.android.com/sdk/index.html).

Choose 'Use an existing IDE' and download the SDK tools. Follow the instructions for installing them [here](http://developer.android.com/sdk/installing/index.html)

Once the SDK tools are installed, add the `tools` and `platform-tools` directories to your PATH environment variable. Open a console and type `android` to launch the SDK Manager. Use it to install the following packages: Android 4.2 API 17 -> SDK Platform and Extras -> Google Play Services.

Configure an emulator: `android create avd -n <emulator-name> -t android-17`
Take note of the name you chose for the emulator.

##Build Instructions

Now navigate to the android directory:

`cd [Gea project root]/android/`

Copy the google-play-services_lib from [androidsdk location]/extras/google/google-play-services/libproject to gea/android.
Run the shell script to get the rest of the libraries: `sh getLibs.sh` 

Prepare the project for building:

Prepare the google-play-services_lib: `android update project -t "android-17" -p google-play-services_lib`

Prepare ActionBarSherlock: `android update project -t "android-17" -p actionbarsherlock`

Prepare the project itself: `android update project -t "android-17" -p Gea`

Link the libary projects: 
`android update project -p Gea --library ../actionbarsherlock`
`android update project -p Gea --library ../google-play-services_lib`

To prepare tests:

`android update test-project -m ../Gea -p GeaTest`

Before building, start the emulator: `emulator "<emulator-name>"`
On 64-bit Linux, you may have to install ia32-libs to run the emulator and build. On Ubuntu: `apt-get install ia32-libs`

To build, `cd [Gea project root]/android/Gea` and run `ant clean debug`

To run tests:

`cd android/GeaTest/` and run `ant debug install test`

To build and run the application:

cd `android/Gea` and run `ant debug install`

The application is now installed on the device, but hasn't been started. To start it from a terminal, type

`adb shell am start -a android.intent.action.MAIN -n net.kenpowers.gea/.MainActivity_`

Or simply find the application in your device or emulator's launcher.

##Notes

Maps will not display and no map functionality will be present on an Android emulator. This is a limitation of Google Play Services and the Android Google Maps API v2.

Additionally, when run on an emulator in debug mode the application will attempt to connect to a server running on the host machine rather than the Gea server at http://gea.kenpowers.net. When run on a physical Android device in debug mode, the application must be given the IP address of a server to connect to. This can be set by editing the value of the `localhost_base_url` element in `gea/android/Gea/res/values/strings.xml` and re-building.
