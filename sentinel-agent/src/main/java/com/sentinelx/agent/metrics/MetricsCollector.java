package com.sentinelx.agent.metrics;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;

public class MetricsCollector {

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem os;

    // Previous CPU tick array for differential measurement
    private long[] prevTicks;

    public MetricsCollector() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.os = systemInfo.getOperatingSystem();
        // Prime the CPU tick baseline
        this.prevTicks = hardware.getProcessor().getSystemCpuLoadTicks();
    }

    public MetricsSnapshot collect() {
        MetricsSnapshot snap = new MetricsSnapshot();
        snap.setHostname(os.getNetworkParams().getHostName());
        snap.setOs(os.toString());
        snap.setCpuUsagePercent(roundTwo(getCpuUsage()));
        snap.setRamUsagePercent(roundTwo(getRamUsage()));
        snap.setDiskUsagePercent(roundTwo(getDiskUsage()));
        snap.setUptimeSeconds(os.getSystemUptime());
        return snap;
    }

    /** Used for the registration call — same data, no metrics. */
    public MetricsSnapshot collectRegistration() {
        MetricsSnapshot snap = new MetricsSnapshot();
        snap.setHostname(os.getNetworkParams().getHostName());
        snap.setOs(os.toString());
        snap.setCpuUsagePercent(0);
        snap.setRamUsagePercent(0);
        snap.setDiskUsagePercent(0);
        snap.setUptimeSeconds(os.getSystemUptime());
        return snap;
    }

    private double getCpuUsage() {
        CentralProcessor processor = hardware.getProcessor();
        long[] ticks = processor.getSystemCpuLoadTicks();
        double load = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;
        prevTicks = ticks;
        return Math.max(0, Math.min(100, load));
    }

    private double getRamUsage() {
        GlobalMemory memory = hardware.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        if (total == 0) return 0;
        return (double) (total - available) / total * 100.0;
    }

    private double getDiskUsage() {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> stores = fileSystem.getFileStores();
        long totalSpace = 0;
        long usableSpace = 0;
        for (OSFileStore store : stores) {
            totalSpace += store.getTotalSpace();
            usableSpace += store.getUsableSpace();
        }
        if (totalSpace == 0) return 0;
        return (double) (totalSpace - usableSpace) / totalSpace * 100.0;
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
