# <img width="60" alt="Frame 1000002619" src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/mario_logo.png?raw=true">   Mario : Everything NCS

[![Repository](https://img.shields.io/badge/GitHub-Repository-brightgreen)](https://github.com/ncs-jss/Mario-App)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/your-username/ncs-oxygen/blob/main/LICENSE)
![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange.svg) 
![Reactive](https://img.shields.io/badge/Reactive-coroutines-blue.svg)


Mario is your gateway to Nibble, the place where dedication meets opportunity. Designed for members and aspiring recruits, Mario tracks your participation, scores your engagement, and brings you closer to exclusive perks and invite-only recruitment. Level up through active involvement, attend events, and grow your score to unlock special rewards and experiences within the Nibble community.

<a href="https://play.google.com/store/apps/details?id=com.ncs.marioapp">
    <img src="https://static.vecteezy.com/system/resources/previews/012/871/364/non_2x/google-play-store-download-button-in-white-colors-download-on-the-google-play-store-free-png.png" width="200">
</a>


![Group 6611](https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/mario_banner_ps2.png?raw=true)
---

<p float="center">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/4.png?raw=true" width="200" />
</p>

<br>

# Table of Contents
* [Technologies](#technologies)
* [Sections](#sections)
   * [Home Page](#home)
   * [Bonus Rewards](#bonus)
   * [Events Page](#events)
   * [Score Page](#score)
   * [Nerd Store](#store)
   * [Transactions](#transactions)
   * [My Redemptions](#redemptions)
   * [Settings](#settings)
   * [Event Details](#eventDetails)
   * [Event Register](#eventRegister)
* [Setup](#Installation)
* [Module/Package structure](#module_structure)
* [Testing](#testing)
* [Other](#other)
    * [Dependencies](#dependencies)
    * [ktlint](#ktlint)
    * [Resource Naming Conventions](#resource_naming_conventions)
* [Recommended Reading](#recommended_reading)
* [Contributors](#contributors)
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
  <img width="400" alt="Frame 1000002619" src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/mario_post.png?raw=true">
</center>
<br><br>

- **Exclusive Invite-Only Recruitment** : With Mario, Nibble is now invite-only, bringing top talent together. Track your score, level up, and earn an exclusive invite to join our prestigious community.

- **Real-Time Score Tracking** : Stay motivated by tracking your score as it grows with each event you attend and every contribution you make. Reach new levels and unlock more privileges as you progress!

- **Levels and Perks** : Mario’s score levels unlock a world of rewards:
 **Noobie**: Entry-level event access, **Intermediate**: Recruitment eligibility and priority invites, **Pro**: Full access to exclusive sessions, personalized recognition, and even more perks.

- **Effortless Event Registration** : Explore upcoming events, register easily, and stay engaged. Mario keeps you connected to all things Nibble in one convenient app.

- **Stay Updated with Notifications** : Never miss an event, score update, or special announcement. Mario’s notifications ensure you’re always in the loop.




# Sections <a name="sections"></a> 

### 1. Home Page <a name="home"></a> 

The Home Page of the Mario app serves as a dynamic hub, delivering the latest updates, events, and social interactions from NCS. It seamlessly integrates banners, event announcements, and engaging posts from NCS social channels, ensuring users stay informed and connected. Posts include rich media content such as images with captions and interactive user polls, fostering engagement within the community.  

## **Highlights:**  
1. **Banners & Announcements** – Key updates and promotions.  
2. **Events** – Upcoming and ongoing NCS events.  
3. **Social Channel Posts** – Aggregated content from NCS social media.  
4. **Image Posts with Captions** – Visual updates from the community.  
5. **Interactive Polls** – Engaging polls for user participation.
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/home1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/home3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/home4.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/home5.png?raw=true" width="200" />

</p>


---

### 2. Bonus Rewards <a name="bonus"></a> 

Users can unlock **limited bonus rewards** in the Mario app by either **scanning a QR code** or **clicking a special reward link**. These rewards grant **Mario Coins** and **Mario Score**, which can be used to redeem exclusive NCS merchandise from the **Nerd Store**.  

## **How It Works:**  
1. **Scan QR Code or Click Link** – Instantly access a surprise reward.  
2. **Scratch Card Feature** – Reveal your bonus Mario Coins and Score.  
3. **Use Rewards** – Redeem NCS merch using collected Mario Coins.
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/gift1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/gift2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/gift3.png?raw=true" width="200" />

</p>


---


### 3. Events Page <a name="events"></a> 

The Events Page in the Mario app provides a centralized space to explore all upcoming and past events. Users can easily browse event details, access their entry tickets, and join online events seamlessly. Whether attending in person or virtually, the page ensures a smooth experience with quick access to essential event information.  

## **Highlights:**  
1. **All Events & Past Events** – View upcoming and previous events in a structured format.  
2. **View More Option** – Load additional events for better discovery.  
3. **Entry Ticket QR Code** – Easily access QR-based entry tickets for in-person events.  
4. **Online Event Links** – Quick access to virtual event participation.
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/events1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/events3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/events2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/events4.png?raw=true" width="200" />

</p>


---

### 4. Score Page <a name="score"></a> 

The Score Page in the Mario app provides a detailed overview of a user's progress, achievements, and event participation. It displays the user's **Mario Score**, **Mario Coins**, and **current level** based on their total score. Users can also track their attended and past events, along with the rewards earned from each event in terms of score and coins.  

## **Highlights:**  
1. **Mario Score & Coins** – Track total accumulated points and coins.  
2. **User Level** –  
   - **Noobie** – Score less than 100  
   - **Intermediate** – Score between 100 and 399  
   - **Pro** – Score 400 and above  
3. **Event Participation** – View attended and past events.  
4. **Event Rewards** – Check the score and coins earned from each event.  
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/score1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/score2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/score3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/score4.png?raw=true" width="200" />

</p>


---

### 5. Nerd Store <a name="store"></a> 

The Nerd Store in the Mario app is the ultimate destination for users to redeem **NCS merchandise** using their hard-earned **Mario Coins**. From exclusive apparel to collectibles, users can browse and claim exciting rewards directly from the store.  

## **Highlights:**  
1. **Exclusive NCS Merchandise** – Explore a variety of items available for redemption.  
2. **Mario Coin Redemption** – Use Mario Coins to claim merch instead of cash.  
3. **Seamless Redemption Process** – Easily redeem items and track your orders.
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/store1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/store2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/store3.png?raw=true" width="200" />

</p>


---

### 6. Transactions <a name="transactions"></a> 

The Transactions Page in the Mario app provides a **detailed log** of all **Mario Coin** activities, allowing users to track where their coins were **earned** and **spent**. This ensures transparency and helps users manage their rewards effectively.  

## **Highlights:**  
1. **Earning History** – Track where Mario Coins were received:  
   - **Bonus Rewards** – Redeemed via QR code or link.  
   - **Event Participation** – Earned from attending events.  
   - **Gifts from NCS Members** – Coins received as special rewards.  
2. **Spending History** – View how Mario Coins were used:  
   - **Nerd Store Purchases** – Redeemed for NCS merchandise.  
   - **Other Redemptions** – Any additional reward claims.
   
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/transactions1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/transactions2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/transactions3.png?raw=true" width="200" />

</p>


---

### 7. My Redemptions <a name="redemptions"></a> 

The **My Redemptions** page allows users to track their **Mario Coin** redemptions from both the **NCS Store** and **Nerd Store**. It provides real-time updates on the status of each redemption, ensuring users stay informed about their claimed rewards.  

## **Highlights:**  
1. **Redemption History** – View all past and current redemptions.  
2. **Stores** – Track redemptions from:  
   - **NCS Store** – Special event-based or exclusive rewards.  
   - **Nerd Store** – Merchandise purchased using Mario Coins.  
3. **Redemption Status** – Stay updated with the progress:  
   - **Pending** – Request is being processed.  
   - **Accepted** – Approved and moving forward.  
   - **Fulfilled** – Successfully delivered or completed.  
   - **Cancelled** – Redemption request was revoked.
     
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/myredem1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/myredem2.png?raw=true" width="200" />
</p>


---

### 8. Settings <a name="settings"></a> 

The **Settings Page** in the Mario app provides users with essential controls and customization options for a personalized experience. From editing their profile to managing notifications and exploring the latest app updates, users can access everything they need in one place.  

## **Highlights:**  
1. **Edit Profile** – Update personal details, avatar, and preferences.  
2. **Feedback Mechanism** – Share suggestions or report issues directly.  
3. **More NCS Apps** – Explore other NCS apps available on the Play Store.  
4. **What’s New in Mario App** – View app version history and latest updates.  
5. **Notification Control** – Manage push notifications and alerts.  
     
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/settings1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/settings2.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/settings3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/settings4.png?raw=true" width="200" />
</p>


---

### 9. Event Details <a name="eventDetails"></a> 

The **Event Details Page** provides an expanded view of each event, offering all the necessary information for users to make informed decisions before enrolling. From event timing to prerequisites, users can access comprehensive details in one place.  

## **Highlights:**  
1. **Timestamps** – View event date, start time, and duration.  
2. **Moderators** – See the list of event organizers or hosts.  
3. **Prerequisites** – Check any requirements before enrolling.  
4. **Enroll Option** – Easily register for the event with a single tap.  
     
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventspage1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventspage3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventspage3.png?raw=true" width="200" />
</p>


---

### 10. Event Register <a name="eventRegister"></a> 

The **Event Register Page** streamlines the enrollment process by collecting brief survey responses and ensuring users receive the necessary access details based on the event type.  

## **Highlights:**  
1. **Brief Survey** – Users answer a few questions related to the event.  
2. **Enrollment Confirmation** – Successfully enroll in the event after survey completion.  
3. **Access Details:**  
   - **Offline Events** – A unique **Entry QR Code** is generated for check-in.  
   - **Online Events** – The **event link** is sent to the user’s email.  
<br>

<p float="left">
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventregister1.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventregister4.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventregister3.png?raw=true" width="200" />
  <img src="https://github.com/mohitsingh35/Mario-App/blob/main/Github%20Assets/eventregister5.png?raw=true" width="200" />

</p>


---

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

## Contributors <a name="contributors"></a>

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



                      
                      
  

