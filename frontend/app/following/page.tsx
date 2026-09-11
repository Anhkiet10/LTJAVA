"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import { UserMinus } from "lucide-react";
import Header from "@/components/Header";
import { listMyFollows, unfollowTarget } from "@/services/followService";
import { useAuth } from "@/hooks/useAuth";
import type { FollowItem } from "@/services/followService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function FollowingPage() {
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [follows, setFollows] = useState<FollowItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    listMyFollows()
      .then(setFollows)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated]);

  async function handleUnfollow(followId: number) {
    try {
      await unfollowTarget(followId);
      setFollows((prev) => prev.filter((f) => f.id !== followId));
    } catch {
      // Giữ nguyên danh sách nếu lỗi
    }
  }

  function linkFor(item: FollowItem) {
    return item.targetType === "KEYWORD"
      ? `/search?keywordId=${item.targetId}&label=${encodeURIComponent(item.targetName)}`
      : `/search?journal=${encodeURIComponent(item.targetName)}`;
  }

  return (
    <main className="min-h-screen bg-white flex flex-col">
      <Header />

      <div className="w-full max-w-2xl mx-auto px-6 pt-4 pb-20">
        <Link
          href="/"
          className={`${sourceSerif.className} text-base text-[#1D3557] mb-6 inline-block`}
        >
          ResearchPulse
        </Link>

        <h1 className="text-xl text-[#202124] mb-6">Đang theo dõi</h1>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để xem danh sách theo dõi.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : follows.length === 0 ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Bạn chưa theo dõi keyword hoặc journal nào. Vào trang chi tiết bài
            báo để bắt đầu theo dõi.
          </p>
        ) : (
          <div>
            {follows.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between gap-4 py-4 border-b border-[#EEF1F4]"
              >
                <div className="flex items-center gap-2">
                  <span className="text-[10px] uppercase tracking-wide text-[#9AA0A6] bg-[#F7F8FA] rounded px-1.5 py-0.5">
                    {item.targetType === "KEYWORD" ? "Keyword" : "Journal"}
                  </span>
                  <Link
                    href={linkFor(item)}
                    className="text-sm text-[#1D3557] hover:underline"
                  >
                    {item.targetName}
                  </Link>
                </div>
                <button
                  onClick={() => handleUnfollow(item.id)}
                  title="Bỏ theo dõi"
                  className="shrink-0 text-[#9AA0A6] hover:text-[#B3261E] transition-colors"
                >
                  <UserMinus size={16} aria-hidden="true" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}