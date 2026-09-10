package com.webnewpaper.backend.repositories.spec;

import com.webnewpaper.backend.entity.Author;
import com.webnewpaper.backend.entity.Journal;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.entity.ResearchPaper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ResearchPaperSpecifications {

    public static Specification<ResearchPaper> search(String keyword, String author, String journal,
                                                        Integer year, Long keywordId) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("abstractText")), pattern)
                ));
            }

            if (author != null && !author.isBlank()) {
                Join<ResearchPaper, Author> authorJoin = root.join("authors", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(authorJoin.get("fullName")), "%" + author.toLowerCase() + "%"));
            }

            if (journal != null && !journal.isBlank()) {
                Join<ResearchPaper, Journal> journalJoin = root.join("journal", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(journalJoin.get("name")), "%" + journal.toLowerCase() + "%"));
            }

            if (year != null) {
                predicates.add(cb.equal(root.get("publicationYear"), year));
            }

            if (keywordId != null) {
                Join<ResearchPaper, Keyword> keywordJoin = root.join("keywords", JoinType.INNER);
                predicates.add(cb.equal(keywordJoin.get("id"), keywordId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}