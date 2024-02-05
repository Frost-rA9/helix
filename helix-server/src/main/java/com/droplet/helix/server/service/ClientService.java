package com.droplet.helix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.droplet.helix.server.entity.dto.Client;
import com.droplet.helix.server.entity.vo.request.ClientDetailVO;
import com.droplet.helix.server.entity.vo.request.RenameClientVO;
import com.droplet.helix.server.entity.vo.request.RenameNodeVO;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.droplet.helix.server.entity.vo.response.ClientDetailsVO;
import com.droplet.helix.server.entity.vo.response.ClientPreviewVo;
import com.droplet.helix.server.entity.vo.response.ClientSimpleVO;
import com.droplet.helix.server.entity.vo.response.RuntimeHistoryVO;

import java.util.List;

public interface ClientService extends IService<Client> {

    String registerToken();

    Client findClientById(int id);

    Client findClientByToken(String token);

    Boolean verifyAndRegister(String token);

    void updateClientDetail(ClientDetailVO clientDetailVO, Client client);

    void updateRuntimeDetail(RuntimeDetailVO runtimeDetailVO, Client client);

    List<ClientPreviewVo> listClients();

    List<ClientSimpleVO> listSimpleList();

    void renameClient(RenameClientVO renameClientVo);

    ClientDetailsVO clientDetails(int clientId);

    void renameNode(RenameNodeVO renameNodeVO);

    RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId);

    RuntimeDetailVO clientRuntimeDetailsNow(int clientId);

    void deleteClient(int clientId);
}
