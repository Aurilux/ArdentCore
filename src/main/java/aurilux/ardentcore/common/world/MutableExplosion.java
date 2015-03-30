package aurilux.ardentcore.common.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The MutableExplosion class allows you to create very customized explosions.
 * Extending the Explosion class is really just to keep this class compatible with most other methods that deal with
 * explosions. Since most of the Explosion class's members are either inaccessible (i.e private or protected) or just
 * difficult to change effectively, this class mimics most of the super math-y calculations but adds a few options (such
 * as including potion effects) while also organizing it to be easier to understand and follow.
 */
public class MutableExplosion extends Explosion {
    /**
     * The maximum radius an explosion may have
     */
    public final int MAX_RADIUS = 16;
    /** The default burn time */
    /** The default force multiplier */
    /** The default entity damage */
    /** The default block damage */

    /**
     * Whether or not to spawn the block destruction particles
     */
    private boolean spawnExtraParticles = true;
    /**
     * Whether or not the explosion spawns smoke particles
     */
    private boolean isSmoking = false;
    /**
     * Whether or not the explosion's damage, force, etc scales by distance
     */
    private boolean scalesWithDistance = false;

    /**
     * The time in ticks that an entity will be lit on fire
     */
    private int burnTime = 0;
    /**
     * The multiplier for the force generated by the explosion
     */
    private float forceMultiplier = 1.0F;
    /**
     * The amount of damage this explosion will deal to entities
     */
    private float entityDamage = 1.0F;
    /**
     * The amount of damage this explosion will deal to blocks
     */
    private float blockDamage = 1.0F;

    /**
     * World in which to apply the explosion
     */
    private World world;
    /**
     * The DamageSource of this explosion
     */
    private DamageSource source = null;
    /**
     * The flag that basically determines if explosions are allowed
     */
    private boolean mobGriefingFlag = false;
    /**
     * The potion effects, if any, to apply to entities
     */
    //TODO turn this into a map<potioneffect, arraylist<entityliving>> so that devs can specify potion effects that are only apply to certain types of entities
    private ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
    /**
     * Holds a list of the entities that will be affected by this explosion
     */
    private ArrayList<Entity> affectedEntities = new ArrayList<Entity>();
    /**
     * Holds a list of the blocks that will be affected by this explosion
     */
    private ArrayList<ChunkPosition> affectedBlocks = new ArrayList<ChunkPosition>();
    /**
     * The list of players affected by the explosion. Used to notify each client of an explosion
     */
    private Map<EntityPlayer, Vec3> affectedPlayers = new HashMap<EntityPlayer, Vec3>();

    public MutableExplosion(World world, Entity entity) {
        super(world, entity, 0, 0, 0, 0);
        this.world = world;
        mobGriefingFlag = this.world.getGameRules().getGameRuleBooleanValue("mobGriefing");
    }

    public void createExplosion(double originX, double originY, double originZ, float size) {
        explosionX = originX;
        explosionY = originY;
        explosionZ = originZ;
        //make sure the blast radius is between 0-16
        explosionSize = size > MAX_RADIUS ? MAX_RADIUS : size < 0 ? 0 : size;

        determineAffectedWithinRadius();

        processBlocks();

        processEntities();

        doSoundAndParticles();

        notifyClients();
    }

    /**
     * Plays the initial explosion sound and spawns the initial explosion particles. Sounds and/or particles determined
     * by blocks or entities should be done in the respective 'processBlocks' and 'processEntities' methods.
     */
    protected void doSoundAndParticles() {
        world.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        String particleType = explosionSize >= 2.0F && isSmoking ? "hugeexplosion" : "largeexplosion";
        world.spawnParticle(particleType, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);
    }

