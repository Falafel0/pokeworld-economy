package pokeworld.economy.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import notsosig.numismaticoverhaul.currency.CoinItem;
import notsosig.numismaticoverhaul.currency.MoneyBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pokeworld.economy.PokeWorldEconomy;

/**
 * Mixin into CobbleDollars' Bank so it recognizes NO coins as sellable.
 *
 * CobbleDollars calls Bank#contains(ItemStack) to decide if a stack can be placed
 * in the Bank sell slot.  We inject BEFORE the original check and return true
 * for bronze_coin, silver_coin, gold_coin, and money_bag.
 *
 * The companion #getPrice override converts coin value to CobbleDollar units (1:1 bronze).
 *
 * Target: net.genki.cobbledollars.bank.Bank
 * NOTE: Exact descriptor must be verified against javap output before compiling.
 */
@Mixin(targets = "net.genki.cobbledollars.bank.Bank", remap = false)
public abstract class BankMixin {

    /** Returns true for any NO coin or money bag — allows selling them in CD Bank slot. */
    @Inject(method = "contains", at = @At("HEAD"), cancellable = true)
    private void pokeworld$contains(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof CoinItem || stack.getItem() instanceof MoneyBagItem) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Returns bronze value for NO coins when Bank#getPrice is called.
     *
     * CobbleDollars values are in "CobbleDollars" (== 1 bronze).
     * CoinItem.getCurrency().getValue() returns bronze units (1 / 100 / 10000).
     */
    @Inject(method = "getPrice", at = @At("HEAD"), cancellable = true)
    private void pokeworld$getPrice(ItemStack stack, CallbackInfoReturnable<Long> cir) {
        if (stack.getItem() instanceof CoinItem coin) {
            long bronzeValue = coin.getCurrency().getValue() * stack.getCount();
            cir.setReturnValue(bronzeValue);
        } else if (stack.getItem() instanceof MoneyBagItem) {
            // MoneyBagItem stores value in components — read via API
            long bagValue = MoneyBagItem.getValue(stack);
            cir.setReturnValue(bagValue);
        }
    }
}
