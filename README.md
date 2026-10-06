# KMP WebSocket Chat

Monorepo for a WebSocket chat application. The Ktor server is available now; Kotlin Multiplatform clients are planned.

## Structure

| Path | Purpose |
| --- | --- |
| [`server/`](server/) | Ktor server and its tests |

The repository uses one Gradle build rooted here. Client modules will be added when client development begins.

## Server

Install JDK 21 and start a local MongoDB instance. From the repository root, run:

```sh
./gradlew :server:run
```

The server listens on port 8080 by default. Set `PORT` to use another port. See the [server README](server/README.md) for its endpoints and test command.
