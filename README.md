# TimeCrunch
## Helping Students Manage Their Time more Productively
### Jasmine Mai, Michelle Yu
### CS65 Final Project

## Overview
For this final project, we created an app targeted towards students that helped students manage their time more efficiently. From experience, we know that many students only put in their mandatory meetings and classes in their digital calendars, leaving the spaces in between blank. Our app strives to take in information about what the user needs to acheive and basically fills in these blanks for them, so that they have a set plan about how their day will go. Furthermore, our app also incorporates interleaving learning (alternating between various studying tasks/subjects), which has been scientifically proven to boost learning.

## UI Overview and Stories:
The user is first taken to a welcome screen, and can click on a button to go to the main part of the app. Here, they are greeted with a tabbed layout and a floating button. Pressing the button will take them to a new activity where they can create a task item.
All the task items they have created so far are listed in the To-Do tab, where users can delete tasks that are no longer relevant. 
There is also a settings tab where users can set their bedtime and waketime to ensure that nothing is scheduled during those hours. Sleep is important!  
There is a button "Schedule Me!!!" in the Todo tab. When the user clicks this button, our app uploads their ideal schedule into their google calendar, checking first to make sure that it has permission to do so.

## Limitations 
We didn't have time to implement some of the functionality we had planned for. This includes the ability to edit tasks. 
We also planned to allow the user to update their schedule (deleting all previous tasks for the day and reimplementing the schedule).

## Using the Google Calendar API
Since many android users are already logged into Google Calendar on their phones and use it daily, we thought it would be perfect to incorporate Google Calendar with this app. The Google Calendar API uses oAuth2.0 to authenticate users and have them sign into the app. There were many different sources on how to use 0Auth 2.0, and it was extremely confusing and frustrating for us because many of these sources were intended for web apps. We found a page on the [Google Identity Platform] (https://developers.google.com/identity/protocols/OAuth2InstalledApp) that recommended the [AuthApp Library for Android] (https://github.com/openid/AppAuth-Android). We lost a lot of time trying to make this library work. Eventually, we decided to scrap this route and directly use the Google Calendar API instead. 

In order to properly run the app from the laptop, it may be necessary to create a Google Client ID in the Google API console, which is what we did. 


## APIs and Libraries Used:
  * Joda Time API
  * Google Calendar API
  * GSON Library

