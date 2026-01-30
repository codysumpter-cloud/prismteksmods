package evilprotectorrelics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.gfx.gameTexture.GameTexture;

public class ProtectorAvatarMob extends BaseVisualSummonMob {

    public static GameTexture texture;

    public ProtectorAvatarMob() {
        // Visual: same boss sprite, larger than Echo but still not full boss size
        super(60, "protectoravatar", 192, 0.75f);

        setSpeed(82.0f);
        setFriction(3.1f);

        collision = new Rectangle(-12, -8, 24, 16);
        hitBox = new Rectangle(-16, -13, 32, 26);
        selectBox = new Rectangle(-18, -52, 36, 60);
    }

    @Override
    protected GameTexture getVisualTexture() {
        return texture;
    }

    @Override
    protected int getBaseDrawYOffset() {
        return 62;
    }

    private Point getFacingVector() {
        return getDirVector();
    }

    private int getFacingDir() {
        return getDir();
    }

    @Override
    protected Point getDrawSprite(int x, int y, int dir) {
        Point sprite = getAnimSprite(x, y, 0);
        int facingDir = getFacingDir();
        if (facingDir == 0) {
            sprite.y = 0;
        } else if (facingDir == 2) {
            sprite.y = 2;
        } else {
            sprite.y = 1;
        }

        int maxX = sprite.y == 0 ? 3 : 2;
        if (sprite.x > maxX) {
            sprite.x = maxX;
        }
        return sprite;
    }

    @Override
    protected boolean shouldMirrorX(int dir) {
        return getFacingVector().x < 0;
    }

    @Override
    protected float getGlowAlpha() {
        return 0.45f;
    }

    @Override
    protected Color getGlowColor() {
        return new Color(220, 160, 255);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this,
                new PlayerFollowerCollisionChaserAI(380, this.summonDamage, 26, 520, 380, 56));
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "protectoravatar");
    }
}
