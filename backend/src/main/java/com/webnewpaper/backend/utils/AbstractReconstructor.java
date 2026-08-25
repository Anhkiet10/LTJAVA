package com.webnewpaper.backend.utils;

import java.util.List;
import java.util.Map;

public class AbstractReconstructor {

    private AbstractReconstructor() {}

    public static String reconstruct(Map<String, List<Integer>> invertedIndex) {
        if (invertedIndex == null || invertedIndex.isEmpty()) {
            return null;
        }

        int maxPosition = invertedIndex.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1);

        String[] words = new String[maxPosition + 1];
        for (Map.Entry<String, List<Integer>> entry : invertedIndex.entrySet()) {
            for (Integer position : entry.getValue()) {
                words[position] = entry.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word != null) sb.append(word).append(' ');
        }
        return sb.toString().trim();
    }
}
