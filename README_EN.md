# Dongcheon Alimi ![CI](https://github.com/Neibce/Dongcheon-Alimi/actions/workflows/android.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/neibce/dongcheonalimi/badge)](https://www.codefactor.io/repository/github/neibce/dongcheonalimi)

[한국어](./README.md)

School information app for Dongcheon High School students

- Developed in Q2 2020
- [API Server Repository](https://github.com/Neibce/DongcheonAlimi-Server)

## Motivation

To check school timetables, meals, or academic schedules, students had to go through complicated processes like logging into NEIS. Noticing that my friends also felt this inconvenience, I decided to create an Android application to solve this problem.

## Implementation

The application was developed using Java in Android Studio. I followed Google's Material Design guidelines and utilized open-source components. The UI was structured using Fragments with BottomNavigationBar for tab navigation.

For communication between the app and API server, I used Thread, AsyncTask, and HttpURLConnection. Many of my friends had limited mobile data, so I implemented **offline support** by downloading and storing necessary data in JSON format via SharedPreferences. This allows all features except the bulletin board to work without internet, while also reducing server load.

## Key Features

### Exam D-Day

All exam schedules for the year are uploaded to the database and downloaded to users' devices annually. The remaining days until the next exam are displayed on the main screen to motivate studying.

### Meal Menu

The meal menu is crawled monthly from the NEIS website using regex, converted to JSON, and stored on the device. ViewPager allows users to swipe between lunch and dinner, with the ability to view the entire month's menu.

### Timetable

All grade/class timetables are stored in the database. Users download only their specific timetable, which is displayed using TableLayout.

### Bulletin Board (Announcements & Suggestions)

Post lists are displayed using RecyclerView with infinite scroll that loads more posts as the user scrolls. For posting, I wanted to allow only our school students to write without requiring login/signup. So I implemented a **quiz-based authentication system** - when writing a post, a random question only our students would know (e.g., "What floor is our library on?") is fetched from the server, and posts are only submitted if answered correctly. FCM Token is used to identify users without login, and push notifications for new posts are also implemented.

### Academic Calendar

Academic schedules are fetched monthly via the school website's API in JSON format, processed to keep only necessary information, stored on the device, and displayed using RecyclerView.

### Settings

Built on PreferenceScreen, users can customize the app's theme color and manually refresh data.

## Screenshots

| Main | Announcements | Suggestions |
|:---:|:---:|:---:|
| ![Main](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8133c553-fbaa-4de5-823e-20be4c1b2c92) | ![Announcements](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/39d298e0-2922-4952-a5dc-6e97f813b815) | ![Suggestions](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8b94ff06-fdab-4a8a-98db-711a56a58b9d) |
| New Post<br>(Quiz Auth) | Calendar | Settings |
| ![New Post](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9010867d-5c5c-468a-b039-52f3a41692eb) | ![Calendar](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/59227acf-a226-41ac-a73d-d863cde3c4bc) | ![Settings](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/0ebe317a-ac9e-4d3f-a339-9e62479feb52) |
| Play Store | | |
| ![Play Store](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9f712c9d-84a2-4af9-b06e-8984460484f3) | | |

## Results

After completing the app, I published it on the Play Store and promoted it within the school. Students found it more useful than expected and gave positive feedback. The suggestions board was especially well-received - students previously had no way to voice complaints about school life, but now they could submit suggestions directly through the app. The suggestions submitted through the app were actually reflected by the student council, leading to real improvements in the school environment.
