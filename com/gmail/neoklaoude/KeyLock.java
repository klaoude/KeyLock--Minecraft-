package com.gmail.neoklaoude;
/**
 *
 * @author Klaoude
 */
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KeyLock extends JavaPlugin implements Listener{
    
    public boolean lock;
    ItemStack tripwire = new ItemStack(Material.TRIPWIRE_HOOK, 1);
    String ConfigStr = "KeyLock.Chests.";
    
    @Override
    public void onEnable() {
        loadConfiguration();
        reloadConfig();
        getLogger().info("[KeyLock] Loaded !");
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        
        Server server = this.getServer();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("[KeyLock] Disabled !");
    }
    
    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && lock) {
            if(event.getClickedBlock().getType() == Material.CHEST) {
                String posChestX = 
                        String.valueOf(event.getClickedBlock().getLocation().getX());
                String posChestY = 
                        String.valueOf(event.getClickedBlock().getLocation().getY());
                String posChestZ = 
                        String.valueOf(event.getClickedBlock().getLocation().getZ());
                
                String[] listPos = {
                        posChestX,
                        posChestY,
                        posChestZ
                    };
                
                String[] playerStr = {event.getPlayer().getName()};
                
                String keyName = "key of " + playerName;
                List<String> keyLore = new ArrayList<>();
                keyLore.add("X : " + posChestX);
                keyLore.add("Y : " + posChestY);
                keyLore.add("Z : " + posChestZ);
                
                if(player.getItemInHand().equals(tripwire)) {
                    player.sendMessage("Your chest have been safe with this key !");
                    
                    String Itemname = "key of " + player.getName().toString();
                    player.getInventory().remove(tripwire);
                    player.getInventory().addItem(
                            setName(new ItemStack(Material.TRIPWIRE_HOOK), 
                                    Itemname, 
                                    keyLore
                            )
                    ); 
                    
                    getConfig().addDefault(ConfigStr + playerName + ".chest", listPos);
                    getConfig().addDefault(
                            ConfigStr + playerName + ".key", "key of " + playerName
                    );
                    getConfig().set(ConfigStr + "Player", playerStr);
                    saveConfig();
                }
                else if(player.getItemInHand().equals(setName(
                        new ItemStack(Material.TRIPWIRE_HOOK),
                        "key of " + playerName,
                        keyLore
                ))) {
                    List<String> playerstr = getConfig().getStringList(ConfigStr);
                    player.sendMessage(playerstr.toString());
                }
                else
                    player.sendMessage(getConfig().getString(ConfigStr));
            } else {
                player.sendMessage("can lock this block");
            }
            lock = false;                
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String label,
                             String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("key")){
            if(args[0].equalsIgnoreCase("lock")) {
                lock = true;
                sender.sendMessage("lock on !");
            }                
            else
                sender.sendMessage("Bitch " + args[0]);
        }
        return false;
    }
    
    private ItemStack setName(ItemStack is, String name, List<String> lore)
    {
        ItemMeta im = is.getItemMeta();
        if(name != null)
            im.setDisplayName(name);
        if(lore != null)
            im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }
}
