package summoningweaponexample;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class ImpSummonStaffItem extends SummonToolItem {
    public ImpSummonStaffItem() {
        super("impsummonmob", FollowPosition.PYRAMID, 1.0f, 350, SummonWeaponsLootTable.summonWeapons);
        rarity = Item.Rarity.UNCOMMON;
        attackDamage.setBaseValue(12).setUpgradedValue(1, 28);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "impsummonstafftip"));
        return tooltips;
    }
}
