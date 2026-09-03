"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import {
  ArrowLeft,
  ExternalLink,
  Bookmark,
  BookmarkCheck,
  Plus,
  Check,
  Sparkles,
  Send,
  Download,
} from "lucide-react";
import Header from "@/components/Header";
import { getPaperById } from "@/services/paperService";
import {
  checkBookmarkStatus,
  addBookmark,
  removeBookmark,
} from "@/services/bookmarkService";
import {
  checkFollowStatus,
  followTarget,
  unfollowTarget,
} from "@/services/followService";
import { processForAi } from "@/services/adminService";
import { askQuestion } from "@/services/ragService";
import { useAuth } from "@/hooks/useAuth";
import type { PaperDetail } from "@/types/paper";
import type { AskResponse } from "@/services/ragService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

type FollowState = {
  followed: boolean;
  followId: number | null;
  busy: boolean;
};

export default function PaperDetailPage() {
  const params = useParams();
  const router = useRouter();
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const isAdmin = isAuthenticated && user?.role === "ADMIN";

  const [paper, setPaper] = useState<PaperDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [bookmarked, setBookmarked] = useState(false);
  const [bookmarkId, setBookmarkId] = useState<number | null>(null);
  const [bookmarkBusy, setBookmarkBusy] = useState(false);

  const [journalFollow, setJournalFollow] = useState<FollowState>({
    followed: false,
    followId: null,
    busy: false,
  });
  const [keywordFollows, setKeywordFollows] = useState<
    Record<number, FollowState>
  >({});

  const [aiProcessing, setAiProcessing] = useState(false);
  const [aiProcessMessage, setAiProcessMessage] = useState<string | null>(null);

  const [question, setQuestion] = useState("");
  const [asking, setAsking] = useState(false);
  const [askResult, setAskResult] = useState<AskResponse | null>(null);
  const [askError, setAskError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    const id = params.id as string;
    getPaperById(id)
      .then(async (data) => {
        setPaper(data);

        const bookmarkStatus = await checkBookmarkStatus(id);
        setBookmarked(bookmarkStatus.bookmarked);
        setBookmarkId(bookmarkStatus.bookmarkId);

        if (data.journalId) {
          const status = await checkFollowStatus("JOURNAL", data.journalId);
          setJournalFollow({
            followed: status.followed,
            followId: status.followId,
            busy: false,
          });
        }

        const keywordStatuses = await Promise.all(
          data.keywords.map((k) => checkFollowStatus("KEYWORD", k.id)),
        );
        const map: Record<number, FollowState> = {};
        data.keywords.forEach((k, i) => {
          map[k.id] = {
            followed: keywordStatuses[i].followed,
            followId: keywordStatuses[i].followId,
            busy: false,
          };
        });
        setKeywordFollows(map);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated, params.id]);

  async function handleToggleBookmark() {
    if (!paper || bookmarkBusy) return;
    setBookmarkBusy(true);
    try {
      if (bookmarked && bookmarkId) {
        await removeBookmark(bookmarkId);
        setBookmarked(false);
        setBookmarkId(null);
      } else {
        const res = await addBookmark(paper.id);
        setBookmarked(true);
        setBookmarkId(res.id);
      }
    } catch {
      // Giữ nguyên trạng thái cũ nếu lỗi
    } finally {
      setBookmarkBusy(false);
    }
  }

  async function handleToggleJournalFollow() {
    if (!paper?.journalId || journalFollow.busy) return;
    setJournalFollow((s) => ({ ...s, busy: true }));
    try {
      if (journalFollow.followed && journalFollow.followId) {
        await unfollowTarget(journalFollow.followId);
        setJournalFollow({ followed: false, followId: null, busy: false });
      } else {
        const res = await followTarget("JOURNAL", paper.journalId);
        setJournalFollow({ followed: true, followId: res.id, busy: false });
      }
    } catch {
      setJournalFollow((s) => ({ ...s, busy: false }));
    }
  }

  async function handleToggleKeywordFollow(keywordId: number) {
    const current = keywordFollows[keywordId] || {
      followed: false,
      followId: null,
      busy: false,
    };
    if (current.busy) return;
    setKeywordFollows((prev) => ({
      ...prev,
      [keywordId]: { ...current, busy: true },
    }));
    try {
      if (current.followed && current.followId) {
        await unfollowTarget(current.followId);
        setKeywordFollows((prev) => ({
          ...prev,
          [keywordId]: { followed: false, followId: null, busy: false },
        }));
      } else {
        const res = await followTarget("KEYWORD", keywordId);
        setKeywordFollows((prev) => ({
          ...prev,
          [keywordId]: { followed: true, followId: res.id, busy: false },
        }));
      }
    } catch {
      setKeywordFollows((prev) => ({
        ...prev,
        [keywordId]: { ...current, busy: false },
      }));
    }
  }

  async function handleProcessForAi() {
    if (!paper || aiProcessing) return;
    setAiProcessing(true);
    setAiProcessMessage(null);
    try {
      const message = await processForAi(paper.id);
      setAiProcessMessage(message);
      const updated = await getPaperById(paper.id);
      setPaper(updated);
    } catch (err) {
      setAiProcessMessage((err as Error).message);
    } finally {
      setAiProcessing(false);
    }
  }

  async function handleAsk(e: React.FormEvent) {
    e.preventDefault();
    if (!paper || !question.trim() || asking) return;
    setAsking(true);
    setAskError(null);
    setAskResult(null);
    try {
      const result = await askQuestion(question, paper.id);
      setAskResult(result);
    } catch (err) {
      setAskError((err as Error).message);
    } finally {
      setAsking(false);
    }
  }

  return (
    <main className="min-h-screen bg-white flex flex-col">
      <Header />

      <div className="w-full max-w-2xl mx-auto px-6 pt-4 pb-20">
        <button
          onClick={() => router.back()}
          className="flex items-center gap-1.5 text-sm text-[#5F6366] hover:text-[#1D3557] transition-colors mb-8"
        >
          <ArrowLeft size={16} aria-hidden="true" />
          Quay lại
        </button>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để xem chi tiết bài báo.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : paper ? (
          <>
            <div className="flex items-start justify-between gap-4 mb-4">
              <h1
                className={`${sourceSerif.className} text-2xl md:text-3xl text-[#202124] leading-snug`}
              >
                {paper.title}
              </h1>
              <button
                onClick={handleToggleBookmark}
                disabled={bookmarkBusy}
                title={bookmarked ? "Bỏ lưu" : "Lưu bài báo"}
                className="shrink-0 flex items-center gap-1.5 rounded-full border border-[#DFE1E5] px-3 py-2 text-xs text-[#5F6366] hover:border-[#1D3557] hover:text-[#1D3557] transition-colors disabled:opacity-50 mt-1"
              >
                {bookmarked ? (
                  <>
                    <BookmarkCheck
                      size={15}
                      className="text-[#1D3557]"
                      aria-hidden="true"
                    />
                    <span className="text-[#1D3557]">Đã lưu</span>
                  </>
                ) : (
                  <>
                    <Bookmark size={15} aria-hidden="true" />
                    <span>Lưu</span>
                  </>
                )}
              </button>
            </div>

            <div className="flex flex-wrap items-center gap-x-2 gap-y-1 text-sm text-[#5F6366] mb-6">
              {paper.authorNames.length > 0 && (
                <span>{paper.authorNames.join(", ")}</span>
              )}
              {paper.journalName && (
                <>
                  <span className="text-[#DFE1E5]">•</span>
                  <span className="italic">
                    {paper.journalName}
                    {paper.journalPublisher
                      ? ` (${paper.journalPublisher})`
                      : ""}
                  </span>
                  {paper.journalId && (
                    <button
                      onClick={handleToggleJournalFollow}
                      disabled={journalFollow.busy}
                      className="inline-flex items-center gap-1 text-xs text-[#1D3557] hover:underline disabled:opacity-50"
                    >
                      {journalFollow.followed ? (
                        <>
                          <Check size={12} aria-hidden="true" /> Đang theo dõi
                        </>
                      ) : (
                        <>
                          <Plus size={12} aria-hidden="true" /> Theo dõi journal
                        </>
                      )}
                    </button>
                  )}
                </>
              )}
              <span className="text-[#DFE1E5]">•</span>
              <span>{paper.publicationYear}</span>
            </div>

            <div className="flex items-center gap-4 mb-8">
              {paper.doi && (
                <a
                  href={`https://doi.org/${paper.doi}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1.5 text-sm text-[#1D3557] hover:underline"
                >
                  doi.org/{paper.doi}
                  <ExternalLink size={13} aria-hidden="true" />
                </a>
              )}

              {paper.oaUrl && (
                <a
                  href={paper.oaUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-1.5 rounded-full border border-[#DFE1E5] px-3 py-1.5 text-xs text-[#1D3557] hover:border-[#1D3557] hover:bg-[#FAFBFC] transition-colors"
                >
                  <Download size={13} aria-hidden="true" />
                  Tải PDF
                </a>
              )}
            </div>

            <div className="mb-10">
              <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-2">
                Tóm tắt
              </h2>
              {paper.abstractText ? (
                <p className="text-sm text-[#202124] leading-relaxed whitespace-pre-line">
                  {paper.abstractText}
                </p>
              ) : (
                <p className="text-sm text-[#9AA0A6] italic">
                  Bài báo này không có tóm tắt.
                </p>
              )}
            </div>

            {paper.keywords.length > 0 && (
              <div className="mb-10">
                <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-3">
                  Chủ đề liên quan
                </h2>
                <div className="flex flex-wrap gap-2">
                  {paper.keywords.map((kw) => {
                    const state = keywordFollows[kw.id] || {
                      followed: false,
                      followId: null,
                      busy: false,
                    };
                    return (
                      <div
                        key={kw.id}
                        className="flex items-center gap-1.5 rounded-full bg-[#F7F8FA] hover:bg-[#EEF1F4] pl-3 pr-1.5 py-1.5 text-xs text-[#202124] transition-colors"
                      >
                        <Link
                          href={`/search?keywordId=${kw.id}&label=${encodeURIComponent(kw.name)}`}
                        >
                          {kw.name}
                        </Link>
                        <button
                          onClick={() => handleToggleKeywordFollow(kw.id)}
                          disabled={state.busy}
                          title={state.followed ? "Bỏ theo dõi" : "Theo dõi"}
                          className={`rounded-full p-1 transition-colors disabled:opacity-50 ${
                            state.followed
                              ? "text-[#1D3557]"
                              : "text-[#9AA0A6] hover:text-[#1D3557]"
                          }`}
                        >
                          {state.followed ? (
                            <Check size={11} aria-hidden="true" />
                          ) : (
                            <Plus size={11} aria-hidden="true" />
                          )}
                        </button>
                      </div>
                    );
                  })}
                </div>
              </div>
            )}

            {/* Khu vực AI */}
            <div className="border-t border-[#EEF1F4] pt-8">
              <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-3 flex items-center gap-1.5">
                <Sparkles size={13} aria-hidden="true" />
                Hỏi AI về bài báo này
              </h2>

              {!paper.aiIndexed ? (
                <div className="text-sm text-[#9AA0A6]">
                  <p className="mb-3">
                    Bài báo này chưa được xử lý để hỏi-đáp bằng AI.
                  </p>
                  {isAdmin ? (
                    <>
                      <button
                        onClick={handleProcessForAi}
                        disabled={aiProcessing}
                        className="rounded-full bg-[#1D3557] text-white text-xs px-4 py-2 hover:bg-[#16294a] transition-colors disabled:opacity-50"
                      >
                        {aiProcessing
                          ? "Đang xử lý (có thể mất vài phút)..."
                          : "Xử lý cho AI"}
                      </button>
                      {aiProcessMessage && (
                        <p className="mt-2 text-xs text-[#5F6366]">
                          {aiProcessMessage}
                        </p>
                      )}
                    </>
                  ) : (
                    <p className="text-xs">
                      Liên hệ quản trị viên để kích hoạt tính năng này cho bài
                      báo.
                    </p>
                  )}
                </div>
              ) : (
                <>
                  <form
                    onSubmit={handleAsk}
                    className="flex items-center gap-2 mb-4"
                  >
                    <input
                      type="text"
                      value={question}
                      onChange={(e) => setQuestion(e.target.value)}
                      placeholder="Đặt câu hỏi về nội dung bài báo..."
                      className="flex-1 rounded-full border border-[#DFE1E5] px-4 py-2.5 text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none focus:border-[#1D3557] transition-colors"
                    />
                    <button
                      type="submit"
                      disabled={asking || !question.trim()}
                      className="shrink-0 rounded-full bg-[#1D3557] text-white p-2.5 hover:bg-[#16294a] transition-colors disabled:opacity-50"
                      title="Gửi câu hỏi"
                    >
                      <Send size={16} aria-hidden="true" />
                    </button>
                  </form>

                  {asking && (
                    <p className="text-sm text-[#9AA0A6]">
                      Đang tìm câu trả lời...
                    </p>
                  )}
                  {askError && (
                    <p className="text-sm text-[#B3261E]">{askError}</p>
                  )}

                  {askResult && (
                    <div className="bg-[#FAFBFC] rounded-lg p-4">
                      <p className="text-sm text-[#202124] leading-relaxed whitespace-pre-line mb-3">
                        {askResult.answer}
                      </p>
                      {askResult.sources.length > 0 && (
                        <p className="text-xs text-[#9AA0A6]">
                          Dựa trên {askResult.sources.length} đoạn trích từ bài
                          báo.
                        </p>
                      )}
                    </div>
                  )}
                </>
              )}
            </div>
          </>
        ) : null}
      </div>
    </main>
  );
}
