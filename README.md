# <img width="60" alt="Frame 1000002619" src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/mario_logo.png?raw=true">   Mario : Everything NCS

[![Repository](https://img.shields.io/badge/GitHub-Repository-brightgreen)](https://github.com/ncs-jss/Mario-App)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/your-username/ncs-oxygen/blob/main/LICENSE)
![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg) 
![Reactive](https://img.shields.io/badge/Reactive-coroutines-blue.svg)


Mario is your gateway to Nibble, the place where dedication meets opportunity. Designed for members and aspiring recruits, Mario tracks your participation, scores your engagement, and brings you closer to exclusive perks and invite-only recruitment. Level up through active involvement, attend events, and grow your score to unlock special rewards and experiences within the Nibble community.

<a href="https://play.google.com/store/apps/details?id=com.ncs.marioapp">
    <img src="https://static.vecteezy.com/system/resources/previews/012/871/364/non_2x/google-play-store-download-button-in-white-colors-download-on-the-google-play-store-free-png.png" width="200">
</a>


![Group 6611](https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/mario_banner_ps2.png?raw=true)
---

<p float="center">
  <img src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/4.png?raw=true" width="200" />
</p>

<br>

# Table of Contents
* [Technologies](#technologies)
* [Sections](#sections)
* [Setup](#Installation)
* [Module/Package structure](#module_structure)
* [Testing](#testing)
* [Other](#other)
    * [Dependencies](#dependencies)
    * [ktlint](#ktlint)
    * [Resource Naming Conventions](#resource_naming_conventions)
* [Recommended Reading](#recommended_reading)
* [License](#license)


# Technologies <a name="technologies"></a>
* Android SDK
* Kotlin Coroutine for Asynchronous tasks
* Workmanager Api
* AndroidX Jetpack for navigation
* Dagger/Hilt for dependency injection
* Retrofit/OkHttp/Gson for networking
* MVVM as architectural pattern
* SQLite/Room for local data storage
* Firebase Firestore, MongoDB as Cloud Databases
* Firebase storage, dynamic links

# Key Features

<center>
  <img width="400" alt="Frame 1000002619" src="https://github.com/mohitsingh35/Mario/blob/main/Github%20Assets/mario_post.png?raw=true">
</center>
<br><br>

- **Exclusive Invite-Only Recruitment** : With Mario, Nibble is now invite-only, bringing top talent together. Track your score, level up, and earn an exclusive invite to join our prestigious community.

- **Real-Time Score Tracking** : Stay motivated by tracking your score as it grows with each event you attend and every contribution you make. Reach new levels and unlock more privileges as you progress!

- **Levels and Perks** : Mario’s score levels unlock a world of rewards:
 **Noobie**: Entry-level event access, **Intermediate**: Recruitment eligibility and priority invites, **Pro**: Full access to exclusive sessions, personalized recognition, and even more perks.

- **Effortless Event Registration** : Explore upcoming events, register easily, and stay engaged. Mario keeps you connected to all things Nibble in one convenient app.

- **Stay Updated with Notifications** : Never miss an event, score update, or special announcement. Mario’s notifications ensure you’re always in the loop.




# Sections <a name="sections"></a> 

## MAD Score
![summary](https://user-images.githubusercontent.com/24237865/102366914-84f6b000-3ffc-11eb-8d49-b20694239782.png)
![kotlin](https://user-images.githubusercontent.com/24237865/102366932-8a53fa80-3ffc-11eb-8131-fd6745a6f079.png)

## Module/Package structure <a name="module_structure"></a>
* Model classes should be located in a `model` package.
* Network related classes and interfaces (e.g. networking api's) are located in a `remote` package.
* Local storage related classes (e.g. databases or dao's) are located in a `local` package.
* Classes that do not correspond to a certain feature, should either be located in an `all` package, an `util` package or at the top level of the module.

These rules can be applied to either whole *modules* or *packages* depending on if you have feature modules or feature packages. An example for such a module/package structure can be found [here](https://github.com/tailoredmedia/AndroidAppTemplateExample).

## Installation <a name="Installation"></a>

To get started with Mario App, follow these steps:

1. Clone the repository: `git clone https://github.com/ncs-jss/Mario-App`
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

Note: 

1. Host your own Mario backend or get the API URLs from backend devs and add those URLs in the `local.properties` file.
2. Create your Firebase project and add the google-services.json file to the root directory to get started.
3. Add the Base API URLs for debug and release servers in the remote config with keys `DEBUG_BASE_URL`, `RELEASE_BASE_URL`
4. Find the sample `local.properties` file below:
```
DEBUG_API_BASE_URL=
RELEASE_API_BASE_URL=

AUTH_ENDPOINT=
PROFILE_ENDPOINT=
EVENT_ENDPOINT=
QR_ENDPOINT=
MERCH_ENDPOINT=
POST_ENDPOINT=
BANNER_ENDPOINT=
REPORTS_ENDPOINT=

CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=

```


## Other <a name="other"></a>

### Dependencies <a name="dependencies"></a>

**All** dependencies are located in the `Libs.kt` file in the `buildSrc` folder. To implement them use `implementation Libs.XXX`.

Checking whether dependencies are ready to be updated, use `./gradlew refreshVersions`. Afterwards the newer version is added as comments to the `versions.properties` file. 

## Testing <a name="testing"></a>
Every module should contain tests for its use cases:

* `test`: Write unit tests for every `ViewModel` or `Service`/`Repository`. Mockito or PowerMock can be used to mock objects and verify correct behaviour.
* `androidTest`: Write UI tests for common actions in your app. Use JUnit 4 Tests with Espresso. Some helper methods are available in EspressoUtils.

The dependencies for testing are located in the `gradle/test-dependencies-android.gradle` and `gradle/test-dependencies.gradle` files. If your `module` already implements `gradle/library-module-android.gradle` or `gradle/library-module.gradle`, then these dependencies are automatically added to the `module`.

If your module does not implement these standard library gradle files, add the test dependencies with:

``` groovy
apply from: rootProject.file("gradle/XXX.gradle")
```

### ktlint <a name="ktlint"></a>
[ktlint](https://ktlint.github.io/) is a *Kotlin* linter and formatter. Using it is required to keep the code base clean and readable.

Use `./gradlew ktlintCheck` to lint your code.

To conform to the rules either:

* configure AndroidStudio [accordingly](https://github.com/pinterest/ktlint#-with-intellij-idea).
* use `./gradlew ktlintApplyToIdea` to overwrite IDE style files. Read more [here](https://github.com/JLLeitschuh/ktlint-gradle).


### Resource Naming Conventions <a name="resource_naming_conventions"></a>

The goal of these conventions is to reduce the effort needed to read and understand code and also enable reviews to focus on more important issues than arguing over syntax.

**Bold** rules should be applied. *Italic* rules are optional.

| Component        | Rule             | Example                   |
| ---------------- | ---------------------- | ----------------------------- |
| Layouts | **\<what\>**\_**\<where\>**.xml | `activity_main.xml`, `item_detail.xml` |
| Sub-Layouts | **\<what\>**\_**\<where\>**\_**\<description\>**.xml | `activity_main_appbar.xml` |
| Strings | **\<where\>**\_**\<what\>**\_**\<description\>** | `detail_tv_location` |
| Drawables | **\<what\>**\_**\<where\>**\_**\<description\>** | `btn_detail_background`, `card_overview_background` |
| Icons | ic_**\<description\>**\_**\<where\>**.xml | `ic_close.xml`, `ic_location_pin_detail.xml` |
| Dimensions | *\<where\>*\_**\<what\>**\_*\<description\>*\_*\<size\>* | `margin`, `detail_height_card`, `textsize_small` |
| Styles | **\<What\>**\.**\<Description\>** | `Text.Bold`, `Ratingbar.Preview` |
| Component Ids | **\<what\>\<Description\>** | `btnOpen`, `tvTitle` |


## Recommended Reading <a name="recommended_reading"></a>
* [Kotlin](https://kotlinlang.org/docs/reference/)
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/basics.html)
* [Kotlin Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html)
* [Navigation Architecture Component](https://developer.android.com/topic/libraries/architecture/navigation/)
* [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
* [control](https://github.com/floschu/control/)
* [Koin](https://insert-koin.io/)
* [Retrofit](http://www.vogella.com/tutorials/Retrofit/article.html)
* [Room](http://www.vogella.com/tutorials/AndroidSQLite/article.html)

## Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/mohitsingh35">
        <img src="https://avatars.githubusercontent.com/u/130476288?v=4" width="100px;" alt="Mohit Singh"/><br>
        <b>Mohit Singh</b>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/sankalpsaxena04">
        <img src="https://avatars.githubusercontent.com/u/125281380?v=4" width="100px;" alt="Sankalp Saxena"/><br>
        <b>Sankalp Saxena</b>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/arpitmx">
        <img src="https://avatars.githubusercontent.com/u/59350776?v=4" width="100px;" alt="Armax | Alok"/><br>
        <b>Armax | Alok</b>
      </a>
    </td>
  </tr>
</table>



## License <a name="license"></a>
```
Copyright 2025 Hackncs

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```



                      
                      
  

