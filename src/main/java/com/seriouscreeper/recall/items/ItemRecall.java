package com.seriouscreeper.recall.items;

import com.seriouscreeper.recall.config.Config;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.command.TextComponentHelper;

import java.util.Random;

public class ItemRecall extends Item {
    public static ForgeConfigSpec.IntValue MaxDamage;
    public static ForgeConfigSpec.IntValue MaxDuration;

    public ItemRecall() {
        super(new Item.Properties()
                .durability(MaxDamage.get())
                .tab(CreativeModeTab.TAB_TOOLS)
        );
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Items.DIAMOND || super.isValidRepairItem(toRepair, repair);
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entity) {
        if(!worldIn.isClientSide) {
            ServerPlayer player = (ServerPlayer) entity;
            BlockPos bedLocation = player.getRespawnPosition(); // find bed in current dimension first

            if (bedLocation == null) {
                player.sendSystemMessage(TextComponentHelper.createComponentTranslation(player, "chat.recall.nobed"));
                return stack;
            }

            if(!Config.AllowCrossDimension.get() && player.level.dimension() != Level.OVERWORLD) {
                player.sendSystemMessage(TextComponentHelper.createComponentTranslation(player, "chat.recall.dimension"));
                //player.sendMessage(new TranslatableContents("chat.recall.dimension"), player.getUUID());

                return stack;
            }

            double distance = entity.distanceToSqr(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ());

            if(Config.PlaySounds.get() && distance > 24) {
                worldIn.playSound(null, player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.75F, 0.75F);
            }

            entity.stopRiding();

            if(player.level.dimension() != player.getRespawnDimension()) {
                LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
                ServerLevel transferWorld = ((ServerLevel)worldIn).getServer().getLevel(player.getRespawnDimension());

                player.teleportTo(transferWorld, bedLocation.getX() + 0.5D, bedLocation.getY() + 0.6D, bedLocation.getZ() + 0.5D, player.getRotationVector().x, player.getRotationVector().y);
            } else {
                entity.moveTo(bedLocation.getX() + 0.5D, bedLocation.getY() + 0.6D, bedLocation.getZ() + 0.5D);
            }

            entity.fallDistance = 0;

            stack.hurtAndBreak(1, player, (p)-> {
                p.broadcastBreakEvent(p.getUsedItemHand());
            });

            player.awardStat(Stats.ITEM_USED.get(this));

            if(Config.PlaySounds.get())
                worldIn.playSound(null, player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.75F, 0.75F);
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

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if(!entity.level.isClientSide) {
            if(Config.PlaySounds.get()) {
                if (entity.hurtTime > 0) {
                    entity.stopUsingItem();
                    entity.level.playSound(null, entity.position().x, entity.position().y, entity.position().z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, 1);
                }

                if (count < MaxDuration.get() - 10 && count % 20 == 0) {
                    entity.level.playSound(null, entity.position().x, entity.position().y, entity.position().z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.05F + 0.4F * (float) clamp(80 - count, 1, 80) / 80.0F, 0.5F + (1 - (count / MaxDuration.get())));
                }
            }
        } else {
            if(Config.EmitParticles.get()) {
                RandomSource rand = entity.level.random;

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
                    entity.level.addParticle(ParticleTypes.PORTAL, entity.blockPosition().getX() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, entity.blockPosition().getY() + rand.nextFloat() * 4 - 2, entity.blockPosition().getZ() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 3, 0, 0.2D, 0);
                }
            }
        }
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return MaxDuration.get();
    }
}
