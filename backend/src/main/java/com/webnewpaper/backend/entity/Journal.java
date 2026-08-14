// Author: Dang Quoc Viet
package com.webnewpaper.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "journals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String publisher;

    @Column(length = 20, unique = true)
    private String issn;

    @OneToMany(mappedBy = "journal")
    private List<ResearchPaper> papers;
}
