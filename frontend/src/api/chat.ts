import type { ChatRequest, ChatHistory } from '../types';
import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export async function streamChat(
  request: ChatRequest,
  onToken: (token: string) => void,
  onSources: (sources: any[]) => void,
  onDone: () => void,
  onError: (error: string) => void
) {
  const response = await fetch('/api/chat/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    onError(`HTTP error ${response.status}`);
    return;
  }

  const reader = response.body!.getReader();
  const decoder = new TextDecoder();
  let buffer = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split('\n');
    buffer = lines.pop() || '';

    for (const line of lines) {
      // Handle both SSE formats produced by Spring SseEmitter:
      // - "event:token" (no space after colon)
      // - "event: token" (with space after colon)
      if (line.startsWith('event:')) {
        // event type not used, skip
        continue;
      }
      // data:{"token":"..."} or data: {"token":"..."}
      if (line.startsWith('data:')) {
        const data = line.slice(5).trim();
        if (!data) continue;

        if (data.includes('"token"')) {
          try {
            const parsed = JSON.parse(data);
            if (parsed.token) onToken(parsed.token);
          } catch {}
        } else if (data.includes('"sources"')) {
          try {
            const parsed = JSON.parse(data);
            if (parsed.sources) onSources(parsed.sources);
          } catch {}
        } else if (data.includes('"finish"')) {
          onDone();
        } else if (data.includes('"error"')) {
          try {
            const parsed = JSON.parse(data);
            if (parsed.error) onError(parsed.error);
          } catch {}
        }
      }
    }
  }
  onDone();
}

export const fetchChatHistory = (bookId: string) =>
  api.get<ChatHistory[]>(`/chat/history/${bookId}`).then(r => r.data);

export const clearChatHistory = (bookId: string) =>
  api.delete(`/chat/history/${bookId}`);
