package com.hotpot.user.controller;

import com.hotpot.common.dto.SysUserDTO;
import com.hotpot.common.entity.UserInfo;
import com.hotpot.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Peter
 * @date 2023/3/6
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getInfoByUsername/{username}")
    public UserInfo getInfoByUsername(@PathVariable("username") String username) {
        return userService.getInfoByUsername(username);
    }

    @GetMapping("/resource/test")
    public String resource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        return "this is a resource server";
    }

    @PostMapping("/save")
    public void save(@RequestBody SysUserDTO sysUserDTO) {
        userService.save(sysUserDTO);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }


}
