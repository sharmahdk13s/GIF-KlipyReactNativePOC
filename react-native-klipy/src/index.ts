import {
  NativeEventEmitter,
  EmitterSubscription,
  NativeModules,
} from "react-native";
import NativeKlipy, { KlipyReaction, KlipyInitOptions } from "./NativeKlipy";
import MediaSelectorView from "./MediaSelectorView";
import MediaSelectorBottomSheet from "./MediaSelectorBottomSheet";

// Native RCTEventEmitter module responsible for emitting reaction events.
const { KlipyEvents } = NativeModules as any;

const emitter = new NativeEventEmitter(KlipyEvents);

export {
  KlipyReaction,
  KlipyInitOptions,
  MediaSelectorView,
  MediaSelectorBottomSheet,
};

export function initialize(apiKey: string, options?: KlipyInitOptions): void {
  NativeKlipy.initialize(apiKey, options);
}

export function open(): void {
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

const Klipy = {
  initialize,
  open,
  addReactionListener,
  removeAllReactionListeners,
};

export default Klipy;
