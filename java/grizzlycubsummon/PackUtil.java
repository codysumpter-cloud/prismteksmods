package grizzlycubsummon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public final class PackUtil {
    private PackUtil() {}

    public static List<GrizzlyCubSummonMob> findPack(Level level, GrizzlyCubSummonMob self, int ownerUniqueID, int radius) {
        List<GrizzlyCubSummonMob> out = new ArrayList<>();
        if (level == null || self == null) return out;

        int r2 = radius * radius;

        // Most Necesse builds expose an iterable list at level.entityManager.mobs
        // If your mapping differs, this will fail to compile and you can replace with the correct iterator.
        for (Mob m : level.entityManager.mobs) {
            if (!(m instanceof GrizzlyCubSummonMob)) continue;
            GrizzlyCubSummonMob cub = (GrizzlyCubSummonMob) m;
            if (cub.removed()) continue;
            if (cub.ownerUniqueID != ownerUniqueID) continue;

            int dx = cub.getX() - self.getX();
            int dy = cub.getY() - self.getY();
            if (dx * dx + dy * dy <= r2) out.add(cub);
        }

        return out;
    }

    public static int countPackMates(Level level, GrizzlyCubSummonMob self, int ownerUniqueID, int radius) {
        int c = findPack(level, self, ownerUniqueID, radius).size();
        return Math.max(1, c);
    }

    public static Mob findSharedTarget(Level level, GrizzlyCubSummonMob self, int ownerUniqueID, int radius) {
        for (GrizzlyCubSummonMob cub : findPack(level, self, ownerUniqueID, radius)) {
            Mob t = cub.getAICurrentTarget();
            if (t != null && !t.removed() && t.isSamePlace(self)) return t;
        }
        return null;
    }

    public static void trySetTarget(Mob self, Mob target) {
        if (self == null || target == null) return;

        for (String name : new String[]{"setTarget", "setAttackTarget"}) {
            try {
                Method m = self.getClass().getMethod(name, Mob.class);
                m.invoke(self, target);
                return;
            } catch (Throwable ignored) {}
        }
    }
}
