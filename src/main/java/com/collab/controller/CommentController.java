package com.collab.controller;

import com.collab.entity.Comment;
import com.collab.entity.SubComment;
import com.collab.repository.SubCommentRepository;
import com.collab.service.CommentService;
import com.collab.service.SubCommentService;
import com.collab.dto.CommentRequestDTO;
import com.collab.dto.SubCommentRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "댓글 관련 API", description = "댓글 관련 CRUD 작업을 처리하는 API")
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private SubCommentService subCommentService;
    private SubCommentRepository subCommentRepository;

    @GetMapping("/{articleId}")
    public ResponseEntity<Page<Comment>> getComment(@PathVariable("articleId") Long articleId, @RequestParam(required = false) String userId) {
        // 게시글에 대한 댓글들 리스트로 가져오기
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        Page<Comment> comments = commentService.getComments(articleId, userId, pageable);

        if(comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> writeComment(@RequestBody Comment comment) {
        try {
            Comment savedComment = commentService.saveComment(comment);
            return ResponseEntity.ok(savedComment);
        } catch(DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Comment> modifyComment(@RequestBody CommentRequestDTO data) {
        try {
            Comment comment = commentService.updateComment(data);

            if(comment == null) {
                throw new Exception();
            }

            return ResponseEntity.ok(comment);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reaction/like")
    public ResponseEntity<Comment> likeArticle(@RequestBody CommentRequestDTO data) {
        Comment comment = commentService.likeComment(data);

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/reaction/hate")
    public ResponseEntity<Comment> hateArticle(@RequestBody CommentRequestDTO data) {
        Comment comment = commentService.hateComment(data);

        return ResponseEntity.ok(comment);
    }

    @GetMapping("/subComments/{commentId}")
    public ResponseEntity<Page<SubComment>> getSubComment(
            @PathVariable("commentId") Long commentId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Page<SubComment> subComments = subCommentService.getComments(commentId, userId, page, size, sortBy, direction);

        if(subComments.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(subComments);
        }
    }

    @PostMapping("/subComments")
    public ResponseEntity<SubComment> addSubComment(@RequestBody SubCommentRequestDTO data) {
        try {
            SubComment subcomment = subCommentService.saveSubComment(data);
            return ResponseEntity.ok(subcomment);
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/subComments/like")
    public ResponseEntity<?> likeSubComment(@RequestBody SubCommentRequestDTO data) {
        try {
            SubComment subComment = subCommentService.likeSubComment(data.getSubCommentId(), data.getUserId());

            return ResponseEntity.ok(subComment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @PostMapping("/subComments/hate")
    public ResponseEntity<?> hateSubComment(@RequestBody SubCommentRequestDTO data) {
        try {
            SubComment subComment = subCommentService.hateSubComment(data.getSubCommentId(), data.getUserId());

            return ResponseEntity.ok(subComment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @DeleteMapping("/subComments")
    public ResponseEntity<?> deleteSubComment(@RequestBody Map<String, Long> payload) {
        try {
            Long subCommentId = payload.get("subCommentId");
            Long commentId = payload.get("commentId");
            System.out.println(payload);
            if(subCommentId == null) {
                return ResponseEntity.badRequest().body("subCommentId is required");
            }
            subCommentService.deleteSubComment(commentId, subCommentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/subComments")
    public ResponseEntity<SubComment> modifySubComment(@RequestBody Map<String, String> payload) {
        try {
            Long subCommentId = Long.parseLong(payload.get("subCommentId"));
            String subCommentText = payload.get("subCommentText");

            subCommentService.modifySubComment(subCommentId, subCommentText);

            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
