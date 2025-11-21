import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { KlipyProvider } from 'react-native-klipy';
import ConversationScreen from './ConversationScreen';

function App() {
  return (
    <SafeAreaProvider>
      <KlipyProvider>
        <ConversationScreen />
      </KlipyProvider>
    </SafeAreaProvider>
  );
}

export default App;
