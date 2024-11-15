package com.wangguangwu.exchangeusercore.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangguangwu.exchange.dto.UserDTO;
import com.wangguangwu.exchange.entity.UserDO;
import com.wangguangwu.exchange.exception.UserException;
import com.wangguangwu.exchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务核心业务实现
 *
 * @author wangguangwu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserService {

    private final UserService userService;

    /**
     * 根据用户 ID 获取用户信息
     */
    public UserDTO getUserById(Long uid) {
        log.info("开始获取用户信息，用户ID: {}", uid);
        UserDO user = userService.getById(uid);
        if (user == null) {
            log.warn("获取失败，用户ID: {} 不存在", uid);
            throw new UserException("用户不存在，用户ID: " + uid);
        }
        log.info("成功获取用户信息: {}", user);
        return mapToDTO(user);
    }

    /**
     * 注册新用户
     */
    public boolean registerUser(UserDTO userDTO) {
        log.info("开始注册用户: {}", userDTO.getUsername());
        if (isUsernameExists(userDTO.getUsername())) {
            log.warn("用户名已存在: {}", userDTO.getUsername());
            throw new UserException("用户名已存在: " + userDTO.getUsername());
        }

        UserDO user = new UserDO();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encryptPassword(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());

        boolean success = userService.save(user);
        if (success) {
            log.info("用户注册成功: {}", userDTO.getUsername());
        } else {
            log.error("用户注册失败: {}", userDTO.getUsername());
            throw new UserException("用户注册失败: " + userDTO.getUsername());
        }
        return success;
    }

    /**
     * 验证登录
     */
    public boolean validateLogin(String username, String password) {
        log.info("尝试登录，用户名: {}", username);
        UserDO user = userService.getOne(new QueryWrapper<UserDO>().eq("username", username));
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", username);
            throw new UserException("用户不存在: " + username);
        }

        boolean isValid = validatePassword(password, user.getPassword());
        if (isValid) {
            log.info("用户登录成功: {}", username);
        } else {
            log.warn("用户登录失败，密码错误: {}", username);
            throw new UserException("密码错误");
        }
        return isValid;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(UserDTO userDTO) {
        log.info("开始更新用户信息，用户ID: {}", userDTO.getId());
        UserDO user = userService.getById(userDTO.getId());
        if (user == null) {
            log.warn("更新失败，用户不存在: {}", userDTO.getId());
            throw new UserException("用户不存在，用户ID: " + userDTO.getId());
        }

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        boolean success = userService.updateById(user);
        if (success) {
            log.info("用户信息更新成功，用户ID: {}", userDTO.getId());
        } else {
            log.error("用户信息更新失败，用户ID: {}", userDTO.getId());
            throw new UserException("用户信息更新失败，用户ID: " + userDTO.getId());
        }
        return success;
    }

    /**
     * 分页查询用户
     */
    public List<UserDTO> listUsers(int page, int size) {
        if (page < 1 || size < 1) {
            log.warn("分页参数不合法，page: {}, size: {}", page, size);
            throw new UserException("分页参数不合法，page 和 size 必须大于 0");
        }

        log.info("开始分页查询用户，page: {}, size: {}", page, size);
        Page<UserDO> userPage = userService.page(new Page<>(page, size));
        List<UserDTO> userList = userPage.getRecords().stream().map(this::mapToDTO).collect(Collectors.toList());
        log.info("分页查询完成，查询到用户数量: {}", userList.size());
        return userList;
    }

    /**
     * 检查用户名是否已存在
     */
    private boolean isUsernameExists(String username) {
        boolean exists = userService.count(new QueryWrapper<UserDO>().eq("username", username)) > 0;
        if (exists) {
            log.info("用户名已存在: {}", username);
        }
        return exists;
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String rawPassword) {
        log.debug("开始加密密码");
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 校验密码
     */
    private boolean validatePassword(String rawPassword, String encodedPassword) {
        log.debug("校验密码");
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * 映射 UserDO 到 UserDTO
     */
    private UserDTO mapToDTO(UserDO user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
