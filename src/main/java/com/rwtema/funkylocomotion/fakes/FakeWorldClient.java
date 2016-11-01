package com.rwtema.funkylocomotion.fakes;

import com.mojang.authlib.GameProfile;
import com.rwtema.funkylocomotion.blocks.TileMovingClient;
import com.rwtema.funkylocomotion.helper.BlockStates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class FakeWorldClient extends WorldClient {
	private static final HashMap<World, FakeWorldClient> cache = new HashMap<World, FakeWorldClient>();
	private static GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
	final World world;
	final WorldClient worldClient;
	public double offset = 0;
	@Nullable
	public EnumFacing dir = null;


	private FakeWorldClient(World world) {
		super(new NetHandlerPlayClient(Minecraft.getMinecraft(),
						null,
						new NetworkManager(EnumPacketDirection.SERVERBOUND),
						MINECRAFT
				),

				new WorldSettings(world.getWorldInfo()),
				world.provider.getDimension(),
				world.getDifficulty(),
				world.theProfiler);
		this.world = world;

		this.worldClient = world instanceof WorldClient ? ((WorldClient) world) : null;
	}

	public static boolean isValid(World world) {
		return world != null && world.provider != null && DimensionManager.isDimensionRegistered(world.provider.getDimension());
	}

	public static FakeWorldClient getFakeWorldWrapper(World world) {
		FakeWorldClient fakeWorldClient = cache.get(world);
		if (fakeWorldClient == null) {
			if (world instanceof FakeWorldClient)
				fakeWorldClient = (FakeWorldClient) world;
			else
				fakeWorldClient = new FakeWorldClient(world);
			cache.put(world, fakeWorldClient);
		}

		return fakeWorldClient;
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new FakeWorldManager());
	}

	@Override
	public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
		Block block = this.getBlock(pos);
		TileMovingClient tile = getTile(pos);

		return tile.block.isNormalCube(tile.getState(), this, pos);
	}

	@Nonnull
	@Override
	public Chunk getChunkFromChunkCoords(int x, int z) {
		return world.getChunkFromChunkCoords(x, z);
	}

	@Nonnull
	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	public Entity getEntityByID(int id) {
		return world.getEntityByID(id);
	}

	public TileMovingClient getTile(BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		return (tile != null && tile.getClass() == TileMovingClient.class) ? (TileMovingClient) tile : null;
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(BlockPos pos) {
		TileMovingClient tile = getTile(pos);
		if (tile != null)
			return tile.getState();

		return BlockStates.AIR;
	}

	public Block getBlock(BlockPos pos) {
		TileMovingClient tile = getTile(pos);
		if (tile != null)
			return tile.block;

		return Blocks.AIR;
	}

	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		TileMovingClient tile = getTile(pos);
		return tile == null ? null : tile.tile;
	}

	@Override
	public boolean setBlockState(BlockPos pos, @Nonnull IBlockState newState, int flags) {
		return false;
	}

	@Override
	public void notifyLightSet(@Nonnull BlockPos pos) {
	}

	@Override
	public int getLight(BlockPos pos) {
		return super.getLight(pos);
	}

	@Override
	public int getLightFor(@Nonnull EnumSkyBlock type, BlockPos pos) {
		return world.getLightFor(type, pos);
	}

	@Override
	public void sendPacketToServer(@Nonnull Packet<?> packetIn) {

	}

	@Nonnull
	@Override
	public ChunkProviderClient getChunkProvider() {
		return worldClient.getChunkProvider();
	}

	@Override
	public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
		worldClient.invalidateBlockReceiveRegion(x1, y1, z1, x2, y2, z2);
	}

	@Override
	public void showBarrierParticles(int p_184153_1_, int p_184153_2_, int p_184153_3_, int p_184153_4_, @Nonnull Random random, boolean p_184153_6_, BlockPos.MutableBlockPos pos) {
		worldClient.showBarrierParticles(p_184153_1_, p_184153_2_, p_184153_3_, p_184153_4_, random, p_184153_6_, pos);
	}

	@Override
	public void playSound(@Nullable EntityPlayer player, double x, double y, double z, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, float volume, float pitch) {
		worldClient.playSound(player, x, y, z, soundIn, category, volume, pitch);
	}

	@Override
	public void playSound(BlockPos pos, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, float volume, float pitch, boolean distanceDelay) {
		worldClient.playSound(pos, soundIn, category, volume, pitch, distanceDelay);
	}

	public int getBlockMetadata(BlockPos pos) {
		TileMovingClient tile = getTile(pos);
		return tile == null ? 0 : tile.meta;
	}

	@Override
	public boolean isAirBlock(@Nonnull BlockPos pos) {
		TileMovingClient tile = getTile(pos);
		return tile == null || tile.block == Blocks.AIR;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight() {
		return world.getHeight();
	}


	@Override
	public boolean isSideSolid(BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		TileMovingClient tile = getTile(pos);
		return tile != null && tile.getState().isSideSolid(this, pos, side);
	}

	@Override
	public boolean spawnEntityInWorld(@Nonnull Entity entity) {
		return false;
	}

	@Override
	public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
		world.spawnParticle(particleType, ignoreRange, x + getXOffset(), y + getYOffset(), z + getZOffset(), xSpeed, ySpeed, zSpeed, parameters);
	}

	@Override
	public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, @Nonnull int... parameters) {
		world.spawnParticle(particleType, xCoord + getXOffset(), yCoord + getYOffset(), zCoord + getZOffset(), xSpeed, ySpeed, zSpeed, parameters);
	}

	private double getZOffset() {
		if (dir == null) return 0;
		return offset * this.dir.getFrontOffsetZ();
	}

	private double getYOffset() {
		if (dir == null) return 0;
		return offset * this.dir.getFrontOffsetY();
	}

	private double getXOffset() {
		if (dir == null) return 0;
		return offset * this.dir.getFrontOffsetX();
	}

	@Override
	public void tick() {

	}

	@Nonnull
	@Override
	public CrashReportCategory addWorldInfoToCrashReport(CrashReport crash) {
		CrashReportCategory crashReportCategory = world.addWorldInfoToCrashReport(crash);
		crashReportCategory.addCrashSection("Fake World", "This world is a fake wrapper used by Funky Locomotion");
		return crashReportCategory;
	}

	@Override
	public void doVoidFogParticles(int posX, int posY, int posZ) {
		if (worldClient != null) worldClient.doVoidFogParticles(posX, posY, posZ);
	}

	@Override
	public void makeFireworks(double x, double y, double z, double vx, double vy, double vz, NBTTagCompound tag) {
		if (worldClient != null)
			worldClient.makeFireworks(x, y, z, vx, vy, vz, tag);
	}


	@Override
	public void removeEntity(Entity entity) {

	}

	@Override
	public void addEntityToWorld(int id, Entity entity) {

	}

	@Nonnull
	@Override
	public Entity removeEntityFromWorld(int entity) {
		return null;
	}

	@Override
	public void playSound(double x, double y, double z, SoundEvent soundIn, @Nonnull SoundCategory category, float volume, float pitch, boolean distanceDelay) {
		if (worldClient != null)
			worldClient.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
	}

	@Override
	public void removeAllEntities() {

	}

	@Override
	public void doPreChunk(int x, int z, boolean load) {

	}

	@Override
	public void sendQuittingDisconnectingPacket() {
		world.sendQuittingDisconnectingPacket();
	}

	@SideOnly(Side.CLIENT)
	public static class FakeWorldManager {

		@SubscribeEvent
		public void onDimensionUnload(WorldEvent.Unload event) {
			cache.remove(event.getWorld());

			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (server != null && !server.isServerRunning()) {
				cache.clear();
			}
		}
	}
}
