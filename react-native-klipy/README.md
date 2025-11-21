# react-native-klipy

Free, high‑quality **GIFs, Clips and Stickers** for React Native apps, powered by a **bridgeless TurboModule** and a native SwiftUI media picker on iOS.

- **npm**: [react-native-klipy](https://www.npmjs.com/package/react-native-klipy)
- **GitHub**: [sharmahdk13s/react-native-klipy](https://github.com/sharmahdk13s/react-native-klipy)

`react-native-klipy` is designed for modern React Native (0.76+) with the **New Architecture** enabled. It provides:

- A simple JS API for initializing Klipy and opening the media picker.
- A native SwiftUI bottom sheet on iOS with search, categories, and infinite masonry grid.
- A minimal Android TurboModule stub that you can wire to your own UI.

> **Status**: early access. APIs may evolve as we collect feedback.

---

## Requirements

- **React Native**: `>= 0.76.0`
- **React**: `>= 18.3.1`
- **iOS**: 13.0+
- **Android**: 6.0+ (API 23+)
- **New Architecture (TurboModules)** enabled in your app.

---

## Installation

Install the package from npm / yarn:

```bash
# with yarn
yarn add react-native-klipy

# or with npm
npm install react-native-klipy --save
```

### iOS setup

1. **Install pods** from your iOS folder:

   ```bash
   cd ios
   USE_FRAMEWORKS=static bundle exec pod install
   ```

   or, if you are not using Bundler:

   ```bash
   cd ios
   USE_FRAMEWORKS=static pod install
   ```

2. Open `KlipyPOCApp.xcworkspace` (or your app workspace) in Xcode and build once
   to let **React Native Codegen** generate the TurboModule.

The podspec is `RNKlipy.podspec` and will automatically include all required Swift / Obj‑C files:

- `Klipy.mm` / `Klipy.h` – iOS TurboModule implementation.
- `KlipyMediaPickerHost.swift` – presents the SwiftUI media picker.
- `KlipyEvents` – `RCTEventEmitter` for reaction events.

### Android setup

Android support ships as a minimal TurboModule stub that you can connect to your own UI.
The module is auto‑linked via `react-native.config.js`.

1. Make sure your app is configured for the **New Architecture**.
2. Rebuild Android:

   ```bash
   cd android
   ./gradlew :app:assembleDebug
   ```

The native module is implemented by:

- `NativeKlipySpec.kt` – abstract base spec used before full codegen.
- `KlipyModule.kt` – Android TurboModule implementation.
- `KlipyPackage.kt` – package used by React Native.

---

## Quick start (JavaScript)

First, wrap your app in `KlipyProvider` so the media picker UI can be rendered
(required for Android):

```tsx
import React from "react";
import { KlipyProvider } from "react-native-klipy";

export function App() {
  return (
    <KlipyProvider>{/* Your existing app layout / navigation */}</KlipyProvider>
  );
}
```

Then use the Klipy API:

```ts
import Klipy, {
  initialize,
  open,
  setMediaPickerVisible,
  addReactionListener,
  removeAllReactionListeners,
  type KlipyReaction,
  type KlipyInitOptions,
} from "react-native-klipy";

// 1) Initialize Klipy (e.g. in your app bootstrap)
initialize("YOUR_KLIPY_API_KEY", {
  userId: "user-123",
  locale: "en",
  theme: "dark",
});

// 2) Open the native media picker bottom sheet
open();

// 3) Listen for selected GIFs / Clips / Stickers
const sub = addReactionListener((reaction: KlipyReaction) => {
  // reaction.type: "gif" | "clip" | "sticker"
  // reaction.value: selected URL or key
  // reaction.extra: provider metadata (slug, URLs, title, etc.)
  console.log("Klipy reaction selected", reaction);
});

// 4) (Optional) Toggle media picker visibility from JS
setMediaPickerVisible(true); // show
setMediaPickerVisible(false); // hide

// 5) Cleanup when appropriate
removeAllReactionListeners();
sub.remove();
```

You can also use the default export:

```ts
import Klipy from "react-native-klipy";

Klipy.initialize("YOUR_API_KEY");
Klipy.open();
```

---

## API Reference

### Types

```ts
export type KlipyReactionType = "emoji" | "gif" | "clip" | "sticker";

export type KlipyReaction = {
  id: string; // unique ID of the selected item
  type: KlipyReactionType; // "gif" | "clip" | "sticker" (emoji reserved for future)
  value: string; // primary URL or identifier
  extra?: { [key: string]: any }; // provider-specific metadata
};

export type KlipyInitOptions = {
  userId?: string;
  locale?: string; // 'en', 'fr', ...
  theme?: "light" | "dark" | "system";
};

export type ReactionListener = (reaction: KlipyReaction) => void;
```

### Functions

All functions are exported from `src/index.ts`.

#### `initialize(apiKey: string, options?: KlipyInitOptions): void`

Configure the Klipy SDK for the current user / app.

- **apiKey** – your Klipy API key (string).
- **options** – optional configuration:
  - `userId` – ID of the logged‑in user.
  - `locale` – UI/content language.
  - `theme` – `'light' | 'dark' | 'system'`.

Call this once near app startup (or when the user logs in).

#### `open(): void`

Open the native Klipy media picker:

- On **iOS**, presents a SwiftUI bottom sheet with:
  - Search bar with categories and icons.
  - Infinite masonry grid of GIFs/Clips/Stickers.
  - Type selector (GIFs / Clips / Stickers).

On Android this currently calls into the TurboModule stub – you can connect it
to your own Activity / Fragment / Compose screen.

#### `setMediaPickerVisible(visible: boolean): void`

Low‑level visibility toggle that sends a native notification to show/hide the
media picker without re‑initializing.

- `true` – present the Klipy media picker.
- `false` – dismiss/hide it.

#### `addReactionListener(listener: ReactionListener): EmitterSubscription`

Subscribe to **reaction events** emitted by the native picker when a user selects
an item. Under the hood, this listens to the native `KlipyEvents` emitter and the
`"onReactionSelected"` event.

Returns a React Native `EmitterSubscription` that you can `.remove()` later.

#### `removeAllReactionListeners(): void`

Remove all `onReactionSelected` listeners from the shared native `NativeEventEmitter`.
Useful when unmounting your root navigation or logging out a user.

---

## iOS UX details

The iOS implementation focuses on a **high‑quality chat UX**:

- SwiftUI bottom sheet with smooth drag gestures.
- Search with categories (trending, recents, etc.).
- Masonry layout grid for fast browsing.
- Keyboard‑aware layout: the sheet stays above the keyboard and remains resizable.

You can use it as a plug‑and‑play GIF picker in your own chat / comments UI.

---

## Android UX

The Android side currently exposes the same TurboModule API but ships with a
minimal stub implementation so you can:

- Reuse the JS API (`initialize`, `open`, listeners).
- Wire `open()` to your own screen (Activity / Fragment / Compose) that talks
  to your backend or Klipy service.

This keeps the module small while still being **New Architecture**‑ready.

---

## Example integration

A simple chat input that opens Klipy and appends the selected GIF URL to the
input field:

```tsx
import React from "react";
import { View, TextInput, Button } from "react-native";
import Klipy, {
  addReactionListener,
  removeAllReactionListeners,
} from "react-native-klipy";

export function ChatInput() {
  const [text, setText] = React.useState("");

  React.useEffect(() => {
    const sub = addReactionListener((reaction) => {
      if (
        reaction.type === "gif" ||
        reaction.type === "clip" ||
        reaction.type === "sticker"
      ) {
        setText((prev) => `${prev} ${reaction.value}`.trim());
      }
    });

    return () => {
      sub.remove();
      removeAllReactionListeners();
    };
  }, []);

  return (
    <View style={{ flexDirection: "row", alignItems: "center" }}>
      <TextInput
        style={{
          flex: 1,
          borderWidth: 1,
          borderColor: "#ccc",
          padding: 8,
          borderRadius: 8,
        }}
        value={text}
        onChangeText={setText}
        placeholder="Type a message"
      />
      <Button title="GIF" onPress={() => Klipy.open()} />
    </View>
  );
}
```

---

## Roadmap

- Full Android UI parity using Jetpack Compose.
- Theming & branding hooks from JS.
- Better TypeScript typings for reactions and metadata.
- Optional analytics callbacks (views, shares, etc.).

---

## License

MIT – see `LICENSE` when published.
