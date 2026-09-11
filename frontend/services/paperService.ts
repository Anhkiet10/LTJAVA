import { apiFetch } from "@/lib/apiClient";
import type { PageResponse, PaperSummary, PaperDetail } from "@/types/paper";

type SearchOptions = {
  keyword?: string;
  keywordId?: number;
  journal?: string;
  page?: number;
  size?: number;
};

export async function searchPapers(
  options: SearchOptions,
): Promise<PageResponse<PaperSummary>> {
  const params = new URLSearchParams({
    page: String(options.page ?? 0),
    size: String(options.size ?? 10),
  });
  if (options.keyword) params.set("keyword", options.keyword);
  if (options.keywordId) params.set("keywordId", String(options.keywordId));
  if (options.journal) params.set("journal", options.journal);

  const res = await apiFetch(`/api/papers?${params.toString()}`);

  if (!res.ok) {
    throw new Error("Không thể tải kết quả tìm kiếm");
  }

  return res.json();
}

export async function getPaperById(id: string | number): Promise<PaperDetail> {
  const res = await apiFetch(`/api/papers/${id}`);

  if (res.status === 404) {
    throw new Error("Không tìm thấy bài báo này");
  }
  if (!res.ok) {
    throw new Error("Không thể tải thông tin bài báo");
  }

  return res.json();
}