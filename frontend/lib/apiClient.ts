import { getToken, clearToken } from "@/lib/auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

type ApiFetchOptions = RequestInit & {
  // Đặt true cho các request KHÔNG nên tự đăng xuất khi gặp 401
  // (ví dụ: check trạng thái phụ, không phải hành động chính của người dùng)
  skipAuthRedirect?: boolean;
};

export async function apiFetch(
  path: string,
  options: ApiFetchOptions = {},
): Promise<Response> {
  const token = getToken();
  const { skipAuthRedirect, headers, ...rest } = options;

  const finalHeaders: Record<string, string> = {
    ...(headers as Record<string, string> | undefined),
  };
  if (token) {
    finalHeaders["Authorization"] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...rest,
    headers: finalHeaders,
  });

  if (res.status === 401 && !skipAuthRedirect) {
    clearToken();
    if (
      typeof window !== "undefined" &&
      window.location.pathname !== "/login"
    ) {
      window.location.href = "/login";
    }
  }

  return res;
}
