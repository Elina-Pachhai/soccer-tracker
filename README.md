# Soccer Tracker

A command-line Java application for managing soccer teams and tracking player statistics.

## Features

- Add **club teams** and **national teams**
- Add players with jersey numbers and positions
- Record stats: goals, assists, penalty kicks, yellow cards, red cards
- Update player information at any time
- Display all players per team, sorted by jersey number
- View the **top 3 goal scorers** across all teams
- **Rank all players by performance score** — a weighted formula that rewards goals and assists and penalizes cards
- **Persistent storage** — all data is automatically saved to `soccer_data.txt` and reloaded on next launch

## Performance Score Formula

```
Score = Goals × 3 + Assists × 2 + Penalty Kicks × 1 − Yellow Cards × 2 − Red Cards × 5
```

## How to Run

**Compile:**
```bash
cd groupproject-w24/src
javac *.java
```

**Run:**
```bash
java Main
```

## Tech Stack

- Java (OOP — classes, encapsulation, file I/O)
- No external libraries required

## Project Structure

```
src/
├── Main.java    — entry point
├── Menu.java    — CLI menu and all user interaction
├── Data.java    — data management and file persistence
├── Team.java    — Team model (club and national)
└── Player.java  — Player model with stats tracking
```

