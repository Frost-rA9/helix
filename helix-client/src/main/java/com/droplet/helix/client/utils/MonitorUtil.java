package com.droplet.helix.client.utils;

import com.droplet.helix.client.entity.BaseDetail;
import com.droplet.helix.client.entity.RuntimeDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Date;
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

    public RuntimeDetail monitorRuntimeDetail() {
        double statisticTime = 10;
        try {
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            NetworkIF networkInterface = this.findNetworkInterface(hardware);
            CentralProcessor processor = hardware.getProcessor();
            double networkUpload = networkInterface.getBytesSent(), networkDownload = networkInterface.getBytesRecv();
            double diskRead = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
            double diskWrite = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();
            long[] ticks = processor.getSystemCpuLoadTicks();
            Thread.sleep((long) (statisticTime * 1000));
            networkInterface = Objects.requireNonNull(this.findNetworkInterface(hardware));
            networkUpload = (networkInterface.getBytesSent() - networkUpload) / statisticTime / 1024;
            networkDownload = (networkInterface.getBytesRecv() - networkDownload) / statisticTime / 1024;
            diskRead = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum() - diskRead) / statisticTime / 1024 / 1024;
            diskWrite = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum() - diskWrite) / statisticTime / 1024 / 1024;
            double memoryUsage = (hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / 1024.0 / 1024 / 1024;
            double diskUsage = Arrays.stream(File.listRoots())
                    .mapToLong(file -> file.getTotalSpace() - file.getFreeSpace()).sum() / 1024.0 / 1024 / 1024;

            return new RuntimeDetail()
                    .setCpuUsage(this.calculateCpuUsage(processor, ticks))
                    .setMemoryUsage(memoryUsage)
                    .setDiskUsage(diskUsage)
                    .setNetworkUpload(networkUpload)
                    .setNetworkDownload(networkDownload)
                    .setDiskRead(diskRead)
                    .setDiskWrite(diskWrite)
                    .setTimestamp(new Date().getTime());
        } catch (Exception exception) {
            log.error("读取运行时数据出现问题", exception);
        }
        return null;
    }

    private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
        return (cSys + cUser) * 1.0 / totalCpu;
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
