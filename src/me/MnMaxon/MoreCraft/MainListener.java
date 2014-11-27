package me.MnMaxon.MoreCraft;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class MainListener implements Listener {
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		YamlConfiguration cfg = Config.Load(Main.dataFolder + "/Config.yml");
		if (cfg.get(e.getRecipe().getResult().getType().name()) == null) {
			cfg.set(e.getRecipe().getResult().getType().name(), e.getRecipe().getResult().getAmount());
			Config.Save(cfg, Main.dataFolder + "/Config.yml");
		}
	}
}
