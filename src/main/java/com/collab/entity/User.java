package com.collab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users")
public class User {
    public User(String id) {
        this.id = id;
    }

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    @Id
    @Column(nullable = false)
    private String id; // 로그인용 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // 실명

    @Column(nullable = false)
    private String department; // 부서명

    // S3 내부에서 파일 참조, 삭제 시 사용
    @Column(name = "image_path")
    private String imagePath;

    // S3 이미지 URL을 저장하기 위한 새 필드
    // 이미지를 화면에 표시할 때 사용
    // 서명된 URL
    @Lob
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }


}
