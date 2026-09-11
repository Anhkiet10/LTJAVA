"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Bell } from "lucide-react";
import { getUnreadCount } from "@/services/notificationService";
import { useAuth } from "@/hooks/useAuth";

export default function NotificationBell() {
  const { isAuthenticated, loading } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (loading || !isAuthenticated) return;

    getUnreadCount().then(setUnreadCount);

    // Kiểm tra lại mỗi 60s để bắt các thông báo mới phát sinh (ví dụ sau khi admin sync)
    const interval = setInterval(() => {
      getUnreadCount().then(setUnreadCount);
    }, 60000);

    return () => clearInterval(interval);
  }, [loading, isAuthenticated]);

  if (loading || !isAuthenticated) return null;

  return (
    <Link
      href="/notifications"
      className="relative flex items-center text-[#5F6366] hover:text-[#1D3557] transition-colors"
    >
      <Bell size={18} aria-hidden="true" />
      {unreadCount > 0 && (
        <span className="absolute -top-1.5 -right-1.5 flex items-center justify-center min-w-[16px] h-[16px] px-1 rounded-full bg-[#B3261E] text-white text-[10px] leading-none">
          {unreadCount > 9 ? "9+" : unreadCount}
        </span>
      )}
    </Link>
  );
}
