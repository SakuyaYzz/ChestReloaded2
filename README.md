# ChestReloaded2

> **Paper 1.21.4 宝箱开箱插件** — 支持多副本、多等级、自定义奖励描述与品质符号。  
> **A Paper 1.21.4 chest-opening plugin** — configurable dungeons, tiers, reward descriptions and quality symbols.

---

## 中文

### 📖 简介

**ChestReloaded2** 是一款适用于 **Paper 1.21.4+** 的宝箱开箱插件。  
管理员可通过命令发放不同副本、不同等级的宝箱；玩家右键宝箱即可随机开出钻石装备，并获得对应属性与自定义描述。  
所有副本、等级、Lore、属性与品质符号均可在 `config.yml` 中自由配置。

### ✨ 功能特性

- 支持多个副本（如：酒店、沙漠神殿、末地地牢）
- 每个副本支持四个等级：`LOW`、`NORMAL`、`HIGH`、`TOP`
- 宝箱可指定数量发放给指定玩家
- 开出物品为随机钻石装备（剑、头盔、胸甲、护腿、靴子）
- 装备属性值根据副本 `base-value` 与等级动态随机
- 支持为每个副本单独设置 **品质符号**（如 `ো`、`৊`）
- 支持为每个副本/等级自定义 **两行奖励描述**
- 物品 Lore 结构清晰：介绍 → 品质符号 → 空行 → 属性栏
- 宝箱物品带有隐藏数据，右键自动识别并开箱，防止误放置
- 完全兼容 Paper 1.21.4 API

### 🎮 命令与权限

| 命令 | 说明 |
|------|------|
| `/bxr get <副本名> <等级> [数量]` | 给自己发放宝箱 |
| `/bxr get <副本名> <玩家> <等级> [数量]` | 给指定玩家发放宝箱 |

- **权限**：`chestreloaded.admin`（默认 OP）
- **等级**：`LOW`、`NORMAL`、`HIGH`、`TOP`

### ⚙️ 配置文件说明

`config.yml` 主要结构如下：

```yaml
dungeons:
  副本名称:
    base-value: 15                 # 基础属性值
    chest-name: "酒店宝箱"          # 宝箱显示名称
    quality-symbol: "ো"            # 开出物品的品质符号（白色）
    show-attributes: true          # 是否显示属性栏
    weapon-attributes:             # 武器属性模板
      - "&8[*]&7攻击力增加&c+%value1%"
      - "&8[*]&7暴击率增加&c+%value2%"
    armor-attributes:              # 防具属性模板
      - "&8[*]&7生命力增加&c+%value1%"
      - "&8[*]&7防御值增加&c+%value2%"
    levels:
      LOW:
        description: "低级品质的酒店宝箱"
        lore:                      # 宝箱物品的 Lore
          - "&e- 右键打开宝箱 -"
          - "&7副本: &a%dungeon%"
          - "&7等级: &a%level%"
        reward-description:        # 开出物品的两行介绍
          - "&7这间酒店开业时埋下的陈酿"
          - "&7如今只剩空瓶与泛黄的回忆"
      NORMAL: { ... }
      HIGH: { ... }
      TOP: { ... }
```

- `%dungeon%`、`%level%`、`%value1%`、`%value2%` 为占位符，插件会自动替换。
- `quality-symbol` 支持任意 Unicode 字符，未配置时默认使用 `ো`。
- `reward-description` 最多读取两行，显示在开出物品 Lore 的前两行。

### 📦 安装与使用

1. 确保服务器为 **Paper 1.21.4+**，Java 21。
2. 下载或构建 `ChestReloaded2.jar`，放入 `plugins/` 文件夹。
3. 启动服务器，生成默认 `config.yml`。
4. 根据需求编辑 `config.yml`，重启或重载插件。
5. 使用 `/bxr get ...` 发放宝箱，玩家右键即可开箱。

### 🛠️ Gradle 构建

在项目根目录创建 `build.gradle`：

```gradle
plugins {
    id 'java'
}

group = 'org.shaku'
version = '1.0'

repositories {
    mavenCentral()
    maven { url = 'https://repo.papermc.io/repository/maven-public/' }
}

dependencies {
    compileOnly 'io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}
```

构建命令：

```bash
./gradlew build
```

生成的 Jar 位于 `build/libs/ChestReloaded2-1.0.jar`。

### ⚠️ 注意事项

