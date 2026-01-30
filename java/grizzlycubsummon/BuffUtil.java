package grizzlycubsummon;

import java.lang.reflect.Method;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;

public final class BuffUtil {
    private BuffUtil() {}

    /**
     * Applies vanilla bleeding buff to the target for durationMs.
     * Uses reflection to support mapping differences.
     */
    public static void applyBleed(Mob target, int durationMs, Mob source) {
        if (target == null) return;

        try {
            int buffID = BuffRegistry.getBuffID("bleeding");

            // addBuff(int buffID, int durationMs, Mob source)
            try {
                Method m = target.getClass().getMethod("addBuff", int.class, int.class, Mob.class);
                m.invoke(target, buffID, durationMs, source);
                return;
            } catch (Throwable ignored) {}

            // addBuff(int buffID, int durationMs)
            try {
                Method m = target.getClass().getMethod("addBuff", int.class, int.class);
                m.invoke(target, buffID, durationMs);
            } catch (Throwable ignored) {}

        } catch (Throwable ignored) {}
    }
}
