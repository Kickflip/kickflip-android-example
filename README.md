# Kickflip SDK Example Application

![kickflip app screenshot](http://i.imgur.com/dbqVqzL.jpg)
[![Google Play link](http://steverichey.github.io/google-play-badge-svg/img/en_get.svg)](https://play.google.com/store/apps/details?id=io.kickflip.sample)

This is an example implementation of the [kickflip-android-sdk](https://github.com/Kickflip/kickflip-android-sdk) that
illustrates easy live, high definition HLS broadcasts. Also check out our slick [iOS example project](https://github.com/Kickflip/kickflip-ios-example)!


## Features

+ **High Definition [HTTP Live Streaming](http://en.wikipedia.org/wiki/HTTP_Live_Streaming)**
+ **Background recording**
+ **OpenGL Video Effects**

## Building this project

### Set up Java, the Android SDK & Build-Tools

1. [Download and install the latest JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. [Download and install the Android SDK](http://developer.android.com/sdk/)
3. Run `path/to/android-sdk/tools/android` to install the following packages:

	+ Android SDK Tools 22.6.3
	+ Android SDK Platform-tools 19.0.2
	+ Android SDK Build-tools 19.1
	+ Android 4.4.2 SDK Platform 3
	+ Android Support Repository 5
	+ Android Support Library 19.1


4. Define the Android SDK root directory as `$ANDROID_HOME` in your environment.

		# ~/.bash_profile
		# e.g If you're using Android Studio on Mac OS X:
		export ANDROID_HOME=/Applications/Android\ Studio.app/sdk/

**ProTip**: You should have the Android SDK root, along with the `/tools` and `/platform-tools` sub-directories added to your PATH.

### SECRETS.java

After you [sign up](https://kickflip.io) for a Kickflip account, copy your API keys to `./app/src/main/java/io/kickflip/sample/SECRETS.java`:

		$ touch ./app/src/main/java/io/kickflip/sample/SECRETS.java

```java
		package io.kickflip.sample;
		public class SECRETS {
		    public static final String CLIENT_KEY = "YourKickflipKey";
		    public static final String CLIENT_SECRET = "YourKickflipSecret";
		}
```


#### Building from the Command Line

1. From this directory run:

	    $ ./gradlew assembleDebug

2. The Kickflip Example .apk will be in `./app/build/apk`. Install it!

		$ adb install ./app/build/apk/app-debug-unaligned.apk
		
#### Building with Android Studio

1. Select `Import Project` and point Android Studio to the root project directory.

2. Hit the `Play` Icon or `control + R` to build and run on your attached Android device.


## License

Apache 2.0

	Copyright 2014 OpenWatch, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
