import type { TurboModule } from "react-native";
import { NativeModules, TurboModuleRegistry } from "react-native";

export type KlipyReactionType = "emoji" | "gif" | "clip" | "sticker";

export type KlipyReaction = {
  id: string;
  type: KlipyReactionType;
  value: string;
  extra?: { [key: string]: any };
};

export type KlipyInitOptions = {
  userId?: string;
  locale?: string;
  theme?: "light" | "dark" | "system";
};

export interface Spec extends TurboModule {
  initialize(apiKey: string, options?: KlipyInitOptions): void;
  open(): void;
  setMediaPickerVisible(visible: boolean): void;
  addListener(eventName: "onReactionSelected"): void;
  removeListeners(count: number): void;
}

let NativeKlipy: Spec;

try {
  NativeKlipy = TurboModuleRegistry.getEnforcing<Spec>("NativeKlipy");
} catch (_error) {
  // Backwards-compatible path for Android (and iOS) when the TurboModule
  // is not yet registered and the module is exposed via NativeModules.
  NativeKlipy = NativeModules.NativeKlipy as Spec;
}

export default NativeKlipy;
