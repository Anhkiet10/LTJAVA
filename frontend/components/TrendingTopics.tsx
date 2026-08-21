"use client";

import { useEffect, useState } from "react";
import { TrendingUp } from "lucide-react";
import { getTrendingTopics } from "@/services/trendingService";
import { useAuth } from "@/hooks/useAuth";
import type { TrendingTopic } from "@/services/trendingService";

export default function TrendingTopics() {
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [topics, setTopics] = useState<TrendingTopic[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    getTrendingTopics(8)
      .then(setTopics)
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated]);

  if (authLoading || loading) return null;
  if (!isAuthenticated) return null;
  if (topics.length === 0) return null;

  return (
    <section className="w-full max-w-3xl mx-auto px-6 pb-20">
      <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-4 text-center">
        Chủ đề nổi bật
      </h2>
      <div className="flex flex-wrap justify-center gap-3">
        {topics.map((topic) => (
          <a
            key={topic.keywordId}
            href={`/search?keywordId=${topic.keywordId}&label=${encodeURIComponent(topic.name)}`}
            className="group flex items-center gap-2 rounded-full bg-[#F7F8FA] hover:bg-[#EEF1F4] px-4 py-2 text-sm text-[#202124] transition-colors"
          >
            <span>{topic.name}</span>
            <span className="flex items-center gap-0.5 text-[#2E7D32] font-mono text-xs">
              <TrendingUp size={12} aria-hidden="true" />
              {topic.paperCount}
            </span>
          </a>
        ))}
      </div>
    </section>
  );
}
