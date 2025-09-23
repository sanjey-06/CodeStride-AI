
# CodeStride AI 📚⚡

CodeStride AI is an **AI-powered gamified self-learning app** built with Kotlin + Jetpack Compose + Firebase.
It helps learners stay consistent by providing roadmaps, quizzes, streaks, and an AI chatbot assistant.

---

## 🚀 Features

* 📌 Predefined & AI-generated learning roadmaps (Java, Python, Docker, etc.)
* 📝 Quizzes with streak tracking & badges
* 🤖 AI chatbot for guidance (OpenAI API)
* 🎥 Auto-linked YouTube tutorials (YouTube Data API v3)
* 🔔 Daily reminders & streak notifications
* 🎨 Modern UI with Jetpack Compose (PixelFont + Sora)

---

## 🛠️ Tech Stack

* **Kotlin** + **Jetpack Compose**
* **Firebase** (Auth, Firestore, Analytics)
* **Hilt** for Dependency Injection
* **Retrofit + OkHttp** for API calls
* **Coil** for image loading

---

## 🔑 Setup Instructions

### 1. Clone the Repo

```bash
git clone https://github.com/<your-username>/CodeStride-AI.git
cd CodeStride-AI
```

### 2. Add API Keys (⚠️ Required)

This project depends on **OpenAI API** and **YouTube Data API v3**.
API keys are not included in this repo for security reasons.

1. Add them to `local.properties` or `gradle.properties` in your project root:

   ```properties
   OPENAI_API_KEY=sk-xxxxxx
   YOUTUBE_API_KEY=AIzaSyxxxxxx
   ```
2. These will be exposed in `BuildConfig` at compile time:

   ```kotlin
   BuildConfig.OPENAI_API_KEY
   BuildConfig.YOUTUBE_API_KEY
   ```

👉 **Do not hardcode keys** in source files.
👉 Keys are ignored via `.gitignore` to prevent accidental commits.

---

### 3. Run the App

* Connect a device or start an emulator
* Run:

```bash
./gradlew installDebug
```

---
