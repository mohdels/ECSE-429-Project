package test.unitTests;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class PerformanceUtils {
    public static double getAverageCpuUsage(long startTime, long endTime) {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double totalCpu = 0;
        int samples = 0;

        while (System.currentTimeMillis() < endTime) {
            totalCpu += osBean.getSystemCpuLoad();
            samples++;
            try {
                Thread.sleep(10); // Sampling interval (10ms)
            } catch (InterruptedException ignored) {}
        }

        return (samples > 0) ? (totalCpu / samples) * 100 : 0.0;
    }


    public static long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
