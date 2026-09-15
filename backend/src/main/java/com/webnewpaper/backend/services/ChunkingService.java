package com.webnewpaper.backend.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ChunkingService {

    private static final int TARGET_CHUNK_CHARS = 3000;
    private static final int MAX_CHUNK_CHARS = 4000;
    private static final int MIN_CHUNK_CHARS = 200; // chunk nhỏ hơn ngưỡng này sẽ được gộp vào chunk kế tiếp
    private static final int OVERLAP_CHARS = 400;

    private static final Pattern SENTENCE_BOUNDARY = Pattern.compile("(?<=[.!?])\\s+");

    public record Chunk(int index, String content) {}

    public List<Chunk> chunkMarkdown(String markdown) {
        if (markdown == null || markdown.isBlank()) return new ArrayList<>();

        // Bước 1: tách theo đoạn (blank line); đoạn nào quá lớn thì cắt tiếp theo câu
        List<String> normalizedBlocks = new ArrayList<>();
        for (String block : markdown.split("\\n\\s*\\n")) {
            block = block.strip();
            if (block.isEmpty()) continue;

            if (block.length() <= MAX_CHUNK_CHARS) {
                normalizedBlocks.add(block);
            } else {
                normalizedBlocks.addAll(splitBySentence(block));
            }
        }

        // Bước 2: gộp các block nhỏ lại thành chunk ~TARGET_CHUNK_CHARS, có overlap
        List<Chunk> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int index = 0;

        for (String block : normalizedBlocks) {
            if (current.length() + block.length() > MAX_CHUNK_CHARS && current.length() >= MIN_CHUNK_CHARS) {
                chunks.add(new Chunk(index++, current.toString().trim()));

                String tail = current.length() > OVERLAP_CHARS
                        ? current.substring(current.length() - OVERLAP_CHARS)
                        : current.toString();
                current = new StringBuilder(tail).append("\n\n");
            }

            current.append(block).append("\n\n");

            if (current.length() >= TARGET_CHUNK_CHARS) {
                chunks.add(new Chunk(index++, current.toString().trim()));
                current = new StringBuilder();
            }
        }

        if (current.length() >= MIN_CHUNK_CHARS) {
            chunks.add(new Chunk(index, current.toString().trim()));
        } else if (!current.isEmpty() && !chunks.isEmpty()) {
            // Đoạn cuối quá ngắn (như "40 ký tự" bạn gặp) → gộp vào chunk trước thay vì để riêng
            Chunk last = chunks.remove(chunks.size() - 1);
            chunks.add(new Chunk(last.index(), last.content() + "\n\n" + current.toString().trim()));
        }

        return chunks;
    }

    private List<String> splitBySentence(String text) {
        List<String> result = new ArrayList<>();
        String[] sentences = SENTENCE_BOUNDARY.split(text);

        StringBuilder piece = new StringBuilder();
        for (String sentence : sentences) {
            if (piece.length() + sentence.length() > TARGET_CHUNK_CHARS && !piece.isEmpty()) {
                result.add(piece.toString().trim());
                piece = new StringBuilder();
            }
            piece.append(sentence).append(" ");
        }
        if (!piece.isEmpty()) result.add(piece.toString().trim());

        return result;
    }
}