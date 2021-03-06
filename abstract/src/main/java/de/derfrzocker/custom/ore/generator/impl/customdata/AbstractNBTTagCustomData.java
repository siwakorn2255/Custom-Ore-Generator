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

import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.derfrzocker.custom.ore.generator.api.CustomDataApplier;
import de.derfrzocker.custom.ore.generator.api.CustomDataType;
import de.derfrzocker.custom.ore.generator.api.Info;
import de.derfrzocker.custom.ore.generator.api.OreConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

public abstract class AbstractNBTTagCustomData extends AbstractCustomData<AbstractNBTTagCustomData.NBTTagApplier> {

    public AbstractNBTTagCustomData(@NotNull final Function<String, Info> infoFunction) {
        super("NBT_TAG", CustomDataType.STRING, infoFunction);
    }

    @Override
    public boolean canApply(@NotNull final OreConfig oreConfig) {
        return getCustomDataApplier().canApply(oreConfig);
    }

    @Override
    public boolean isValidCustomData(@NotNull final Object customData, @NotNull final OreConfig oreConfig) {
        if (!(customData instanceof String))
            return false;

        final String data = (String) customData;

        if (data.startsWith("file:")) {
            try {
                new JsonParser().parse(new FileReader(new File(data.replace("file:", ""))));
                return true;
            } catch (final FileNotFoundException | JsonIOException | JsonSyntaxException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            new JsonParser().parse(data);
            return true;
        } catch (final JsonSyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    @NotNull
    @Override
    public Object normalize(@NotNull final Object customData, @NotNull final OreConfig oreConfig) {
        final String data = (String) customData;

        if (!data.startsWith("file:")) {
            return customData;
        }

        try {
            final byte[] encoded = Files.readAllBytes(Paths.get(data.replace("file:", "")));
            return new String(encoded);
        } catch (final IOException e) {
            throw new RuntimeException("Unexpected error while reading String from " + data, e);
        }
    }

    @Override
    public boolean hasCustomData(@NotNull final BlockState blockState) {
        Validate.notNull(blockState, "BlockState can not be null");

        return getCustomDataApplier().hasCustomData(blockState);
    }

    @NotNull
    @Override
    public String getCustomData(@NotNull final BlockState blockState) {
        Validate.isTrue(hasCustomData(blockState), "The given BlockState '" + blockState.getType() + ", " + blockState.getLocation() + "' can not have the CustomData '" + getName() + "'");

        return getCustomDataApplier().getCustomData(blockState);
    }

    public interface NBTTagApplier extends CustomDataApplier {

        /**
         * Checks, if the given OreConfig can use this CustomData
         *
         * @param oreConfig that get's checked
         * @return true if this OreConfig can apply the CustomData
         * @throws IllegalArgumentException if oreConfig is null
         */
        boolean canApply(@NotNull OreConfig oreConfig);

        /**
         * @param blockState to check
         * @return true if the blockState has a NBT Tag
         */
        boolean hasCustomData(@NotNull BlockState blockState);

        /**
         * @param blockState to get the data from
         * @return the NBT Tag as String
         */
        @NotNull
        String getCustomData(@NotNull BlockState blockState);

    }

}