- 必须使用 **Paper 1.21.4+** 与 **Java 21**。
- 命令中的副本名必须与 `config.yml` 中的键完全一致（区分中英文与大小写）。
- 请勿手动修改宝箱 Lore 中的隐藏数据行（`ChestData:<副本>:<等级>`），否则无法开箱。
- 品质符号为 Unicode 字符，请确保服务器客户端支持显示。
- 开出物品的属性值随机范围由 `base-value` 和等级共同决定。
- 若背包已满，奖励会掉落在玩家脚下。

### 📄 许可证

本项目使用 MIT 许可证，可自由修改与分发。

---

## English

### 📖 Introduction

**ChestReloaded2** is a chest-opening plugin for **Paper 1.21.4+**.  
Admins can give chests of different dungeons and tiers via commands. Players right-click the chest to receive random diamond gear with attributes and custom descriptions.  
All dungeons, tiers, lore, attributes and quality symbols are configurable in `config.yml`.

### ✨ Features

- Multiple dungeons (e.g. Hotel, Desert Temple, End Dungeon)
- Four tiers per dungeon: `LOW`, `NORMAL`, `HIGH`, `TOP`
- Give chests to yourself or other players with amount
- Rewards are random diamond gear (sword, helmet, chestplate, leggings, boots)
- Attribute values scale with dungeon `base-value` and tier
- Per-dungeon **quality symbol** (e.g. `ো`, `৊`)
- Custom **two-line reward description** per dungeon/tier
- Clear item lore: description → quality symbol → blank line → attributes
- Hidden data in chest item; right-click opens it and prevents placement
- Fully compatible with Paper 1.21.4 API

### 🎮 Commands & Permissions

| Command | Description |
|---------|-------------|
| `/bxr get <dungeon> <tier> [amount]` | Give chest to yourself |
| `/bxr get <dungeon> <player> <tier> [amount]` | Give chest to another player |

- **Permission**: `chestreloaded.admin` (default OP)
- **Tiers**: `LOW`, `NORMAL`, `HIGH`, `TOP`

### ⚙️ Configuration

Main structure of `config.yml`:

```yaml
dungeons:
  DungeonName:
    base-value: 15
    chest-name: "Hotel Chest"
    quality-symbol: "ো"
    show-attributes: true
    weapon-attributes:
      - "&8[*]&7Attack +&c%value1%"
      - "&8[*]&7Crit Rate +&c%value2%"
    armor-attributes:
      - "&8[*]&7Health +&c%value1%"
      - "&8[*]&7Defense +&c%value2%"
    levels:
      LOW:
        description: "Low tier hotel chest"
        lore:
          - "&e- Right-click to open -"
          - "&7Dungeon: &a%dungeon%"
          - "&7Tier: &a%level%"
        reward-description:
          - "&7A vintage bottle buried when the hotel opened"
          - "&7Now only empty glass and faded memories remain"
      NORMAL: { ... }
      HIGH: { ... }
      TOP: { ... }
```

- Placeholders: `%dungeon%`, `%level%`, `%value1%`, `%value2%` are replaced automatically.
- `quality-symbol` supports any Unicode character; defaults to `ো`.
- `reward-description` reads up to two lines, shown at the top of the reward item's lore.

### 📦 Installation & Usage

1. Ensure **Paper 1.21.4+** and **Java 21**.
2. Put `ChestReloaded2.jar` into `plugins/`.
3. Start the server to generate `config.yml`.
4. Edit `config.yml` as needed, then restart/reload.
5. Use `/bxr get ...` to give chests; players right-click to open.

### 🛠️ Building with Gradle

Create `build.gradle`:

```gradle
plugins {
    id 'java'
}

group = 'org.shaku'
version = '1.0'

repositories {
    mavenCentral()
    maven { url = 'https://repo.papermc.io/repository/maven-public/' }
}

dependencies {
    compileOnly 'io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}
```

Build:

```bash
./gradlew build
```

The Jar will be at `build/libs/ChestReloaded2-1.0.jar`.

### ⚠️ Notes

- Requires **Paper 1.21.4+** and **Java 21**.
- Dungeon names in commands must exactly match the keys in `config.yml`.
- Do not modify the hidden `ChestData:<dungeon>:<tier>` line in the chest lore.
- Quality symbols are Unicode; ensure client font support.
- Attribute values are randomly generated based on `base-value` and tier.
- If the inventory is full, rewards will drop on the ground.

### 📄 License

MIT License. Free to modify and distribute.
