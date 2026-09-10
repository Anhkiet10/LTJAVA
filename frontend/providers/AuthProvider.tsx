"use client";

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import {
  saveToken,
  getToken,
  clearToken,
  decodeToken,
  isTokenExpired,
} from "@/lib/auth";

type AuthUser = {
  username: string;
  role: string;
};

type AuthContextValue = {
  user: AuthUser | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  // Khi app khởi động (F5, mở tab mới...), đọc token đã lưu để khôi phục trạng thái đăng nhập
  useEffect(() => {
    const token = getToken();
    if (token) {
      const decoded = decodeToken(token);
      if (decoded && !isTokenExpired(decoded)) {
        setUser({ username: decoded.sub, role: decoded.role });
      } else {
        clearToken();
      }
    }
    setLoading(false);
  }, []);

  function login(token: string) {
    saveToken(token);
    const decoded = decodeToken(token);
    if (decoded) {
      setUser({ username: decoded.sub, role: decoded.role });
    }
  }

  function logout() {
    clearToken();
    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated: !!user, loading, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuthContext phải được dùng bên trong <AuthProvider>");
  }
  return context;
}
