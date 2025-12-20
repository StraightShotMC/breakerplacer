package com.khazoda.breakerplacer.networking;

import com.khazoda.breakerplacer.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigSyncPayload(boolean toolTakesDamage) implements CustomPayload {

  public static final Id<ConfigSyncPayload> ID = new Id<>(Identifier.of(Constants.NAMESPACE, "config_sync"));

  public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.BOOLEAN, ConfigSyncPayload::toolTakesDamage,
      ConfigSyncPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}