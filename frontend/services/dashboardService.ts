import { apiFetch } from "@/lib/apiClient";

export type TopItem = {
  id: number;
  name: string;
  count: number;
};

export type DashboardSummary = {
  topKeywords: TopItem[];
  topJournals: TopItem[];
};

export type YearCount = {
  year: number;
  count: number;
};

export type GapAnalysis = {
  keywordName: string;
  paperCount: number;
  yearFrom: number;
  yearTo: number;
  analysis: string;
};

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const res = await apiFetch(`/api/dashboard/summary`);
  if (!res.ok) throw new Error("Không thể tải dữ liệu dashboard");
  return res.json();
}

export async function getTrend(keywordId: number): Promise<YearCount[]> {
  const res = await apiFetch(`/api/trends?keywordId=${keywordId}`);
  if (!res.ok) throw new Error("Không thể tải dữ liệu xu hướng");
  return res.json();
}

export async function getGapAnalysis(keywordId: number): Promise<GapAnalysis> {
  const res = await apiFetch(`/api/gap-analysis?keywordId=${keywordId}`);
  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new Error(body?.error || "Không thể phân tích lúc này");
  }
  return res.json();
}
