package com.kgat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="articles")
public class Article extends ReactionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @Column(nullable = false)
    private String articleTitle;

    @Column(nullable = true)
    private String articleContent;

    @Column(nullable = false)
    private String articleWriter;

    @Column(nullable = false)
    private int viewCount;

    @Override
    public String toString() {
        return "Article{" +
                "articleId=" + articleId +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleContent='" + articleContent + '\'' +
                ", articleWriter='" + articleWriter + '\'' +
                ", viewCount=" + viewCount +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }

        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Article article = (Article) o;
        return Objects.equals(articleId, article.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }

}