package com.droplet.helix.client.utils;

import com.droplet.helix.client.entity.BaseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
public class MonitorUtil {

    private final SystemInfo systemInfo = new SystemInfo();

    private final Properties properties = System.getProperties();

    public BaseDetail monitorBaseDetail() {
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        double memoryValue = hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024;
        double memorySize = new BigDecimal(memoryValue).setScale(0, RoundingMode.HALF_UP).doubleValue();
        double diskSizeValue = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
        double diskSize = new BigDecimal(diskSizeValue).setScale(0, RoundingMode.HALF_UP).doubleValue();
        String ip = Objects.requireNonNull(this.findNetworkInterface(hardware)).getIPv4addr()[0];

        return new BaseDetail()
                .setOsArch(properties.getProperty("os.arch"))
                .setOsName(operatingSystem.getFamily())
                .setOsVersion(operatingSystem.getVersionInfo().getVersion())
                .setOsBit(operatingSystem.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                .setMemorySize(memorySize)
                .setDiskSize(diskSize)
                .setIp(ip);
    }

    private NetworkIF findNetworkInterface(HardwareAbstractionLayer hardware) {
        try {
            for (NetworkIF network : hardware.getNetworkIFs()) {
                String[] ipv4Addr = network.getIPv4addr();
                NetworkInterface networkInterface = network.queryNetworkInterface();
                if (!networkInterface.isLoopback() && !networkInterface.isPointToPoint() && networkInterface.isUp() && !networkInterface.isVirtual()
                        && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en"))
                        && ipv4Addr.length > 0) {
                    return network;
                }
            }
        } catch (IOException exception) {
            log.error("读取网络接口信息时出错", exception);
        }
        return null;
    }
}
