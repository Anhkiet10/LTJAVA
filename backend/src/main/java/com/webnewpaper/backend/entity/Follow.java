package com.webnewpaper.backend.entity;

import com.webnewpaper.backend.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;
}