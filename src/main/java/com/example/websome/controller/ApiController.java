package com.example.websome.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // Tất cả API sẽ bắt đầu bằng đường dẫn /api
@CrossOrigin(origins = "http://localhost:4200") // Cho phép Frontend Angular (port 4200) gọi sang không bị chặn CORS
public class ApiController {

    // API 1: Trả về một chuỗi chữ đơn giản để test kết nối nhanh
    // Đường dẫn test: http://localhost:8081/api/hello
    @GetMapping("/hello")
    public String sayHello() {
        return "Xin chào! Kết nối API thành công từ Java Spring Boot trên VS Code rồi nhé!";
    }

    // API 2: Trả về danh sách dữ liệu (JSON) để sau này hiển thị lên giao diện Angular
    // Đường dẫn test: http://localhost:8081/api/users
    @GetMapping("/users")
    public List<Map<String, String>> getUsers() {
        return List.of(
            Map.of("name", "Nguyễn Văn A", "email", "vana@gmail.com"),
            Map.of("name", "Trần Thị B", "email", "thib@gmail.com"),
            Map.of("name", "Lê Văn C", "email", "vanc@gmail.com")
        );
    }
}
