package coma112.creports.menu;

import coma112.creports.enums.keys.ConfigKeys;
import coma112.creports.item.IItemBuilder;
import coma112.creports.utils.MenuUtils;
import lombok.Getter;


public abstract class PaginatedMenu extends Menu {
    protected int page = 0;
    @Getter
    protected int maxItemsPerPage = ConfigKeys.MENU_SIZE.getInt() - 2;
    protected int index = 0;

    public PaginatedMenu(MenuUtils menuUtils) {
        super(menuUtils);
    }

    public void addMenuBorder(){
        inventory.setItem(ConfigKeys.BACK_SLOT.getInt(), IItemBuilder.createItemFromSection("menu.back-item"));
        inventory.setItem(ConfigKeys.FORWARD_SLOT.getInt(), IItemBuilder.createItemFromSection("menu.forward-item"));
    }
}

