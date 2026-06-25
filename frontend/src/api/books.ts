import axios from 'axios';
import type { Book, Chapter } from '../types';

const api = axios.create({ baseURL: '/api' });

export const fetchBooks = () => api.get<Book[]>('/books').then(r => r.data);
export const fetchBook = (bookId: string) => api.get<Book>(`/books/${bookId}`).then(r => r.data);
export const fetchChapters = (bookId: string) => api.get<Chapter[]>(`/books/${bookId}/chapters`).then(r => r.data);

export interface BookSearchParams {
  category?: string;
  source?: string;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface BookSearchResult {
  content: Book[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const searchBooks = (params: BookSearchParams) =>
  api.get<BookSearchResult | Book[]>('/books', { params }).then(r => r.data);

export const fetchCategories = () =>
  api.get<string[]>('/books/categories').then(r => r.data);

export const fetchSources = () =>
  api.get<string[]>('/books/sources').then(r => r.data);

export const deleteBook = (bookId: string) =>
  api.delete(`/books/${bookId}`);
