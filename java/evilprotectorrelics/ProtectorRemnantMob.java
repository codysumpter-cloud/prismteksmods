package evilprotectorrelics;

import java.awt.Rectangle;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.gfx.gameTexture.GameTexture;

public class ProtectorRemnantMob extends BaseVisualSummonMob {

    public static GameTexture texture;

    public ProtectorRemnantMob() {
        // Visual: vanilla evil minion sprite is 32x32
        super(60, "protectorremnant", 32, 1.0f);

        setSpeed(95.0f);
        setFriction(3.5f);

        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-14, -12, 28, 24);
        selectBox = new Rectangle(-14, -41, 28, 48);
    }

    @Override
    protected GameTexture getVisualTexture() {
        return texture;
    }

    @Override
    protected int getBaseDrawYOffset() {
        return 3;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this,
                new PlayerFollowerCollisionChaserAI(320, this.summonDamage, 18, 420, 320, 44));
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "protectorremnant");
    }
}
