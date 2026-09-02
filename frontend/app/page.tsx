import { Source_Serif_4 } from "next/font/google";
import SearchBar from "@/components/SearchBar";
import TrendingTopics from "@/components/TrendingTopics";
import Header from "@/components/Header";

const sourceSerif = Source_Serif_4({
  subsets: ["latin"],
  weight: ["600", "700"],
});

export default function HomePage() {
  return (
    <main className="min-h-screen bg-white flex flex-col">
      <Header />

      <section className="flex-1 flex flex-col items-center justify-center px-6">
        <h1
          className={`${sourceSerif.className} text-5xl md:text-6xl text-[#202124] tracking-tight mb-3`}
        >
          ResearchPulse
        </h1>
        <p className="text-[#5F6366] text-base md:text-lg mb-10 text-center">
          Theo dõi xu hướng nghiên cứu khoa học
        </p>
        <SearchBar />
      </section>

      <TrendingTopics />

      <footer className="text-center text-xs text-[#9AA0A6] py-8">
        © 2026 ResearchPulse — Dữ liệu từ OpenAlex &amp; Semantic Scholar
      </footer>
    </main>
  );
}
