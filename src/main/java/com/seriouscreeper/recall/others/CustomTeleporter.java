package com.seriouscreeper.recall.others;

import net.minecraft.world.Teleporter;
import net.minecraft.world.server.ServerWorld;

public class CustomTeleporter extends Teleporter {
    public CustomTeleporter(ServerWorld worldIn) {
        super(worldIn);
    }
    /*
    @Override
    public void placeEntity(World world, Entity entity, float yaw)
    {
        if(world.isRemote)
            return;

        BlockPos returnPos = world.getSpawnPoint();
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            boolean hasBed = true;

            BlockPos bedLocation = player.getBedLocation(player.dimension);
            if (bedLocation == null || EntityPlayer.getBedSpawnLocation(world, bedLocation, false) == null)
                hasBed = false;

            if (!hasBed)
            {
                BlockPos blockpos = world.provider.getRandomizedSpawnPoint();
                returnPos = world.getTopSolidOrLiquidBlock(blockpos);
            }
            else
            {
                returnPos = EntityPlayer.getBedSpawnLocation(world, bedLocation, false);
            }
        }

        entity.setLocationAndAngles(returnPos.getX(), returnPos.getY(), returnPos.getZ(), entity.rotationYaw, 0.0F);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.fallDistance = 0;
    }

    @Override
    public void placeInPortal(Entity pEntity, float rotationYaw) {
    }

     */
}
