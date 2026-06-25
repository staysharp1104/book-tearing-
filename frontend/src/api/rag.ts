import type { RagStatus, RagConfig, SourceRecord } from '../types';
import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export const fetchRagStatus = (bookId: string) =>
  api.get<RagStatus>(`/rag/status/${bookId}`).then(r => r.data);

export const buildRagIndex = (bookId: string, config?: RagConfig) =>
  api.post<RagStatus>(`/rag/index/${bookId}`, config || {}).then(r => r.data);

export const rebuildRagIndex = (bookId: string, config?: RagConfig) =>
  api.put<RagStatus>(`/rag/reindex/${bookId}`, config || {}).then(r => r.data);

export const clearRagIndex = (bookId: string) =>
  api.delete(`/rag/index/${bookId}`);

export const fetchRagConfig = (bookId: string) =>
  api.get<RagConfig>(`/rag/config/${bookId}`).then(r => r.data);

export const updateRagConfig = (bookId: string, config: RagConfig) =>
  api.put(`/rag/config/${bookId}`, config);

export const clearGlobalRagCache = () =>
  api.delete('/rag/cache/global');

export const searchRag = (bookId: string, query: string) =>
  api.post<SourceRecord[]>(`/rag/search/${bookId}`, { query }).then(r => r.data);
