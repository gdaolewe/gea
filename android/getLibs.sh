#!/usr/bin/env bash

# Function which gets a library if it doesn't exist
getLib() {
  echo "Getting $1"
  curl -L $2 > $TEMP_DIR/$1
}

#Remove any existing lib directories and files
if [ -d Gea/libs ]; then
	rm -r Gea/libs
fi
if [ -d Gea/compilelibs ]; then
	rm -r Gea/compile-libs
fi
if [ -d actionbarsherlock ]; then
	rm -r actionbarsherlock
fi

# Create lib directories if necessary
mkdir Gea/libs
mkdir Gea/compile-libs

# Get libraries
mkdir temp
TEMP_DIR=temp
#Android Annotations
getLib androidannotations-bundle-2.7.1.zip http://search.maven.org/remotecontent?filepath=com/googlecode/androidannotations/androidannotations-bundle/2.7.1/androidannotations-bundle-2.7.1.zip
#Rdio
getLib rdio-android.tar.gz http://rdio.com/media/static/developer/android/rdio-android.tar.gz
#OAuth
getLib signpost-core-1.2.1.2.jar https://oauth-signpost.googlecode.com/files/signpost-core-1.2.1.2.jar
getLib signpost-commonshttp4-1.2.1.2.jar https://oauth-signpost.googlecode.com/files/signpost-commonshttp4-1.2.1.2.jar
#ActionBarSherlock
getLib actionbarsherlock.zip https://api.github.com/repos/JakeWharton/ActionBarSherlock/zipball/4.3.1


#Uncompress and move libraries
#AndroidAnnotations
unzip temp/androidannotations-bundle-2.7.1.zip -d temp
cp temp/androidannotations-bundle-2.7.1/androidannotations-api-2.7.1.jar Gea/libs
cp temp/androidannotations-bundle-2.7.1/androidannotations-2.7.1.jar Gea/compile-libs
#Rdio
gunzip temp/rdio-android.tar.gz
tar -xf temp/rdio-android.tar -C temp
cp temp/rdio-android-1.1/rdio-android-sdk.jar Gea/libs
#OAuth
cp temp/signpost-core-1.2.1.2.jar Gea/libs
cp temp/signpost-commonshttp4-1.2.1.2.jar Gea/libs
#ActionBarSherlock
unzip temp/actionbarsherlock.zip -d temp
mv temp/JakeWharton-ActionBarSherlock*/actionbarsherlock actionbarsherlock

#Clean up
rm -r temp