import type { AnalysisPageData } from '../types';
import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

export const fetchAnalysisPage = (bookId: string) =>
  api.get<AnalysisPageData>(`/books/${bookId}/analysis-page`).then(r => r.data);
