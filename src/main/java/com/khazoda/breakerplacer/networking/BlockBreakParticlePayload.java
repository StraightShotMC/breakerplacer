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
import net.minecraft.world.level.block.state.BlockState;

public record BlockBreakParticlePayload(BlockPos pos, BlockState state) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<BlockBreakParticlePayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "block_break_particle_packet"));

  public static final StreamCodec<RegistryFriendlyByteBuf, BlockBreakParticlePayload> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, BlockBreakParticlePayload::pos,
      ByteBufCodecs.fromCodec(BlockState.CODEC), BlockBreakParticlePayload::state,
      BlockBreakParticlePayload::new);

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return ID;
  }

  public static void sendBlockBreakParticlePayloadToClients(ServerLevel world, BlockBreakParticlePayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see particle event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> ServerPlayNetworking.send(player, new BlockBreakParticlePayload(payload.pos, payload.state)));
  }
}
