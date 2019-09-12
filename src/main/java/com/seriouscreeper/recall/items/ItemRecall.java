package com.seriouscreeper.recall.items;

import com.seriouscreeper.recall.Recall;
import com.seriouscreeper.recall.config.Config;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class ItemRecall extends Item {
    public static int MaxDamage = 50;
    public static int MaxDuration = 100;

    public ItemRecall() {
        this.setCreativeTab(CreativeTabs.TOOLS);
        this.setMaxDamage(MaxDamage);
        this.setMaxStackSize(1);

        setRegistryName("item_recall");
        setUnlocalizedName(Recall.MODID + ".item_recall");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(stack.getItemDamage() < stack.getMaxDamage()) {
            player.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity) {
        if(!worldIn.isRemote && stack.getItemDamage() < stack.getMaxDamage()) {
            EntityPlayer player = (EntityPlayer) entity;
            BlockPos bedLocation = player.getBedLocation(player.dimension);
            BlockPos returnPos = bedLocation;

            if (bedLocation == null) {
                ((EntityPlayerMP) entity).sendStatusMessage(new TextComponentTranslation("chat.recall.nobed"), true);
                return stack;
            }
/*
            if(!Config.AllowCrossDimension && player.dimension != player.getSpawnDimension()) {
                if(entity instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) entity).sendStatusMessage(new TextComponentTranslation("chat.recall.dimension"), true);
                }

                return stack;
            }
*/
            double distance = entity.getDistanceSq(returnPos);

            if(Config.PlaySounds && distance > 24) {
                worldIn.playSound(null, returnPos.getX(), returnPos.getY(), returnPos.getZ(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
            }

            if(entity.isRiding())
                entity.dismountRidingEntity();

            //if(false && player.dimension != player.getSpawnDimension()) {
            //   entity.changeDimension(player.getSpawnDimension(), new CustomTeleporter((WorldServer) worldIn));
            //} else {
                entity.setPositionAndUpdate(returnPos.getX(), returnPos.getY(), returnPos.getZ());
                entity.fallDistance = 0;
            //}

            stack.damageItem(1, entity);

            if(Config.PlaySounds)
                worldIn.playSound(null, returnPos.getX(), returnPos.getY(), returnPos.getZ(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
        }

        return stack;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int maxDamage = stack.getMaxDamage();
        if(damage >= maxDamage) {
            stack.shrink(1);
        }

        super.setDamage(stack, damage);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if(!entity.world.isRemote) {
            if(Config.PlaySounds) {
                if (entity.hurtTime > 0) {
                    entity.stopActiveHand();
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1, 1);
                }

                if (count < MaxDuration - 10 && count % 20 == 0) {
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.05F + 0.4F * (float) MathHelper.clamp(80 - count, 1, 80) / 80.0F, 0.5F + (1 - (count / MaxDuration)));
                }
            }
        } else {
            if(Config.EmitParticles) {
                Random rand = entity.world.rand;

            /*
            double angle = (1 - (count / 100D)) * 360D * 3D;
            for(int i = 0; i < 10; i++) {
                entity.world.spawnParticle(EnumParticleTypes.PORTAL,
                        entity.posX + (1 * Math.sin(Math.toRadians(angle))),
                        entity.posY + 1,
                        entity.posZ + (1 * Math.cos(Math.toRadians(angle))), 0, -0.5D, 0);

                entity.world.spawnParticle(EnumParticleTypes.PORTAL,
                        entity.posX - (1 * Math.sin(Math.toRadians(angle))),
                        entity.posY + 1,
                        entity.posZ - (1 * Math.cos(Math.toRadians(angle))), 0, -0.5D, 0);
            }
            */

                for (int i = 0; i < 60; i++) {
                    entity.world.spawnParticle(EnumParticleTypes.PORTAL, entity.posX + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, entity.posY + rand.nextFloat() * 4 - 2, entity.posZ + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, 0, 0.2D, 0);
                }
            }
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return MaxDuration;
    }
}
