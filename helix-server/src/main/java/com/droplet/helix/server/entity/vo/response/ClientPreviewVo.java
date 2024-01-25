package com.droplet.helix.server.entity.vo.response;

import lombok.Data;

@Data
public class ClientPreviewVo {

    int id;

    boolean online;

    String name;

    String location;

    String osName;

    String osVersion;

    String ip;

    String cpuName;

    int cpuCore;

    double memorySize;

    double cpuUsage;

    double memoryUsage;

    double networkUpload;

    double networkDownload;

}
