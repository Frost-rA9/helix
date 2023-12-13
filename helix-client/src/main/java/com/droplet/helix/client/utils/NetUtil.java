package com.droplet.helix.client.utils;

import com.alibaba.fastjson2.JSONObject;
import com.droplet.helix.client.entity.BaseDetail;
import com.droplet.helix.client.entity.ConnectionConfig;
import com.droplet.helix.client.entity.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Slf4j
@Component
public class NetUtil {

    @Lazy
    @Resource
    ConnectionConfig connectionConfig;

    private final HttpClient client = HttpClient.newHttpClient();

    public boolean registerToServer(String address, String token) {
        log.info("正在向服务端注册，请稍后...");
        Response response = this.doGet("/register", address, token);
        if (response.success()) {
            log.info("客户端注册已完成！");
        } else {
            log.error("客户端注册失败: {}", response.message());
        }
        return response.success();
    }

    private Response doGet(String rul) {
        return this.doGet(rul, connectionConfig.getAddress(), connectionConfig.getToken());
    }

    private Response doGet(String url, String address, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(new URI(address + "/monitor" + url))
                    .header("Authorization", token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(), Response.class);
        } catch (Exception exception) {
            log.error("在发起服务端请求时出现问题", exception);
            return Response.errorResponse(exception);
        }
    }

    public void updateBaseDetails(BaseDetail baseDetail) {
        Response response = this.doPost("/detail", baseDetail);
        if(response.success()) {
            log.info("系统基本信息已更新完成");
        } else {
            log.error("系统基本信息更新失败: {}", response.message());
        }
    }

    private Response doPost(String url, Object data) {
        try {
            String rawData = JSONObject.from(data).toJSONString();
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(rawData))
                    .uri(new URI(connectionConfig.getAddress() + "/monitor" + url))
                    .header("Authorization", connectionConfig.getToken())
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(), Response.class);
        } catch (Exception exception) {
            log.error("在发起服务端请求时出现问题", exception);
            return Response.errorResponse(exception);
        }
    }
}
