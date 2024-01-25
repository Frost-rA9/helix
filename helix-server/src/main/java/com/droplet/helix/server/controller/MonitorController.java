package com.droplet.helix.server.controller;

import com.droplet.helix.server.entity.RestBean;
import com.droplet.helix.server.entity.vo.request.RenameClientVo;
import com.droplet.helix.server.entity.vo.request.RenameNodeVO;
import com.droplet.helix.server.entity.vo.response.ClientDetailsVo;
import com.droplet.helix.server.entity.vo.response.ClientPreviewVo;
import com.droplet.helix.server.service.ClientService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Resource
    ClientService clientService;


    @GetMapping("/list")
    public RestBean<List<ClientPreviewVo>> listAllClient() {
        return RestBean.success(clientService.listClients());
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVo renameClientVo) {
        clientService.renameClient(renameClientVo);
        return RestBean.success();
    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO renameNodeVO) {
        clientService.renameNode(renameNodeVO);
        return RestBean.success();
    }

    @GetMapping("/details")
    public RestBean<ClientDetailsVo> details(int clientId) {
        return RestBean.success(clientService.clientDetails(clientId));
    }
}
