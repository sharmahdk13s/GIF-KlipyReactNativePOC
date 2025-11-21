import React, { useEffect, useState } from 'react';
import {
  SafeAreaView,
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  Keyboard,
  Platform,
  ScrollView,
} from 'react-native';
import Klipy, { KlipyReaction } from 'react-native-klipy';

type MediaSelectorVisibility = 'hidden' | 'partial' | 'full';

const ConversationScreen = () => {
  const [initialized, setInitialized] = useState(false);
  const [reaction, setReaction] = useState<KlipyReaction | null>(null);
  const [messageText, setMessageText] = useState('');
  const [messages, setMessages] = useState<string[]>([]);
  const [mediaSelectorVisibility, setMediaSelectorVisibility] =
    useState<MediaSelectorVisibility>('hidden');
  const [keyboardHeight, setKeyboardHeight] = useState(0);

  useEffect(() => {
    try {
      Klipy.initialize(
        '685pfsUU3EODe5rjG3li8rLUdfyydxxfh8fPym7wM5dvr0jklulSi6g5BSWlL3zG',
      );
      setInitialized(true);
      // eslint-disable-next-line no-console
      console.log('Klipy initialized');
    } catch (error) {
      // eslint-disable-next-line no-console
      console.error('Failed to initialize Klipy', error);
    }

    const reactionSub = Klipy.addReactionListener(selected => {
      setReaction(selected);
      setMessages(prev => [...prev, `${selected.type}: ${selected.value}`]);
      setMediaSelectorVisibility('hidden');
      // eslint-disable-next-line no-console
      console.log('Received reaction from Klipy', selected);
    });

    const showEvent =
      Platform.OS === 'ios' ? 'keyboardWillShow' : 'keyboardDidShow';
    const hideEvent =
      Platform.OS === 'ios' ? 'keyboardWillHide' : 'keyboardDidHide';

    const showSub = Keyboard.addListener(showEvent, e => {
      setKeyboardHeight(e.endCoordinates.height);
    });
    const hideSub = Keyboard.addListener(hideEvent, () => {
      setKeyboardHeight(0);
    });

    return () => {
      reactionSub.remove();
      Klipy.removeAllReactionListeners();
      showSub.remove();
      hideSub.remove();
    };
  }, []);

  useEffect(() => {
    if (mediaSelectorVisibility === 'hidden') {
      Keyboard.dismiss();
    }
  }, [mediaSelectorVisibility]);

  const handleSend = () => {
    if (!messageText.trim()) {
      return;
    }
    setMessages(prev => [...prev, `You: ${messageText.trim()}`]);
    setMessageText('');
  };

  const toggleMore = () => {
    Klipy.open();
  };

  const inputBottomPadding =
    mediaSelectorVisibility === 'hidden' ? keyboardHeight : 0;

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        {/* Toolbar */}
        <View style={styles.toolbar}>
          <Text style={styles.toolbarTitle}>Conversation</Text>
          <View style={styles.toolbarActions}>
            <Text style={styles.toolbarActionText}>Call</Text>
          </View>
        </View>

        {/* Message list */}
        <View
          style={[
            styles.messagesContainer,
            mediaSelectorVisibility === 'full' && { height: 0 },
          ]}
        >
          <ScrollView contentContainerStyle={styles.messagesContent}>
            {messages.map((msg, index) => (
              <Text key={index} style={styles.messageBubble}>
                {msg}
              </Text>
            ))}
            {reaction && (
              <View style={styles.lastReactionContainer}>
                <Text style={styles.lastReactionLabel}>
                  Last selected reaction:
                </Text>
                <Text style={styles.lastReactionValue}>
                  {reaction.type} - {reaction.value}
                </Text>
              </View>
            )}
          </ScrollView>
        </View>

        {/* Message input */}
        <View
          style={[styles.inputContainer, { paddingBottom: inputBottomPadding }]}
        >
          <TouchableOpacity onPress={toggleMore} style={styles.moreButton}>
            <Text style={styles.moreButtonText}>＋</Text>
          </TouchableOpacity>
          <TextInput
            style={styles.textInput}
            placeholder="Type a message"
            value={messageText}
            onChangeText={setMessageText}
          />
          <TouchableOpacity onPress={handleSend} style={styles.sendButton}>
            <Text style={styles.sendButtonText}>Send</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#000',
  },
  container: {
    flex: 1,
    backgroundColor: '#000',
  },
  toolbar: {
    height: 56,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#333',
  },
  toolbarTitle: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '600',
  },
  toolbarActions: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  toolbarActionText: {
    color: '#4CAF50',
    fontSize: 14,
  },
  messagesContainer: {
    flex: 1,
  },
  messagesContent: {
    paddingHorizontal: 16,
    paddingVertical: 12,
  },
  messageBubble: {
    alignSelf: 'flex-start',
    backgroundColor: '#222',
    color: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
    marginBottom: 8,
  },
  lastReactionContainer: {
    marginTop: 16,
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#1E1E1E',
  },
  lastReactionLabel: {
    color: '#AAAAAA',
    fontSize: 12,
    marginBottom: 4,
  },
  lastReactionValue: {
    color: '#FFFFFF',
    fontSize: 14,
    fontWeight: '500',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 8,
    paddingTop: 8,
    paddingBottom: 8,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#333',
    backgroundColor: '#000',
  },
  moreButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#333',
    marginRight: 8,
  },
  moreButtonText: {
    color: '#fff',
    fontSize: 18,
  },
  textInput: {
    flex: 1,
    borderRadius: 16,
    backgroundColor: '#111',
    color: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  arrowButton: {
    width: 32,
    height: 32,
    borderRadius: 16,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#333',
    marginLeft: 8,
  },
  arrowButtonText: {
    color: '#fff',
    fontSize: 16,
  },
  sendButton: {
    marginLeft: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
    backgroundColor: '#4CAF50',
  },
  sendButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
});

export default ConversationScreen;
