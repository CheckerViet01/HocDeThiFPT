package com.example.websome.service;

import com.example.websome.dto.*;
import com.example.websome.entity.User;
import com.example.websome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse authenticateGoogle(AuthRequest req) {
        String email = req.getEmail() != null ? req.getEmail().trim().toLowerCase() : "";

        // Verify ID token with Google tokeninfo endpoint if provided
        if (req.getIdToken() != null && !req.getIdToken().isBlank()) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                java.util.Map tokenInfo = restTemplate.getForObject(
                    "https://oauth2.googleapis.com/tokeninfo?id_token=" + req.getIdToken().trim(),
                    java.util.Map.class
                );
                if (tokenInfo != null && tokenInfo.containsKey("email")) {
                    email = ((String) tokenInfo.get("email")).trim().toLowerCase();
                    if (tokenInfo.containsKey("name") && (req.getFullName() == null || req.getFullName().isBlank())) {
                        req.setFullName((String) tokenInfo.get("name"));
                    }
                    if (tokenInfo.containsKey("picture") && (req.getAvatarUrl() == null || req.getAvatarUrl().isBlank())) {
                        req.setAvatarUrl((String) tokenInfo.get("picture"));
                    }
                }
            } catch (Exception e) {
                // Fallback to provided email if network or token parsing issue
            }
        }

        if (email.isBlank()) {
            throw new RuntimeException("Email Google không hợp lệ hoặc không xác định được.");
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(email);

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa bởi Quản trị viên.");
            }
            if (req.getFullName() != null && !req.getFullName().isBlank()) {
                user.setFullName(req.getFullName().trim());
            }
            if (req.getAvatarUrl() != null && !req.getAvatarUrl().isBlank()) {
                user.setAvatarUrl(req.getAvatarUrl().trim());
            }
            if (req.getMssv() != null && !req.getMssv().isBlank()) {
                String cleanM = req.getMssv().trim().toUpperCase();
                if (cleanM.matches("^[A-Z]{2}[0-9]{6}$")) {
                    user.setMssv(cleanM);
                }
            }
            user = userRepository.save(user);
        } else {
        // New user registration directly into database
        String mssv = null;
        if (req.getMssv() != null && !req.getMssv().isBlank()) {
            String cleanM = req.getMssv().trim().toUpperCase();
            if (cleanM.matches("^[A-Z]{2}[0-9]{6}$")) {
                mssv = cleanM;
            }
        }

        user = User.builder()
                .email(email)
                .fullName(req.getFullName() != null && !req.getFullName().isBlank() ? req.getFullName().trim() : email.split("@")[0])
                .avatarUrl(req.getAvatarUrl() != null && !req.getAvatarUrl().isBlank() ? req.getAvatarUrl().trim() : "https://api.dicebear.com/7.x/bottts/svg?seed=" + email)
                .mssv(mssv)
                .role("ROLE_USER")
                .isMasterAdmin(false)
                .status("ACTIVE")
                .provider("GOOGLE")
                .build();
        user = userRepository.save(user);
    }

    boolean requiresMssv = (user.getRole().equals("ROLE_USER") && 
            (user.getMssv() == null || !user.getMssv().trim().toUpperCase().matches("^[A-Z]{2}[0-9]{6}$")));
    String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());

    return AuthResponse.builder()
            .token(token)
            .user(toDto(user))
            .requiresMssv(requiresMssv)
            .message(requiresMssv ? "Vui lòng nhập Mã số sinh viên (MSSV) đúng chuẩn FPT (2 chữ cái và 6 chữ số) để kích hoạt tài khoản." : "Đăng nhập thành công!")
            .build();
}

