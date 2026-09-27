package dev.herrastudio.tacticalactions;

import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Registers the animation layer before Player Animation Library creates player stacks. */
@EventBusSubscriber(modid = TacticalActions.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TacticalActionsClientSetup {
    private TacticalActionsClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                TacticalActionsClient.LAYER,
                1500,
                player -> {
                    PlayerAnimationController controller = new PlayerAnimationController(
                            player, (stateController, state, setter) -> PlayState.STOP);
                    // PAL defaults to NONE, which deliberately skips animations during its
                    // first-person render pass. Render the animated player model so Q/E/Z
                    // are visible from the camera as well as to other players.
                    controller.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
                    controller.setFirstPersonConfiguration(
                            new FirstPersonConfiguration(true, true, true, true, false));
                    controller.setFirstPersonFollowsCamera(true);
                    controller.setFirstPersonTransitionLength(2);
                    return controller;
                }
        ));
    }
}
