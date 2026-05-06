# CtT-triage
A backend application, accepting comments as input and, if relevant, returning a support ticket.

## How It Works
 
CtT-triage accepts raw user comments and uses a two-stage AI pipeline to determine whether they warrant support tickets, in case a comment is determined as "ticket-worthy" both the ticket(s) and the comment are stored in an in-memory H2 database.
 
**Stage 1 - Zero-shot classification**
A lightweight HuggingFace zero-shot model first checks whether the comment resembles a real issue or request. If it scores below a configurable threshold, the comment is discarded immediately - no ticket is created and the comment is not saved. This step is intentionally cheap, filtering out obvious noise (casual conversation, compliments, spam) before spending on more expensive calls.
 
**Stage 2 - Generative model verification**
If the comment passes Stage 1, a generative model (`Qwen/Qwen2.5-7B-Instruct`) performs a second check to catch false positives. Only comments that pass both stages proceed.
 
**Ticket generation**
For comments that clear both checks, the same generative model parses the comment and produces one ticket per distinct issue found. Each ticket is assigned a **title**, **summary**, **category** (`BUG`, `FEATURE`, `BILLING`, `ACCOUNT`, `OTHER`), and **priority** (`LOW`, `MEDIUM`, `HIGH`). The source comment and all generated tickets are then persisted.
 
If a comment produces no tickets, an empty list is returned.

Once the application is started, the full api documentation is available on http://localhost:8080/swagger-ui/index.html#/
 
---
 
## Example
 
**Request**
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Content-Type: application/json" \
  -d '{"body": "The export button crashes the app and I was charged twice this month."}'
```
 
**Response** - two issues detected, two tickets created:
```json
[
  {
    "id": 1,
    "title": "Export button causes app crash",
    "category": "BUG",
    "priority": "HIGH",
    "summary": "User reports the export button crashes the application.",
    "sourceComment": {
      "id": 1,
      "body": "The export button crashes the app and I was charged twice this month."
    }
  },
  {
    "id": 2,
    "title": "Duplicate charge this month",
    "category": "BILLING",
    "priority": "HIGH",
    "summary": "User reports being charged twice in the current billing period.",
    "sourceComment": {
      "id": 1,
      "body": "The export button crashes the app and I was charged twice this month."
    }
  }
]
```
 
**Response** - no ticket warranted, empty list returned:
```json
[]
```

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.6**

## Prerequisites

### Running with Maven

- **Java 25** - [Download Eclipse Temurin 25](https://adoptium.net/)
- **Maven 3.9+** - [Download Maven](https://maven.apache.org/download.cgi)

### Running with Docker

- **Docker** - [Download Docker](https://www.docker.com/get-started)

The Docker setup uses a multi-stage builds, no need for Java and Maven.

## Run backend server locally

### 1.0 Set environment variables

Before running, set huggingFace API key:

**Mac/Linux**
```bash
export HUGGINGFACE_API_KEY=your_key_here
```

**Windows (Command Prompt)**
```cmd
set HUGGINGFACE_API_KEY=your_key_here
```

**Windows (PowerShell)**
```powershell
$env:HUGGINGFACE_API_KEY="your_key_here"
```

---

### 2.1 Running with Maven

```bash
mvn spring-boot:run
```

The app will start on `http://localhost:8080`.

---

### 2.2 Running with Docker

```bash
docker build -t ctt-triage-backend .
docker run -p 8080:8080 -e HUGGINGFACE_API_KEY=$HUGGINGFACE_API_KEY ctt-triage-backend
```

The app will start on `http://localhost:8080`.

## Running a simple frontend UI server

This repository features a secondary application, allowing the user to submit comments and view tickets using a browser.

### Prerequisites
 
### Running with npm
- **Node.js 20+** — [Download Node.js](https://nodejs.org/) (npm is included)
### Running with Docker
- **Docker** — [Download Docker](https://www.docker.com/get-started)
---

### Run

```bash
docker build --build-arg VITE_API_URL=$VITE_API_URL -t ctt-triage-frontend .
docker run -p 80:80 ctt-triage-frontend
```

The app will start on `http://localhost:80`.

OR

### Run with npm
 
```bash
npm install
npm run dev
```

The app will start on `http://localhost:5173`.



