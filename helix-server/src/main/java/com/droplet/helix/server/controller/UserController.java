package com.droplet.helix.server.controller;

import com.droplet.helix.server.entity.RestBean;
import com.droplet.helix.server.entity.vo.request.ChangePasswordVO;
import com.droplet.helix.server.entity.vo.request.CreateSubAccountVO;
import com.droplet.helix.server.entity.vo.response.SubAccountVO;
import com.droplet.helix.server.service.AccountService;
import com.droplet.helix.server.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    AccountService accountService;

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO changePasswordVO,
                                         @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return accountService.changePassword(userId, changePasswordVO.getOldPassword(), changePasswordVO.getNewPassword()) ?
                RestBean.success() : RestBean.failure(401, "原密码输入错误！");
    }

    @PostMapping("/sub/create")
    public RestBean<Void> createSubAccount(@RequestBody @Valid CreateSubAccountVO createSubAccountVO) {
        accountService.createSubAccount(createSubAccountVO);
        return RestBean.success();
    }

    @GetMapping("/sub/delete")
    public RestBean<Void> deleteSubAccount(int uid,
                                           @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        if (uid == userId){
            return RestBean.failure(401, "非法参数");

        }
        accountService.deleteSubAccount(uid);
        return RestBean.success();
    }

    @GetMapping("/sub/list")
    public RestBean<List<SubAccountVO>> subAccountList() {
        return RestBean.success(accountService.listSubAccount());
    }
}
