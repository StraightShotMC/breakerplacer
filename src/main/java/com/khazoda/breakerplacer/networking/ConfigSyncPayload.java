package com.khazoda.breakerplacer.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.khazoda.breakerplacer.Constants.ID;

public record ConfigSyncPayload(boolean toolTakesDamage) implements CustomPacketPayload {

  public static final Type<ConfigSyncPayload> ID = new Type<>(ID("config_sync"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, ConfigSyncPayload::toolTakesDamage,
      ConfigSyncPayload::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}