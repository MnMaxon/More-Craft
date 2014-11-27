package me.MnMaxon.MoreCraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	public static String dataFolder;
	public static Main plugin;

	@Override
	public void onEnable() {
		plugin = this;
		dataFolder = this.getDataFolder().getAbsolutePath();
		setupConfig();
		getServer().getPluginManager().registerEvents(new MainListener(), this);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				updateRecipies();
			}
		}, 1L);
	}

	public static YamlConfiguration setupConfig() {
		cfgSetter("ARROW", 16);
		cfgSetter("TNT", 2);
		return Config.Load(dataFolder + "/Config.yml");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
			return false;
		}
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("name")) {
			if (p.getItemInHand() != null)
				p.sendMessage(ChatColor.RED + p.getItemInHand().getType().name());
		} else if (cmd.getName().equalsIgnoreCase("MCReload"))
			if (p.isOp())
				updateRecipies();
		return false;
	}

	public static void cfgSetter(String path, Object value) {
		YamlConfiguration cfg = Config.Load(dataFolder + "/Config.yml");
		if (cfg.get(path) == null) {
			cfg.set(path, value);
			Config.Save(cfg, dataFolder + "/Config.yml");
		}
	}

	public static void updateRecipies() {
		YamlConfiguration cfg = Config.Load(dataFolder + "/Config.yml");
		// Bukkit.resetRecipes();
		Set<String> rawKeys = cfg.getKeys(true);
		Map<String, Integer> keys = new HashMap<String, Integer>();
		for (String key : rawKeys)
			keys.put(key, cfg.getInt(key));
		Bukkit.getServer().recipeIterator();
		Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
		Bukkit.getServer().recipeIterator();
		ArrayList<Recipe> toAdd = new ArrayList<Recipe>();
		while (iterator.hasNext()) {
			Recipe recipe = iterator.next();
			if (keys.containsKey(recipe.getResult().getType().name())) {
				ItemStack is = recipe.getResult();
				is.setAmount(keys.get(recipe.getResult().getType().name()));
				Bukkit.broadcastMessage("1) " + is.getAmount());
				if (recipe instanceof ShapedRecipe) {
					ShapedRecipe rep = new ShapedRecipe(is);
					Bukkit.broadcastMessage("2) " + rep.getResult().getAmount());
					rep = rep.shape(((ShapedRecipe) recipe).getShape());
					Bukkit.broadcastMessage("3) " + rep.getResult().getAmount());
					for (Entry<Character, ItemStack> entry : ((ShapedRecipe) recipe).getIngredientMap().entrySet())
						if (entry.getKey() != null && entry.getValue() != null)
							rep.setIngredient(entry.getKey(), new MaterialData(entry.getValue().getType()));
					toAdd.add(rep);
				} else if (recipe instanceof ShapelessRecipe) {
					ShapelessRecipe rep = new ShapelessRecipe(is);
					Iterator<ItemStack> iterator1 = ((ShapelessRecipe) recipe).getIngredientList().iterator();
					while (iterator1.hasNext())
						rep.addIngredient(iterator1.next().getData());
					toAdd.add(rep);
				}
			} else
				toAdd.add(recipe);
		}
		Bukkit.clearRecipes();
		for (Recipe toAddrec : toAdd)
			Bukkit.addRecipe(toAddrec);
	}
}