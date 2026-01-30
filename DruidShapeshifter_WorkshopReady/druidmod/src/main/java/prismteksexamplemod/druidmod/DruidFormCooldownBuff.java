package prismteksexamplemod.druidmod;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class DruidFormCooldownBuff extends Buff {

    public DruidFormCooldownBuff() {
        canCancel = false;
        isVisible = false;
        shouldSave = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber subscriber) {
    }
}
