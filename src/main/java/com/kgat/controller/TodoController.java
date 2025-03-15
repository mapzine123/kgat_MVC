package com.kgat.controller;

import com.kgat.dto.TodoChangesDTO;
import com.kgat.dto.TodoChangeRequestDTO;
import com.kgat.entity.Todo;
import com.kgat.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    // 해당 User의 전체 to-do 반환
    @GetMapping
    public ResponseEntity<List<TodoChangeRequestDTO>> getTodos(@AuthenticationPrincipal UserDetails userDetails) {
        List<TodoChangeRequestDTO> todos = todoService.findAllTodos();

        if(todos.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());  // 빈 배열 반환
        } else {
            return ResponseEntity.ok(todos);
        }
    }

    // To-do 추가
    @PostMapping
    public ResponseEntity<TodoChangeRequestDTO> addTodo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TodoChangeRequestDTO todoChangeRequestDTO) {
        String userId = userDetails.getUsername();
        TodoChangeRequestDTO savedTodo = todoService.addTodo(todoChangeRequestDTO, userId);
        System.out.println(savedTodo);
        return ResponseEntity.ok(savedTodo);
    }

    // 수정 / 삭제는 배치단위로 일괄처리
    @PostMapping("/batch")
    public ResponseEntity<List<Todo>> batchTodo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TodoChangesDTO changes) {
        String userId = userDetails.getUsername();
        todoService.processBatchUpdate(changes, userId);
        return ResponseEntity.ok().build();
    }
}
