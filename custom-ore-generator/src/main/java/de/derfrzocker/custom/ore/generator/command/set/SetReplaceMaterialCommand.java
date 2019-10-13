package de.derfrzocker.custom.ore.generator.command.set;

import com.google.common.collect.Sets;
import de.derfrzocker.custom.ore.generator.CustomOreGeneratorMessages;
import de.derfrzocker.custom.ore.generator.api.CustomOreGeneratorService;
import de.derfrzocker.custom.ore.generator.api.OreConfig;
import de.derfrzocker.custom.ore.generator.api.WorldConfig;
import de.derfrzocker.custom.ore.generator.command.OreGenCommand;
import de.derfrzocker.spigot.utils.Pair;
import de.derfrzocker.spigot.utils.command.CommandUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class SetReplaceMaterialCommand implements TabExecutor {

    @NotNull
    private final Supplier<CustomOreGeneratorService> serviceSupplier;
    @NotNull
    private final JavaPlugin javaPlugin;
    @NotNull
    private final CustomOreGeneratorMessages messages;

    public SetReplaceMaterialCommand(@NotNull final Supplier<CustomOreGeneratorService> serviceSupplier, @NotNull final JavaPlugin javaPlugin, @NotNull final CustomOreGeneratorMessages messages) {
        Validate.notNull(serviceSupplier, "Service supplier can not be null");
        Validate.notNull(javaPlugin, "JavaPlugin can not be null");
        Validate.notNull(messages, "CustomOreGeneratorMessages can not be null");

        this.serviceSupplier = serviceSupplier;
        this.javaPlugin = javaPlugin;
        this.messages = messages;
    }

    @Override //oregen set replacematerial <world> <config_name> <material> <material> ...
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (args.length < 3) {
            messages.COMMAND_SET_REPLACEMATERIAL_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, javaPlugin, () -> {
            final String worldName = args[0];
            final String configName = args[1];

            final World world = CommandUtil.getWorld(worldName, messages.COMMAND_WORLD_NOT_FOUND, sender);
            final CustomOreGeneratorService service = serviceSupplier.get();
            final Pair<WorldConfig, OreConfig> pair = OreGenCommand.getWorldAndOreConfig(world, configName, service, messages.COMMAND_ORE_CONFIG_NOT_FOUND, sender);
            final WorldConfig worldConfig = Objects.requireNonNull(pair.getFirst(), "This should never happen");
            final OreConfig oreConfig = Objects.requireNonNull(pair.getSecond(), "This should never happen");
            final Set<Material> materials = new HashSet<>();

            for (int i = 2; i < args.length; i++) {
                try {
                    final Material material = Material.valueOf(args[i].toUpperCase());
                    if (!material.isBlock()) {
                        messages.COMMAND_MATERIAL_NO_BLOCK.sendMessage(sender, new MessageValue("material", args[i]));
                        return;
                    }

                    materials.add(material);
                } catch (IllegalArgumentException e) {
                    messages.COMMAND_MATERIAL_NOT_FOUND.sendMessage(sender, new MessageValue("material", args[i]));
                    return;
                }
            }

            oreConfig.getReplaceMaterials().forEach(oreConfig::removeReplaceMaterial);
            materials.forEach(oreConfig::addReplaceMaterial);

            service.saveWorldConfig(worldConfig);
            messages.COMMAND_SET_REPLACEMATERIAL_SUCCESS.sendMessage(sender);
        });

        return true;
    }

    @Nullable
    @Override //oregen set material <world> <config_name> <material> <material> ...
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        final List<String> list = new ArrayList<>();
        final CustomOreGeneratorService service = serviceSupplier.get();

        if (args.length == 1) {
            final String world_name = args[0].toLowerCase();
            Bukkit.getWorlds().stream().map(World::getName).filter(value -> value.toLowerCase().contains(world_name)).forEach(list::add);
            return list;
        }

        if (args.length == 2) {
            final Optional<World> world = Bukkit.getWorlds().stream().filter(value -> value.getName().equalsIgnoreCase(args[0])).findAny();

            if (!world.isPresent())
                return list;

            final Optional<WorldConfig> worldConfig = service.getWorldConfig(world.get().getName());

            if (!worldConfig.isPresent())
                return list;

            final String config_name = args[1];
            worldConfig.get().getOreConfigs().stream().map(OreConfig::getName).filter(name -> name.contains(config_name)).forEach(list::add);
            return list;
        }

        final Optional<World> world = Bukkit.getWorlds().stream().filter(value -> value.getName().equalsIgnoreCase(args[0])).findAny();

        if (!world.isPresent())
            return list;

        final Optional<WorldConfig> worldConfig = service.getWorldConfig(world.get().getName());

        if (!worldConfig.isPresent())
            return list;

        final Optional<OreConfig> oreConfig = worldConfig.get().getOreConfig(args[1]);

        if (!oreConfig.isPresent())
            return list;

        final Set<Material> materials = new HashSet<>();

        for (int i = 2; i < (args.length - 1); i++) {
            try {
                final Material material = Material.valueOf(args[i].toUpperCase());
                if (!material.isBlock()) {
                    return list;
                }

                materials.add(material);
            } catch (IllegalArgumentException e) {
                return list;
            }
        }

        final Set<Material> materialSet = Sets.newHashSet(Material.values());

        materialSet.removeAll(materials);

        materialSet.stream().filter(Material::isBlock).map(Enum::toString).filter(value -> value.contains(args[args.length - 1])).forEach(list::add);

        return list;
    }

}