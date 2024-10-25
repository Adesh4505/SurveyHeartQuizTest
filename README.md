**Quiz App**
________

A simple, user-friendly Quiz App built with Kotlin that fetches quiz questions from an API and allows users to test their knowledge through timed multiple-choice questions. The app supports offline access by storing questions locally using Room database, ensuring users can continue quizzing without an active internet connection. High scores are saved using SharedPreferences, giving users a sense of progress and achievement. With dynamic UI updates, Material Design elements, and efficient state management during orientation changes, the app offers a seamless and engaging experience. Built with Android best practices, it emphasizes performance, user experience, and accessibility.

**Features:**

Multiple choice questions fetched from the Open Trivia Database API (OTDB).
Timer-based quiz that tracks the time taken to answer questions.
High score tracking using SharedPreferences.
Local storage of quiz data using Room database for offline use.
Dynamic UI updates based on user performance.
State management to preserve quiz progress during orientation changes.
Seamless integration of navigation between activities and fragments.
Attractive UI with Material Design and ConstraintLayout.
Persistent offline access to previously fetched questions.

**Architecture:**

The app follows the MVVM (Model-View-ViewModel) architecture with the following components:

Room Database for local storage of quiz questions.
Retrofit for network calls to fetch quiz questions from the API.
SharedPreferences for saving high scores.
Coroutines for background tasks.
LiveData to observe data changes in the UI.

**Screenshots:**

Home Screen
![Home Activity](https://github.com/user-attachments/assets/83c088e9-3950-4278-828b-44594b92efad)

Quiz Screen
![QuizActivity](https://github.com/user-attachments/assets/1203d37f-d812-4a5a-83d0-e29d7cee2b8b)

Result Screen
![ResultActivity](https://github.com/user-attachments/assets/f7b23b1a-e7c4-41fb-bb29-4fd94fb64c12)
