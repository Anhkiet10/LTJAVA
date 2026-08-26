import { apiFetch } from "@/lib/apiClient";

export type TargetType = "KEYWORD" | "JOURNAL";

export type FollowItem = {
  id: number;
  targetType: TargetType;
  targetId: number;
  targetName: string;
};

export type FollowStatus = {
  followed: boolean;
  followId: number | null;
};

export async function checkFollowStatus(
  targetType: TargetType,
  targetId: number,
): Promise<FollowStatus> {
  const res = await apiFetch(
    `/api/follows/check?targetType=${targetType}&targetId=${targetId}`,
    {
      skipAuthRedirect: true,
    },
  );
  if (!res.ok) return { followed: false, followId: null };
  return res.json();
}

export async function followTarget(
  targetType: TargetType,
  targetId: number,
): Promise<{ id: number }> {
  const res = await apiFetch(`/api/follows`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ targetType, targetId }),
  });
  if (!res.ok) throw new Error("Không thể theo dõi");
  return res.json();
}

export async function unfollowTarget(followId: number): Promise<void> {
  const res = await apiFetch(`/api/follows/${followId}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Không thể hủy theo dõi");
}

export async function listMyFollows(): Promise<FollowItem[]> {
  const res = await apiFetch(`/api/follows/me`);
  if (!res.ok) throw new Error("Không thể tải danh sách theo dõi");
  return res.json();
}