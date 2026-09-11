export type PaperSummary = {
  id: number;
  title: string;
  publicationYear: number;
  doi: string | null;
  journalName: string | null;
  authorNames: string[];
};

export type KeywordTag = {
  id: number;
  name: string;
};

export type PaperDetail = {
  id: number;
  title: string;
  abstractText: string | null;
  publicationYear: number;
  doi: string | null;
  sourceApi: string;
  journalId: number | null;
  journalName: string | null;
  journalPublisher: string | null;
  authorNames: string[];
  keywords: KeywordTag[];
  oaUrl: string | null;
  fullTextExtracted: boolean;
  aiIndexed: boolean;
  createdAt: string;
};

export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};