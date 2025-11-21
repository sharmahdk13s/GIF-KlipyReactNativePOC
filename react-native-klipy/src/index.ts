import {
  NativeEventEmitter,
  EmitterSubscription,
  NativeModules,
  Platform,
} from "react-native";
import NativeKlipy, { KlipyReaction, KlipyInitOptions } from "./NativeKlipy";
import MediaSelectorView from "./MediaSelectorView";
import MediaSelectorBottomSheet from "./MediaSelectorBottomSheet";
import KlipyProvider from "./KlipyProvider";

// Native RCTEventEmitter module responsible for emitting reaction events.
const { KlipyEvents } = NativeModules as any;

const emitter = new NativeEventEmitter(KlipyEvents);

export {
  KlipyReaction,
  KlipyInitOptions,
  MediaSelectorView,
  MediaSelectorBottomSheet,
  KlipyProvider,
};

export function initialize(apiKey: string, options?: KlipyInitOptions): void {
  NativeKlipy.initialize(apiKey, options);
}

let androidOpenHandler: (() => void) | null = null;

export function __setAndroidOpenHandler(handler: (() => void) | null): void {
  androidOpenHandler = handler;
}

export function open(): void {
  if (Platform.OS === "android" && androidOpenHandler) {
    androidOpenHandler();
    return;
  }
  NativeKlipy.open();
}

export function setMediaPickerVisible(visible: boolean): void {
  NativeKlipy.setMediaPickerVisible(visible);
}

export type ReactionListener = (reaction: KlipyReaction) => void;

export function addReactionListener(
  listener: ReactionListener
): EmitterSubscription {
  return emitter.addListener("onReactionSelected", listener);
}

export function removeAllReactionListeners() {
  emitter.removeAllListeners("onReactionSelected");
}

export type KlipyMediatorApi = {
  initialize(apiKey: string, options?: KlipyInitOptions): void;
  open(): void;
  setMediaPickerVisible(visible: boolean): void;
  addReactionListener(listener: ReactionListener): EmitterSubscription;
  removeAllReactionListeners(): void;
};

const Klipy: KlipyMediatorApi = {
  initialize,
  open,
  setMediaPickerVisible,
  addReactionListener,
  removeAllReactionListeners,
};

export default Klipy;
