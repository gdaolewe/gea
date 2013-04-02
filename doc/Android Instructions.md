Building for Android with Ant
=============================

Navigate to the android directory:

`cd gea/android/`

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
