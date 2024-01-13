package com.droplet.helix.server.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientDetailVO {

    @NotNull
    String osArch;

    @NotNull
    String osName;

    @NotNull
    String osVersion;

    @NotNull
    int osBit;

    @NotNull
    String cpuName;

    @NotNull
    int cpuCore;

    @NotNull
    double memorySize;

    @NotNull
    double diskSize;

    @NotNull
    String ip;
}
