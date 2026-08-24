package net.dice7000.menthaoil;

import net.dice7000.menthaoil.mixin.IMenthaOilVictim;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.dice7000.menthaoil.MenthaOil.MOD_ID;

public class MORegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<Block> MINT_BLOCK = BLOCKS.register("mint_grass", MintBlock::new);

    public static class MintBlock extends FlowerBlock {
        public MintBlock() {
            super(() -> null, 100, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY));
        }
        @Override public int getEffectDuration() {
            return 100;
        }
        @Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return 100;
        }
        @Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return 60;
        }
        @Override public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
            return new ItemStack(MINT.get());
        }
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> MINT = ITEMS.register("mint", () -> new BlockItem(MINT_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> MENTHA_OIL = ITEMS.register("mentha_oil", MenthaOilItem::new);
    public static final RegistryObject<Item> MENTHA_MIST = ITEMS.register("mentha_spray", MenthaSprayItem::new);

    public static abstract class AbstractMenthaItem extends Item {
        List<Component> tooltip = new ArrayList<>();
        public AbstractMenthaItem() {
            super(new Properties().durability(20));
            for (int i = 0; i <= 30; i++) {
                MutableComponent sentence = Component.translatable("tooltip.menthaoil.menthaitem." + i);
                tooltip.add(i != 0 && i != 30 ? sentence.withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC) : sentence);
                if (i == 0 || i == 16 || i == 28 || i == 29) for (int j = 0; j < (i == 0 || i == 29 ? 1 : 5); j++) tooltip.add(Component.empty());
            }
        }
        protected void useMentha(Player user, Entity entity, InteractionHand hand) {
            Entity target = entity instanceof PartEntity<?> p ? p.getParent() : entity;
            ((IMenthaOilVictim) target).menthaoil$setAffected();
            if (!user.isCreative()) user.getItemInHand(hand).hurtAndBreak(5, user, player -> player.broadcastBreakEvent(hand));
        }
        @Override public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, @NotNull List<Component> list, @NotNull TooltipFlag p_41424_) {
            list.addAll(tooltip);
            if (p_41421_.getItem() instanceof AbstractMenthaItem) {
                int durability = p_41421_.getMaxDamage() - p_41421_.getDamageValue();
                list.add(Component.empty());
                list.add(Component.translatable("tooltip.menthaoil.durability").append(durability + "/" + p_41421_.getMaxDamage()).withStyle(ChatFormatting.YELLOW));
                list.add(Component.empty());
            }
            super.appendHoverText(p_41421_, p_41422_, list, p_41424_);
        }
    }
    public static class MenthaOilItem extends AbstractMenthaItem {
        @Override public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
            if (stack.equals(player.getItemInHand(InteractionHand.MAIN_HAND))) useMentha(player, entity, InteractionHand.MAIN_HAND);
            return true;
        }
        @Override public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
            if (player.isShiftKeyDown()) useMentha(player, player, hand);
            return super.use(level, player, hand);
        }
    }
    public static class MenthaSprayItem extends AbstractMenthaItem {
        @Override public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
            player.startUsingItem(hand);
            return super.use(level, player, hand);
        }
        @Override public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingTick) {
            if (entity instanceof Player user) {
                Vec3 look = user.getLookAngle(); Vec3 origin = user.getEyePosition().add(look.scale(0.5));
                AABB hitbox = new AABB(origin, origin.add(look.scale(5.0))).inflate(3.0);
                level.getEntities(user, hitbox).forEach(target ->
                        useMentha(user, target, getHand(user, stack)));
                if (level.isClientSide) for (double d = 0; d < 5.0; d += 0.2) {
                    Vec3 pos = origin.add(look.scale(d));
                    level.addParticle(ParticleTypes.DOLPHIN,
                            pos.x + (Math.random() - 0.50), pos.y + (Math.random() - 0.50) - 0.2, pos.z + (Math.random() - 0.50), look.x * 0.05, look.y * 0.05 - 0.2, look.z * 0.05);
                }
                if ((getUseDuration(stack) - remainingTick) % 20 == 0) stack.hurtAndBreak(1, user, player -> player.broadcastBreakEvent(getHand(user, stack)));
                super.onUseTick(level, entity, stack, remainingTick);
            }

        }
        @Override public int getUseDuration(@NotNull ItemStack p_41454_) {
            return Integer.MAX_VALUE;
        }

        private InteractionHand getHand(LivingEntity entity, ItemStack stack) {
            return stack.equals(entity.getItemInHand(InteractionHand.MAIN_HAND)) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        }
    }

    public static final ResourceKey<DamageType> DEATH_MINT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "death_mint"));
    public static @NotNull DamageSource causeDeathMintDamage(Level level) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DEATH_MINT));
    }


    public static void confirm(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        eventBus.addListener(MORegistry::addCreative);
    }
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) event.accept(MINT);
        else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(MENTHA_OIL);
            event.accept(MENTHA_MIST);
        }
    }
}
