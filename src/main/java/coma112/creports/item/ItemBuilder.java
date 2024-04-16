package coma112.creports.item;

import coma112.creports.processor.MessageProcessor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBuilder implements IItemBuilder {
    private final ItemStack is;
    private final ItemMeta meta;
    private boolean finished = false;

    public ItemBuilder(@NotNull ItemStack item) {
        is = item;
        meta = item.getItemMeta();
    }

    ItemBuilder(@NotNull Material type) {
        this(type, 1);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount, short damage) {
        this(type, amount, damage, null);
    }

    public ItemBuilder(@NotNull Material type, @Range(from = 0, to = 64) int amount, short damage, @Nullable Byte data) {
        is = new ItemStack(type, amount, damage, data);
        meta = is.getItemMeta();
    }

    @Override
    public ItemBuilder setType(@NotNull Material material) {
        is.setType(material);
        return this;
    }

    @Override
    public ItemBuilder setCount(@Range(from = 0, to = 64) int newCount) {
        is.setAmount(newCount);
        return this;
    }

    @Override
    public ItemBuilder setName(@NotNull String name) {
        meta.setDisplayName(MessageProcessor.process(name));
        return this;
    }

    @Override
    public ItemBuilder setLocalizedName(@NotNull String name) {
        meta.setLocalizedName(name);
        return this;
    }

    @Override
    public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    @Override
    public ItemBuilder addLore(@NotNull String lore) {
        List<String> lores = meta.getLore();
        lores = lores == null ? new ArrayList<>() : lores;
        lores.add(MessageProcessor.process(lore));
        meta.setLore(lores);

        return this;
    }

    @Override
    public ItemBuilder setUnbreakable() {
        meta.setUnbreakable(true);

        return this;
    }

    public ItemBuilder addFlag(@NotNull ItemFlag flag) {
        meta.addItemFlags(flag);

        return this;
    }

    @Override
    public ItemBuilder removeLore(int line) {
        List<String> lores = meta.getLore();
        lores = lores == null ? new ArrayList<>() : lores;

        lores.remove(Math.min(line, lores.size()));

        meta.setLore(lores);

        return this;
    }

    @Override
    public ItemStack finish() {
        is.setItemMeta(meta);

        finished = true;
        return is;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}