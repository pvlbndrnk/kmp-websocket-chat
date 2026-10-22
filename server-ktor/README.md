# Server

Ktor server for the chat application. It exposes `GET /messages` and a WebSocket endpoint at `/chat-socket`.

## Requirements

- JDK 21
- MongoDB available on the default local connection, or set `MONGO_URI`

## Run and test

Run these commands from the repository root:

```sh
./gradlew :server-ktor:run
./gradlew :server-ktor:test
```

The server listens on port 8080 by default. Set `PORT` to use another port.
