package com.webnewpaper.backend.services;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class MarkdownConverterService {

    private static final Pattern SECTION_HEADING = Pattern.compile(
        "^(abstract|introduction|related work|background|method(ology)?|approach|" +
        "experiment(s)?|result(s)?|discussion|conclusion(s)?|references|acknowledg(e)?ments?|" +
        "\\d+(\\.\\d+)*\\.?\\s+[A-Z].{0,60})$", Pattern.CASE_INSENSITIVE);

    public String toMarkdown(String rawText) {
        if (rawText == null || rawText.isBlank()) return "";

        String[] lines = rawText.split("\\r?\\n");
        StringBuilder md = new StringBuilder();
        int blankStreak = 0;

        for (String line : lines) {
            String trimmed = line.strip();

            if (trimmed.isEmpty()) {
                blankStreak++;
                if (blankStreak <= 1) md.append("\n");
                continue;
            }
            blankStreak = 0;

            if (isLikelyHeading(trimmed)) {
                md.append("\n## ").append(trimmed).append("\n\n");
            } else {
                md.append(trimmed).append(" ");
            }
        }

        return md.toString().replaceAll("[ \\t]+", " ").replaceAll("\n{3,}", "\n\n").trim();
    }

    private boolean isLikelyHeading(String line) {
        if (line.length() > 80) return false;
        if (SECTION_HEADING.matcher(line).matches()) return true;

        boolean isAllCaps = line.equals(line.toUpperCase()) && line.chars().anyMatch(Character::isLetter);
        return isAllCaps && line.length() < 60;
    }
}