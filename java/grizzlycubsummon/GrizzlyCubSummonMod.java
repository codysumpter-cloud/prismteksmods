package grizzlycubsummon;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;

@ModEntry
public class GrizzlyCubSummonMod {
    public void init() {
        MobRegistry.registerMob("grizzlycubsummon", GrizzlyCubSummonMob.class, true);
        ItemRegistry.registerItem("grizzlycubstaff", new GrizzlyCubStaffItem(), 250.0f, true);
        ItemRegistry.registerItem("grizzlybearstaff", new GrizzlyBearStaffItem(), 420.0f, true);
    }

    public void initResources() {
        GrizzlyCubSummonMob.cubTexture = GameTexture.fromFile("mobs/grizzlycubsummon");
        GrizzlyCubSummonMob.bearTexture = GameTexture.fromFile("mobs/grizzlybearsummon");
    }
}
