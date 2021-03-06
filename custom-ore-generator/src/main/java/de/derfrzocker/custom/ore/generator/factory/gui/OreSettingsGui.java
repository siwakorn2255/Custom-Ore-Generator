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

package de.derfrzocker.custom.ore.generator.factory.gui;

import de.derfrzocker.custom.ore.generator.api.BlockSelector;
import de.derfrzocker.custom.ore.generator.api.CustomOreGeneratorService;
import de.derfrzocker.custom.ore.generator.api.OreGenerator;
import de.derfrzocker.custom.ore.generator.api.OreSetting;
import de.derfrzocker.custom.ore.generator.factory.OreConfigBuilder;
import de.derfrzocker.custom.ore.generator.factory.OreConfigFactory;
import de.derfrzocker.custom.ore.generator.factory.gui.settings.OreSettingsGuiSettings;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OreSettingsGui extends PageGui<OreSetting> {

    private static OreSettingsGuiSettings oreSettingsGuiSettings;

    public OreSettingsGui(@NotNull final JavaPlugin plugin, @NotNull final Supplier<CustomOreGeneratorService> serviceSupplier, @NotNull final OreConfigFactory oreConfigFactory, @NotNull final Consumer<OreConfigFactory> consumer) {
        super(plugin, checkSettings(plugin));

        final OreConfigBuilder oreConfigBuilder = oreConfigFactory.getOreConfigBuilder();
        final OreGenerator oreGenerator = oreConfigBuilder.oreGenerator();
        final BlockSelector blockSelector = oreConfigBuilder.blockSelector();
        final Set<OreSetting> settings = new LinkedHashSet<>();

        if (oreGenerator != null) {
            settings.addAll(oreGenerator.getNeededOreSettings());
        }

        if (blockSelector != null) {
            settings.addAll(blockSelector.getNeededOreSettings());
        }

        if (settings.isEmpty())
            throw new IllegalStateException("Cant create OreSettingsGui with out settings");

        addDecorations();
        init(settings.toArray(new OreSetting[0]), OreSetting[]::new, this::getItemStack, (oreSetting, inventoryClickEvent) ->
                new OreSettingGui(getPlugin(), serviceSupplier, oreConfigFactory, consumer, oreSetting).openSync(inventoryClickEvent.getWhoClicked())
        );

        addItem(oreSettingsGuiSettings.getMenuSlot(), MessageUtil.replaceItemStack(plugin, oreSettingsGuiSettings.getMenuItemStack()), inventoryClickEvent -> {
            oreConfigFactory.setRunning(false);
            new MenuGui(plugin, serviceSupplier, oreConfigFactory).openSync(inventoryClickEvent.getWhoClicked());
        });
        addItem(oreSettingsGuiSettings.getAbortSlot(), MessageUtil.replaceItemStack(plugin, oreSettingsGuiSettings.getAbortItemStack()), inventoryClickEvent -> closeSync(inventoryClickEvent.getWhoClicked()));
        addItem(oreSettingsGuiSettings.getNextSlot(), MessageUtil.replaceItemStack(plugin, oreSettingsGuiSettings.getNextItemStack()), inventoryClickEvent -> {
            consumer.accept(oreConfigFactory);
        });

    }

    private ItemStack getItemStack(@NotNull final OreSetting oreSetting) {
        final ItemStack itemStack = oreSettingsGuiSettings.getOreSettingItemStack();

        itemStack.setType(oreSetting.getMaterial());

        return MessageUtil.replaceItemStack(getPlugin(), itemStack, new MessageValue("name", oreSetting.getName()));
    }

    private static OreSettingsGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (oreSettingsGuiSettings == null)
            oreSettingsGuiSettings = new OreSettingsGuiSettings(javaPlugin, "data/factory/gui/ore-settings-gui.yml", true);

        return oreSettingsGuiSettings;
    }

}
