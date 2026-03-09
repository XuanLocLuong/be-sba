package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.ChangePasswordRequest;
import sba301.fe.edu.vn.besba.dto.request.UpdateProfileRequest;
import sba301.fe.edu.vn.besba.dto.response.UserProfileResponse;
import sba301.fe.edu.vn.besba.security.UserPrincipal;
import sba301.fe.edu.vn.besba.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @GetMapping("/me")
    public BaseResponse<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        return wrapSuccess(userService.getMyProfile(currentUser));
    }

    @PutMapping("/me")
    public BaseResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        return wrapSuccess(userService.updateProfile(currentUser, request));
    }

    @PostMapping("/me/change-password")
    public BaseResponse<String> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser, request);
        return wrapSuccess("Đổi mật khẩu thành công!");
    }

    @GetMapping("/admin/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<UserProfileResponse>> getUsersByRole(@RequestParam String role) {
        return wrapSuccess(userService.getUsersByRole(role));
    }

    @PutMapping("/admin/toggle-status/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<String> toggleStatus(@PathVariable Integer id) {
        userService.toggleUserStatus(id);
        return wrapSuccess("Cập nhật trạng thái người dùng thành công!");
    }

    @PutMapping("/admin/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<UserProfileResponse> adminUpdateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return wrapSuccess(userService.adminUpdateUser(id, request));
    }
}