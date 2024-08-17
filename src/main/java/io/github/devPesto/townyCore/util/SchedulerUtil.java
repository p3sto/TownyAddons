package io.github.devPesto.townyCore.util;

import java.util.concurrent.TimeUnit;

public class SchedulerUtil {

    public static long toTicks(int duration, TimeUnit unit) {
        return unit.toSeconds(duration) * 20;
    }

}
