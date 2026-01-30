package prismteksexamplemod.druidmod;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ProjectileRegistry;

@ModEntry
public class DruidMod {

    public void init() {
        ItemRegistry.registerItem("druidhelm", new DruidHelmetArmorItem(), 200, true);
        ItemRegistry.registerItem("druidchest", new DruidChestArmorItem(), 200, true);
        ItemRegistry.registerItem("druidboots", new DruidBootsArmorItem(), 200, true);
        ItemRegistry.registerItem("druidfocus", new DruidFocusItem(), 250, true);

        ProjectileRegistry.registerProjectile("druidnaturebolt", DruidNatureBoltProjectile.class, "druidnaturebolt", "druidnaturebolt_shadow");

        BuffRegistry.registerBuff("druidsetbonus", new DruidSetBonusBuff());
        BuffRegistry.registerBuff("druidformcooldown", new DruidFormCooldownBuff());
    }
}
