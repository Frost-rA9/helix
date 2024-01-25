package com.droplet.helix.server.entity.vo.response;

import lombok.Data;

@Data
public class ClientDetailsVo {
    int id;

    String name;

    boolean online;

    String node;

    String location;

    String ip;

    String cpuName;

    String osName;

    String osVersion;

    double memorySize;

    int cpuCore;

    double diskSize;
}
