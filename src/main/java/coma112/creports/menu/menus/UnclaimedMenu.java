package coma112.creports.menu.menus;

import com.google.common.primitives.Ints;
import coma112.creports.CReports;
import coma112.creports.database.AbstractDatabase;
import coma112.creports.enums.keys.ConfigKeys;
import coma112.creports.enums.keys.ItemKeys;
import coma112.creports.enums.keys.MessageKeys;
import coma112.creports.item.IItemBuilder;
import coma112.creports.managers.Report;
import coma112.creports.menu.PaginatedMenu;
import coma112.creports.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class UnclaimedMenu extends PaginatedMenu implements Listener {

    public UnclaimedMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.UNCLAIMED_BACK_SLOT.getInt(), ItemKeys.UNCLAIMED_BACK_ITEM.getItem());
        inventory.setItem(ConfigKeys.UNCLAIMED_FORWARD_SLOT.getInt(), ItemKeys.CLAIMED_FORWARD_ITEM.getItem());
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.UNCLAIMED_MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.UNCLAIMED_MENU_SIZE.getInt();
    }

    @Override
    public int getMaxItemsPerPage() {
        return ConfigKeys.UNCLAIMED_MENU_SIZE.getInt() - 2;
    }

    @Override
    public int getMenuTick() {
        return ConfigKeys.UNCLAIMED_MENU_TICK.getInt();
    }

    @Override
    public boolean enableFillerGlass() {
        return false;
    }

    @Override
    public void setMenuItems() {
        AbstractDatabase database = CReports.getDatabaseManager();
        List<Report> reports = CReports.getDatabaseManager().getReports();
        inventory.clear();
        addMenuBorder();

        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), reports.size());

        IntStream.range(startIndex, endIndex).forEach(index -> {
            Report report = reports.get(index);

            if (database.getClaimer(report) == null || database.getClaimer(report).isEmpty()) inventory.addItem(createReportItem(reports.get(index)));
        });
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        AbstractDatabase database = CReports.getDatabaseManager();
        List<Report> reports = database.getReports();
        int clickedIndex = event.getSlot();

        if (clickedIndex == ConfigKeys.UNCLAIMED_FORWARD_SLOT.getInt()) {
            int nextPageIndex = page + 1;
            int totalPages = (int) Math.ceil((double) reports.size() / getMaxItemsPerPage());

            if (nextPageIndex >= totalPages) {
                player.sendMessage(MessageKeys.LAST_PAGE.getMessage());
                return;
            } else {
                page++;
                super.open();
            }
        }

        if (clickedIndex == ConfigKeys.UNCLAIMED_BACK_SLOT.getInt()) {
            if (page == 0) {
                player.sendMessage(MessageKeys.FIRST_PAGE.getMessage());
                return;
            } else {
                page--;
                super.open();
            }
        }

        if (clickedIndex >= 0 && clickedIndex < reports.size()) {
            Report selectedReport = reports.get(clickedIndex);
            Player target = player.getServer().getPlayerExact(selectedReport.target());

            if (target == null) {
                player.sendMessage(MessageKeys.OFFLINE_PLAYER.getMessage());
                return;
            }

            if (target.isDead()) {
                player.sendMessage(MessageKeys.DEAD_PLAYER.getMessage());
                return;
            }

            player.teleport(target.getLocation());
            inventory.close();
            if (Bukkit.getPlayerExact(database.getPlayer(selectedReport)) != player) database.claimReport(player, selectedReport);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }

    private static ItemStack createReportItem(@NotNull Report report) {
        ItemStack itemStack = ItemKeys.REPORT_ITEM.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            String displayName = meta.getDisplayName()
                    .replace("{player}", report.player())
                    .replace("{id}", String.valueOf(report.id()));
            meta.setDisplayName(displayName);

            List<String> lore = meta.getLore();
            if (lore != null) {
                List<String> updatedLore = new ArrayList<>();

                lore.forEach(line -> {
                    String updatedLine = line
                            .replace("{target}", report.target())
                            .replace("{reason}", report.reason())
                            .replace("{date}", report.date());
                    updatedLore.add(updatedLine);
                });

                meta.setLore(updatedLore);
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
