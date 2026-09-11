"use client";

import { useEffect, useState, FormEvent, Suspense } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import { Search } from "lucide-react";
import Header from "@/components/Header";
import PaperCard from "@/components/PaperCard";
import { searchPapers } from "@/services/paperService";
import { useAuth } from "@/hooks/useAuth";
import type { PageResponse, PaperSummary } from "@/types/paper";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

type ActiveQuery =
  | { type: "keyword"; value: string }
  | { type: "keywordId"; value: number }
  | { type: "journal"; value: string };

function SearchPageInner() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { isAuthenticated, loading: authLoading } = useAuth();

  const initialKeywordId = searchParams.get("keywordId");
  const initialLabel = searchParams.get("label");
  const initialJournal = searchParams.get("journal");
  const initialQuery = searchParams.get("q") || "";

  const [query, setQuery] = useState(initialQuery);
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<PageResponse<PaperSummary> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [heading, setHeading] = useState<string | null>(null);
  const [activeQuery, setActiveQuery] = useState<ActiveQuery | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) return;

    if (initialKeywordId) {
      const q: ActiveQuery = {
        type: "keywordId",
        value: Number(initialKeywordId),
      };
      setActiveQuery(q);
      setHeading(initialLabel);
      runSearch(q, 0);
    } else if (initialJournal) {
      const q: ActiveQuery = { type: "journal", value: initialJournal };
      setActiveQuery(q);
      setHeading(initialJournal);
      runSearch(q, 0);
    } else if (initialQuery) {
      const q: ActiveQuery = { type: "keyword", value: initialQuery };
      setActiveQuery(q);
      setHeading(null);
      runSearch(q, 0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [authLoading, isAuthenticated]);

  async function runSearch(q: ActiveQuery, targetPage: number) {
    setLoading(true);
    setError(null);
    try {
      const options =
        q.type === "keyword"
          ? { keyword: q.value }
          : q.type === "keywordId"
            ? { keywordId: q.value }
            : { journal: q.value };

      const res = await searchPapers({
        ...options,
        page: targetPage,
        size: 10,
      });
      setResult(res);
      setPage(targetPage);
    } catch {
      setError("Không thể tải kết quả tìm kiếm. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!query.trim()) return;
    const q: ActiveQuery = { type: "keyword", value: query };
    setActiveQuery(q);
    setHeading(null);
    router.replace(`/search?q=${encodeURIComponent(query)}`);
    runSearch(q, 0);
  }

  function handlePageChange(newPage: number) {
    if (activeQuery) runSearch(activeQuery, newPage);
  }

  const displayTitle =
    heading || (activeQuery?.type === "keyword" ? activeQuery.value : null);

  return (
    <main className="min-h-screen bg-white flex flex-col">
      <Header />

      <div className="w-full max-w-2xl mx-auto px-6 pt-4 pb-2">
        <Link
          href="/"
          className={`${sourceSerif.className} text-base text-[#1D3557] mb-6 inline-block`}
        >
          ResearchPulse
        </Link>

        <form onSubmit={handleSubmit} className="mb-8">
          <div className="flex items-center gap-3 rounded-full border border-[#DFE1E5] px-5 py-3 shadow-sm focus-within:shadow-md focus-within:border-[#1D3557] transition-all">
            <Search
              size={18}
              className="text-[#9AA0A6] shrink-0"
              aria-hidden="true"
            />
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Tìm bài báo, tác giả, từ khóa..."
              className="w-full bg-transparent text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none"
            />
          </div>
        </form>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-10">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để tìm kiếm bài báo.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-10">
            Đang tìm kiếm...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-10">{error}</p>
        ) : result && result.content.length === 0 ? (
          <p className="text-sm text-[#9AA0A6] text-center py-10">
            Không tìm thấy bài báo nào phù hợp với &quot;{displayTitle}&quot;.
          </p>
        ) : result ? (
          <>
            <p className="text-xs text-[#9AA0A6] mb-2">
              {displayTitle && (
                <span className="font-medium text-[#202124]">
                  {displayTitle}
                </span>
              )}
              {displayTitle ? " — " : ""}Khoảng {result.totalElements} kết quả
            </p>
            <div>
              {result.content.map((paper) => (
                <PaperCard key={paper.id} paper={paper} />
              ))}
            </div>

            <div className="flex items-center justify-center gap-4 mt-8 mb-16 text-sm">
              <button
                onClick={() => handlePageChange(page - 1)}
                disabled={page === 0}
                className="text-[#1D3557] disabled:text-[#DFE1E5] disabled:cursor-not-allowed"
              >
                ← Trước
              </button>
              <span className="text-[#9AA0A6]">
                Trang {page + 1} / {Math.max(result.totalPages, 1)}
              </span>
              <button
                onClick={() => handlePageChange(page + 1)}
                disabled={page + 1 >= result.totalPages}
                className="text-[#1D3557] disabled:text-[#DFE1E5] disabled:cursor-not-allowed"
              >
                Sau →
              </button>
            </div>
          </>
        ) : null}
      </div>
    </main>
  );
}

export default function SearchPage() {
  return (
    <Suspense fallback={null}>
      <SearchPageInner />
    </Suspense>
  );
}