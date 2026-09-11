const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export type AuthResponse = {
  token: string;
  username: string;
  role: string;
};

export type ApiError = {
  status: number;
  message: string;
};

async function handleResponse(res: Response): Promise<AuthResponse> {
  const data = await res.json();
  if (!res.ok) {
    const message = data?.message || "Đã xảy ra lỗi, vui lòng thử lại";
    throw { status: res.status, message } as ApiError;
  }
  return data as AuthResponse;
}

export async function login(
  email: string,
  password: string,
): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  return handleResponse(res);
}

export async function register(
  username: string,
  email: string,
  password: string,
): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, email, password }),
  });
  return handleResponse(res);
}
