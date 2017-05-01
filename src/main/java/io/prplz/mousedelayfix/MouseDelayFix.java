package io.prplz.mousedelayfix;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class MouseDelayFix extends DummyModContainer {

    public static final String MOD_ID = "mousedelayfix";
    public static final String MOD_NAME = "MouseDelayFix";
    public static final String VERSION = "1.0";

    public MouseDelayFix() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = MOD_ID;
        meta.name = MOD_NAME;
        meta.version = VERSION;
        meta.authorList = Collections.singletonList("prplz");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
