"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { Sparkles } from "lucide-react";
import Header from "@/components/Header";
import {
  getDashboardSummary,
  getTrend,
  getGapAnalysis,
} from "@/services/dashboardService";
import { useAuth } from "@/hooks/useAuth";
import type {
  DashboardSummary,
  YearCount,
  GapAnalysis,
} from "@/services/dashboardService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

function TopList({
  title,
  items,
  hrefFor,
}: {
  title: string;
  items: { id: number; name: string; count: number }[];
  hrefFor: (id: number, name: string) => string;
}) {
  return (
    <div className="flex-1">
      <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-3">
        {title}
      </h2>
      <div>
        {items.map((item) => (
          <Link
            key={item.id}
            href={hrefFor(item.id, item.name)}
            className="flex items-center justify-between py-2.5 border-b border-[#EEF1F4] text-sm hover:bg-[#FAFBFC] transition-colors -mx-2 px-2 rounded"
          >
            <span className="text-[#202124]">{item.name}</span>
            <span className="text-[#9AA0A6] font-mono text-xs">
              {item.count}
            </span>
          </Link>
        ))}
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [selectedKeywordId, setSelectedKeywordId] = useState<number | null>(
    null,
  );
  const [trend, setTrend] = useState<YearCount[]>([]);
  const [trendLoading, setTrendLoading] = useState(false);

  const [gapAnalysis, setGapAnalysis] = useState<GapAnalysis | null>(null);
  const [gapLoading, setGapLoading] = useState(false);
  const [gapError, setGapError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    getDashboardSummary()
      .then((data) => {
        setSummary(data);
        if (data.topKeywords.length > 0) {
          setSelectedKeywordId(data.topKeywords[0].id);
        }
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated]);

  useEffect(() => {
    if (!selectedKeywordId) return;
    setTrendLoading(true);
    setGapAnalysis(null);
    setGapError(null);
    getTrend(selectedKeywordId)
      .then(setTrend)
      .finally(() => setTrendLoading(false));
  }, [selectedKeywordId]);

  async function handleAnalyzeGap() {
    if (!selectedKeywordId || gapLoading) return;
    setGapLoading(true);
    setGapError(null);
    setGapAnalysis(null);
    try {
      const result = await getGapAnalysis(selectedKeywordId);
      setGapAnalysis(result);
    } catch (err) {
      setGapError((err as Error).message);
    } finally {
      setGapLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-white flex flex-col">
      <Header />

      <div className="w-full max-w-3xl mx-auto px-6 pt-4 pb-20">
        <Link
          href="/"
          className={`${sourceSerif.className} text-base text-[#1D3557] mb-6 inline-block`}
        >
          ResearchPulse
        </Link>

        <h1 className="text-xl text-[#202124] mb-8">Dashboard</h1>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để xem dashboard.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : summary ? (
          <>
            <div className="mb-10">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6]">
                  Xu hướng công bố theo năm
                </h2>
                {summary.topKeywords.length > 0 && (
                  <select
                    value={selectedKeywordId ?? ""}
                    onChange={(e) =>
                      setSelectedKeywordId(Number(e.target.value))
                    }
                    className="text-xs border border-[#DFE1E5] rounded-lg px-2 py-1.5 text-[#202124] outline-none focus:border-[#1D3557]"
                  >
                    {summary.topKeywords.map((k) => (
                      <option key={k.id} value={k.id}>
                        {k.name}
                      </option>
                    ))}
                  </select>
                )}
              </div>

              {trendLoading ? (
                <p className="text-sm text-[#9AA0A6] text-center py-10">
                  Đang tải biểu đồ...
                </p>
              ) : trend.length === 0 ? (
                <p className="text-sm text-[#9AA0A6] text-center py-10">
                  Chưa đủ dữ liệu để vẽ biểu đồ.
                </p>
              ) : (
                <div className="h-56 -ml-4">
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart
                      data={trend}
                      margin={{ top: 5, right: 20, left: 0, bottom: 0 }}
                    >
                      <CartesianGrid
                        strokeDasharray="3 3"
                        stroke="#EEF1F4"
                        vertical={false}
                      />
                      <XAxis
                        dataKey="year"
                        tick={{ fontSize: 12, fill: "#9AA0A6" }}
                        axisLine={{ stroke: "#EEF1F4" }}
                        tickLine={false}
                      />
                      <YAxis
                        allowDecimals={false}
                        tick={{ fontSize: 12, fill: "#9AA0A6" }}
                        axisLine={false}
                        tickLine={false}
                        width={30}
                      />
                      <Tooltip
                        contentStyle={{
                          fontSize: 12,
                          borderRadius: 8,
                          border: "1px solid #EEF1F4",
                        }}
                        labelStyle={{ color: "#202124" }}
                      />
                      <Line
                        type="monotone"
                        dataKey="count"
                        stroke="#1D3557"
                        strokeWidth={2}
                        dot={{ r: 3, fill: "#1D3557" }}
                        name="Số bài báo"
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              )}
            </div>

            {/* Research Gap Analysis */}
            <div className="mb-10 border-t border-[#EEF1F4] pt-8">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] flex items-center gap-1.5">
                  <Sparkles size={13} aria-hidden="true" />
                  Phân tích khoảng trống nghiên cứu (AI)
                </h2>
                <button
                  onClick={handleAnalyzeGap}
                  disabled={!selectedKeywordId || gapLoading}
                  className="rounded-full bg-[#1D3557] text-white text-xs px-4 py-2 hover:bg-[#16294a] transition-colors disabled:opacity-50"
                >
                  {gapLoading ? "Đang phân tích..." : "Phân tích"}
                </button>
              </div>

              {gapError && <p className="text-sm text-[#B3261E]">{gapError}</p>}

              {gapAnalysis && (
                <div className="bg-[#FAFBFC] rounded-lg p-5">
                  <p className="text-xs text-[#9AA0A6] mb-3">
                    Dựa trên {gapAnalysis.paperCount} bài báo chủ đề &quot;
                    {gapAnalysis.keywordName}&quot;, giai đoạn{" "}
                    {gapAnalysis.yearFrom}–{gapAnalysis.yearTo}
                  </p>
                  <p className="text-sm text-[#202124] leading-relaxed whitespace-pre-line">
                    {gapAnalysis.analysis}
                  </p>
                </div>
              )}

              {!gapAnalysis && !gapError && !gapLoading && (
                <p className="text-sm text-[#9AA0A6]">
                  Bấm &quot;Phân tích&quot; để AI tổng hợp xu hướng và chỉ ra
                  khoảng trống nghiên cứu cho chủ đề đang chọn ở trên.
                </p>
              )}
            </div>

            <div className="flex gap-10">
              <TopList
                title="Chủ đề nhiều bài báo nhất"
                items={summary.topKeywords}
                hrefFor={(id, name) =>
                  `/search?keywordId=${id}&label=${encodeURIComponent(name)}`
                }
              />
              <TopList
                title="Journal nhiều bài báo nhất"
                items={summary.topJournals}
                hrefFor={(_, name) =>
                  `/search?journal=${encodeURIComponent(name)}`
                }
              />
            </div>
          </>
        ) : null}
      </div>
    </main>
  );
}
