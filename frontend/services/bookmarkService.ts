import { apiFetch } from "@/lib/apiClient";

export type BookmarkStatus = {
  bookmarked: boolean;
  bookmarkId: number | null;
};

export type BookmarkItem = {
  id: number;
  paperId: number;
  paperTitle: string;
  createdAt: string;
};

export async function checkBookmarkStatus(
  paperId: number | string,
): Promise<BookmarkStatus> {
  const res = await apiFetch(`/api/bookmarks/check/${paperId}`, {
    skipAuthRedirect: true,
  });
  if (!res.ok) return { bookmarked: false, bookmarkId: null };
  return res.json();
}

export async function addBookmark(paperId: number): Promise<{ id: number }> {
  const res = await apiFetch(`/api/bookmarks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ paperId }),
  });
  if (!res.ok) throw new Error("Không thể lưu bookmark");
  return res.json();
}

export async function removeBookmark(bookmarkId: number): Promise<void> {
  const res = await apiFetch(`/api/bookmarks/${bookmarkId}`, {
    method: "DELETE",
  });
  if (!res.ok) throw new Error("Không thể bỏ bookmark");
}

export async function listMyBookmarks(): Promise<BookmarkItem[]> {
  const res = await apiFetch(`/api/bookmarks/me`);
  if (!res.ok) throw new Error("Không thể tải danh sách bookmark");
  return res.json();
}
