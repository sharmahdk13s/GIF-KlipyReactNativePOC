import React from "react";
import { requireNativeComponent, ViewProps } from "react-native";

export type KlipyMediaSelectorProps = ViewProps;

const NativeMediaSelectorView = requireNativeComponent<KlipyMediaSelectorProps>(
  "KlipyMediaSelectorView"
);

export default NativeMediaSelectorView;
