package com.droplet.helix.server.controller;

import com.droplet.helix.server.entity.RestBean;
import com.droplet.helix.server.entity.dto.Client;
import com.droplet.helix.server.entity.vo.request.ClientDetailVO;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.droplet.helix.server.service.ClientService;
import com.droplet.helix.server.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monitor")
public class ClientController {

    @Resource
    ClientService clientService;

    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token) {
        return clientService.verifyAndRegister(token) ?
                RestBean.success() : RestBean.failure(401, "客户端注册失败，请检查Token是否正确");
    }

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetail(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                             @RequestBody @Valid ClientDetailVO clientDetailVO) {
        clientService.updateClientDetail(clientDetailVO, client);
        return RestBean.success();
    }

    @PostMapping("/runtime")
    public RestBean<Void> updateRuntimeDetail(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                              @RequestBody @Valid RuntimeDetailVO runtimeDetailVO) {
        clientService.updateRuntimeDetail(runtimeDetailVO, client);
        return RestBean.success();
    }
}
