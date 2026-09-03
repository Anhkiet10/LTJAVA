export interface TrendingTopic {
  name: string;
  paperCount: number;
}

export async function fetchTrendingTopics(limit: number = 10): Promise<TrendingTopic[]> {
  try {
    const res = await fetch(`http://localhost:8080/api/trending-topics?limit=${limit}`);
    if (!res.ok) throw new Error("Failed to fetch trending topics");
    return await res.json();
  } catch (error) {
    console.error("Error fetching trending topics:", error);
    return [];
  }
}
