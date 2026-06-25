export interface Book {
  bookId: string;
  title: string;
  author: string;
  bookUrl: string;
  intro: string;
  category: string;
  wordCount: number;
  status: string;
  source: string;
  chapterCount: number;
  totalChapters: number;
  crawlStatus: number;
  coverUrl: string;
  coverPath: string;
  createdAt: string;
  updatedAt: string;
}

export interface Chapter {
  id: number;
  bookId: string;
  chapterIndex: number;
  chapterTitle: string;
  chapterUrl: string;
  contentPath: string;
  contentSize: number;
  source: string;
  createdAt: string;
}

export interface ChatHistory {
  id: number;
  bookId: string;
  role: string;
  content: string;
  source: string;
  createTime: string;
}

export interface ChatRequest {
  bookId: string;
  message: string;
  promptTemplateId?: number;
  tenChapterContext?: boolean;
}

export interface SourceRecord {
  chapterIndex: number;
  chapterTitle: string;
  excerpt: string;
  rank: number;
}

export interface RagStatus {
  bookId: string;
  status: string; // not_built / built / rebuilding
  chunkSize: number;
  overlap: number;
  topK: number;
  shortBookFullText: boolean;
  chunkCount: number;
  wordCount: number;
  builtAt: string | null;
}

export interface RagConfig {
  chunkSize: number;
  overlap: number;
  topK: number;
  shortBookFullText: boolean;
}

export interface PromptTemplate {
  id: number;
  name: string;
  description: string;
  scene: string;
  content: string;
  isQuickBtn: boolean;
  isSystem: boolean;
  enabled: boolean;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface AnalysisPageData {
  book: {
    bookId: string;
    title: string;
    author: string;
    source: string;
    intro: string;
    coverUrl: string;
  };
  chapters: {
    index: number;
    title: string;
    content: string;
  }[];
  ragStatus: RagStatus;
  quickPrompts: PromptTemplate[];
}

export interface SSEEvent {
  type: 'token' | 'sources' | 'done' | 'error';
  data: any;
}
