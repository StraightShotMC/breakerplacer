package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacerConfig;
import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.networking.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class RegClientNetworking {
  public static void init() {
    PayloadTypeRegistry.playC2S().register(ParticlePayload.ID, ParticlePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(BlockBreakParticlePayload.ID, BlockBreakParticlePayload.CODEC);
    PayloadTypeRegistry.playC2S().register(SoundPayload.ID, SoundPayload.CODEC);

    /* Particle Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(ParticlePayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      if (context.client().player == null) return;
      context.client().execute(() -> {
        if (context.client().level == null)
          return;
        ModNetworking.spawnParticlesOnClient(payload.particle(), context.client().level, payload.pos(), payload.offset(), payload.particleCount(), payload.spread(), payload.iterations());
      });
    });
    /* Block Breaking Particle Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(BlockBreakParticlePayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      if (context.client().player == null) return;
      context.client().execute(() -> {
        if (context.client().level == null)
          return;
        context.client().level.addDestroyBlockEffect(payload.pos(), payload.state());
      });
    });
    /* Sound Event Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(SoundPayload.ID, (payload, context) -> {
      if (context.client() == null) return;
      context.client().execute(() -> {
        if (context.client().level == null)
          return;
        ModNetworking.playSoundOnClient(payload.soundEvent(), context.client().level, payload.pos(), 1f, payload.pitch());
      });
    });

    /* Config Networking Packet Client Receipt */
    ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
      context.client().execute(() -> {
        BreakerPlacerConfig.getInstance().setToolTakesDamage(payload.toolTakesDamage());
        Constants.LOG.info("Synced config from server: Tools take damage = {}", payload.toolTakesDamage());
      });
    });
  }
}
