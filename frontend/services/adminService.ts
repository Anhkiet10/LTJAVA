import { apiFetch } from "@/lib/apiClient";

export type AdminUser = {
  id: number;
  username: string;
  email: string;
  role: "USER" | "ADMIN";
  enabled: boolean;
  createdAt: string;
};

export type SyncLog = {
  id: number;
  apiSource: string;
  status: string;
  startedAt: string;
  finishedAt: string | null;
  recordsSynced: number;
};

export async function listUsers(): Promise<AdminUser[]> {
  const res = await apiFetch(`/api/admin/users`);
  if (!res.ok) throw new Error("Không thể tải danh sách người dùng");
  return res.json();
}

export async function updateUser(
  id: number,
  data: { role?: "USER" | "ADMIN"; enabled?: boolean },
): Promise<AdminUser> {
  const res = await apiFetch(`/api/admin/users/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Không thể cập nhật người dùng");
  return res.json();
}

export async function listSyncLogs(): Promise<SyncLog[]> {
  const res = await apiFetch(`/api/admin/sync-logs`);
  if (!res.ok) throw new Error("Không thể tải lịch sử đồng bộ");
  return res.json();
}

export async function triggerSync(
  source: string = "openalex",
): Promise<string> {
  const res = await apiFetch(`/api/admin/sync/${source}`, { method: "POST" });
  const text = await res.text();
  if (!res.ok) throw new Error(text || "Đồng bộ thất bại");
  return text;
}

export async function processForAi(paperId: number): Promise<string> {
  const res = await apiFetch(`/api/admin/full-text/${paperId}/process`, {
    method: "POST",
  });
  const text = await res.text();
  if (!res.ok) throw new Error(text || "Xử lý thất bại");
  return text;
}
