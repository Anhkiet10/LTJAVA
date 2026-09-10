"use client";

import { useState, FormEvent } from "react";
import { useRouter } from "next/navigation";
import { Source_Serif_4 } from "next/font/google";
import Link from "next/link";
import { login as loginApi } from "@/services/authService";
import { useAuth } from "@/hooks/useAuth";
import type { ApiError } from "@/services/authService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuth();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await loginApi(email, password);
      login(res.token);
      router.push("/");
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || "Sai email hoặc mật khẩu");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-white flex flex-col items-center justify-center px-6">
      <Link
        href="/"
        className={`${sourceSerif.className} text-2xl text-[#1D3557] mb-10`}
      >
        ResearchPulse
      </Link>

      <div className="w-full max-w-sm">
        <h1 className="text-xl text-[#202124] mb-6 text-center">Đăng nhập</h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            required
            className="rounded-lg border border-[#DFE1E5] px-4 py-2.5 text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none focus:border-[#1D3557] transition-colors"
          />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Mật khẩu"
            required
            className="rounded-lg border border-[#DFE1E5] px-4 py-2.5 text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none focus:border-[#1D3557] transition-colors"
          />

          {error && <p className="text-sm text-[#B3261E]">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="mt-2 rounded-full bg-[#1D3557] text-white text-sm py-2.5 hover:bg-[#16294a] transition-colors disabled:opacity-60"
          >
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>
        </form>

        <p className="text-sm text-[#5F6366] text-center mt-6">
          Chưa có tài khoản?{" "}
          <Link href="/register" className="text-[#1D3557] hover:underline">
            Đăng ký
          </Link>
        </p>
      </div>
    </main>
  );
}
