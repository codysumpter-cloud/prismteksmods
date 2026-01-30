package evilprotectorrelics;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

@ModEntry
public class EvilProtectorRelicsMod {

    // Locked from sprite source filenames in vanilla assets:
    // mobs/evilsprotector.png and mobs/evilminion.png
    public static final String EVIL_PROTECTOR_ID = "evilsprotector";

    public void init() {
        // Mobs (summon entities)
        MobRegistry.registerMob("protectorremnant", ProtectorRemnantMob.class, true);
        MobRegistry.registerMob("protectorecho", ProtectorEchoMob.class, true);
        MobRegistry.registerMob("protectoravatar", ProtectorAvatarMob.class, true);

        // Items (summon weapons)
        ItemRegistry.registerItem("relicprotectorwill", new RelicProtectorWillItem(), 240.0f, true);
        ItemRegistry.registerItem("relicfallenprotector", new RelicFallenProtectorItem(), 420.0f, true);
        ItemRegistry.registerItem("relicperfectedprotector", new RelicPerfectedProtectorItem(), 520.0f, true);

        // 100% drops from Evil Protector:
        // dropBoth=true => always drops BOTH relics (you requested 100% drop chance; this is the strongest form).
        // If you prefer "exactly one guaranteed", set dropBoth=false and adjust in EvilProtectorDropInjector.
        EvilProtectorDropInjector.install(
                EVIL_PROTECTOR_ID,
                true,
                "relicprotectorwill",
                "relicfallenprotector"
        );

    }

    public void initResources() {
        ProtectorRemnantMob.texture = GameTexture.fromFile("mobs/protectorremnant");
        ProtectorEchoMob.texture = GameTexture.fromFile("mobs/protectorecho");
        ProtectorAvatarMob.texture = GameTexture.fromFile("mobs/protectoravatar");

    }

    public void postInit() {
        // Recipes (keep/remove as desired)
        Recipes.registerModRecipe(new Recipe(
                "relicprotectorwill", 1, RecipeTechRegistry.WORKSTATION,
                new Ingredient[] {
                        new Ingredient("ancientfossilbar", 6),
                        new Ingredient("demonicbar", 4)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "relicfallenprotector", 1, RecipeTechRegistry.WORKSTATION,
                new Ingredient[] {
                        new Ingredient("ancientfossilbar", 10),
                        new Ingredient("demonicbar", 8),
                        new Ingredient("voidshard", 4)
                }
        ));

        // Perfected fusion version (craft BOTH into one)
        Recipes.registerModRecipe(new Recipe(
                "relicperfectedprotector", 1, RecipeTechRegistry.WORKSTATION,
                new Ingredient[] {
                        new Ingredient("relicprotectorwill", 1),
                        new Ingredient("relicfallenprotector", 1),
                        new Ingredient("voidshard", 8),
                        new Ingredient("ancientfossilbar", 12)
                }
        ));
    }
}
