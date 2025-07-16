package com.hotpot.auth.feign;


import com.hotpot.common.dto.SysUserDTO;
import com.hotpot.common.entity.UserInfo;
import com.hotpot.auth.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "UserCenter", name = "user-center", configuration = FeignClientConfig.class)
public interface UserServiceApi {

    @GetMapping("/user/getInfoByUsername/{username}")
    UserInfo getUserDetails(@PathVariable String username);

    @PostMapping("/user/save")
    UserInfo save(@RequestBody SysUserDTO userDTO);
}
