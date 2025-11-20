import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import ConversationScreen from './ConversationScreen';

function App() {
  return (
    <SafeAreaProvider>
      <ConversationScreen />
    </SafeAreaProvider>
  );
}

export default App;
