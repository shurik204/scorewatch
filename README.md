# Scorewatch

Utility mod that adds scoreboard operation debug/tracing.

How to use:

- `/scorewatch subscribe <objective> [scoreHolder]` (per-player)
- Define in function file: `#scorewatch <objective> [scoreHolder]` (global)

Features:

- Log scoreboard operations (with function call stack) to chat.
- Per-player and global scoreboard tracing.
- Objective or objective + score holder filtering.