package dev.herrastudio.tacticalactions;

import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.neoforge.event.PlayerAnimationRegisterEvent;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Input state machine and PlayerAnimationLibrary controller registration. */
@EventBusSubscriber(modid = TacticalActions.MOD_ID, value = Dist.CLIENT)
public final class TacticalActionsClient {
    public static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "tactical_actions");
    private static final ResourceLocation LEAN_LEFT = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "lean_left");
    private static final ResourceLocation LEAN_RIGHT = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "lean_right");
    private static final ResourceLocation PRONE = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "prone");

    private static boolean prone;
    private static TacticalActionState active = TacticalActionState.STANDING;

    private TacticalActionsClient() {}

    @SubscribeEvent
    public static void registerPlayerAnimation(PlayerAnimationRegisterEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(LAYER, 1500,
                player -> new PlayerAnimationController(player, (controller, state, setter) -> PlayState.STOP));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isAlive()) {
            prone = false;
            active = TacticalActionState.STANDING;
            return;
        }
        if (mc.screen != null) return;
        while (TacticalActionsKeyMappings.PRONE.consumeClick()) prone = !prone;
        TacticalActionState requested = TacticalActionState.resolve(
                prone,
                TacticalActionsKeyMappings.LEAN_LEFT.isDown(),
                TacticalActionsKeyMappings.LEAN_RIGHT.isDown());
        applyPose(mc.player, requested == TacticalActionState.PRONE);
        if (requested == active) return;
        PlayerAnimationController controller = controller(mc.player);
        if (controller == null) return;
        controller.stopTriggeredAnimation();
        ResourceLocation animation = requested == TacticalActionState.LEAN_LEFT ? LEAN_LEFT
                : requested == TacticalActionState.LEAN_RIGHT ? LEAN_RIGHT
                : requested == TacticalActionState.PRONE ? PRONE : null;
        if (animation != null) controller.triggerAnimation(animation);
        active = requested;
    }

    private static void applyPose(AbstractClientPlayer player, boolean prone) {
        if (prone) {
            player.setPose(Pose.SWIMMING);
        } else if (player.getPose() == Pose.SWIMMING) {
            player.setPose(Pose.STANDING);
        }
    }

    private static PlayerAnimationController controller(AbstractClientPlayer player) {
        var layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, LAYER);
        return layer instanceof PlayerAnimationController controller ? controller : null;
    }

}
