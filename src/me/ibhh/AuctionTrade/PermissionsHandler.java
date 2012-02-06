/**
 * 
 */
package me.ibhh.AuctionTrade;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * @author Simon
 * 
 */
public class PermissionsHandler {
	private AuctionTrade auTrade;
	public PermissionsHandler(AuctionTrade AuctTrade) {
		auTrade = AuctTrade;
	}
	public boolean checkpermissions(CommandSender sender , String action){
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!action.isEmpty()) {
				if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
					try {
						if (player.hasPermission(action)) {
							return true;
						} // if(permissions.has(player, "AuctionTrade." + action))
						else {
							player.sendMessage(AuctionTrade.Prefix + auTrade.getConfig().getString("permissions.error." + auTrade.getConfig().getString("language")));
							return false;
						}
					} catch (Exception e) {
						System.out.println("[AuctionTrade] " + "Error on checking permissions with BukkitPermissions!");
						player.sendMessage("[AuctionTrade] " + "Error on checking permissions with BukkitPermissions!");
						e.printStackTrace();
						return false;
					}
				} else {
					if (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
						try {
							PermissionManager permissions = PermissionsEx.getPermissionManager();
							// Permission check
							if (permissions.has(player, "AuctionTrade." + action)) {
								// yay!
								return true;
							} else {
								// houston, we have a problem :)
                                                                player.sendMessage(AuctionTrade.Prefix + auTrade.getConfig().getString("permissions.error." + auTrade.getConfig().getString("language")));
								return false;
							}
						} catch (Exception e) {
							System.out.println("[AuctionTrade] " + "Error on checking permissions with PermissionsEx!");
							player.sendMessage("[AuctionTrade] " + "Error on checking permissions with PermissionsEx!");
							e.printStackTrace();
							return false;
						}
					} else {
						System.out.println("PermissionsEx plugin are not found.");
						return false;
					}
				}
			} else {
				return false;
			}
		} else {
			System.out.println("[AuctionTrade] " + (auTrade.getConfig().getString("command.error.noplayer" + auTrade.getConfig().getString("language"))));
			return false;
		}
	}
}
