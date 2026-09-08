const TOKEN_KEY = "auth_token";

export type DecodedToken = {
  sub: string; // username
  userId: number;
  role: string;
  iat: number;
  exp: number;
};

export function saveToken(token: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem(TOKEN_KEY, token);
  }
}

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function clearToken() {
  if (typeof window !== "undefined") {
    localStorage.removeItem(TOKEN_KEY);
  }
}

// Chỉ giải mã payload để hiển thị UI (username, role, hạn token) —
// KHÔNG dùng để xác thực bảo mật, vì không kiểm tra chữ ký.
// Việc xác thực thật sự luôn do backend (JwtDecoder) đảm nhiệm.
export function decodeToken(token: string): DecodedToken | null {
  try {
    const payload = token.split(".")[1];
    const decoded = JSON.parse(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/")),
    );
    return decoded as DecodedToken;
  } catch {
    return null;
  }
}

export function isTokenExpired(decoded: DecodedToken): boolean {
  return decoded.exp * 1000 < Date.now();
}
