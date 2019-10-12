package de.derfrzocker.custom.ore.generator.impl.v1_13_R2.customdata;

import de.derfrzocker.custom.ore.generator.api.CustomData;
import de.derfrzocker.custom.ore.generator.api.CustomDataApplier;
import de.derfrzocker.custom.ore.generator.api.OreConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.BlockStateBoolean;
import net.minecraft.server.v1_13_R2.GeneratorAccess;
import net.minecraft.server.v1_13_R2.IBlockData;
import org.bukkit.block.BlockFace;

import java.util.Optional;

@RequiredArgsConstructor
public class DirectionApplier_v1_13_R2 implements CustomDataApplier {

    @NonNull
    private final CustomData customData;

    private final BlockFace blockFace;

    @Override
    public void apply(OreConfig oreConfig, Object location, Object blockAccess) {
        final BlockPosition blockPosition = (BlockPosition) location;
        final GeneratorAccess generatorAccess = (GeneratorAccess) blockAccess;
        IBlockData iBlockData = generatorAccess.getType(blockPosition);

        final BlockStateBoolean blockStateBoolean = (BlockStateBoolean) iBlockData.getBlock().getStates().a(blockFace.name().toLowerCase());

        if (blockStateBoolean == null)
            return; //TODO maybe throw exception?

        final Optional<Object> objectOptional = oreConfig.getCustomData(customData);

        if (!objectOptional.isPresent())
            return; //TODO maybe throw exception?

        iBlockData = iBlockData.set(blockStateBoolean, (boolean) objectOptional.get());

        generatorAccess.setTypeAndData(blockPosition, iBlockData, 2);
    }

}