    /**
     * Goes through the list of affected blocks and determines if they are destroyed, set of fire, etc
     */
    protected void processBlocks() {
        Iterator<ChunkPosition> locations = affectedBlocks.iterator();
        while (locations.hasNext()) {
            ChunkPosition location = locations.next();
            int xCoord = location.chunkPosX, yCoord = location.chunkPosY, zCoord = location.chunkPosZ;
            Block block = world.getBlock(xCoord, yCoord, zCoord);

            //destroy blocks
            if (damagesBlocks() && mobGriefingFlag) {
                if (block.getMaterial() != Material.air) {
                    if (block.canDropFromExplosion(this)) {
                        block.dropBlockAsItemWithChance(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord), 1.0F / explosionSize, 0);
                    }
                    block.onBlockExploded(world, xCoord, yCoord, zCoord, this);
                }
            }
            //ignite blocks
            if (isFlaming()) {
                Block blockBelow = world.getBlock(xCoord, yCoord - 1, zCoord);
                if (Block.getIdFromBlock(blockBelow) == 0 && block.isOpaqueCube() && world.rand.nextInt(3) == 0) {
                    world.setBlock(xCoord, yCoord, zCoord, Blocks.fire);
                }
            }
            //spawn the block destruction particles
            if (isSmoking && spawnExtraParticles) {
                double d0 = (double) ((float) xCoord + world.rand.nextFloat());
                double d1 = (double) ((float) yCoord + world.rand.nextFloat());
                double d2 = (double) ((float) zCoord + world.rand.nextFloat());
                double d3 = d0 - explosionX;
                double d4 = d1 - explosionY;
                double d5 = d2 - explosionZ;
                double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5D / (d6 / (double) explosionSize + 0.1D);
                d7 *= (double) (world.rand.nextFloat() * world.rand.nextFloat() + 0.3F);
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                world.spawnParticle("explode", (d0 + explosionX * 1.0D) / 2.0D, (d1 + explosionY * 1.0D) / 2.0D, (d2 + explosionZ * 1.0D) / 2.0D, d3, d4, d5);
                world.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
            }
        }
    }

    /**
     * Goes through the list of affected entities and determines if they are damaged, set of fire, etc
     */
    protected void processEntities() {
        float diameter = explosionSize * 2.0F;
        Iterator<Entity> entities = affectedEntities.iterator();
        Vec3 vec3 = Vec3.createVectorHelper(explosionX, explosionY, explosionZ);
        while (entities.hasNext()) {
            Entity entity = entities.next();
            double d7 = (scalesWithDistance ? entity.getDistance(explosionX, explosionY, explosionZ) / (double) diameter : 0.0D);

            if (d7 <= 1.0D) {
                double d0 = entity.posX - explosionX;
                double d1 = entity.posY + (double) entity.getEyeHeight() - explosionY;
                double d2 = entity.posZ - explosionZ;
                double d8 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

                if (d8 != 0.0D) {
                    d0 /= d8;
                    d1 /= d8;
                    d2 /= d8;
                    double d9 = (double) world.getBlockDensity(vec3, entity.boundingBox);
                    double d10 = (1.0D - d7) * d9;

                    //damage entities
                    if (damagesEntities()) {
                        float amount = (entityDamage == 0.0F ? (float) ((int) ((d10 * d10 + d10) / 2.0D * 8.0D * diameter + 1.0D)) : entityDamage * (float) d10);
                        entity.attackEntityFrom(getDamageSource(), amount);
                    }
                    //ignite entities
                    if (isFlaming && !entity.isImmuneToFire() && world.rand.nextFloat() < d10) {
                        entity.setFire(burnTime);
                    }
                    //knockback entities
                    if (appliesForce()) {
                        double d11 = EnchantmentProtection.func_92092_a(entity, d10);
                        entity.motionX += d0 * d11 * forceMultiplier;
                        entity.motionY += d1 * d11 * forceMultiplier;
                        entity.motionZ += d2 * d11 * forceMultiplier;
                    }
                    //apply potion effects
                    if (entity instanceof EntityLivingBase) {
                        for (PotionEffect effect : potionEffects) {
                            if (effect != null) {
                                ((EntityLivingBase) entity).addPotionEffect(effect);
                            }
                        }
                    }
                    //add players to the player list
                    if (entity instanceof EntityPlayer) {
                        affectedPlayers.put((EntityPlayer) entity, Vec3.createVectorHelper(d0 * d10, d1 * d10, d2 * d10));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void determineAffectedWithinRadius() {
        //determine the entities within the radius of the explosion
        if (damagesEntities()) {
            int minX = MathHelper.floor_double(explosionX - (double) explosionSize - 1.0D);
            int maxX = MathHelper.floor_double(explosionX + (double) explosionSize + 1.0D);
            int minY = MathHelper.floor_double(explosionY - (double) explosionSize - 1.0D);
            int maxY = MathHelper.floor_double(explosionY + (double) explosionSize + 1.0D);
            int minZ = MathHelper.floor_double(explosionZ - (double) explosionSize - 1.0D);
            int maxZ = MathHelper.floor_double(explosionZ + (double) explosionSize + 1.0D);
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double) minX, (double) minY, (double) minZ, (double) maxX, (double) maxY, (double) maxZ);
            affectedEntities.addAll(world.getEntitiesWithinAABBExcludingEntity(exploder, bounds));
        }

        //determine the blocks within the radius of the explosion
        if (damagesBlocks()) {
            for (int x = 0; x < explosionSize; x++) {
                for (int y = 0; y < explosionSize; y++) {
                    for (int z = 0; z < explosionSize; z++) {
                        //if we are at the edges of the explosion's area
                        if (x == 0 || x == explosionSize - 1 || y == 0 || y == explosionSize - 1 || z == 0 || z == explosionSize - 1) {
                            double d3 = (double) ((float) x / (explosionSize - 1.0F) * 2.0F - 1.0F);
                            double d4 = (double) ((float) y / (explosionSize - 1.0F) * 2.0F - 1.0F);
                            double d5 = (double) ((float) z / (explosionSize - 1.0F) * 2.0F - 1.0F);
                            double distance = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                            d3 /= distance;
                            d4 /= distance;
                            d5 /= distance;
                            float intensity = blockDamage * (0.7F + world.rand.nextFloat() * 0.6F);
                            double d0 = explosionX;
                            double d1 = explosionY;
                            double d2 = explosionZ;

                            for (float decayFactor = 0.3F; intensity > 0.0F; intensity -= decayFactor * 0.75F) {
                                int l = MathHelper.floor_double(d0);
                                int i1 = MathHelper.floor_double(d1);
                                int j1 = MathHelper.floor_double(d2);
                                Block block = world.getBlock(l, i1, j1);

                                if (block.getMaterial() != Material.air) {
                                    float blockAbsorption = this.exploder != null ? this.exploder.func_145772_a(this,
                                            this.world, l, i1, j1, block) : block.getExplosionResistance(this.exploder, world,
                                            l, i1, j1, explosionX, explosionY, explosionZ);
                                    intensity -= (blockAbsorption + 0.3F) * decayFactor;
                                }

                                if (intensity > 0.0F && (this.exploder == null || this.exploder.func_145774_a(this, this.world, l, i1, j1, block, intensity))) {
                                    affectedBlocks.add(new ChunkPosition(l, i1, j1));
                                }

                                d0 += d3 * (double) decayFactor;
                                d1 += d4 * (double) decayFactor;
                                d2 += d5 * (double) decayFactor;
                            }
                        }
                    }
                }
            }
        }
    }

    protected DamageSource getDamageSource() {
        return (source != null ? source : DamageSource.setExplosionSource(this));
    }

    public MutableExplosion setBurnTime(int ticks) {
        this.burnTime = ticks;
        return this;
    }

    public MutableExplosion setSmoking(boolean smoking) {
        this.isSmoking = smoking;
        return this;
    }

    public MutableExplosion setForce(float multiplier) {
        this.forceMultiplier = multiplier;
        return this;
    }

    /**
     * Sets the damage this explosion does to both blocks and entities
     *
     * @param toEntities damage dealt to entities
     * @param toBlocks   damage dealt to blocks
     * @return this MutableExplosion instance
     */
    public MutableExplosion setDamage(float toEntities, float toBlocks) {
        this.entityDamage = toEntities;
        this.blockDamage = toBlocks;
        return this;
    }

    public MutableExplosion addPotionEffect(PotionEffect effect) {
        this.potionEffects.add(effect);
        return this;
    }

    public boolean isFlaming() {
        return burnTime > 0;
    }

    public boolean damagesBlocks() {
        return blockDamage > 0;
    }

    public boolean damagesEntities() {
        return entityDamage > 0;
    }

    public boolean appliesForce() {
        return forceMultiplier > 0;
    }

    @SuppressWarnings("rawtypes")
    private void notifyClients() {
        //TODO turn this into a packet call? Look at S27PacketExplosion as a reference
        if (!world.isRemote) {
            Iterator iterator = world.playerEntities.iterator();
            while (iterator.hasNext()) {
                EntityPlayerMP entityplayer = (EntityPlayerMP) iterator.next();
                if (entityplayer.getDistanceSq(explosionX, explosionY, explosionZ) < 4096.0D) {
                    entityplayer.playerNetServerHandler.sendPacket(new S27PacketExplosion(explosionX,
                            explosionY, explosionZ, explosionSize, affectedBlocks, affectedPlayers.get(entityplayer)));
                }
            }
        }
    }
}
