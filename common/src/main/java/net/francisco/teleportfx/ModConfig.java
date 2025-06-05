package net.francisco.teleportfx;

public class ModConfig {

    public GeneralSettings general = new GeneralSettings();
    public static class GeneralSettings {
        public boolean enableAllEffects = true; // Interruptor mestre para todos os efeitos
    }

    public ParticleSettings particles = new ParticleSettings();
    public static class ParticleSettings {
        public boolean enablePortalSpiral = true;
        public int portalSpiralCount = 50;
        public double spiralHeight = 3.0;
        public double spiralRadius = 1.5;

        public boolean enableOriginParticles = true;
        public int originParticles = 30;

        public boolean enableDestinationParticles = true;
        public int destinationParticles = 25;

        public boolean enableGroundCircle = true;
        public int groundCirclePoints = 20;
        public double groundCircleRadius = 2.0;
    }

    public SoundSettings sound = new SoundSettings();
    public static class SoundSettings {
        public boolean enableMainTeleportSound = true;
        public float mainVolume = 0.8f;
        public float mainPitch = 1.2f;

        public boolean enablePortalTriggerSound = true;
        public float portalVolume = 0.5f;
        public float portalPitch = 0.8f;

        public boolean enableWhooshSound = true;
        public float whooshVolume = 0.3f;
        public float whooshPitch = 2.0f;

        public boolean enableCoordinateTeleportSound = true;
        public float coordinateTeleportVolume = 0.5f;
        public float coordinateTeleportPitch = 1.5f;
    }

    public LightBeamSettings lightBeam = new LightBeamSettings();
    public static class LightBeamSettings {
        public boolean enableLightBeamEffect = true; // Para o efeito principal
        public double maxHeight = 8.0;
        public double verticalSpacing = 0.2;
        public double beamRadius = 0.3;
        public int borderPoints = 8;
        public int topFireworks = 15;

        public boolean enableSimpleEffectsForTpcoord = true; // Interruptor para os efeitos de /tpcoord
        public int simplePortalParticles = 16;
        public double simplePortalRadius = 1.0;
        public double simpleLightBeamHeight = 4.0;
        public double simpleLightBeamSpacing = 0.5;
    }

    public TimingSettings timing = new TimingSettings();
    public static class TimingSettings {
        public long effectDelayMs = 100;
        public long coordinateEffectDelayMs = 50;
    }

    public PermissionSettings permissions = new PermissionSettings();
    public static class PermissionSettings {
        public int tprPermissionLevel = 0;
        public int tpcoordPermissionLevel = 0;
        public int tpherePermissionLevel = 2;
        public int tplistPermissionLevel = 0;
        public int configReloadPermissionLevel = 2; // Permissão para o comando de reload
    }

    public CoordinateValidationSettings coordinateValidation = new CoordinateValidationSettings();
    public static class CoordinateValidationSettings {
        public int minY = -64;
        public int maxY = 320;
    }

    public MessageSettings messages = new MessageSettings();
    public static class MessageSettings {
        public boolean sendExecutorFeedback = true; // Feedback geral para quem executa o comando
        public boolean notifyTargetPlayerOnTpr = true;
        public boolean notifyTargetPlayerOnTphere = true;
    }

    // Construtor padrão é necessário para Gson e para inicializar sub-objetos
    public ModConfig() {
        // Garante que todas as sub-classes sejam instanciadas para evitar NullPointerExceptions
        // se o arquivo de configuração for de uma versão anterior sem todas as seções.
        if (general == null) general = new GeneralSettings();
        if (particles == null) particles = new ParticleSettings();
        if (sound == null) sound = new SoundSettings();
        if (lightBeam == null) lightBeam = new LightBeamSettings();
        if (timing == null) timing = new TimingSettings();
        if (permissions == null) permissions = new PermissionSettings();
        if (coordinateValidation == null) coordinateValidation = new CoordinateValidationSettings();
        if (messages == null) messages = new MessageSettings();
    }
}