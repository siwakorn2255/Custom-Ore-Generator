/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.custom.ore.generator.impl.customdata;

import de.derfrzocker.custom.ore.generator.api.CustomData;
import de.derfrzocker.custom.ore.generator.api.CustomDataApplier;
import de.derfrzocker.custom.ore.generator.api.CustomDataType;
import de.derfrzocker.custom.ore.generator.api.OreConfig;
import de.derfrzocker.custom.ore.generator.impl.v1_13_R1.customdata.FacingApplier_v1_13_R1;
import de.derfrzocker.custom.ore.generator.impl.v1_13_R2.customdata.FacingApplier_v1_13_R2;
import de.derfrzocker.custom.ore.generator.impl.v1_14_R1.customdata.FacingApplier_v1_14_R1;
import de.derfrzocker.custom.ore.generator.impl.v1_15_R1.customdata.FacingApplier_v1_15_R1;
import de.derfrzocker.spigot.utils.Version;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacingCustomData implements CustomData {

    public static final FacingCustomData INSTANCE = new FacingCustomData();

    @Nullable
    private CustomDataApplier customDataApplier;

    private FacingCustomData() {
    }

    @NotNull
    @Override
    public String getName() {
        return "FACING";
    }

    @NotNull
    @Override
    public CustomDataType getCustomDataType() {
        return CustomDataType.STRING;
    }

    @Override
    public boolean canApply(@NotNull final OreConfig oreConfig) {
        return Bukkit.createBlockData(oreConfig.getMaterial()) instanceof Directional;
    }

    @Override
    public boolean isValidCustomData(@NotNull final Object customData, @NotNull final OreConfig oreConfig) {
        if (!(customData instanceof String))
            return false;

        try {
            final BlockFace blockFace = BlockFace.valueOf(((String) customData).toUpperCase());

            return ((Directional) Bukkit.createBlockData(oreConfig.getMaterial())).getFaces().contains(blockFace);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public Object normalize(@NotNull final Object customData, @NotNull final OreConfig oreConfig) {
        return customData;
    }

    @Override
    public boolean hasCustomData(@NotNull final BlockState blockState) {
        Validate.notNull(blockState, "BlockState can not be null");

        return blockState.getBlockData() instanceof Directional;
    }

    @NotNull
    @Override
    public BlockFace getCustomData(@NotNull final BlockState blockState) {
        Validate.isTrue(hasCustomData(blockState), "The given BlockState '" + blockState.getType() + ", " + blockState.getLocation() + "' can not have the CustomData '" + getName() + "'");

        final Directional directional = (Directional) blockState.getBlockData();

        return directional.getFacing();
    }

    @NotNull
    @Override
    public CustomDataApplier getCustomDataApplier() {
        if (customDataApplier == null)
            customDataApplier = getCustomDataApplier0();

        return customDataApplier;
    }

    private CustomDataApplier getCustomDataApplier0() {
        switch (Version.getCurrent()) {
            case v1_15_R1:
                return new FacingApplier_v1_15_R1(this);
            case v1_14_R1:
                return new FacingApplier_v1_14_R1(this);
            case v1_13_R2:
                return new FacingApplier_v1_13_R2(this);
            case v1_13_R1:
                return new FacingApplier_v1_13_R1(this);
        }

        throw new UnsupportedOperationException("Version not supported jet!");
    }

}
