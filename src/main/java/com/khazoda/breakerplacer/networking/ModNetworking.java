package com.khazoda.breakerplacer.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class ModNetworking {

  /* Call this method after sending packet when wanting to spawn particles */
  public static void spawnParticlesOnClient(ParticleOptions particleType, Level world, BlockPos pos, Vec3 offset, int particleCount, float velocityMagnitude, byte iterations) {
    try {
      Vec3 center = pos.getCenter();
      RandomSource r = world.random;

      for (int i = 0; i < iterations; i++) {
        float x = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);
        float y = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);
        float z = randomFloatBetween(r.nextFloat(), -0.48f, 0.48f);

        for (int j = 0; j < particleCount; j++) {
          world.addParticle(particleType, center.x + x + offset.x, center.y + y + offset.y, center.z + z + offset.z,
              randomVelocity(r.nextFloat(), velocityMagnitude),
              randomVelocity(r.nextFloat(), velocityMagnitude),
              randomVelocity(r.nextFloat(), velocityMagnitude));
        }
      }

    } catch (Exception e) {
      System.out.println("Caught log-in animation exception");
    }
  }

  private static float randomVelocity(float r, float spread) {
    return ((((r * 2) - 1) / 2) * spread);
  }

  private static float randomFloatBetween(float r, float min, float max) {
    return ((((r * (max - min)) + min)));
  }

  /* Call this method clientside after sending packet when wanting to play sound */
  public static void playSoundOnClient(SoundEvent sound, Level world, BlockPos pos, float volume, float pitch) {
    try {
      Vec3 vec = pos.getCenter();
      world.playLocalSound(BlockPos.containing(vec), sound, SoundSource.BLOCKS, volume, pitch, true);
    } catch (Exception e) {
      System.out.println("Caught sound exception");
    }
  }


}