package grizzlycubsummon;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class GrizzlyCubStaffItem extends SummonToolItem {

    public GrizzlyCubStaffItem() {
        super("grizzlycubsummon", FollowPosition.PYRAMID, 1.0f, 360, SummonWeaponsLootTable.summonWeapons);

        this.rarity = Rarity.RARE;
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 90.0f);
        this.manaCost.setBaseValue(16.0f);
    }

    @Override
    protected void beforeSpawn(ToolItemSummonedMob mob, InventoryItem item, ItemAttackerMob attacker) {
        super.beforeSpawn(mob, item, attacker);

        if (mob instanceof GrizzlyCubSummonMob) {
            GrizzlyCubSummonMob cub = (GrizzlyCubSummonMob) mob;

            if (attacker instanceof PlayerMob) {
                cub.ownerUniqueID = ((PlayerMob) attacker).getUniqueID();
            } else {
                cub.ownerUniqueID = -1;
            }
            cub.evolutionTier = UpgradeUtil.getUpgradeTierSafe(item);
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tips = super.getPreEnchantmentTooltips(item, perspective, blackboard);

        int tier = UpgradeUtil.getUpgradeTierSafe(item);
        tips.add(Localization.translate("itemtooltip",
                tier >= GrizzlyCubSummonMob.EVOLVE_AT_TIER ? "grizzlycubstafftip_evolved" : "grizzlycubstafftip"));

        return tips;
    }
}
