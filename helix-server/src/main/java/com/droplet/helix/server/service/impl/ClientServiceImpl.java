package com.droplet.helix.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.droplet.helix.server.entity.dto.Client;
import com.droplet.helix.server.entity.dto.ClientDetail;
import com.droplet.helix.server.entity.vo.request.ClientDetailVO;
import com.droplet.helix.server.entity.vo.request.RenameClientVo;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.droplet.helix.server.entity.vo.response.ClientDetailsVo;
import com.droplet.helix.server.entity.vo.response.ClientPreviewVo;
import com.droplet.helix.server.mapper.ClientDetailMapper;
import com.droplet.helix.server.mapper.ClientMapper;
import com.droplet.helix.server.service.ClientService;
import com.droplet.helix.server.utils.InfluxDBUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken = this.generateNewToken();

    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();

    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();

    @Resource
    ClientDetailMapper clientDetailMapper;

    @Resource
    InfluxDBUtils influxDBUtils;

    @PostConstruct
    public void initClientCache() {
        this.list().forEach(this::addClientCache);
    }

    @Override
    public String registerToken() {
        return registerToken;
    }

    @Override
    public Client findClientById(int id) {
        return clientIdCache.get(id);
    }

    @Override
    public Client findClientByToken(String token) {
        return clientTokenCache.get(token);
    }

    @Override
    public Boolean verifyAndRegister(String token) {
        if (this.registerToken.equals(token)) {
            int id = this.randomClientId();
            Client client = new Client(id, "未命名主机", token, "cn", "未命名节点", new Date());
            if (this.save(client)) {
                registerToken = this.generateNewToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateClientDetail(ClientDetailVO clientDetailVO, Client client) {
        ClientDetail clientDetail = new ClientDetail();
        BeanUtils.copyProperties(clientDetailVO, clientDetail);
        clientDetail.setId(client.getId());
        if (Objects.nonNull(clientDetailMapper.selectById(client.getId()))) {
            clientDetailMapper.updateById(clientDetail);
        } else {
            clientDetailMapper.insert(clientDetail);
        }
    }

    private final Map<Integer, RuntimeDetailVO> currentRuntime = new ConcurrentHashMap<>();

    @Override
    public void updateRuntimeDetail(RuntimeDetailVO runtimeDetailVO, Client client) {
        currentRuntime.put(client.getId(), runtimeDetailVO);
        influxDBUtils.writeRuntimeData(client.getId(), runtimeDetailVO);
    }

    @Override
    public List<ClientPreviewVo> listClients() {
        return clientIdCache.values().stream().map(client -> {
            ClientPreviewVo clientPreviewVo = client.asViewObject(ClientPreviewVo.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(clientPreviewVo.getId()), clientPreviewVo);
            RuntimeDetailVO runtimeDetailVO = currentRuntime.get(client.getId());
            if (this.isOnline(runtimeDetailVO)) {
                BeanUtils.copyProperties(runtimeDetailVO, clientPreviewVo);
                clientPreviewVo.setOnline(true);
            }
            return clientPreviewVo;
        }).toList();
    }

    @Override
    public void renameClient(RenameClientVo renameClientVo) {
        this.update(new LambdaUpdateWrapper<Client>()
                .eq(Client::getId, renameClientVo.getId())
                .set(Client::getName, renameClientVo.getName()));
        this.initClientCache();
    }

    @Override
    public ClientDetailsVo clientDetails(int clientId) {
        ClientDetailsVo clientDetailsVo = this.clientIdCache.get(clientId).asViewObject(ClientDetailsVo.class);
        BeanUtils.copyProperties(clientDetailMapper.selectById(clientId), clientDetailsVo);
        clientDetailsVo.setOnline(this.isOnline(currentRuntime.get(clientId)));
        return clientDetailsVo;
    }

    private boolean isOnline(RuntimeDetailVO runtimeDetailVO) {
        return Objects.nonNull(runtimeDetailVO) && System.currentTimeMillis() - runtimeDetailVO.getTimestamp() < 60 * 1000;
    }

    private void addClientCache(Client client) {
        clientIdCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }

    private int randomClientId() {
        return new Random().nextInt(90000000) + 10000000;
    }

    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(24);
        for (int index = 0; index < 24; index++)
            stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }
}
