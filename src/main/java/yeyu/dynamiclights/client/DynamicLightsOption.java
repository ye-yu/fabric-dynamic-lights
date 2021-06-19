package yeyu.dynamiclights.client;

import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DynamicLightsOption {
    public static final String OPTION_NAME = "dynamic_light_level";
    private static DynamicLightsLevel currentOption = DynamicLightsLevel.FOUR;
    public static final DoubleOption DYNAMIC_LIGHTS_OPTION = new DoubleOption("dynamiclights.level",
            0.0D,
            DynamicLightsLevel.values().length - 1,
            1.0F,
            $ -> (double) currentOption.ordinal(),
            ($, mipmapLevels) -> setCurrentOption((int) (double) mipmapLevels),
            ($, option) -> {
                int d = getCurrentOption().ordinal() + 1;
                return new TranslatableText("options.generic_value", new TranslatableText("dynamiclights.level"), d);
            },
            (client) -> client.textRenderer.wrapLines(new TranslatableText("dynamiclights.level.desc"), 200));

    public static DynamicLightsLevel getCurrentOption() {
        return currentOption;
    }

    public static void setCurrentOption(int level) {
        currentOption = DynamicLightsLevel.values()[level];
    }

    public static float getCurrentLightMultiplier() {
        return currentOption.MULTIPLIER;
    }

    public static int getCurrentLightRadius() {
        return currentOption.RADIUS;
    }

    public enum DynamicLightsLevel {
        TWO(5, 5),
        THREE(6, 2.5f),
        FOUR(7, 1.3f),
        FIVE(8, 0.8f),
        SIX(9, 0.55f),
        SEVEN(10, 0.43f);

        public final int RADIUS;
        public final float MULTIPLIER;

        DynamicLightsLevel(int radius, float multiplier) {
            RADIUS = radius;
            MULTIPLIER = multiplier;
        }

        void iterateLightMap(Vec3d cameraPosVec, ClientWorld clientWorld, int maxLight) {
            final BlockPos playerBp = new BlockPos(cameraPosVec);
            final int radius = RADIUS;
            clientWorld.getProfiler().push("queueCheckLight");
            BlockPos.iterate(playerBp.add(-radius - 1, -radius - 1, -radius - 1), playerBp.add(radius, radius, radius)).forEach((bp) -> {
                final int vanillaLuminance = clientWorld.getBlockState(bp).getLuminance();
                final float x = bp.getX() + 0.5f;
                final float y = bp.getY() + 0.5f;
                final float z = bp.getZ() + 0.5f;
                final float camX = (float) cameraPosVec.getX();
                final float camY = (float) cameraPosVec.getY();
                final float camZ = (float) cameraPosVec.getZ();
                final float dx = camX - x;
                final float dy = camY - y;
                final float dz = camZ - z;
                final float dist = dx * dx + dy * dy + dz * dz;
                final double lightLevel = Math.max(LightFunction.INSTANCE.apply(dist, maxLight), vanillaLuminance);
                if (!DynamicLightsClient.setLightLevel(bp, lightLevel)) return;
                clientWorld.getChunkManager().getLightingProvider().checkBlock(bp);
            });
            clientWorld.getProfiler().pop();
        }
    }
}
