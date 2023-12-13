package com.droplet.helix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.droplet.helix.server.entity.dto.Client;
import com.droplet.helix.server.entity.vo.request.ClientDetailVO;

public interface ClientService extends IService<Client> {

    String registerToken();

    Client findClientById(int id);

    Client findClientByToken(String token);

    Boolean verifyAndRegister(String token);

    void updateClientDetail(ClientDetailVO clientDetailVO, Client client);
}
