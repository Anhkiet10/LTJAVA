import { getToken } from "@/lib/auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export type TrendingTopic = {
  keywordId: number;
  name: string;
  paperCount: number;
};

export async function getTrendingTopics(
  limit: number = 8,
): Promise<TrendingTopic[]> {
  const token = getToken();
  if (!token) return [];

  const res = await fetch(
    `${API_BASE_URL}/api/trending-topics?limit=${limit}`,
    {
      headers: { Authorization: `Bearer ${token}` },
    },
  );

  if (!res.ok) return [];
  return res.json();
}
