package dev.herrastudio.tacticalactions;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;

/** Entry point for the standalone Herra tactical actions mod. */
@Mod(TacticalActions.MOD_ID)
public final class TacticalActions {
    public static final String MOD_ID = "tacticalactions";

    /** NeoForge requires exactly one public constructor for every @Mod entry point. */
    public TacticalActions(IEventBus eventBus) {
        // Client event subscribers are discovered from their annotations.
    }
}
