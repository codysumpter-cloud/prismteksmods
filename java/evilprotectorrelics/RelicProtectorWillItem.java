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

public class RelicProtectorWillItem extends SummonToolItem {

    public RelicProtectorWillItem() {
        super("protectorremnant", FollowPosition.PYRAMID, 1.0f, 320, SummonWeaponsLootTable.summonWeapons);

        this.rarity = Rarity.RARE;
        this.attackDamage.setBaseValue(38.0f).setUpgradedValue(1.0f, 74.0f);
        this.manaCost.setBaseValue(16.0f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tips.add(Localization.translate("itemtooltip", "relicprotectorwilltip"));
        return tips;
    }
}
