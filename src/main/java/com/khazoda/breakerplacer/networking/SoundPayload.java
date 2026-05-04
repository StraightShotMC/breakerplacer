package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;

public record SoundPayload(BlockPos pos, SoundEvent soundEvent,
                           float pitch) implements CustomPacketPayload {
  public static final Type<SoundPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "plushable_sound_packet_without_player"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SoundPayload> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, SoundPayload::pos,
      SoundEvent.DIRECT_STREAM_CODEC, SoundPayload::soundEvent,
      ByteBufCodecs.FLOAT, SoundPayload::pitch,
      SoundPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return ID;
  }

  public static void sendNoPlayerPacketToClients(ServerLevel world, SoundPayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see sound event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> ServerPlayNetworking.send(player, new SoundPayload(payload.pos, payload.soundEvent, payload.pitch)));
  }

}
