"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import { Circle } from "lucide-react";
import Header from "@/components/Header";
import {
  getMyNotifications,
  markNotificationRead,
} from "@/services/notificationService";
import { useAuth } from "@/hooks/useAuth";
import type { NotificationItem } from "@/services/notificationService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

function formatDate(iso: string) {
  const date = new Date(iso);
  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function NotificationsPage() {
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    getMyNotifications(0, 30)
      .then((res) => setNotifications(res.content))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAuthenticated]);

  async function handleMarkRead(id: number) {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, isRead: true } : n)),
    );
    try {
      await markNotificationRead(id);
    } catch {
      // Nếu lỗi, giữ nguyên UI đã đổi (không nghiêm trọng — chỉ ảnh hưởng trạng thái đọc)
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

        <h1 className="text-xl text-[#202124] mb-6">Thông báo</h1>

        {authLoading ? null : !isAuthenticated ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn cần{" "}
            <Link href="/login" className="text-[#1D3557] hover:underline">
              đăng nhập
            </Link>{" "}
            để xem thông báo.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : notifications.length === 0 ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Bạn chưa có thông báo nào. Theo dõi keyword/journal để nhận thông
            báo khi có bài báo mới.
          </p>
        ) : (
          <div>
            {notifications.map((n) => (
              <button
                key={n.id}
                onClick={() => !n.isRead && handleMarkRead(n.id)}
                className={`w-full text-left flex items-start gap-3 py-4 border-b border-[#EEF1F4] transition-colors ${
                  n.isRead ? "" : "bg-[#FAFBFC]"
                }`}
              >
                {!n.isRead && (
                  <Circle
                    size={8}
                    className="text-[#1D3557] fill-[#1D3557] mt-1.5 shrink-0"
                    aria-hidden="true"
                  />
                )}
                <div className={n.isRead ? "pl-[20px]" : ""}>
                  <p className="text-sm text-[#202124]">{n.content}</p>
                  <p className="text-xs text-[#9AA0A6] mt-1">
                    {formatDate(n.createdAt)}
                  </p>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}
