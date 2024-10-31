package com.yxmax.betterbundle.BundlePreview;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.yxmax.betterbundle.BetterBundle.isBelow1122;

public class BackpackContent {
    private List<BackpackContent.InventorySlot> populatedSlots = new ArrayList();
    private int slotAmount;

    public BackpackContent(Backpack var4) {
        this.slotAmount = var4.getSize();
        var4.getItems().forEach((var2x, var3x) -> {
            this.populatedSlots.add(new BackpackContent.InventorySlot(var3x, var2x));
        });
    }

    public List<InventorySlot> getPopulatedSlots() {
        return this.populatedSlots;
    }

    public int getSlotAmount() {
        return this.slotAmount;
    }

    public void setPopulatedSlots(List<BackpackContent.InventorySlot> var1) {
        this.populatedSlots = var1;
    }

    public void setSlotAmount(int var1) {
        this.slotAmount = var1;
    }

    public static class InventorySlot {
        private int index;
        private short id;
        private String itemName;
        private int amount;
        private int durability;
        private boolean enchanted;
        private int customModelData;

        public InventorySlot(ItemStack var2, int var3) {
            this.itemName = var2.getType().toString();
            this.amount = var2.getAmount();
            this.durability = var2.getDurability();
            this.index = var3;
            this.enchanted = var2.getEnchantments().size() > 0;
            this.customModelData = 0;
            if(var2.getItemMeta().hasCustomModelData()){
                this.customModelData = var2.getItemMeta().getCustomModelData();
            }

            this.setId(var2);
        }

        private void setId(ItemStack var2) {
            if (isBelow1122) {
                this.id = var2.getDurability();
            }

        }

        public int getIndex() {
            return this.index;
        }

        public int getId() {
            return this.id;
        }

        public String getItemName() {
            return this.itemName;
        }

        public int getAmount() {
            return this.amount;
        }

        public int getDurability() {
            return this.durability;
        }

        public boolean isEnchanted() {
            return this.enchanted;
        }

        public int getCustomModelData() {
            return this.customModelData;
        }

        public void setIndex(int var1) {
            this.index = var1;
        }

        public void setId(short var1) {
            this.id = var1;
        }

        public void setItemName(String var1) {
            this.itemName = var1;
        }

        public void setAmount(int var1) {
            this.amount = var1;
        }

        public void setDurability(int var1) {
            this.durability = var1;
        }

        public void setEnchanted(boolean var1) {
            this.enchanted = var1;
        }

        public void setCustomModelData(int var1) {
            this.customModelData = var1;
        }

        public boolean equals(Object var1) {
            if (var1 == this) {
                return true;
            } else if (!(var1 instanceof BackpackContent.InventorySlot)) {
                return false;
            } else {
                BackpackContent.InventorySlot var2 = (BackpackContent.InventorySlot)var1;
                if (!var2.canEqual(this)) {
                    return false;
                } else if (this.getIndex() != var2.getIndex()) {
                    return false;
                } else if (this.getId() != var2.getId()) {
                    return false;
                } else if (this.getAmount() != var2.getAmount()) {
                    return false;
                } else if (this.getDurability() != var2.getDurability()) {
                    return false;
                } else if (this.isEnchanted() != var2.isEnchanted()) {
                    return false;
                } else if (this.getCustomModelData() != var2.getCustomModelData()) {
                    return false;
                } else {
                    String var3 = this.getItemName();
                    String var4 = var2.getItemName();
                    if (var3 == null) {
                        if (var4 != null) {
                            return false;
                        }
                    } else if (!var3.equals(var4)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object var1) {
            return var1 instanceof BackpackContent.InventorySlot;
        }

        public int hashCode() {
            boolean var1 = true;
            byte var2 = 1;
            int var4 = var2 * 59 + this.getIndex();
            var4 = var4 * 59 + this.getId();
            var4 = var4 * 59 + this.getAmount();
            var4 = var4 * 59 + this.getDurability();
            var4 = var4 * 59 + (this.isEnchanted() ? 79 : 97);
            var4 = var4 * 59 + this.getCustomModelData();
            String var3 = this.getItemName();
            var4 = var4 * 59 + (var3 == null ? 43 : var3.hashCode());
            return var4;
        }

        public String toString() {
            return "BackpackContent.InventorySlot(index=" + this.getIndex() + ", id=" + this.getId() + ", itemName=" + this.getItemName() + ", amount=" + this.getAmount() + ", durability=" + this.getDurability() + ", enchanted=" + this.isEnchanted() + ", customModelData=" + this.getCustomModelData() + ")";
        }
    }

}
