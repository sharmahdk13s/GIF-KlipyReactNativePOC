import React from "react";
import { Keyboard, KeyboardEvent, Platform } from "react-native";
import MediaSelectorBottomSheet, {
  MediaSelectorVisibility,
} from "./MediaSelectorBottomSheet";
import { __setAndroidOpenHandler } from "./index";

type Props = {
  children: React.ReactNode;
};

const KlipyProvider: React.FC<Props> = ({ children }) => {
  const [visibility, setVisibility] =
    React.useState<MediaSelectorVisibility>("hidden");
  const [keyboardHeight, setKeyboardHeight] = React.useState(0);

  React.useEffect(() => {
    if (Platform.OS !== "android") {
      return;
    }

    __setAndroidOpenHandler(() => {
      setVisibility("partial");
    });

    const showSub = Keyboard.addListener(
      "keyboardDidShow",
      (e: KeyboardEvent) => {
        setKeyboardHeight(e.endCoordinates.height);
      }
    );
    const hideSub = Keyboard.addListener("keyboardDidHide", () => {
      setKeyboardHeight(0);
    });

    return () => {
      __setAndroidOpenHandler(null);
      showSub.remove();
      hideSub.remove();
    };
  }, []);

  return (
    <>
      {children}
      <MediaSelectorBottomSheet
        visibility={visibility}
        keyboardHeight={keyboardHeight}
        onVisibilityChange={setVisibility}
      />
    </>
  );
};

export default KlipyProvider;
