CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    mssv VARCHAR(50) NULL,
    full_name VARCHAR(150) NOT NULL,
    avatar_url VARCHAR(500) NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    provider VARCHAR(50) NOT NULL DEFAULT 'GOOGLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT NULL,
    icon VARCHAR(50) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    subject_id BIGINT NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 90,
    total_marks DECIMAL(5,2) NOT NULL DEFAULT 10.0,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    description TEXT NULL,
    content TEXT NOT NULL,
    solution TEXT NULL,
    file_url VARCHAR(500) NULL,
    views_count INT NOT NULL DEFAULT 0,
    downloads_count INT NOT NULL DEFAULT 0,
    likes_count INT NOT NULL DEFAULT 0,
    author_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed Initial Users
MERGE INTO users (id, email, mssv, full_name, avatar_url, role, status, provider)
KEY (email)
VALUES 
(1, 'admin@fpt.edu.vn', 'ADMIN001', 'Master Grand Arcane (Admin FPT)', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80', 'ROLE_ADMIN', 'ACTIVE', 'GOOGLE'),
(2, 'hoangndse170123@fpt.edu.vn', 'SE170123', 'Nguyễn Đức Hoàng', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=250&q=80', 'ROLE_USER', 'ACTIVE', 'GOOGLE'),
(3, 'anhntmhe163456@fpt.edu.vn', 'HE163456', 'Trần Mai Anh', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=250&q=80', 'ROLE_USER', 'ACTIVE', 'GOOGLE'),
(4, 'baolqqe150789@fpt.edu.vn', 'QE150789', 'Lê Quốc Bảo', 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&w=250&q=80', 'ROLE_USER', 'ACTIVE', 'GOOGLE');

-- Seed Subjects
MERGE INTO subjects (id, code, name, category, description, icon)
KEY (code)
VALUES
(1, 'CSD201', 'Data Structures and Algorithms', 'Software Engineering', 'Cấu trúc dữ liệu và giải thuật (Binary Search Tree, AVL, Graph, Dijkstra, DFS/BFS...)', 'binary'),
(2, 'PRO192', 'Object-Oriented Programming with Java', 'Software Engineering', 'Lập trình hướng đối tượng với Java (OOP, Inheritance, Polymorphism, Collection Framework)', 'coffee'),
(3, 'PRF192', 'Programming Fundamentals with C', 'Software Engineering', 'Nhập môn lập trình căn bản C (Pointers, Arrays, Strings, File IO, Recursion)', 'code'),
(4, 'DBI202', 'Database Systems', 'Software Engineering', 'Cơ sở dữ liệu quan hệ, SQL Server, Stored Procedures, Triggers, Views, Normalization', 'database'),
(5, 'PRN211', 'Basic Cross-Platform Application (.NET/C#)', 'Software Engineering', 'Phát triển ứng dụng với C#, .NET WinForms/WPF, LINQ, Entity Framework Core', 'cpu'),
(6, 'MAS291', 'Applied Statistics for Mathematics', 'Mathematics', 'Thống kê ứng dụng FPT (Xác suất, Phân phối chuẩn, Kiểm định giả thuyết t-test, ANOVA)', 'sigma'),
(7, 'MAD101', 'Discrete Mathematics', 'Mathematics', 'Toán rời rạc (Logic mệnh đề, Tập hợp, Đồ thị, Quan hệ, Cây, Euler/Hamilton)', 'compass'),
(8, 'OSG202', 'Operating Systems', 'Information Assurance', 'Hệ điều hành (Process, Thread, Scheduling CPU, Semaphore, Deadlock, Memory Management)', 'terminal');

-- Seed Exams
MERGE INTO exams (id, title, subject_id, exam_type, semester, duration_minutes, total_marks, difficulty, description, content, solution, file_url, views_count, downloads_count, likes_count, author_id, status)
KEY (id)
VALUES
(1, 'Đề thi PE Practical Exam Spring 2025 - PRO192 (Java Core) - Đề Chính Thức', 2, 'PE', 'Spring 2025', 90, 10.0, 'HARD', 'Đề thi PE thực hành chính thức môn PRO192 kỳ Spring 2025 bao gồm 4 câu hỏi: Class Design, Collection Manager, Sort & Filter bằng Comparable, String Processing.', '### Câu 1 (2 điểm): Thiết kế lớp ArcaneSpell\nXây dựng lớp `ArcaneSpell` gồm các thuộc tính: `spellId` (String), `manaCost` (int), `power` (double).\n- Viết Constructor có tham số và không tham số.\n- Getter/Setter cho toàn bộ thuộc tính.\n- Phương thức `toString()` hiển thị định dạng: `[ID: spellId, Mana: manaCost, Power: power]`.\n\n### Câu 2 (3 điểm): Xây dựng lớp ManaManager\nQuản lý danh sách `ArrayList<ArcaneSpell>`.\n- `void addSpell(ArcaneSpell s)`: Thêm spell, nếu ID đã tồn tại thì ném ra thông báo hoặc không thêm.\n- `ArcaneSpell findHighestPower()`: Tìm spell có chỉ số `power` cao nhất.\n\n### Câu 3 (2.5 điểm): Sắp xếp theo Mana Cost tăng dần\n- Triển khai `Comparable<ArcaneSpell>` hoặc `Comparator`.\n\n### Câu 4 (2.5 điểm): Phân tích chuỗi chú thuật\nNhập chuỗi rune từ bàn phím, đếm số từ có chứa ký tự hoa và đảo ngược thứ tự các từ.', '```java\npublic class ArcaneSpell implements Comparable<ArcaneSpell> {\n    private String spellId;\n    private int manaCost;\n    private double power;\n    // getters, setters, compareTo...\n}\n```', 'https://fuoverflow-files.fpt.edu.vn/exams/PE_PRO192_SP25.zip', 1540, 890, 312, 1, 'PUBLISHED'),
(2, 'Đề thi Final Exam (FE) Lý thuyết & Thuật toán - CSD201 Fall 2024', 1, 'FE', 'Fall 2024', 60, 10.0, 'NIGHTMARE', 'Bộ 40 câu hỏi trắc nghiệm và tự luận thuật toán CSD201 đề thi cuối kỳ. Bao gồm cây nhị phân tìm kiếm BST, cân bằng AVL, thuật toán Dijkstra tìm đường đi ngắn nhất.', '### Phần 1: Cây tìm kiếm nhị phân BST (Tree Operations)\nCho dãy khóa: 50, 30, 70, 20, 40, 60, 80, 15, 25, 35.\n1. Vẽ cây BST thu được khi chèn lần lượt từng phần tử.\n2. Nêu thứ tự duyệt cây theo In-Order, Pre-Order, Post-Order.\n3. Thực hiện xóa nút 30 bằng phương pháp copy và merging.\n\n### Phần 2: Đồ thị và Thuật toán Dijkstra\nCho đồ thị vô hướng có trọng số gồm 6 đỉnh (A, B, C, D, E, F). Xác định đường đi ngắn nhất từ A đến F.', '1. Duyệt In-Order: 15, 20, 25, 30, 35, 40, 50, 60, 70, 80.\n2. Đường đi ngắn nhất A -> B -> E -> F, tổng trọng số 14.', 'https://fuoverflow-files.fpt.edu.vn/exams/FE_CSD201_FA24.pdf', 2410, 1420, 480, 1, 'PUBLISHED'),
(3, 'Đề thi Midterm Giữa kỳ - MAS291 (Xác suất thống kê ứng dụng) - Kỳ Summer 2024', 6, 'MIDTERM', 'Summer 2024', 60, 10.0, 'MEDIUM', 'Đề thi trắc nghiệm giữa kỳ MAS291 chuẩn format FPT University. Bao gồm xác suất có điều kiện Bayes, phân phối Poisson, phân phối nhị thức Binomial.', '### Câu hỏi trọng tâm Midterm MAS291:\n1. Tính xác suất một bug kiểm thử false-positive theo định lý Bayes.\n2. Biến ngẫu nhiên Poisson với lambda = 3.5 cuộc gọi/phút.', '1. Áp dụng Bayes: P(C | FP) = 40.32%.\n2. P(X >= 5) = 0.827.', 'https://fuoverflow-files.fpt.edu.vn/exams/Midterm_MAS291_SU24.pdf', 980, 620, 195, 1, 'PUBLISHED'),
(4, 'Đề thi Progress Test 1 (PT1) - PRF192 (C Programming) - Spring 2025', 3, 'PT1', 'Spring 2025', 45, 10.0, 'EASY', 'Đề thi kiểm tra tiến độ lần 1 môn Lập trình C cơ bản. Cấu trúc rẽ nhánh if/else, vòng lặp for/while, mảng một chiều và kiểm tra số nguyên tố.', 'Nhập mảng N phần tử nguyên: 1. Tính tổng số nguyên tố. 2. In phần tử duy nhất. 3. Sắp xếp số lẻ tăng dần.', 'Sử dụng hàm isPrime(n) từ 2 đến sqrt(n). Dùng mảng phụ lọc số lẻ.', 'https://fuoverflow-files.fpt.edu.vn/exams/PT1_PRF192_SP25.c', 750, 410, 140, 1, 'PUBLISHED'),
(5, 'Đề thi Progress Test 2 (PT2) - DBI202 (SQL Server Stored Procedure & Trigger)', 4, 'PT2', 'Fall 2024', 50, 10.0, 'HARD', 'Kiểm tra kỹ năng viết câu lệnh truy vấn phức tạp: JOIN, Stored Procedure và Trigger tự động cập nhật tồn kho.', '1. Viết Stored Procedure sp_GetTopStudentsByCourse.\n2. Viết Trigger trg_UpdateStockAfterOrder trừ tồn kho khi thêm order.', 'Trigger kiểm tra tồn kho và ROLLBACK TRANSACTION nếu số lượng không đủ.', 'https://fuoverflow-files.fpt.edu.vn/exams/PT2_DBI202_FA24.sql', 1120, 780, 260, 1, 'PUBLISHED'),
(6, 'Đề thi Progress Test 3 (PT3) - PRN211 (.NET Core / Entity Framework Core & LINQ)', 5, 'PT3', 'Spring 2025', 60, 10.0, 'MEDIUM', 'Kiểm tra kiến thức LINQ query syntax, Lambda expression, Asynchronous programming và DbContext trong EF Core.', '1. LINQ group by Major, tính trung bình GPA >= 7.5.\n2. SearchStudentsAsync theo tên hoặc MSSV.', 'Dùng GroupBy kết hợp Select và Where(m => m.AverageGPA >= 7.5).', 'https://fuoverflow-files.fpt.edu.vn/exams/PT3_PRN211_SP25.zip', 890, 510, 180, 1, 'PUBLISHED');

-- Seed Comments
MERGE INTO comments (id, exam_id, user_id, content)
KEY (id)
VALUES
(1, 1, 2, 'Đề PRO192 này câu 3 Comparator cần chú ý kiểu double dùng Double.compare nhé các bạn!'),
(2, 1, 3, 'Kỳ trước mình thi đúng dạng đề này luôn, làm theo hướng dẫn này đạt 9.5 ngon lành! Cảm ơn admin nhiều lắm.'),
(3, 2, 4, 'Phần xóa copy với xóa merging hay bị lừa ở số lượng phép gán con trỏ, các bạn ôn kỹ nhé!'),
(4, 5, 2, 'Trigger trừ tồn kho này là dạng kinh điển của DBI202 luôn, kỳ nào cũng có 1 câu tương tự.');

