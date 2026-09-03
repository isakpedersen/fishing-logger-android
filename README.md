# Fishing Logger Android

Android companion app for [Fishing Logger Garmin](https://github.com/isakpedersen/fishing-logger-garmin). Source of truth for the catch history and lure catalog, synced with the watch over Garmin Connect Mobile.

## Features

- Catch list screen
    - View all catches received from [Fishing Logger Garmin](https://github.com/isakpedersen/fishing-logger-garmin), grouped by date
    - Click on a catch to view its details (timestamp, species, weight, lure, notes)
    - Delete catches
- Lure catalog screen
    - View lure catalog
    - Add new lure models and variants (type, name, brand, color, weight, length)
    - Delete lure models and variants
- Sync with [Fishing Logger Garmin](https://github.com/isakpedersen/fishing-logger-garmin)
    - Receive catches exported from the watch via Garmin Connect Mobile
    - Send lure catalog upon request from watch app

## How It Works

The lure catalog is built by the user and sent to the watch app using Garmin Connect Mobile upon
receiving a request. The watch app can then log catches using the newly stored lure catalog (no 
phone needed). After a fishing trip, the watch can export its logged catches back to the phone app.
The phone then acts as a single source of truth, persisting both catches and lures.
