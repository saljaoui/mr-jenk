package com.buy01.users.service;

import com.buy01.users.dto.UpdateUserRequest;
import com.buy01.users.dto.SellerPublicResponse;
import com.buy01.users.dto.UserResponse;
import com.buy01.users.exception.UserNotFoundException;
import com.buy01.users.model.Role;
import com.buy01.users.model.User;
import com.buy01.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser(String userId) {
        return toUserResponse(getUserById(userId));
    }

    public SellerPublicResponse getSellerProfile(String userId) {
        User user = getUserById(userId);
        if (user.getRole() != Role.SELLER) {
            throw new UserNotFoundException("Seller not found");
        }
        return SellerPublicResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .build();
    }

    public UserResponse updateCurrentUser(String userId, UpdateUserRequest request) {
        User user = getUserById(userId);
        boolean updated = false;

        if (request.getName() != null && !request.getName().equals(user.getName())) {
            user.setName(request.getName());
            updated = true;
        }

        if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
            user.setAvatar(request.getAvatar().isEmpty() ? null : request.getAvatar());
            updated = true;
        }

        if (updated) {
            user = userRepository.save(user);
        }

        return toUserResponse(user);
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
