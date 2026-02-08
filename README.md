# 🔮 Palmystic: Unveil Thy Destiny

> *"The stars incline, but do not bind.  
> Thy future is etched upon the very rivers of thy palm."*

**Palmystic** is a mystical fortune-telling platform that bridges **ancient palmistry** with **cutting-edge artificial intelligence**.  
By leveraging the visual intelligence of **Google Gemini (VLM)** and the orchestration power of **Spring AI**, Palmystic analyzes the intricate lines of your palm to provide deep insights and a **1:1 consultation with the Archmage of the Eastern Heavens**.

---
## 👥 Team Palmystic
	
  ```
  •	Backend Architect: [Eunse]
•	Frontend Sorcerers: [Tithi]
  •	Prompt Engineer : [Cathy]
  ```

---

## 🎯 Our Goal

We set out to explore the intersection of **human curiosity** and **digital precision**:

- **Data-Driven Mysticism**  
  Transform subjective palm reading into structured, objective analysis using high-dimensional vision models.

- **Hyper-Personalized Interaction**  
  Utilize **Context Injection** so the AI *remembers your palm traits* across the entire consultation.

- **Actionable Wisdom**  
  Move beyond vague predictions by delivering daily **Action Items** that encourage positive lifestyle changes.

---

## 🛠 Tech Stack

### Frontend
- **React.js**  
  Responsive, dynamic user experience with a high-fidelity **Antique UI**
- **WebCam API**  
  Seamless real-time palm capture directly in the browser

### Backend
- **Java 21**
- **Spring Boot 3.x**
- **Spring AI**  
  Structured prompt templates and clean orchestration with LLM/VLM models

### AI Core
- **Google Gemini 2.5 Flash**
  - **VLM (Vision)**: Palm line feature extraction  
  - **LLM (Chat)**: Persona-driven consultation as the Archmage

---

## ✨ Key Features

### 🖐️ AI Real-Time Palm Analysis (VLM)
The vision model detects and evaluates the four major palm lines:

- **Life Line** – Vitality and physical resilience  
- **Heart Line** – Emotional intelligence and soul connections  
- **Head Line** – Strategic thinking and mental clarity  
- **Fate Line** – Professional destiny and social impact  

---

### 🧙‍♂️ 1:1 Consultation with the Archmage

Unlike generic chatbots, Palmystic delivers a **true 1:1 consultation**:

- Palm traits extracted by the VLM are injected as **background context**
- The Archmage cites concrete evidence during dialogue  
  > “I see the *Strategic Mind* trait in thy Head Line, suggesting a path toward leadership…”

---

### 🍀 Mystical Metadata & Action Items

Each user receives a personalized **Destiny Profile**, including:

- **Lucky Context**  
  - Power Animal  
  - Lucky Color  
  - Divine Number
- **Action Item**  
  A specific task for the day to align your Qi with the stars

---

## 🪐 The Magic of Gemini (AI Strategy)

Palmystic implements a **RAG (Retrieval-Augmented Generation) Lite** architecture:

1. **Visual Extraction**  
   Palm images are converted into a structured **JSON schema** of mystical traits.

2. **Contextual Memory**  
   The JSON is stored in session context and passed to the LLM at every chat turn.

3. **Persona Consistency**  
   Strict system prompting ensures:
   - The Archmage stays in character
   - Interpretations remain grounded in extracted palm features

---

## 🚀 Getting Started

### Backend (Spring Boot)
```bash
# Set your Gemini API Key in application.yml
./gradlew bootRun
```

### Frontend (React)
```bash
npm install
npm start
```
---

## 📜 Epilogue

In the world of Palmystic, we are more than developers — we are digital alchemists.
Our hope is to offer users a moment of reflection, a touch of wonder, and a clearer view of the path ahead.

“The universe is contained within the palm of thy hand.”


