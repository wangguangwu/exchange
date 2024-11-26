package com.wangguangwu.exchange.response;

import com.wangguangwu.exchange.entity.UserInfoDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangguangwu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private UserDTO user;

    public static LoginResponse create(String token, UserInfoDO user) {
        return LoginResponse.builder()
                .token(token)
                .user(UserDTO.create(user))
                .build();
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UserDTO {

    private String name;
    private Long id;

    public static UserDTO create(UserInfoDO user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .build();
    }
}