package com.webnewpaper.backend.services;

import com.webnewpaper.backend.client.OpenAiClient;
import com.webnewpaper.backend.dto.GapAnalysisResponse;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.repositories.KeywordRepository;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GapAnalysisService {

    private static final int MAX_PAPERS = 30; // giới hạn để không vượt quá kích thước prompt
    private static final int MIN_PAPERS = 3;  // tối thiểu để phân tích có ý nghĩa

    private final ResearchPaperRepository paperRepository;
    private final KeywordRepository keywordRepository;
    private final OpenAiClient openAiClient;

    public GapAnalysisService(ResearchPaperRepository paperRepository, KeywordRepository keywordRepository,
                               OpenAiClient openAiClient) {
        this.paperRepository = paperRepository;
        this.keywordRepository = keywordRepository;
        this.openAiClient = openAiClient;
    }

    public GapAnalysisResponse analyze(Long keywordId) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy keyword"));

        List<ResearchPaper> papers = paperRepository.findPapersWithAbstractByKeyword(
                keywordId, PageRequest.of(0, MAX_PAPERS));

        if (papers.size() < MIN_PAPERS) {
            throw new IllegalArgumentException(
                    "Cần tối thiểu " + MIN_PAPERS + " bài báo có abstract thuộc chủ đề này để phân tích (hiện có " + papers.size() + ")");
        }

        StringBuilder context = new StringBuilder();
        for (ResearchPaper p : papers) {
            context.append("- [").append(p.getPublicationYear()).append("] ").append(p.getTitle())
                    .append("\n  ").append(p.getAbstractText()).append("\n\n");
        }

        String systemPrompt = "Bạn là trợ lý nghiên cứu khoa học chuyên phân tích xu hướng. " +
                "Dựa trên danh sách tiêu đề và tóm tắt các bài báo dưới đây (cùng một chủ đề nghiên cứu), hãy: " +
                "1) Tóm tắt các hướng nghiên cứu chính đã được khai thác nhiều. " +
                "2) Chỉ ra khoảng trống nghiên cứu (research gap) — khía cạnh liên quan chưa được đề cập nhiều hoặc còn thiếu. " +
                "3) Đề xuất 2-3 hướng nghiên cứu tiềm năng. Trả lời bằng tiếng Việt, có cấu trúc rõ ràng theo 3 phần trên.";

        String userPrompt = "Chủ đề: " + keyword.getName() + "\n\nDanh sách " + papers.size() +
                " bài báo (sắp xếp theo năm):\n\n" + context;

        String analysis = openAiClient.chat(systemPrompt, userPrompt);

        int yearFrom = papers.get(0).getPublicationYear();
        int yearTo = papers.get(papers.size() - 1).getPublicationYear();

        return new GapAnalysisResponse(keyword.getName(), papers.size(), yearFrom, yearTo, analysis);
    }
}