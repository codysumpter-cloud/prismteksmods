package evilprotectorrelics;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.gfx.gameTexture.GameTexture;

public class ProtectorEchoMob extends BaseVisualSummonMob {

    public static GameTexture texture;

    public ProtectorEchoMob() {
        // Visual: vanilla evil protector sprite is 192x192; draw as scaled-down echo
        super(60, "protectorecho", 192, 0.55f);

        setSpeed(68.0f);
        setFriction(2.8f);

        collision = new Rectangle(-12, -9, 24, 18);
        hitBox = new Rectangle(-18, -14, 36, 28);
        selectBox = new Rectangle(-20, -56, 40, 64);
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
        return 0.35f;
    }

    @Override
    protected Color getGlowColor() {
        return new Color(190, 130, 255);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this,
                new PlayerFollowerCollisionChaserAI(420, this.summonDamage, 34, 720, 420, 62));
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "protectorecho");
    }
}
