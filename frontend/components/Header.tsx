"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { Source_Serif_4 } from "next/font/google";
import { useAuth } from "@/hooks/useAuth";
import NotificationBell from "@/components/NotificationBell";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function Header() {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const router = useRouter();

  function handleLogout() {
    logout();
    router.push("/");
  }

  return (
    <header className="w-full flex items-center justify-between px-8 py-6">
      <Link
        href="/"
        className={`${sourceSerif.className} text-lg tracking-tight text-[#1D3557]`}
      >
        ResearchPulse
      </Link>

      <nav className="flex items-center gap-6 text-sm text-[#5F6366]">
        {loading ? null : isAuthenticated ? (
          <>
            <NotificationBell />
            {user?.role === "ADMIN" && (
              <Link
                href="/admin"
                className="hover:text-[#1D3557] transition-colors"
              >
                Admin
              </Link>
            )}
            <Link
              href="/dashboard"
              className="hover:text-[#1D3557] transition-colors"
            >
              Dashboard
            </Link>
            <Link
              href="/following"
              className="hover:text-[#1D3557] transition-colors"
            >
              Đang theo dõi
            </Link>
            <Link
              href="/bookmarks"
              className="hover:text-[#1D3557] transition-colors"
            >
              Đã lưu
            </Link>
            <span className="text-[#202124]">Xin chào, {user?.username}</span>
            <button
              onClick={handleLogout}
              className="rounded-full border border-[#DFE1E5] px-4 py-1.5 hover:border-[#1D3557] hover:text-[#1D3557] transition-colors"
            >
              Đăng xuất
            </button>
          </>
        ) : (
          <>
            <Link
              href="/login"
              className="hover:text-[#1D3557] transition-colors"
            >
              Đăng nhập
            </Link>
            <Link
              href="/register"
              className="rounded-full border border-[#1D3557] px-4 py-1.5 text-[#1D3557] hover:bg-[#1D3557] hover:text-white transition-colors"
            >
              Đăng ký
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}
