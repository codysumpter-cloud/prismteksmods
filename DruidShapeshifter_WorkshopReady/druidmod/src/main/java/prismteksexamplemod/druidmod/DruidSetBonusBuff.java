package prismteksexamplemod.druidmod;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DruidSetBonusBuff extends SetBonusBuff {

    private static final String FORM_KEY = "druidForm";

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber subscriber) {
        ensureFormData(buff);
        applyFormModifiers(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        applyFormModifiers(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        applyFormModifiers(buff);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff buff, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(buff, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "druidsetbonus"), 400);
        return tooltips;
    }

    public static DruidForm getForm(Mob mob) {
        ActiveBuff buff = getActiveBuff(mob);
        if (buff == null) {
            return DruidForm.HUMAN;
        }
        GNDItemMap data = getData(buff);
        return DruidForm.fromOrdinal(data.getInt(FORM_KEY, DruidForm.HUMAN.ordinal()));
    }

    public static boolean setForm(Mob mob, DruidForm form) {
        ActiveBuff buff = getActiveBuff(mob);
        if (buff == null) {
            return false;
        }
        GNDItemMap data = getData(buff);
        data.setInt(FORM_KEY, form.ordinal());
        buff.setGndData(data);
        applyFormModifiers(buff);
        buff.forceManagerUpdate();
        return true;
    }

    private static void ensureFormData(ActiveBuff buff) {
        GNDItemMap data = getData(buff);
        if (!data.hasKey(FORM_KEY)) {
            data.setInt(FORM_KEY, DruidForm.HUMAN.ordinal());
            buff.setGndData(data);
        }
    }

    private static ActiveBuff getActiveBuff(Mob mob) {
        if (mob == null) {
            return null;
        }
        return mob.buffManager.getBuff("druidsetbonus");
    }

    private static GNDItemMap getData(ActiveBuff buff) {
        GNDItemMap data = buff.getGndData();
        if (data == null) {
            data = new GNDItemMap();
        }
        return data;
    }

    private static void applyFormModifiers(ActiveBuff buff) {
        DruidForm form = DruidForm.fromOrdinal(getData(buff).getInt(FORM_KEY, DruidForm.HUMAN.ordinal()));

        float speed = 0f;
        float meleeAttackSpeed = 0f;
        int armorFlat = 0;
        float maxHealth = 0f;

        if (form == DruidForm.WOLF) {
            speed = 0.35f;
            meleeAttackSpeed = 0.25f;
        } else if (form == DruidForm.BEAR) {
            armorFlat = 15;
            maxHealth = 0.30f;
        }

        buff.setModifier(BuffModifiers.SPEED, speed);
        buff.setModifier(BuffModifiers.MELEE_ATTACK_SPEED, meleeAttackSpeed);
        buff.setModifier(BuffModifiers.ARMOR_FLAT, armorFlat);
        buff.setModifier(BuffModifiers.MAX_HEALTH, maxHealth);
    }
}
