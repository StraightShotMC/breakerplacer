package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ConfigSyncPayload(boolean toolTakesDamage) implements CustomPacketPayload {

  public static final Type<ConfigSyncPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "config_sync"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, ConfigSyncPayload::toolTakesDamage,
      ConfigSyncPayload::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return ID;
  }
}