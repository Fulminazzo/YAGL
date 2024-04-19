import it.angrybear.yagl.items.BukkitItem
import it.angrybear.yagl.items.DeathAction
import it.angrybear.yagl.items.Item
import it.angrybear.yagl.items.Mobility
import it.angrybear.yagl.items.PersistentItem
import it.angrybear.yagl.items.fields.ItemFlag
import it.angrybear.yagl.wrappers.Enchantment
import it.angrybear.yagl.wrappers.WrapperParser
import org.bukkit.entity.Player

def run = { sender, label, args ->
    if (sender instanceof Player)
        try {
            DeathAction action = DeathAction.valueOf(args[0].toUpperCase())
            Mobility mobility = Mobility.valueOf(args[1].toUpperCase())
            PersistentItem item = PersistentItem.newItem(args[2]).setDeathAction(action).setMobility(mobility)
            try {
                item.setAmount(Integer.valueOf(args[3]))
                        .setDurability(Integer.valueOf(args[4]))
                        .setDisplayName(args[5])
                        .setLore(args[6].split(';'))
                        .addEnchantments(Arrays.stream(args[7].split(';'))
                                .map(a -> WrapperParser.parseWrapperFromString(a, Enchantment))
                                .toArray(Enchantment[]::new))
                        .addItemFlags(Arrays.stream(args[8].split(';'))
                                .map(a -> ItemFlag.valueOf(a.toUpperCase()))
                                .toArray(ItemFlag[]::new))
                        .setUnbreakable(Boolean.valueOf(args[9]))
                        .setCustomModelData(Integer.valueOf(args[10]))
            } catch (IndexOutOfBoundsException ignored) {

            }
            sender.getInventory().addItem(item.copy(BukkitItem).create())
        } catch (NumberFormatException ignored) {

        } catch (IndexOutOfBoundsException ignored) {
            sender.sendMessage('Usage: /givepersistentitem <death-action> <mobility> <material> <amount> <durability> <name> <lore> <enchantments> <item-flags> <unbreakable> <custom-model-data>')
            sender.sendMessage('At least death-action, mobility and material are required!')
        }
    else sender.sendMessage('Console cannot execute this command!')
}