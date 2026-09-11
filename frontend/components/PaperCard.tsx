import type { PaperSummary } from "@/types/paper";

export default function PaperCard({ paper }: { paper: PaperSummary }) {
  return (
    <a
      href={`/papers/${paper.id}`}
      className="block py-5 border-b border-[#EEF1F4] hover:bg-[#FAFBFC] transition-colors -mx-4 px-4 rounded-lg"
    >
      <h3 className="text-base text-[#1D3557] mb-1.5 leading-snug">
        {paper.title}
      </h3>
      <div className="flex flex-wrap items-center gap-x-2 text-xs text-[#5F6366]">
        {paper.authorNames.length > 0 && (
          <span>
            {paper.authorNames.slice(0, 3).join(", ")}
            {paper.authorNames.length > 3 ? " và cộng sự" : ""}
          </span>
        )}
        {paper.journalName && (
          <>
            <span className="text-[#DFE1E5]">•</span>
            <span className="italic">{paper.journalName}</span>
          </>
        )}
        <span className="text-[#DFE1E5]">•</span>
        <span>{paper.publicationYear}</span>
      </div>
    </a>
  );
}