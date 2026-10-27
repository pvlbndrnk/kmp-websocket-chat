# KMP WebSocket Chat

Monorepo for a WebSocket chat application with Ktor and Spring server implementations and Kotlin Multiplatform clients for Android and iOS.

## Structure

| Path                           | Purpose                           |
|--------------------------------|-----------------------------------|
| [`server-ktor/`](server-ktor/) | Server module (ktor)              |
| [`server-spring/`](server-spring/) | Server module (Spring)            |
| [`shared/`](shared/)           | Shared module for Android and iOS |
| [`androidApp/`](androidApp/)   | Android application               |
| [`iosApp/`](iosApp/)           | iOS application and Xcode project |

## Run

- [Android](androidApp/README.md)
- [iOS](iosApp/README.md)
