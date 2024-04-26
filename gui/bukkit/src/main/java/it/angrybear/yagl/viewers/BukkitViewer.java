package it.angrybear.yagl.viewers;

import it.angrybear.yagl.WrappersAdapter;
import it.angrybear.yagl.wrappers.Sound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link Viewer} for the Bukkit platform.
 */
class BukkitViewer extends Viewer {

    /**
     * Instantiates a new Viewer.
     *
     * @param uniqueId the unique id
     * @param name     the name
     */
    BukkitViewer(final @NotNull UUID uniqueId, final @NotNull String name) {
        super(uniqueId, name);
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        Player player = getPlayer().orElseThrow(() -> new PlayerOfflineException(this.name));
        WrappersAdapter.playCustomSound(player, sound);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Player player = getPlayer().orElseThrow(() -> new PlayerOfflineException(this.name));
        player.sendMessage(message);
    }

    @Override
    public void executeCommand(final @NotNull String command) {
        Player player = getPlayer().orElseThrow(() -> new PlayerOfflineException(this.name));
        Bukkit.dispatchCommand(player, command);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return getPlayer().filter(p -> p.hasPermission(permission)).isPresent();
    }

    @Override
    public void closeGUI() {
        getPlayer().ifPresent(HumanEntity::closeInventory);
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public Optional<Player> getPlayer() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        return Optional.ofNullable(player).filter(OfflinePlayer::isOnline);
    }

    /**
     * Gets an instance of {@link Viewer} from the given {@link Player}.
     *
     * @param player the player
     * @return the viewer
     */
    public static Viewer newViewer(final @NotNull HumanEntity player) {
        return new BukkitViewer(player.getUniqueId(), player.getName());
    }
}
