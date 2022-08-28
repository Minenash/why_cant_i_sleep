package com.minenash.monsters_in_the_closet.mixin;


import com.minenash.monsters_in_the_closet.MonstersInTheCloset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(MessageHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
        method = "onGameMessage",
        at = @At(value = "INVOKE",
            shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"
        )
    )
    private void interceptDangerousSleepMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!(message.getContent() instanceof TranslatableTextContent && message.getString().equals("block.minecraft.bed.not_safe"))
            || client.player == null || client.world == null) return;

        Vec3d vec3d = Vec3d.ofBottomCenter(client.player.getBlockPos());
        List<HostileEntity> list = client.world.getEntitiesByClass(
            HostileEntity.class,
            new Box(vec3d.getX() - 8.0D, vec3d.getY() - 5.0D, vec3d.getZ() - 8.0D, vec3d.getX() + 8.0D, vec3d.getY() + 5.0D,
                vec3d.getZ() + 8.0D),
            hostileEntity -> hostileEntity.isAngryAt(client.player));

        if (!list.isEmpty()) {
            MonstersInTheCloset.duration = 60;
            MonstersInTheCloset.list = list;
        }
    }
}
