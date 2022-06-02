package eChinBot;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
 
import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills.Skills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.script.Script;
import simple.robot.utils.ScriptUtils;

@ScriptManifest(author = "Nate/Trester/Esmaabi", category = Category.HUNTER, description = "Start near chins with boxes in inv, rework by Trester & Esmaabi",
discord = "Nathan#6809 | Loreen#4582 | Esmaabi#5752", name = "Amazing Chins v3", servers = { "Zaros, OSRSPS" }, version = "3")

public class eMain extends Script{

public final int BOX_TRAP_ITEM = 10008;
public WorldPoint startingTile;
public DecimalFormat formatter= new DecimalFormat("#,###,###,###");
public String status;
public int startExperience;
public int chinsGained;
public long startTime;
public long timeRan;

private WorldPoint[] locs;

@Override
public void paint(Graphics g) {
	timeRan = System.currentTimeMillis() - startTime;
	g.setColor(Color.BLACK);
    g.fillRect(5, 230, 200, 70);
	g.setColor(Color.ORANGE);
    g.drawRect(5, 230, 200, 70);
    g.drawString("Amazing Chins v3", 7, 245);
    g.drawString("Time Ran: " + ft(timeRan), 7, 260);
    g.drawString("Exp: " + formatter.format(this.getGainedXP()) + " (" + formatter.format(ScriptUtils.getValuePerHour(startTime, System.currentTimeMillis(), getGainedXP())) + ")", 7, 275);
    g.drawString("Chins: " + formatter.format(this.getGainedChins()) + " (" + formatter.format(ScriptUtils.getValuePerHour(startTime, System.currentTimeMillis(), getGainedChins())) + ")", 7, 290);

}

@Override
public void onChatMessage(ChatMessage arg0) {
	// TODO Auto-generated method stub

}

@Override
public void onExecute() {
	//ctx.updateStatus("Starting Perfect Chin Catcher");
	startTime = System.currentTimeMillis();
	chinsGained = 0;
	startExperience = ctx.skills.experience(Skills.HUNTER);
	startingTile = ctx.players.getLocal().getLocation();
	int p = ctx.players.getLocal().getLocation().getPlane();
	locs = new WorldPoint[] {
			new WorldPoint(startingTile.getX(), startingTile.getY(), p),
			new WorldPoint(startingTile.getX() - 1, startingTile.getY() + 1, p),
			new WorldPoint(startingTile.getX() + 1, startingTile.getY() + 1, p),
			new WorldPoint(startingTile.getX() + 1, startingTile.getY() - 1, p),
			new WorldPoint(startingTile.getX() - 1, startingTile.getY() - 1, p)
	};
}

@Override
public void onProcess() {
	if(ctx.players.getLocal().getAnimation() != -1){
		ctx.onCondition(() -> ctx.players.getLocal().getAnimation() == -1, 200,10);
		ctx.sleep(500);
		return;
	}
	//ctx.updateStatus("Scanning for Traps");
	SimpleGroundItem floorTrap = ctx.groundItems.populate().filter(BOX_TRAP_ITEM).nearest().next();
	if(floorTrap != null && floorTrap.validateInteractable()){
		//ctx.updateStatus("Picking up broken trap");
		int trapAm = ctx.inventory.populate().filter(BOX_TRAP_ITEM).population();
		if(floorTrap.click("lay")){
			ctx.sleep(1000);
			ctx.onCondition(() -> ctx.inventory.populate().filter(BOX_TRAP_ITEM).population() > trapAm,250,5);
		}
		return;
	}
	WorldPoint trapTile = getAvailableTrapLocation();
	if(placedTraps() != trapAmount() && trapTile != null) {
		//ctx.updateStatus("Trap is in inventory");
		SimpleItem invTrap = ctx.inventory.populate().filter(BOX_TRAP_ITEM).next();

		if(invTrap != null && trapTile != null){

			if(!ctx.players.getLocal().getLocation().equals(trapTile)){
				//ctx.updateStatus("Walking to available trap spot");
				ctx.pathing.step(trapTile.getX(),trapTile.getY());
				ctx.onCondition(()-> ctx.players.getLocal().getLocation().equals(trapTile),200,10);
			}
			if(ctx.players.getLocal().getLocation().equals(trapTile)){
				//ctx.updateStatus("Setting up trap");
				setupTrap(invTrap, trapTile);
			}
		} else {
			//System.out.println("Shit is nulled");
		}
	} else {
	//	//ctx.updateStatus("Scanning ground objects for traps");
		SimpleObject trap = ctx.objects.populate().filter(9382,9385,9384).filter(t -> objectInLocation(t.getLocation())).next();
		if(trap != null && trap.validateInteractable()){
		//	//ctx.updateStatus("Resetting traps");
			int chins = getChinCount();
			if(trap.click("Reset")){
				if(trap.getName().toLowerCase().equals("shaking box") && ctx.onCondition(()-> getChinCount() > chins,250,10)){
					chinsGained++;
				}
				ctx.onCondition(()-> !trapExistsForTile(trap.getLocation()));
			}
		}
	}
}

private boolean objectInLocation(WorldPoint w)
{
	for (int i = 0; i < locs.length; i++) {
		if (w.distanceTo(locs[i]) == 0) {
			return true;
		}
	}
	return false;
}

public WorldPoint getAvailableTrapLocation(){
	for (int i = 0; i < trapAmount(); i++) {
		WorldPoint loc = locs[i];
		if (!trapExistsForTile(loc)) {
			return loc;
		}
	}
	return null;
}

public int trapAmount() {
	final int level = ctx.skills.level(Skills.HUNTER);
	if (level >= 80) {
		return 5;
	} else if (level >= 60) {
		return 4;
	} else if (level >= 40) {
		return 3;
	} else if (level >= 20) {
		return 2;
	}
	return 1;
}

public int getGainedXP() {
	return this.ctx.skills.experience(Skills.HUNTER) - startExperience;
}

public int getGainedChins() {
	return chinsGained;
}

public int getChinCount() {
	return ctx.inventory.populate().filter("chinchompa", "red chinchompa", "black chinchompa", "crystal chinchompa").population(true);
}

public void setupTrap(final SimpleItem invTrap, final WorldPoint tile) {
	if (invTrap.click(1)) {
		ctx.sleepCondition(() -> this.trapExistsForTile(tile));
	}
}

public boolean trapExistsForTile(final WorldPoint tile) {
	return !ctx.objects.populate().filter(9380, 9382,9385,9384).filter(tile).isEmpty();
}

public int placedTraps() {
	int count = 0;
	for (int i = 0; i < trapAmount(); i++) {
		WorldPoint loc = locs[i];
		if (trapExistsForTile(loc)) {
			count++;
		}
	}
	return count;
}

@Override
public void onTerminate() {
	// TODO Auto-generated method stub

}

private String ft(long duration) {
	String res = "";
	long days = TimeUnit.MILLISECONDS.toDays(duration);
	long hours = TimeUnit.MILLISECONDS.toHours(duration)
			- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
	long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
			- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
					.toHours(duration));
	long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
			- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
					.toMinutes(duration));
	if (days == 0) {
		res = (hours + ":" + minutes + ":" + seconds);
	} else {
		res = (days + ":" + hours + ":" + minutes + ":" + seconds);
	}
	return res;

}

}