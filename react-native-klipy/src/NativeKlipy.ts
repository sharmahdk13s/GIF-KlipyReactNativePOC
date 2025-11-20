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

export interface Spec {
  initialize(apiKey: string, options?: KlipyInitOptions): void;
  open(): void;
  setMediaPickerVisible(visible: boolean): void;
  addListener(eventName: "onReactionSelected"): void;
  removeListeners(count: number): void;
}
const turboModule = TurboModuleRegistry.get<Spec>("NativeKlipy");

const NativeKlipy: Spec =
  turboModule != null ? turboModule : (NativeModules.NativeKlipy as Spec);

export default NativeKlipy;
