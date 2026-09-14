import { apiFetch } from "@/lib/apiClient";

export type SourceChunk = {
  paperId: number;
  paperTitle: string;
  chunkIndex: number;
  score: number;
};

export type AskResponse = {
  answer: string;
  sources: SourceChunk[];
};

export async function askQuestion(
  question: string,
  paperId: number,
): Promise<AskResponse> {
  const res = await apiFetch(`/api/ask`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ question, paperId }),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Không thể trả lời câu hỏi lúc này");
  }

  return res.json();
}
