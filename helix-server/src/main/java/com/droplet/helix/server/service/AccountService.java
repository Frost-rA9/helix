package com.droplet.helix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.droplet.helix.server.entity.dto.Account;
import com.droplet.helix.server.entity.vo.request.ConfirmResetVO;
import com.droplet.helix.server.entity.vo.request.CreateSubAccountVO;
import com.droplet.helix.server.entity.vo.request.EmailResetVO;
import com.droplet.helix.server.entity.vo.response.SubAccountVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetVO info);
    String resetConfirm(ConfirmResetVO info);

    boolean changePassword(int userId, String oldPassword, String newPassword);

    void createSubAccount(CreateSubAccountVO createSubAccountVO);

    void deleteSubAccount(int uid);

    List<SubAccountVO> listSubAccount();
}
