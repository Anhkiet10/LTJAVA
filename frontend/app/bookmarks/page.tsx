"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import { BookmarkX } from "lucide-react";
import Header from "@/components/Header";
import { listMyBookmarks, removeBookmark } from "@/services/bookmarkService";
import { useAuth } from "@/hooks/useAuth";
import type { BookmarkItem } from "@/services/bookmarkService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function BookmarksPage() {
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [bookmarks, setBookmarks] = useState<BookmarkItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    listMyBookmarks()
      .then(setBookmarks)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated]);

  async function handleRemove(bookmarkId: number) {
    try {
      await removeBookmark(bookmarkId);
      setBookmarks((prev) => prev.filter((b) => b.id !== bookmarkId));
    } catch {
      // Giữ nguyên danh sách nếu xóa thất bại
    }
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

        <h1 className="text-xl text-[#202124] mb-6">Bài báo đã lưu</h1>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để xem danh sách đã lưu.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : bookmarks.length === 0 ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Bạn chưa lưu bài báo nào. Vào trang chi tiết bài báo và bấm
            &quot;Lưu&quot; để thêm vào đây.
          </p>
        ) : (
          <div>
            {bookmarks.map((bookmark) => (
              <div
                key={bookmark.id}
                className="flex items-start justify-between gap-4 py-4 border-b border-[#EEF1F4]"
              >
                <Link
                  href={`/papers/${bookmark.paperId}`}
                  className="text-sm text-[#1D3557] hover:underline leading-snug"
                >
                  {bookmark.paperTitle}
                </Link>
                <button
                  onClick={() => handleRemove(bookmark.id)}
                  title="Bỏ lưu"
                  className="shrink-0 text-[#9AA0A6] hover:text-[#B3261E] transition-colors"
                >
                  <BookmarkX size={16} aria-hidden="true" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}
