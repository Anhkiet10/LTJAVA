import { apiFetch } from "@/lib/apiClient";
import { getToken } from "@/lib/auth";
import type { PageResponse } from "@/types/paper";

export type NotificationItem = {
  id: number;
  content: string;
  type: string;
  isRead: boolean;
  createdAt: string;
};

export async function getMyNotifications(
  page = 0,
  size = 20,
): Promise<PageResponse<NotificationItem>> {
  const res = await apiFetch(`/api/notifications/me?page=${page}&size=${size}`);
  if (!res.ok) throw new Error("Không thể tải thông báo");
  return res.json();
}

export async function markNotificationRead(id: number): Promise<void> {
  await apiFetch(`/api/notifications/${id}/read`, {
    method: "PATCH",
    skipAuthRedirect: true,
  });
}

export async function getUnreadCount(): Promise<number> {
  const token = getToken();
  if (!token) return 0;

  const res = await apiFetch(`/api/notifications/unread-count`, {
    skipAuthRedirect: true,
  });
  if (!res.ok) return 0;
  return res.json();
}
