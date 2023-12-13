package com.droplet.helix.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.droplet.helix.server.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
