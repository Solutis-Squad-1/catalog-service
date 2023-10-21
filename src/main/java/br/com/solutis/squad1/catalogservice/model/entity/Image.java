package br.com.solutis.squad1.catalogservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "archive_name", nullable = false)
    private String archiveName;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(nullable = false)
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void delete() {
        deleted = true;
        deletedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ProductImage{" +
                "id=" + id +
                ", archiveName='" + archiveName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", deleted=" + deleted +
                ", url='" + url + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
