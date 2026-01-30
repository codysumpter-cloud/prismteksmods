package prismteksexamplemod.saddlemod;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ItemRegistry;

@ModEntry
public class SaddleMod {

    public void init() {
        ItemRegistry.registerItem("saddle", new SaddleMountItem(), 100, true);
        TameMobMountListener.register();
    }
}