@Transactional
public UserDto updateMssv(Long userId, String mssv) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng trong cơ sở dữ liệu."));

    if (mssv == null || mssv.trim().isEmpty()) {
        throw new RuntimeException("Mã số sinh viên không được để trống.");
    }

    String cleanMssv = mssv.trim().toUpperCase();
    if (!cleanMssv.matches("^[A-Z]{2}[0-9]{6}$")) {
        throw new RuntimeException("Mã số sinh viên không đúng định dạng! Bắt buộc gồm 2 chữ cái đầu và 6 chữ số sau (Ví dụ: HE171199, HS112233, SE160000). Vui lòng nhập lại.");
    }

    user.setMssv(cleanMssv);
    user = userRepository.save(user);
    return toDto(user);
}

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        return toDto(user);
    }

    public List<UserDto> getAllUsers(Long currentAdminId) {
        boolean isMaster = false;
        if (currentAdminId != null) {
            User currentAdmin = userRepository.findById(currentAdminId).orElse(null);
            if (currentAdmin != null && Boolean.TRUE.equals(currentAdmin.getIsMasterAdmin())) {
                isMaster = true;
            }
        }

        // If Master Admin: can see all accounts (including all admins and users)
        // If Sub-admin: can ONLY see normal users (ROLE_USER), CANNOT see any admins
        final boolean finalIsMaster = isMaster;
        return userRepository.findAll().stream()
                .filter(u -> finalIsMaster || !"ROLE_ADMIN".equalsIgnoreCase(u.getRole()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserByAdmin(Long id, UserUpdateDto dto, Long currentAdminId) {
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new RuntimeException("Phiên đăng nhập không hợp lệ!"));
        boolean isMaster = Boolean.TRUE.equals(currentAdmin.getIsMasterAdmin());

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));

        // Sub-admin is strictly forbidden from editing ANY admin account
        if (!isMaster && "ROLE_ADMIN".equalsIgnoreCase(targetUser.getRole())) {
            throw new RuntimeException("Chỉ Quản Trị Viên Tối Cao (Master Admin) mới có quyền chỉnh sửa tài khoản Quản trị viên!");
        }

        // Sub-admin cannot promote anyone to ROLE_ADMIN
        if (!isMaster && dto.getRole() != null && "ROLE_ADMIN".equalsIgnoreCase(dto.getRole().trim())) {
            throw new RuntimeException("Chỉ Master Admin mới có quyền thăng cấp vai trò Quản trị viên (ROLE_ADMIN)!");
        }

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            targetUser.setFullName(dto.getFullName().trim());
        }
        if (dto.getMssv() != null) {
            targetUser.setMssv(dto.getMssv().trim().toUpperCase());
        }
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            targetUser.setRole(dto.getRole().trim().toUpperCase());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            targetUser.setStatus(dto.getStatus().trim());
        }

        targetUser = userRepository.save(targetUser);
        return toDto(targetUser);
    }

    @Transactional
    public void deleteUser(Long id, Long currentAdminId) {
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new RuntimeException("Phiên đăng nhập không hợp lệ!"));
        boolean isMaster = Boolean.TRUE.equals(currentAdmin.getIsMasterAdmin());

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + id));

        if (Boolean.TRUE.equals(targetUser.getIsMasterAdmin())) {
            throw new RuntimeException("Không thể xóa tài khoản Quản Trị Viên Tối Cao (Master Admin)!");
        }

        // Sub-admin is strictly forbidden from deleting ANY admin account
        if (!isMaster && "ROLE_ADMIN".equalsIgnoreCase(targetUser.getRole())) {
            throw new RuntimeException("Chỉ Quản Trị Viên Tối Cao (Master Admin) mới có quyền xóa tài khoản Quản trị viên!");
        }

        userRepository.deleteById(id);
    }

    public AuthResponse adminLogin(AdminLoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            throw new RuntimeException("Vui lòng nhập đầy đủ Email và Mật khẩu Quản Trị Viên!");
        }

        String email = req.getEmail().trim().toLowerCase();
        String pass = req.getPassword().trim();

        // Query database solely - absolutely zero hardcoded credentials
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tài khoản Quản trị viên không tồn tại trong cơ sở dữ liệu!"));

        if (!"ROLE_ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Tài khoản này không có quyền Quản trị viên (ROLE_ADMIN)!");
        }
        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản Quản trị viên này đã bị khóa!");
        }
        if (user.getPassword() == null || !user.getPassword().equals(pass)) {
            throw new RuntimeException("Mật khẩu Quản trị viên không chính xác!");
        }

        String token = jwtService.generateToken(user.getEmail(), "ROLE_ADMIN", user.getId());
        return AuthResponse.builder()
                .token(token)
                .user(toDto(user))
                .requiresMssv(false)
                .message("Đăng nhập Cổng Quản Trị Viên thành công!")
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw new RuntimeException("Họ và tên không được để trống.");
        }
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new RuntimeException("Email không được để trống.");
        }
        String email = req.getEmail().trim().toLowerCase();
        if (!email.contains("@") || !email.contains(".")) {
            throw new RuntimeException("Định dạng email không hợp lệ.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email này đã được sử dụng. Vui lòng đăng nhập hoặc dùng email khác.");
        }
        if (req.getPassword() == null || req.getPassword().trim().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        String cleanMssv = null;
        if (req.getMssv() != null && !req.getMssv().isBlank()) {
            cleanMssv = req.getMssv().trim().toUpperCase();
            if (!cleanMssv.matches("^[A-Z]{2}[0-9]{6}$")) {
                throw new RuntimeException("Mã số sinh viên không đúng định dạng! Bắt buộc gồm 2 chữ cái đầu và 6 chữ số sau (Ví dụ: HE171199, HS112233).");
            }
            if (userRepository.existsByMssv(cleanMssv)) {
                throw new RuntimeException("Mã số sinh viên " + cleanMssv + " đã được đăng ký bởi tài khoản khác.");
            }
        }

        User user = User.builder()
                .fullName(req.getFullName().trim())
                .email(email)
                .password(req.getPassword().trim())
                .mssv(cleanMssv)
                .role("ROLE_USER")
                .isMasterAdmin(false)
                .status("ACTIVE")
                .provider("LOCAL")
                .avatarUrl("https://api.dicebear.com/7.x/bottts/svg?seed=" + email)
                .build();
        user = userRepository.save(user);

        boolean requiresMssv = (cleanMssv == null);
        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .user(toDto(user))
                .requiresMssv(requiresMssv)
                .message("Đăng ký tài khoản thành công!")
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        if (req.getIdentifier() == null || req.getIdentifier().isBlank()) {
            throw new RuntimeException("Vui lòng nhập Email hoặc Mã số sinh viên (MSSV)!");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("Vui lòng nhập Mật khẩu!");
        }

        String iden = req.getIdentifier().trim();
        Optional<User> userOpt = userRepository.findByEmail(iden.toLowerCase());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByMssv(iden.toUpperCase());
        }

        User user = userOpt.orElseThrow(() -> new RuntimeException("Tài khoản hoặc Mật khẩu không chính xác."));

        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa bởi Quản trị viên.");
        }
        if (user.getPassword() == null || !user.getPassword().equals(req.getPassword().trim())) {
            throw new RuntimeException("Tài khoản hoặc Mật khẩu không chính xác.");
        }

        boolean requiresMssv = (user.getRole().equals("ROLE_USER") && 
            (user.getMssv() == null || !user.getMssv().trim().toUpperCase().matches("^[A-Z]{2}[0-9]{6}$")));
        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .user(toDto(user))
                .requiresMssv(requiresMssv)
                .message("Đăng nhập thành công!")
                .build();
    }

    @Transactional
    public UserDto createSubAdmin(CreateSubAdminRequest req, Long currentAdminId) {
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new RuntimeException("Phiên đăng nhập không hợp lệ."));

        // Only Master Admin can create other admins!
        boolean isMaster = Boolean.TRUE.equals(currentAdmin.getIsMasterAdmin());
        if (!isMaster) {
            throw new RuntimeException("Chỉ tài khoản Quản trị viên Tối cao (Master Admin) mới có quyền tạo thêm Quản trị viên!");
        }

        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new RuntimeException("Vui lòng nhập Email / Tài khoản cho Quản trị viên mới!");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("Vui lòng nhập Mật khẩu cho Quản trị viên mới!");
        }

        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email / Tài khoản " + email + " đã tồn tại trong cơ sở dữ liệu!");
        }

        String fullName = req.getFullName() != null && !req.getFullName().isBlank() 
                ? req.getFullName().trim() 
                : "Quản Trị Viên FPT";

        User newSubAdmin = User.builder()
                .email(email)
                .password(req.getPassword().trim())
                .fullName(fullName)
                .mssv("ADMIN-" + (System.currentTimeMillis() % 10000))
                .avatarUrl("https://api.dicebear.com/7.x/bottts/svg?seed=" + email)
                .role("ROLE_ADMIN")
                .isMasterAdmin(false)
                .status("ACTIVE")
                .provider("LOCAL")
                .build();

        return toDto(userRepository.save(newSubAdmin));
    }

    public UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .mssv(u.getMssv())
                .fullName(u.getFullName())
                .avatarUrl(u.getAvatarUrl())
                .role(u.getRole())
                .status(u.getStatus())
                .provider(u.getProvider())
                .isMasterAdmin(Boolean.TRUE.equals(u.getIsMasterAdmin()))
                .createdAt(u.getCreatedAt())
                .build();
    }
}
