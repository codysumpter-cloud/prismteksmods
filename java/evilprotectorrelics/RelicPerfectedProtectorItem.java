package evilprotectorrelics;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class RelicPerfectedProtectorItem extends SummonToolItem {

    public RelicPerfectedProtectorItem() {
        // 1 summon slot fusion: strongest single-minion option, but leaves room for mixed-summon builds via +slots gear.
        super("protectoravatar", FollowPosition.PYRAMID, 1.0f, 360, SummonWeaponsLootTable.summonWeapons);

        this.rarity = Rarity.LEGENDARY;
        this.attackDamage.setBaseValue(78.0f).setUpgradedValue(1.0f, 136.0f);
        this.manaCost.setBaseValue(24.0f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tips.add(Localization.translate("itemtooltip", "relicperfectedprotectortip"));
        return tips;
    }
}
