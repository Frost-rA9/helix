package com.droplet.helix.client.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseDetail {

    String osArch;

    String osName;

    String osVersion;

    int osBit;

    String cpuName;

    int cpuCore;

    double memorySize;

    double diskSize;

    String ip;
}
