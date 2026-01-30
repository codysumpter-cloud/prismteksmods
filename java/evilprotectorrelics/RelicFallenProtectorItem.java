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

public class RelicFallenProtectorItem extends SummonToolItem {

    public RelicFallenProtectorItem() {
        // 1 summon slot: intended to pair with armor that grants extra slots; Echo remains a single “big minion”.
        super("protectorecho", FollowPosition.PYRAMID, 1.0f, 420, SummonWeaponsLootTable.summonWeapons);

        this.rarity = Rarity.EPIC;
        this.attackDamage.setBaseValue(62.0f).setUpgradedValue(1.0f, 112.0f);
        this.manaCost.setBaseValue(28.0f);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tips.add(Localization.translate("itemtooltip", "relicfallenprotectortip"));
        return tips;
    }
}
