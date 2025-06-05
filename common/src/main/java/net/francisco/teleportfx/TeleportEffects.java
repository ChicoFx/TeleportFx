package net.francisco.teleportfx;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class TeleportEffects {
    private static final Random random = new Random();

    public static void playTeleportEffects(ServerWorld world, Vec3d originPos, Vec3d destinationPos, ServerPlayerEntity player) {
        // A verificação ConfigManager.CONFIG.general.enableAllEffects já é feita em TeleportCommands
        // antes de chamar esta função. Aqui, focamos nas flags individuais por efeito.

        playTeleportEffectsAtPosition(world, originPos, true);

        world.getServer().execute(() -> {
            try {
                Thread.sleep(ConfigManager.CONFIG.timing.effectDelayMs);
                playTeleportEffectsAtPosition(world, destinationPos, false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                TeleportFX.LOGGER.warn("Teleport effect delay interrupted.", e);
            }
        });

        playTeleportSound(world, originPos);
        playTeleportSound(world, destinationPos);
    }

    private static void playTeleportEffectsAtPosition(ServerWorld world, Vec3d pos, boolean isOrigin) {
        if (ConfigManager.CONFIG.particles.enablePortalSpiral) {
            spawnPortalSpiralParticles(world, pos);
        }

        if (isOrigin) {
            if (ConfigManager.CONFIG.particles.enableOriginParticles) {
                spawnOriginParticles(world, pos);
            }
        } else {
            if (ConfigManager.CONFIG.particles.enableDestinationParticles) {
                spawnDestinationParticles(world, pos);
            }
        }

        if (ConfigManager.CONFIG.lightBeam.enableLightBeamEffect) {
            spawnLightBeam(world, pos);
        }
    }

    private static void spawnPortalSpiralParticles(ServerWorld world, Vec3d pos) {
        int particleCount = ConfigManager.CONFIG.particles.portalSpiralCount;
        double height = ConfigManager.CONFIG.particles.spiralHeight;
        double baseRadius = ConfigManager.CONFIG.particles.spiralRadius;

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            double angle = progress * Math.PI * 4;
            double radius = baseRadius * (1 - progress * 0.5);
            double y = pos.y + progress * height;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double velX = -Math.cos(angle) * 0.1;
            double velZ = -Math.sin(angle) * 0.1;
            double velY = 0.05;
            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 1, velX, velY, velZ, 0.1);
        }
    }

    private static void spawnOriginParticles(ServerWorld world, Vec3d pos) {
        for (int i = 0; i < ConfigManager.CONFIG.particles.originParticles; i++) {
            double velX = (random.nextDouble() - 0.5) * 0.5;
            double velY = random.nextDouble() * 0.3;
            double velZ = (random.nextDouble() - 0.5) * 0.5;
            world.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1, pos.z, 1, velX, velY, velZ, 0.1);
        }
        if (ConfigManager.CONFIG.particles.enableGroundCircle) {
            spawnGroundCircle(world, pos, ParticleTypes.ENCHANT);
        }
    }

    private static void spawnDestinationParticles(ServerWorld world, Vec3d pos) {
        for (int i = 0; i < ConfigManager.CONFIG.particles.destinationParticles; i++) {
            double x = pos.x + (random.nextDouble() - 0.5) * 3;
            double y = pos.y + 4 + random.nextDouble() * 2;
            double z = pos.z + (random.nextDouble() - 0.5) * 3;
            world.spawnParticles(ParticleTypes.FALLING_OBSIDIAN_TEAR, x, y, z, 1, 0, -0.1, 0, 0);
        }
        if (ConfigManager.CONFIG.particles.enableGroundCircle) {
            spawnGroundCircle(world, pos, ParticleTypes.HAPPY_VILLAGER);
        }
    }

    private static void spawnGroundCircle(ServerWorld world, Vec3d center, net.minecraft.particle.ParticleEffect particleType) {
        int points = ConfigManager.CONFIG.particles.groundCirclePoints;
        double radius = ConfigManager.CONFIG.particles.groundCircleRadius;
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i) / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            world.spawnParticles(particleType, x, center.y + 0.1, z, 3, 0.1, 0.1, 0.1, 0.02);
        }
    }

    private static void spawnLightBeam(ServerWorld world, Vec3d pos) {
        ModConfig.LightBeamSettings beamConfig = ConfigManager.CONFIG.lightBeam;
        for (double y = 0; y < beamConfig.maxHeight; y += beamConfig.verticalSpacing) {
            double currentY = pos.y + y;
            world.spawnParticles(ParticleTypes.END_ROD, pos.x, currentY, pos.z, 2, 0.05, 0, 0.05, 0.01);
            for (int i = 0; i < beamConfig.borderPoints; i++) {
                double angle = (2 * Math.PI * i) / beamConfig.borderPoints;
                double offsetX = Math.cos(angle) * beamConfig.beamRadius;
                double offsetZ = Math.sin(angle) * beamConfig.beamRadius;
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x + offsetX, currentY, pos.z + offsetZ, 1, 0, 0.1, 0, 0.05);
            }
        }
        world.spawnParticles(ParticleTypes.FIREWORK, pos.x, pos.y + beamConfig.maxHeight, pos.z, beamConfig.topFireworks, 0.5, 0.2, 0.5, 0.1);
    }

    private static void playTeleportSound(ServerWorld world, Vec3d pos) {
        ModConfig.SoundSettings soundConfig = ConfigManager.CONFIG.sound;
        if (soundConfig.enableMainTeleportSound) {
            world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, soundConfig.mainVolume, soundConfig.mainPitch);
        }
        if (soundConfig.enablePortalTriggerSound) {
            world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, soundConfig.portalVolume, soundConfig.portalPitch);
        }
        if (soundConfig.enableWhooshSound) {
            world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, soundConfig.whooshVolume, soundConfig.whooshPitch);
        }
    }

    public static void playCoordinateTeleportEffects(ServerWorld world, Vec3d originPos, Vec3d destinationPos, ServerPlayerEntity player) {
        // A verificação de ConfigManager.CONFIG.general.enableAllEffects e
        // ConfigManager.CONFIG.lightBeam.enableSimpleEffectsForTpcoord
        // já foi feita em TeleportCommands.executeTeleportToCoordinates.

        // Este if é uma dupla checagem ou para caso este método seja chamado de outro lugar sem as checagens prévias.
        if (ConfigManager.CONFIG.lightBeam.enableSimpleEffectsForTpcoord) {
            spawnSimplePortalEffect(world, originPos);

            world.getServer().execute(() -> {
                try {
                    Thread.sleep(ConfigManager.CONFIG.timing.coordinateEffectDelayMs);
                    spawnSimplePortalEffect(world, destinationPos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    TeleportFX.LOGGER.warn("Coordinate teleport effect delay interrupted.", e);
                }
            });
        }

        if (ConfigManager.CONFIG.sound.enableCoordinateTeleportSound) {
            world.playSound(null, originPos.x, originPos.y, originPos.z,
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    SoundCategory.PLAYERS,
                    ConfigManager.CONFIG.sound.coordinateTeleportVolume,
                    ConfigManager.CONFIG.sound.coordinateTeleportPitch);
        }
    }

    private static void spawnSimplePortalEffect(ServerWorld world, Vec3d pos) {
        ModConfig.LightBeamSettings beamConfig = ConfigManager.CONFIG.lightBeam;

        if (beamConfig.simplePortalParticles > 0) {
            for (int i = 0; i < beamConfig.simplePortalParticles; i++) {
                double angle = (2 * Math.PI * i) / beamConfig.simplePortalParticles;
                double x = pos.x + Math.cos(angle) * beamConfig.simplePortalRadius;
                double z = pos.z + Math.sin(angle) * beamConfig.simplePortalRadius;
                world.spawnParticles(ParticleTypes.PORTAL, x, pos.y + 1, z, 3, 0, 0.1, 0, 0.1);
            }
        }

        if (beamConfig.simpleLightBeamHeight > 0) {
            for (double y = 0; y < beamConfig.simpleLightBeamHeight; y += beamConfig.simpleLightBeamSpacing) {
                world.spawnParticles(ParticleTypes.END_ROD, pos.x, pos.y + y, pos.z, 1, 0.02, 0, 0.02, 0.01);
            }
        }
    }
}