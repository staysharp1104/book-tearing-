import type { PromptTemplate } from '../types';
import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export const fetchPrompts = () =>
  api.get<PromptTemplate[]>('/prompts').then(r => r.data);

export const fetchPrompt = (id: number) =>
  api.get<PromptTemplate>(`/prompts/${id}`).then(r => r.data);

export const fetchQuickButtons = () =>
  api.get<PromptTemplate[]>('/prompts/quick-buttons').then(r => r.data);

export const createPrompt = (data: Partial<PromptTemplate>) =>
  api.post<PromptTemplate>('/prompts', data).then(r => r.data);

export const updatePrompt = (id: number, data: Partial<PromptTemplate>) =>
  api.put<PromptTemplate>(`/prompts/${id}`, data).then(r => r.data);

export const deletePrompt = (id: number) =>
  api.delete(`/prompts/${id}`);

export const togglePrompt = (id: number) =>
  api.put<PromptTemplate>(`/prompts/${id}/toggle`).then(r => r.data);

export const exportPrompt = (id: number) =>
  api.get<PromptTemplate>(`/prompts/export/${id}`).then(r => r.data);

export const exportAllPrompts = () =>
  api.get<PromptTemplate[]>('/prompts/export').then(r => r.data);

export const importPrompts = (templates: Partial<PromptTemplate>[]) =>
  api.post('/prompts/import', templates);
