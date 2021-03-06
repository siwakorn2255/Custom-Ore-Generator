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

package de.derfrzocker.custom.ore.generator.api;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public final class OreSetting {

    private static final Map<String, OreSetting> ORE_SETTINGS = Collections.synchronizedMap(new HashMap<>());
    private static final Pattern NAME_PATTER = Pattern.compile("^[A-Z_]*$");

    /**
     * @param name of the OreSetting
     * @return the OreSetting with the given name or null if not present
     * @throws IllegalArgumentException if name is null
     */
    @Nullable
    public static OreSetting getOreSetting(@NotNull final String name) {
        Validate.notNull(name, "Name can't be null");

        return ORE_SETTINGS.get(name);
    }

    /**
     * Create a new OreSetting with the given name.
     * If a OreSetting with the same name already exist, it return's the already
     * existing one
     * The name of the OreSetting must match the following Regex: ^[A-Z_]*$
     * The name can be empty
     *
     * @param name of the OreSetting
     * @return a new OreSetting with the given name and save value, or an already existing one
     * @throws IllegalArgumentException if name is null
     * @throws IllegalArgumentException if name is empty
     * @throws IllegalArgumentException if the name doesn't match the following Regex: ^[A-Z_]*$
     */
    @NotNull
    public static OreSetting createOreSetting(@NotNull final String name) {
        Validate.notNull(name, "Name can't be null");

        return ORE_SETTINGS.computeIfAbsent(name, name2 -> new OreSetting(name));
    }

    /**
     * @return a new Set with all OreSettings
     */
    public static Set<OreSetting> getOreSettings() {
        return new HashSet<>(ORE_SETTINGS.values());
    }

    @NotNull
    private final String name;

    private OreSetting(@NotNull final String name) {
        Validate.notNull(name, "Name can't be null");
        Validate.notEmpty(name, "Name can't be empty");
        Validate.isTrue(NAME_PATTER.matcher(name).matches(), "Name " + name + " doesn't match the regex: ^[A-Z_]*$");

        this.name = name;
    }

    /**
     * The name of this OreSetting is not empty,
     * and match the following Regex: ^[A-Z_]*$
     *
     * @return the name of this OreSetting
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return a copy of the OreGenerator Icon
     */
    @NotNull
    public Material getMaterial() { //TODO read from OreSetting info / add Info
        return Material.COAL;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof OreSetting))
            return false;

        if (this == obj)
            return true;

        final OreSetting other = (OreSetting) obj;

        return other.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
