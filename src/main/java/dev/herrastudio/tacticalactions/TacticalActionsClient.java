package dev.herrastudio.tacticalactions;

import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** Input state machine and PlayerAnimationLibrary controller registration. */
@EventBusSubscriber(modid = TacticalActions.MOD_ID, value = Dist.CLIENT)
public final class TacticalActionsClient {
    public static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "tactical_actions");
    private static final ResourceLocation LEAN_LEFT = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "lean_left");
    private static final ResourceLocation LEAN_RIGHT = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "lean_right");
    private static final ResourceLocation PRONE = ResourceLocation.fromNamespaceAndPath(TacticalActions.MOD_ID, "prone");

    private static boolean prone;
    private static TacticalActionState active = TacticalActionState.STANDING;
    private static float cameraLeanRoll;

    private TacticalActionsClient() {}

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
        float targetRoll = requested == TacticalActionState.LEAN_LEFT ? 10.0F
                : requested == TacticalActionState.LEAN_RIGHT ? -10.0F : 0.0F;
        cameraLeanRoll = Mth.lerp(0.35F, cameraLeanRoll, targetRoll);
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

    /** Rolls only the local first-person camera so the lean is readable immediately. */
    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null
                && mc.options.getCameraType() == CameraType.FIRST_PERSON
                && event.getCamera().getEntity() == mc.player) {
            event.setRoll(event.getRoll() + cameraLeanRoll);
        }
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
