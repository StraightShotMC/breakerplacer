package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record ParticlePayload(ParticleOptions particle, BlockPos pos, Vec3 offset,
                              float spread, byte iterations, byte particleCount) implements CustomPacketPayload {
  public static final Type<ParticlePayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "particle_packet"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ParticlePayload> CODEC = StreamCodec.composite(
      ParticleTypes.STREAM_CODEC, ParticlePayload::particle,
      BlockPos.STREAM_CODEC, ParticlePayload::pos,
      ByteBufCodecs.fromCodec(Vec3.CODEC), ParticlePayload::offset,
      ByteBufCodecs.FLOAT, ParticlePayload::spread,
      ByteBufCodecs.BYTE, ParticlePayload::iterations,
      ByteBufCodecs.BYTE, ParticlePayload::particleCount,
      ParticlePayload::new);


  @Override
  public Type<? extends CustomPacketPayload> type() {
    return ID;
  }

  public static void sendParticlePacketToClients(ServerLevel world, ParticlePayload payload) {
    BlockPos builderPos = new BlockPos(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ());
    /* Iterate through players that can see particle event emitter */
    PlayerLookup.tracking(world, builderPos).forEach(player -> ServerPlayNetworking.send(player, new ParticlePayload(payload.particle, payload.pos, payload.offset, payload.spread, payload.iterations, payload.particleCount)));
  }
}

