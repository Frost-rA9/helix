package com.droplet.helix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.droplet.helix.server.entity.dto.Account;
import com.droplet.helix.server.entity.vo.request.ConfirmResetVO;
import com.droplet.helix.server.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String address);
    String resetEmailAccountPassword(EmailResetVO info);
    String resetConfirm(ConfirmResetVO info);
}
