"use client";

import { useState, FormEvent } from "react";
import { useRouter } from "next/navigation";
import { Source_Serif_4 } from "next/font/google";
import Link from "next/link";
import { register as registerApi } from "@/services/authService";
import { useAuth } from "@/hooks/useAuth";
import type { ApiError } from "@/services/authService";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function RegisterPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuth();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);

    if (password.length < 6) {
      setError("Mật khẩu phải tối thiểu 6 ký tự");
      return;
    }

    setLoading(true);
    try {
      const res = await registerApi(username, email, password);
      login(res.token);
      router.push("/");
    } catch (err) {
      const apiError = err as ApiError;
      setError(apiError.message || "Đăng ký thất bại, vui lòng thử lại");
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
        <h1 className="text-xl text-[#202124] mb-6 text-center">
          Tạo tài khoản
        </h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Tên đăng nhập"
            required
            minLength={3}
            className="rounded-lg border border-[#DFE1E5] px-4 py-2.5 text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none focus:border-[#1D3557] transition-colors"
          />
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
            placeholder="Mật khẩu (tối thiểu 6 ký tự)"
            required
            minLength={6}
            className="rounded-lg border border-[#DFE1E5] px-4 py-2.5 text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none focus:border-[#1D3557] transition-colors"
          />

          {error && <p className="text-sm text-[#B3261E]">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="mt-2 rounded-full bg-[#1D3557] text-white text-sm py-2.5 hover:bg-[#16294a] transition-colors disabled:opacity-60"
          >
            {loading ? "Đang tạo tài khoản..." : "Đăng ký"}
          </button>
        </form>

        <p className="text-sm text-[#5F6366] text-center mt-6">
          Đã có tài khoản?{" "}
          <Link href="/login" className="text-[#1D3557] hover:underline">
            Đăng nhập
          </Link>
        </p>
      </div>
    </main>
  );
}
