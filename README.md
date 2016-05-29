# cgc-game
_____
This repo contains the source code for Chain Gang Chase.

### Project Overview
```
/
android/..................source for the android build, also, where image files live
core/..................common source for all builds, where most dev work takes place
desktop/................................................source for the desktop build
gradle/wrapper......magic that allows you to run the project without installing much
build.gradle.....................................configuration for the build scripts
package.json...............listing of non-gradle project dependencies (like cgc-art)
```

### Installation
Clone the repo.

Install the [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). The libGDX project requires 7+. Android Studio requires 8+. For consistency, 8u91 is recommended - which `java -version` reports as 1.8.0_91.

Optionally, you may install gradle v2.13 on your system ([mac](https://www.jayway.com/2013/05/12/getting-started-with-gradle/) | [windows](http://www.bryanlor.com/blog/gradle-tutorial-how-install-gradle-windows) | [linux](http://exponential.io/blog/2015/03/30/install-gradle-on-ubuntu-linux/)). But if you'd rather not, the bundled gradle wrappers will take care of everything for you.

### Usage
There are two options here, depending on how you decided to do the Installation. If you elected to just use the built-in gradle wrapper, you can run commands with `./gradlew <command>` (mac | linux) or `gradlew <command>` (windows). If you installed gradle yourself (show-off!) you can run commands with `gradle <command>`.

###### Importing Sprite Sheets
The art assets for this game are stored in the [cgc-art](https://github.com/ChainGangChase/cgc-art) repository. When updated, cgc-art will get a new release tag. You can include those changes by updating the version number to match in `package.json`, and then running `gradlew updateTextures`.

###### Running the Project
The can be run with the command `./gradlew desktop:run` (mac | linux) or `gradlew desktop:run` (windows). Android support coming soon.

### Contribution Guidelines
Pull requests should be submitted from branches that follow the convention `feature/feature-name`, and have been rebased so they can be merged automatically. Please include any changes to `.gitignore` needed to ensure files specific to your IDE of choice are not included.

Please understand that any contributions you provide are done out of the kindness of your heart and will not be rewarded financially through the project. The base game will always be free to play, so it's not like I will be charging people for your work and not sharing profit. (Players may elect to pay a one-time fee to unlock the ability to download maps, however that system is not part of this repository. Proceeds from these payments go to the MAGIC Center, the original development team, and paying for the server.)
