Original App Design Project - README Template
===

# MyGuide

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
People can search for a tutor for a specific course or list of courses and they will get a list of tutors based on their course need, and the location of the tutor from their location. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Productivity
- **Mobile:** It is mobile because it has core features which uses googels map/location to find nearby tutors. It also has push notification feature and direct message would be suitable for mobile. 
- **Story:** Users can search for tutors based on a course and location. They can direct message and connect, schedule meetings and will be notified of                  existing schedules.
- **Market:** Any tutor who has some knowledge, is proficient and of age can be able to use this app. Users can also be kids, teens, adults or anybody who                   need support in a specific course or area. 
- **Habit:** Users can get help with different courses. So users can use it as often as they can direct message tutors, check their progress and schedule meetings. They also get notified regularly of their schedule meeting and progress. 
- **Scope:** It will start with people being able to search and connect with tutors nearby and directly message them. Then user can see search for question they have or post new questions and get answers.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

  * Users
    * Registration,  sign up and login using email or linkedin account
    * Home page that shows briefly upcoming schedules and tutors the user connected with
    * User can see all the list of tutors they connected with 
    * User can search for a tutor by name or find a tutor based on location range, course and grade level
    * User can schedule an appointment with date picker and see all their list of appointments / option to add to google calendar and pushing                   notifications, edit and delete schedule
    * User can chat with tutors
    * User can see a specific tutor's profile
    * Users can send request to connect
    
    
  * Tutor 
    * Registration,  sign up and login using email or Linkedin account 
    * Tutors can see list of requests and accept
    * Tutors can add, edit or delete information in their profile (regarding qualification, courses they give and grade level) 
    * Tutors can grab their profile from Linkedin


**Optional Nice-to-have Stories**

 * User
   * User can rate a tutor 
   * Once connected, users can track their progress (milestones) 
   * Prioritizing search based on qualifications 
   * Subscription and payment gateway
   * User can see list of questions already asked (from stackoverflow via their api), ask new questions and get answer
   * Chat and schedules are persisted in sqlite local database for offline use

 * Tutor
   * Tutors will see their rating and review on their profile 
   * Tutor can schedule progress for clients and give feedback
   * Qualification checking for tutors
   * Tutors can see user's profile
   * log how many hours a tutor tutored and how many clients they have tutored
   * Tutors can post their share ther achievements on facebook or linkedin
   * Chat and schedules are persisted in sqlite local database for offline use






### 2. Screen Archetypes

* User

  * Sign up screen
    * Users can sign up  
  * Login Screen
     * Activity where users can log into their account.
  * Home Screen
     * User can see brief overview of schedules and connected tutors
  * Tutor finder and search
     * Activity where user can search for tutors, connect and chat
  * Messaging Screen
    * Activity for users and tutors to message each other
  * Schedule screen
    * Users can schedule for an appointment and see all schedules 
  * Profile screen
    * Users can see their profile

* Tutor
  * Sign up screen
    * Tutors can sign up  
  * Login Screen
     * Activity where tutors can log into their account.
  * Profile screen
    * Tutors can add, edit or delete information in their profile (regarding qualification, courses they give and grade level) 
  * Schedule screen
    * Tutors can add, edit or delete and see all schedules 
  * Messaging Screen
    * Activity for tutors to message each other
    * Tutors can see support request from clients
  

### 3. Navigation

**Tab Navigation** (Tab to Screen)
* User
  * Home Tab  
    * See more upcoming schedules
    * See more list of connected tutors   
  * Tutor Tab
    * All list of tutors connected
    * find a tutor
  * Schedule Tab
    * schedule an appointment
    * see list of all appointments   
  * Chat Tab 
    * List of chat
    * see connection requests
* Tutor    
  * Home Tab
  * Chat tab
  * Schedule Tab
  * Profile Tab

**Flow Navigation** (Screen to Screen)

* User
  * Login -> Home
  * find tutor -> List of tutors -> Tutor profile 
  * list of Chat ->  specific chat
  
* Tutor
  * Login -> Home
  * Chat -> Specific chat
  * list of chat -> connection requests



## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="Image (5).jpeg" width=600>
<img src="Image (6).jpeg" width=600>


### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
Property
Type
Description
objectId
String
unique id for the messages (default field)
message
String
The message the author sends
author
Pointer to user
Sender of the message
receiver
Pointer to user
The user that receives the message
createdAt
Datetime
date when post is created (default field)
updatedAt
DateTime
Date when the post is updated


### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
