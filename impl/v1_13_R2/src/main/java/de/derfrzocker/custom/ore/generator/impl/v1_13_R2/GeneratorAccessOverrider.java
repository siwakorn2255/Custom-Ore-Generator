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

package de.derfrzocker.custom.ore.generator.impl.v1_13_R2;

import de.derfrzocker.custom.ore.generator.api.ChunkAccess;
import de.derfrzocker.custom.ore.generator.api.OreConfig;
import net.minecraft.server.v1_13_R2.*;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GeneratorAccessOverrider implements GeneratorAccess, ChunkAccess {

    @NotNull
    private final GeneratorAccess parent;
    @NotNull
    private final OreConfig oreConfig;

    public GeneratorAccessOverrider(@NotNull final GeneratorAccess parent, @NotNull final OreConfig oreConfig) {
        Validate.notNull(parent, "Parent GeneratorAccess can not be null");
        Validate.notNull(oreConfig, "OreConfig can not be null");

        this.parent = parent;
        this.oreConfig = oreConfig;
    }

    @Override
    public void setMaterial(@NotNull org.bukkit.Material material, final int x, final int y, final int z) {
        setTypeAndData(new BlockPosition(x, y, z), CraftMagicNumbers.getBlock(material).getBlockData(), 2);
    }

    @NotNull
    @Override
    public org.bukkit.Material getMaterial(final int x, final int y, final int z) {
        return CraftMagicNumbers.getMaterial(parent.getType(new BlockPosition(x, y, z)).getBlock());
    }

    @Override
    public boolean setTypeAndData(final BlockPosition blockPosition, final IBlockData iBlockData, final int i) {
        if (y(blockPosition) instanceof ProtoChunkExtension)
            return false;

        final boolean success = parent.setTypeAndData(blockPosition, iBlockData, i);

        if (!success)
            return false;

        oreConfig.getCustomData().forEach((customData, object) -> customData.getCustomDataApplier().apply(oreConfig, blockPosition, parent));

        return true;
    }

    @Override
    public long getSeed() {
        return parent.getSeed();
    }

    @Override
    public TickList<Block> getBlockTickList() {
        return parent.getBlockTickList();
    }

    @Override
    public TickList<FluidType> getFluidTickList() {
        return parent.getFluidTickList();
    }

    @Override
    public IChunkAccess getChunkAt(int i, int i1) {
        return parent.getChunkAt(i, i1);
    }

    @Override
    public World getMinecraftWorld() {
        return parent.getMinecraftWorld();
    }

    @Override
    public WorldData getWorldData() {
        return parent.getWorldData();
    }

    @Override
    public DifficultyDamageScaler getDamageScaler(BlockPosition blockPosition) {
        return parent.getDamageScaler(blockPosition);
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return parent.getChunkProvider();
    }

    @Override
    public IDataManager getDataManager() {
        return parent.getDataManager();
    }

    @Override
    public Random m() {
        return parent.m();
    }

    @Override
    public void update(BlockPosition blockPosition, Block block) {
        parent.update(blockPosition, block);
    }

    @Override
    public BlockPosition getSpawn() {
        return parent.getSpawn();
    }

    @Override
    public void a(@Nullable EntityHuman entityHuman, BlockPosition blockPosition, SoundEffect soundEffect, SoundCategory soundCategory, float v, float v1) {
        parent.a(entityHuman, blockPosition, soundEffect, soundCategory, v, v1);
    }

    @Override
    public void addParticle(ParticleParam particleParam, double v, double v1, double v2, double v3, double v4, double v5) {
        parent.addParticle(particleParam, v, v1, v2, v3, v4, v5);
    }

    @Nullable
    @Override
    public PersistentCollection h() {
        return parent.h();
    }

    @Override
    public boolean isEmpty(BlockPosition blockPosition) {
        return parent.isEmpty(blockPosition);
    }

    @Override
    public BiomeBase getBiome(BlockPosition blockPosition) {
        return parent.getBiome(blockPosition);
    }

    @Override
    public int getBrightness(EnumSkyBlock enumSkyBlock, BlockPosition blockPosition) {
        return parent.getBrightness(enumSkyBlock, blockPosition);
    }

    @Override
    public int getLightLevel(BlockPosition blockPosition, int i) {
        return parent.getLightLevel(blockPosition, i);
    }

    @Override
    public boolean isChunkLoaded(int i, int i1, boolean b) {
        return parent.isChunkLoaded(i, i1, b);
    }

    @Override
    public boolean e(BlockPosition blockPosition) {
        return parent.e(blockPosition);
    }

    @Override
    public int a(HeightMap.Type type, int i, int i1) {
        return 255;
    }

    @Nullable
    @Override
    public EntityHuman a(double v, double v1, double v2, double v3, Predicate<Entity> predicate) {
        return parent.a(v, v1, v2, v3, predicate);
    }

    @Override
    public int c() {
        return parent.c();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return parent.getWorldBorder();
    }

    @Override
    public boolean a(@Nullable Entity entity, VoxelShape voxelShape) {
        return parent.a(entity, voxelShape);
    }

    @Override
    public int a(BlockPosition blockPosition, EnumDirection enumDirection) {
        return parent.a(blockPosition, enumDirection);
    }

    @Override
    public boolean e() {
        return parent.e();
    }

    @Override
    public int getSeaLevel() {
        return parent.getSeaLevel();
    }

    @Override
    public WorldProvider o() {
        return parent.o();
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockPosition) {
        return parent.getTileEntity(blockPosition);
    }

    @Override
    public IBlockData getType(BlockPosition blockPosition) {
        return parent.getType(blockPosition);
    }

    @Override
    public Fluid getFluid(BlockPosition blockPosition) {
        return parent.getFluid(blockPosition);
    }

    @Override
    public boolean addEntity(Entity entity) {
        return parent.addEntity(entity);
    }

    @Override
    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason spawnReason) {
        return parent.addEntity(entity, spawnReason);
    }

    @Override
    public boolean setAir(BlockPosition blockPosition) {
        return parent.setAir(blockPosition);
    }

    @Override
    public void a(EnumSkyBlock enumSkyBlock, BlockPosition blockPosition, int i) {
        parent.a(enumSkyBlock, blockPosition, i);
    }

    @Override
    public boolean setAir(BlockPosition blockPosition, boolean b) {
        return parent.setAir(blockPosition, b);
    }

    @Override
    public BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return parent.getHighestBlockYAt(heightmap_type, blockposition);
    }

    @Override
    public boolean a(StructureBoundingBox structureboundingbox) {
        return parent.a(structureboundingbox);
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockPosition blockposition) {
        return parent.a(iblockdata, blockposition);
    }

    @Override
    public boolean a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return parent.a(entity, axisalignedbb, set);
    }

    @Override
    public boolean a(StructureBoundingBox structureboundingbox, boolean flag) {
        return parent.a(structureboundingbox, flag);
    }

    @Override
    public boolean a_(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return parent.a_(entity, axisalignedbb);
    }

    @Override
    public boolean areChunksLoaded(BlockPosition blockposition, int i) {
        return parent.areChunksLoaded(blockposition, i);
    }

    @Override
    public boolean areChunksLoaded(BlockPosition blockposition, int i, boolean flag) {
        return parent.areChunksLoaded(blockposition, i, flag);
    }

    @Override
    public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
        return parent.areChunksLoadedBetween(blockposition, blockposition1);
    }

    @Override
    public boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1, boolean flag) {
        return parent.areChunksLoadedBetween(blockposition, blockposition1, flag);
    }

    @Override
    public boolean B(BlockPosition blockposition) {
        return parent.B(blockposition);
    }

    @Override
    public boolean b(BlockPosition blockposition, boolean flag) {
        return parent.b(blockposition, flag);
    }

    @Override
    public boolean containsLiquid(AxisAlignedBB axisalignedbb) {
        return parent.containsLiquid(axisalignedbb);
    }

    @Override
    public boolean getCubes(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return parent.getCubes(entity, axisalignedbb);
    }

    @Override
    public boolean i(Entity entity) {
        return parent.i(entity);
    }

    @Override
    public boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1, boolean flag) {
        return parent.isAreaLoaded(i, j, k, l, i1, j1, flag);
    }

    @Override
    public boolean isLoaded(BlockPosition blockposition) {
        return parent.isLoaded(blockposition);
    }

    @Override
    public boolean z(BlockPosition blockposition) {
        return parent.z(blockposition);
    }

    @Nullable
    @Override
    public EntityHuman a(double d0, double d1, double d2, double d3, boolean flag) {
        return parent.a(d0, d1, d2, d3, flag);
    }

    @Nullable
    @Override
    public EntityHuman b(Entity entity, double d0) {
        return parent.b(entity, d0);
    }

    @Nullable
    @Override
    public EntityHuman findNearbyPlayer(Entity entity, double d0) {
        return parent.findNearbyPlayer(entity, d0);
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return parent.getDifficulty();
    }

    @Override
    public float A(BlockPosition blockposition) {
        return parent.A(blockposition);
    }

    @Override
    public float ah() {
        return parent.ah();
    }

    @Override
    public float k(float var0) {
        return parent.k(var0);
    }


    @Override
    public IChunkAccess y(BlockPosition var0) {
        return parent.y(var0);
    }

    @Override
    public int a(DimensionManager var0, String var1) {
        return parent.a(var0, var1);
    }

    @Override
    public int d(BlockPosition blockposition, int i) {
        return parent.d(blockposition, i);
    }

    @Override
    public int getLightLevel(BlockPosition blockposition) {
        return parent.getLightLevel(blockposition);
    }

    @Override
    public int K() {
        return parent.K();
    }

    @Override
    public Stream<VoxelShape> a(VoxelShape voxelshape, VoxelShape voxelshape1, boolean flag) {
        return parent.a(voxelshape, voxelshape1, flag);
    }

    @Override
    public Stream<VoxelShape> a(@Nullable Entity entity, VoxelShape voxelshape, VoxelShape voxelshape1, Set<Entity> set) {
        return parent.a(entity, voxelshape, voxelshape1, set);
    }

    @Override
    public Stream<VoxelShape> a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, double d0, double d1, double d2) {
        return parent.a(entity, axisalignedbb, d0, d1, d2);
    }

    @Override
    public Stream<VoxelShape> a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set, double d0, double d1, double d2) {
        return parent.a(entity, axisalignedbb, set, d0, d1, d2);
    }

    @Override
    public Stream<VoxelShape> b(@Nullable Entity entity, AxisAlignedBB axisalignedbb) {
        return parent.b(entity, axisalignedbb);
    }

    @Nullable
    @Override
    public <T extends PersistentBase> T a(DimensionManager var0, Function<String, T> var1, String var2) {
        return parent.a(var0, var1, var2);
    }

    @Override
    public void a(DimensionManager var0, String var1, PersistentBase var2) {
        parent.a(var0, var1, var2);
    }

}
