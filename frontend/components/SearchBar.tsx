"use client";

import { useState, FormEvent } from "react";
import { useRouter } from "next/navigation";
import { Search } from "lucide-react";

export default function SearchBar() {
  const [query, setQuery] = useState("");
  const router = useRouter();

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    const trimmed = query.trim();
    if (!trimmed) return;
    router.push(`/search?q=${encodeURIComponent(trimmed)}`);
  }

  return (
    <form onSubmit={handleSubmit} className="w-full max-w-xl">
      <div className="flex items-center gap-3 rounded-full border border-[#DFE1E5] px-5 py-3 shadow-sm hover:shadow-md focus-within:shadow-md focus-within:border-[#1D3557] transition-all">
        <Search
          size={18}
          className="text-[#9AA0A6] shrink-0"
          aria-hidden="true"
        />
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Tìm bài báo, tác giả, từ khóa..."
          aria-label="Tìm kiếm bài báo"
          className="w-full bg-transparent text-sm text-[#202124] placeholder:text-[#9AA0A6] outline-none"
        />
      </div>
    </form>
  );
}
