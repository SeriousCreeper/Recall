package com.seriouscreeper.recall.items;

import com.seriouscreeper.recall.config.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Random;

public class ItemRecall extends Item {
    public static ForgeConfigSpec.IntValue MaxDamage;
    public static ForgeConfigSpec.IntValue MaxDuration;

    public ItemRecall() {
        super(new Item.Properties()
                .maxDamage(MaxDamage.get())
                .group(ItemGroup.TOOLS)
        );
        //this.setCreativeTab(CreativeTabs.TOOLS);

        setRegistryName("item_recall");
        //setUnlocalizedName(Recall.MODID + ".item_recall");
    }
/*
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }


*/

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(stack.getDamage() < stack.getMaxDamage()) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<ItemStack>(ActionResultType.PASS, stack);
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entity) {
        if(!worldIn.isRemote && stack.getDamage() < stack.getMaxDamage()) {
            PlayerEntity player = (PlayerEntity) entity;
            BlockPos bedLocation = player.getBedLocation(player.dimension);
            BlockPos returnPos = bedLocation;

            if (bedLocation == null) {
                player.sendStatusMessage(new TranslationTextComponent("chat.recall.nobed"), true);
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
            double distance = entity.getDistanceSq(returnPos.getX(), returnPos.getY(), returnPos.getZ());

            if(Config.PlaySounds.get() && distance > 24) {
                worldIn.playSound(null, returnPos.getX(), returnPos.getY(), returnPos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
            }

            if(entity.getRidingEntity() != null)
                entity.stopRiding();

            //if(false && player.dimension != player.getSpawnDimension()) {
            //   entity.changeDimension(player.getSpawnDimension(), new CustomTeleporter((WorldServer) worldIn));
            //} else {
                entity.setPositionAndUpdate(returnPos.getX(), returnPos.getY(), returnPos.getZ());
                entity.fallDistance = 0;
            //}

            stack.damageItem(1, player, (p)-> {
                p.sendBreakAnimation(p.getActiveHand());
            });

            if(Config.PlaySounds.get())
                worldIn.playSound(null, returnPos.getX(), returnPos.getY(), returnPos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
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
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if(!entity.world.isRemote) {
            if(Config.PlaySounds.get()) {
                if (entity.hurtTime > 0) {
                    entity.stopActiveHand();
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1, 1);
                }

                if (count < MaxDuration.get() - 10 && count % 20 == 0) {
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.05F + 0.4F * (float) MathHelper.clamp(80 - count, 1, 80) / 80.0F, 0.5F + (1 - (count / MaxDuration.get())));
                }
            }
        } else {
            if(Config.EmitParticles.get()) {
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
                    entity.world.addParticle(ParticleTypes.PORTAL, entity.posX + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, entity.posY + rand.nextFloat() * 4 - 2, entity.posZ + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, 0, 0.2D, 0);
                }
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return MaxDuration.get();
    }
}
