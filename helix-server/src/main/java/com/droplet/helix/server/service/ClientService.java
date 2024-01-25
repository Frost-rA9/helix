package com.droplet.helix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.droplet.helix.server.entity.dto.Client;
import com.droplet.helix.server.entity.vo.request.ClientDetailVO;
import com.droplet.helix.server.entity.vo.request.RenameClientVo;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.droplet.helix.server.entity.vo.response.ClientDetailsVo;
import com.droplet.helix.server.entity.vo.response.ClientPreviewVo;

import java.util.List;

public interface ClientService extends IService<Client> {

    String registerToken();

    Client findClientById(int id);

    Client findClientByToken(String token);

    Boolean verifyAndRegister(String token);

    void updateClientDetail(ClientDetailVO clientDetailVO, Client client);

    void updateRuntimeDetail(RuntimeDetailVO runtimeDetailVO, Client client);

    List<ClientPreviewVo> listClients();

    void renameClient(RenameClientVo renameClientVo);

    ClientDetailsVo clientDetails(int clientId);
}
