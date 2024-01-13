package com.droplet.helix.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.droplet.helix.server.entity.dto.Client;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {
}
