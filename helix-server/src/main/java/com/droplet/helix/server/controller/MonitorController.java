package com.droplet.helix.server.controller;

import com.droplet.helix.server.entity.RestBean;
import com.droplet.helix.server.entity.vo.response.ClientPreviewVo;
import com.droplet.helix.server.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Resource
    ClientService clientService;


    @GetMapping("/list")
    public RestBean<List<ClientPreviewVo>> listAllClient(){
        return RestBean.success(clientService.listClients());
    }
}
