# kickflip-android-example

**note:** This is a pre-release preview. Consider nothing stable.

This is an example implementation of the [kickflip-android-sdk](https://github.com/Kickflip/kickflip-android-sdk) that
illustrates creating and browsing live high definition HLS broadcasts.


## Features

+ **High Definition [HTTP Live Streaming](http://en.wikipedia.org/wiki/HTTP_Live_Streaming)**
+ **Background recording**
+ **OpenGL Video Effects**

## Building this project

### Set up Java, the Android SDK & Build-Tools

1. [Download and install JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
2. [Download and install the Android SDK](http://developer.android.com/sdk/)
3. Run `/android-sdk/tools/android` to install the following packages:

![Installing packages from the Android SDK Manager](http://i.imgur.com/PuWsBEB.png)

**ProTip**: You should have the Android SDK root, along with the `/tools` and `/platform-tools` sub-directories added to your PATH.

### Build

1. Define the Android SDK location as `$ANDROID_HOME` in your environment.

		# ~/.bash_profile
		# e.g If you're using Android Studio on Mac OS X:
		export ANDROID_HOME=/Applications/Android\ Studio.app/sdk/



2. Create a file named `SECRETS.java` in `./app/src/main/java/io/kickflip/sample/`:

		package io.kickflip.sample;
		public class SECRETS {
		    public static final String CLIENT_KEY = "YourKickflipKey";
		    public static final String CLIENT_SECRET = "YourKickflipSecret";
		}


3. From this directory run:

	    $ ./gradlew assembleDebug

The Kickflip Example .apk will be in `./app/build/apk`.

## Using the Kickflip Android SDK

### Use the latest Release

Stable releases of the Kickflip Android SDK are available from the [Maven Central Repository](http://search.maven.org/).
The sample project here has our latest Maven release pre-added:

	// ./app/build.gradle
	...
    dependencies {
			...
			compile 'io.kickflip:sdk:0.9.9'
	}

### Develop on the bleeding edge

If you'd like to modify the sdk or just develop on the bleeding edge,
add the kickflip-android-sdk to this project as a submodule.

1. Clone the kickflip-android-sdk repo:

		$ git submodule add https://github.com/Kickflip/kickflip-android-sdk ./submodules/kickflip-android-sdk/

2. Add the sdk to your top-level `settings.gradle`:

		// settings.gradle
		include ':app'
		include ':submodules:kickflip-android-sdk:sdk'

3. Add the sdk as a dependency to the sample module's `build.gradle`:

		// ./app/build.gradle
		...
		dependencies {
				...
				//compile 'io.kickflip:sdk:0.9'
				compile project(':submodules:kickflip-android-sdk:sdk')
		}

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
