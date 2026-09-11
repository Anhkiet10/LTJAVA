"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Source_Serif_4 } from "next/font/google";
import { RefreshCw, Lock, Unlock } from "lucide-react";
import Header from "@/components/Header";
import {
  listUsers,
  updateUser,
  listSyncLogs,
  triggerSync,
} from "@/services/adminService";
import { useAuth } from "@/hooks/useAuth";
import type { AdminUser, SyncLog } from "@/services/adminService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

function formatDate(iso: string | null) {
  if (!iso) return "—";
  return new Date(iso).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function statusColor(status: string) {
  if (status === "SUCCESS") return "text-[#2E7D32]";
  if (status === "FAILED") return "text-[#B3261E]";
  return "text-[#9AA0A6]";
}

export default function AdminPage() {
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const isAdmin = isAuthenticated && user?.role === "ADMIN";

  const [users, setUsers] = useState<AdminUser[]>([]);
  const [logs, setLogs] = useState<SyncLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [syncing, setSyncing] = useState(false);
  const [syncMessage, setSyncMessage] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!isAdmin) {
      setLoading(false);
      return;
    }

    Promise.all([listUsers(), listSyncLogs()])
      .then(([u, l]) => {
        setUsers(u);
        setLogs(l);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authLoading, isAdmin]);

  async function handleTriggerSync() {
    setSyncing(true);
    setSyncMessage(null);
    try {
      const message = await triggerSync("openalex");
      setSyncMessage(message);
      const l = await listSyncLogs();
      setLogs(l);
    } catch (err) {
      setSyncMessage((err as Error).message);
    } finally {
      setSyncing(false);
    }
  }

  async function handleToggleEnabled(u: AdminUser) {
    try {
      const updated = await updateUser(u.id, { enabled: !u.enabled });
      setUsers((prev) => prev.map((x) => (x.id === u.id ? updated : x)));
    } catch {
      // Giữ nguyên nếu lỗi
    }
  }

  async function handleChangeRole(u: AdminUser, role: "USER" | "ADMIN") {
    try {
      const updated = await updateUser(u.id, { role });
      setUsers((prev) => prev.map((x) => (x.id === u.id ? updated : x)));
    } catch {
      // Giữ nguyên nếu lỗi
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

        <h1 className="text-xl text-[#202124] mb-8">Quản trị hệ thống</h1>

        {authLoading ? null : !isAdmin ? (
          <p className="text-sm text-[#5F6366] text-center py-16">
            Bạn không có quyền truy cập trang này.
          </p>
        ) : loading ? (
          <p className="text-sm text-[#9AA0A6] text-center py-16">
            Đang tải...
          </p>
        ) : error ? (
          <p className="text-sm text-[#B3261E] text-center py-16">{error}</p>
        ) : (
          <>
            {/* Đồng bộ dữ liệu */}
            <section className="mb-12">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6]">
                  Đồng bộ dữ liệu
                </h2>
                <button
                  onClick={handleTriggerSync}
                  disabled={syncing}
                  className="flex items-center gap-1.5 rounded-full bg-[#1D3557] text-white text-xs px-4 py-2 hover:bg-[#16294a] transition-colors disabled:opacity-50"
                >
                  <RefreshCw
                    size={13}
                    className={syncing ? "animate-spin" : ""}
                    aria-hidden="true"
                  />
                  {syncing ? "Đang đồng bộ..." : "Đồng bộ ngay (OpenAlex)"}
                </button>
              </div>
              {syncMessage && (
                <p className="text-sm text-[#5F6366] mb-4">{syncMessage}</p>
              )}

              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-xs text-[#9AA0A6] border-b border-[#EEF1F4]">
                    <th className="py-2 font-normal">Trạng thái</th>
                    <th className="py-2 font-normal">Bắt đầu</th>
                    <th className="py-2 font-normal">Kết thúc</th>
                    <th className="py-2 font-normal text-right">Số bài mới</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.slice(0, 8).map((log) => (
                    <tr key={log.id} className="border-b border-[#EEF1F4]">
                      <td className={`py-2 ${statusColor(log.status)}`}>
                        {log.status}
                      </td>
                      <td className="py-2 text-[#5F6366]">
                        {formatDate(log.startedAt)}
                      </td>
                      <td className="py-2 text-[#5F6366]">
                        {formatDate(log.finishedAt)}
                      </td>
                      <td className="py-2 text-right font-mono text-xs">
                        {log.recordsSynced}
                      </td>
                    </tr>
                  ))}
                  {logs.length === 0 && (
                    <tr>
                      <td
                        colSpan={4}
                        className="py-6 text-center text-[#9AA0A6]"
                      >
                        Chưa có lượt đồng bộ nào.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </section>

            {/* Quản lý người dùng */}
            <section>
              <h2 className="text-xs uppercase tracking-widest text-[#9AA0A6] mb-3">
                Quản lý người dùng ({users.length})
              </h2>
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-xs text-[#9AA0A6] border-b border-[#EEF1F4]">
                    <th className="py-2 font-normal">Username</th>
                    <th className="py-2 font-normal">Email</th>
                    <th className="py-2 font-normal">Role</th>
                    <th className="py-2 font-normal">Trạng thái</th>
                    <th className="py-2 font-normal"></th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id} className="border-b border-[#EEF1F4]">
                      <td className="py-2.5 text-[#202124]">{u.username}</td>
                      <td className="py-2.5 text-[#5F6366]">{u.email}</td>
                      <td className="py-2.5">
                        <select
                          value={u.role}
                          onChange={(e) =>
                            handleChangeRole(
                              u,
                              e.target.value as "USER" | "ADMIN",
                            )
                          }
                          className="text-xs border border-[#DFE1E5] rounded px-2 py-1 outline-none focus:border-[#1D3557]"
                        >
                          <option value="USER">USER</option>
                          <option value="ADMIN">ADMIN</option>
                        </select>
                      </td>
                      <td className="py-2.5">
                        <span
                          className={
                            u.enabled ? "text-[#2E7D32]" : "text-[#B3261E]"
                          }
                        >
                          {u.enabled ? "Hoạt động" : "Đã khóa"}
                        </span>
                      </td>
                      <td className="py-2.5 text-right">
                        <button
                          onClick={() => handleToggleEnabled(u)}
                          title={u.enabled ? "Khóa tài khoản" : "Mở khóa"}
                          className="text-[#9AA0A6] hover:text-[#1D3557] transition-colors"
                        >
                          {u.enabled ? (
                            <Lock size={15} aria-hidden="true" />
                          ) : (
                            <Unlock size={15} aria-hidden="true" />
                          )}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          </>
        )}
      </div>
    </main>
  );
}
