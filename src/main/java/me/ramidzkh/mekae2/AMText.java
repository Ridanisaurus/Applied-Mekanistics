package me.ramidzkh.mekae2;

import net.minecraft.network.chat.Component;

public enum AMText {
    CHEMICALS("chemical"),
    CREATIVE_TAB("creative_tab"),
    QIO_FREQUENCY("qio_frequency"),
    ;

    public final String key;

    AMText(String key) {
        this.key = "text.%s.%s".formatted(AppliedMekanistics.ID, key);
    }

    public Component formatted(Object... params) {
        return Component.translatable(this.key, params);
    }
}
