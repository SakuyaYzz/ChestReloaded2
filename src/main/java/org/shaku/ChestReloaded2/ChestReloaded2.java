package org.shaku.ChestReloaded2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ChestReloaded2 extends JavaPlugin {
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.saveDefaultConfig();

        getCommand("bxr").setExecutor(new ChestCommand(this));
        Bukkit.getPluginManager().registerEvents(new ChestListener(this), this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    // ==================== 内部类 ConfigManager ====================
    public static class ConfigManager {
        private final JavaPlugin plugin;
        private FileConfiguration config;

        public ConfigManager(JavaPlugin plugin) {
            this.plugin = plugin;
            reloadConfig();
        }

        public void reloadConfig() {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }

        public void saveDefaultConfig() {
            plugin.saveDefaultConfig();
        }

        public String getChestName(String dungeon) {
            return config.getString("dungeons." + dungeon + ".chest-name", "&a" + dungeon + "宝箱");
        }

        public List<String> getChestLore(String dungeon, String level) {
            List<String> lore = config.getStringList("dungeons." + dungeon + ".levels." + level + ".lore");
            return lore.isEmpty() ? getDefaultLore() : lore;
        }

        private List<String> getDefaultLore() {
            return Arrays.asList(
                    "&e- 右键打开宝箱 -",
                    "&f==========&6&l宝箱信息&f==========",
                    "&7副本: &a%dungeon%",
                    "&7等级: &a%level%",
                    "&f========================"
            );
        }

        public List<String> getAttributes(String dungeon) {
            return config.getStringList("dungeons." + dungeon + ".attributes");
        }

        public int getBaseValue(String dungeon) {
            return config.getInt("dungeons." + dungeon + ".base-value");
        }

        public String getDescription(String dungeon, String level) {
            return config.getString("dungeons." + dungeon + ".levels." + level + ".description", "");
        }

        public List<String> getWeaponAttributes(String dungeon) {
            return config.getStringList("dungeons." + dungeon + ".weapon-attributes");
        }

        public List<String> getArmorAttributes(String dungeon) {
            return config.getStringList("dungeons." + dungeon + ".armor-attributes");
        }

        public boolean showAttributes(String dungeon) {
            return config.getBoolean("dungeons." + dungeon + ".show-attributes", true);
        }

        public List<String> getRewardDescription(String dungeon, String level) {
            return config.getStringList("dungeons." + dungeon + ".levels." + level + ".reward-description");
        }

        // 新增：获取品质符号
        public String getQualitySymbol(String dungeon) {
            return config.getString("dungeons." + dungeon + ".quality-symbol", "ো");
        }
    }

    // ==================== 内部类 ItemBuilder ====================
    public static class ItemBuilder {
        private static final Map<Material, String> CHINESE_NAMES = new HashMap<>();

        static {
            CHINESE_NAMES.put(Material.DIAMOND_SWORD, "之剑");
            CHINESE_NAMES.put(Material.DIAMOND_HELMET, "头盔");
            CHINESE_NAMES.put(Material.DIAMOND_CHESTPLATE, "盔甲");
            CHINESE_NAMES.put(Material.DIAMOND_LEGGINGS, "护腿");
            CHINESE_NAMES.put(Material.DIAMOND_BOOTS, "之靴");
        }

        public static ItemStack buildChest(ConfigManager config, String dungeon, String level, int amount) {
            ItemStack chest = new ItemStack(Material.CHEST, amount);
            ItemMeta meta = chest.getItemMeta();

            String displayName = ChatColor.translateAlternateColorCodes('&',
                    "&a" + getChineseLevel(level) + config.getChestName(dungeon));
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            String chineseLevel = getChineseLevel(level);
            for (String line : config.getChestLore(dungeon, level)) {
                String processed = line
                        .replace("%dungeon%", dungeon)
                        .replace("%level%", chineseLevel);
                lore.add(ChatColor.translateAlternateColorCodes('&', processed));
            }

            lore.add(ChatColor.MAGIC + "ChestData:" + dungeon + ":" + level);
            meta.setLore(lore);

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            chest.setItemMeta(meta);

            return chest;
        }

        public static ItemStack generateItem(ConfigManager config, String dungeon, String level) {
            Random rand = new Random();

            int baseValue = config.getBaseValue(dungeon);
            int minValue = getMinValueByLevel(level, baseValue);
            int value1 = rand.nextInt(5) + minValue;
            int value2 = rand.nextInt(5) + minValue;

            Material material;
            boolean isWeapon;
            int type = rand.nextInt(5);
            switch (type) {
                case 0:
                    material = Material.DIAMOND_SWORD;
                    isWeapon = true;
                    break;
                case 1:
                    material = Material.DIAMOND_HELMET;
                    isWeapon = false;
                    break;
                case 2:
                    material = Material.DIAMOND_CHESTPLATE;
                    isWeapon = false;
                    break;
                case 3:
                    material = Material.DIAMOND_LEGGINGS;
                    isWeapon = false;
                    break;
                default:
                    material = Material.DIAMOND_BOOTS;
                    isWeapon = false;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            String prefix = (value1 < 25 || value2 < 25) ? "&a破碎的" : "";
            String chineseType = CHINESE_NAMES.getOrDefault(material, "装备");
            String displayName = prefix + "&a" + dungeon + chineseType;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

            // ------------------- 构建 Lore -------------------
            List<String> lore = new ArrayList<>();

            // 1. 两行介绍
            List<String> desc = config.getRewardDescription(dungeon, level);
            if (desc != null && !desc.isEmpty()) {
                int count = 0;
                for (String line : desc) {
                    if (count >= 2) break;
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                    count++;
                }
            }

            // 2. 品质行（使用配置的符号，白色）
            String symbol = config.getQualitySymbol(dungeon);
            lore.add(ChatColor.WHITE + symbol);

            // 3. 空行
            lore.add("");

            // 4. 属性栏
            boolean showAttributes = config.showAttributes(dungeon);
            if (showAttributes) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7=======&9&l装备属性&7======="));

                List<String> attributes = isWeapon ?
                        config.getWeaponAttributes(dungeon) :
                        config.getArmorAttributes(dungeon);

                for (String attr : attributes) {
                    String formatted = attr
                            .replace("%value1%", String.valueOf(value1))
                            .replace("%value2%", String.valueOf(value2 / 3))
                            .replace("%dungeon%", dungeon);
                    lore.add(ChatColor.translateAlternateColorCodes('&', formatted));
                }
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }

        private static int getMinValueByLevel(String level, int base) {
            switch (level.toUpperCase()) {
                case "LOW":
                    return base;
                case "NORMAL":
                    return base + 5;
                case "HIGH":
                    return base + 10;
                case "TOP":
                    return base + 15;
                default:
                    return base;
            }
        }

        private static String getChineseLevel(String level) {
            switch (level.toUpperCase()) {
                case "LOW":
                    return "低级的";
                case "NORMAL":
                    return "普通的";
                case "HIGH":
                    return "高级的";
                case "TOP":
                    return "顶级的";
                default:
                    return "";
            }
        }
    }

    // ==================== 内部类 ChestCommand ====================
    public static class ChestCommand implements CommandExecutor {
        private final ChestReloaded2 plugin;
        private static final String PERMISSION = "chestreloaded.admin";

        public ChestCommand(ChestReloaded2 plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!sender.hasPermission(PERMISSION)) {
                sender.sendMessage("§c你没有权限使用此命令!");
                return true;
            }

            if (args.length < 3 || !args[0].equalsIgnoreCase("get")) {
                sendUsage(sender);
                return true;
            }

            String dungeon = args[1];
            Player targetPlayer;
            String level;
            int amount = 1;

            try {
                if (args.length >= 4 && Bukkit.getPlayer(args[2]) != null) {
                    targetPlayer = Bukkit.getPlayer(args[2]);
                    level = args[3].toUpperCase();
                    if (args.length >= 5) amount = Integer.parseInt(args[4]);
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§c控制台使用必须指定玩家: /bxr get <副本> <玩家> <等级> [数量]");
                        return true;
                    }
                    targetPlayer = (Player) sender;
                    level = args[2].toUpperCase();
                    if (args.length >= 4) amount = Integer.parseInt(args[3]);
                }

                if (!isValidLevel(level)) {
                    sender.sendMessage("§c无效的宝箱等级! 可用等级: LOW, NORMAL, HIGH, TOP");
                    return true;
                }

                ItemStack chest = ItemBuilder.buildChest(
                        plugin.getConfigManager(),
                        dungeon,
                        level,
                        amount
                );

                targetPlayer.getInventory().addItem(chest);
                sender.sendMessage("§a成功给予 " + amount + " 个" + dungeon + "宝箱给 " + targetPlayer.getName());

            } catch (NumberFormatException e) {
                sender.sendMessage("§c数量参数必须是数字!");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c错误: " + e.getMessage());
            }
            return true;
        }

        private boolean isValidLevel(String level) {
            return level.equals("LOW") || level.equals("NORMAL")
                    || level.equals("HIGH") || level.equals("TOP");
        }

        private void sendUsage(CommandSender sender) {
            sender.sendMessage("§6用法:");
            sender.sendMessage("§a/bxr get <副本名> <等级> [数量] §7- 给自己宝箱");
            sender.sendMessage("§a/bxr get <副本名> <玩家> <等级> [数量] §7- 给其他玩家宝箱");
        }
    }

    // ==================== 内部类 ChestListener ====================
    public static class ChestListener implements Listener {
        private final ChestReloaded2 plugin;

        public ChestListener(ChestReloaded2 plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onChestClick(PlayerInteractEvent event) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                    event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            ItemStack item = event.getItem();
            if (item == null || item.getType() != Material.CHEST) return;

            if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return;

            List<String> lore = item.getItemMeta().getLore();
            for (String line : lore) {
                if (line.startsWith(ChatColor.MAGIC + "ChestData:")) {
                    event.setCancelled(true);
                    String data = line.replace(ChatColor.MAGIC + "ChestData:", "");
                    String[] parts = data.split(":");
                    openChest(event.getPlayer(), parts[0], parts[1]);

                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        event.getPlayer().getInventory().setItemInMainHand(null);
                    }
                    break;
                }
            }
        }

        private void openChest(Player player, String dungeon, String level) {
            ItemStack reward = ItemBuilder.generateItem(
                    plugin.getConfigManager(),
                    dungeon,
                    level
            );
            player.getInventory().addItem(reward);
            player.sendMessage(ChatColor.GREEN + "成功打开宝箱!");
        }
    }
}