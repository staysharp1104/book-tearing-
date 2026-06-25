import React, { createContext, useContext, useState, useCallback } from 'react';
import type { ChatHistory, SourceRecord } from '../types';

interface ChatContextType {
  messages: ChatHistory[];
  sources: Record<number, SourceRecord[]>;
  isStreaming: boolean;
  addMessage: (msg: ChatHistory) => void;
  updateLastMessage: (content: string) => void;
  setSources: (messageIndex: number, sources: SourceRecord[]) => void;
  setStreaming: (streaming: boolean) => void;
  clearMessages: () => void;
  resetForBook: (bookId: string) => void;
}

const ChatContext = createContext<ChatContextType | null>(null);

export const ChatProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [messages, setMessages] = useState<ChatHistory[]>([]);
  const [sources, setSourcesMap] = useState<Record<number, SourceRecord[]>>({});
  const [isStreaming, setIsStreaming] = useState(false);

  const addMessage = useCallback((msg: ChatHistory) => {
    setMessages(prev => [...prev, msg]);
  }, []);

  const updateLastMessage = useCallback((token: string) => {
    setMessages(prev => {
      const updated = [...prev];
      if (updated.length > 0) {
        const last = updated[updated.length - 1];
        updated[updated.length - 1] = { ...last, content: last.content + token };
      }
      return updated;
    });
  }, []);

  const setSources = useCallback((messageIndex: number, srcs: SourceRecord[]) => {
    setSourcesMap(prev => ({ ...prev, [messageIndex]: srcs }));
  }, []);

  const setStreaming = useCallback((streaming: boolean) => {
    setIsStreaming(streaming);
  }, []);

  const clearMessages = useCallback(() => {
    setMessages([]);
    setSourcesMap({});
    setIsStreaming(false);
  }, []);

  const resetForBook = useCallback((_bookId: string) => {
    clearMessages();
  }, [clearMessages]);

  return (
    <ChatContext.Provider value={{
      messages, sources, isStreaming,
      addMessage, updateLastMessage, setSources, setStreaming, clearMessages, resetForBook
    }}>
      {children}
    </ChatContext.Provider>
  );
};

export const useChat = () => {
  const ctx = useContext(ChatContext);
  if (!ctx) throw new Error('useChat must be used within ChatProvider');
  return ctx;
};
