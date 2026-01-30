package prismteksexamplemod.druidmod;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class DruidHelmetArmorItem extends SetHelmetArmorItem {

    public DruidHelmetArmorItem() {
        super(6, DamageTypeRegistry.MAGIC, 100, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets,
                Item.Rarity.UNCOMMON, "druidhelm", "druidchest", "druidboots", "druidsetbonus");
    }

    @Override
    public ArmorModifiers getArmorModifiers(InventoryItem item, Mob mob) {
        return new ArmorModifiers(
                new ModifierValue(BuffModifiers.MAGIC_DAMAGE, 0.10f),
                new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.10f)
        );
    }
}
