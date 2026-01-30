package prismteksexamplemod.druidmod;

public enum DruidForm {
    HUMAN,
    WOLF,
    BEAR;

    public static DruidForm fromOrdinal(int ordinal) {
        DruidForm[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return HUMAN;
        }
        return values[ordinal];
    }

    public DruidForm next() {
        DruidForm[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
