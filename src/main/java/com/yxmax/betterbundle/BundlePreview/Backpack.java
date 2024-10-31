package com.yxmax.betterbundle.BundlePreview;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Backpack {
    private int size;
    private Map<Integer, ItemStack> items;
    private UUID id;

    public Backpack(int var4, UUID var5) {
        this.items = new HashMap();
        this.size = var4;
        this.id = var5;
    }

    public Map<Integer, ItemStack> getItems() {
        return Collections.unmodifiableMap(this.items);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setItems(Map<Integer, ItemStack> items) {
        this.items = items;
    }

    public int getSize() {
        return this.size;
    }
}
