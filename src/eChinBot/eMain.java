package eChinBot;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.filters.SimpleSkills.Skills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.script.Script;

@ScriptManifest(author = "Nate/Trester/Esmaabi", category = Category.HUNTER, description = "Start near chinchompa hunting spot with boxes in inventory. Script reworked by Trester & Esmaabi",
        discord = "Nathan#6809 | Loreen#4582 | Esmaabi#5752", name = "Amazing Chins v3", servers = {"Zaros, OSRSPS"}, version = "3.4")

public class eMain extends Script {

    public final int BOX_TRAP_ITEM = 10008;
    public WorldPoint startingTile;
    public String status;
    public int chinsGained;
    boolean trapsPickup = false;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private WorldPoint[] locs = null;

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void paint(Graphics g) {

        Color PhilippineRed = new Color(196, 18, 48);
        Color RaisinBlack = new Color(35, 31, 32, 127);
        g.setColor(RaisinBlack);
        g.fillRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawString("Amazing Chins v3", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(Skills.HUNTER);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.HUNTER);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillExpPerHour = (int) ((SkillExpGained * 3600000D) / runTime);
        long ThingsPerHour = (int) (getGainedChins() / ((System.currentTimeMillis() - this.startTime) / 3600000.0D));
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillExpPerHour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Chins caught: " + getGainedChins() + " (" + ThingsPerHour + " chins/h)", 15, 210);
        g.drawString("Status: " + status, 15, 225);


        //highlight next tile where box trap will be interacted with
        if (locs != null && getAvailableTrapLocation() != null) {
            for (int i = 0; i < placedTraps(); i++) {
                if (i < placedTraps()) {
                    ctx.paint.drawTileMatrix((Graphics2D) g, getAvailableTrapLocation(), Color.GREEN);
                }
            }
        }

/*        SimpleObject trap = ctx.objects.populate().filter(9380, 9382, 9385, 9384).filter(t -> objectInLocation(t.getLocation())).next();
		if (locs != null && trap != null) {
				ctx.paint.drawTileMatrix((Graphics2D) g, trap.getLocation(), Color.YELLOW);
		}*/


    }

    @Override
    public void onChatMessage(ChatMessage m) {
        if (m.getMessage() != null) {
            String message = m.getMessage().toLowerCase();
            if (message.contains(ctx.players.getLocal().getName().toLowerCase())) {
                ctx.updateStatus(currentTime() + " Someone asked for you");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            } else if (message.contains("don't have a high enough hunter")) {
                status = "Too many traps!";
                trapsPickup = true;
            } else if (message.contains("can't lay a trap here")) {
                status = "Can't lay here!";
                trapsPickup = true;
            }
        }
    }

    @Override
    public void onExecute() {
        status = "Setting up bot";
        //vars
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(Skills.HUNTER);
        this.startingSkillExp = this.ctx.skills.experience(Skills.HUNTER);
        chinsGained = 0;

        // viewport settings
        ctx.viewport.angle(0);
        ctx.viewport.pitch(true);

        //traps location
        startingTile = ctx.players.getLocal().getLocation();
        int p = ctx.players.getLocal().getLocation().getPlane();
        locs = new WorldPoint[]{
                new WorldPoint(startingTile.getX(), startingTile.getY(), p),
                new WorldPoint(startingTile.getX() - 1, startingTile.getY() + 1, p),
                new WorldPoint(startingTile.getX() + 1, startingTile.getY() + 1, p),
                new WorldPoint(startingTile.getX() + 1, startingTile.getY() - 1, p),
                new WorldPoint(startingTile.getX() - 1, startingTile.getY() - 1, p)
        };
    }

    @Override
    public void onProcess() {
        if (ctx.players.getLocal().getAnimation() != -1) {
            ctx.onCondition(() -> ctx.players.getLocal().getAnimation() == -1, 200, 10);
            ctx.sleep(500);
            return;
        }

        if (!trapsPickup) {

            SimpleGroundItem floorTrap = ctx.groundItems.populate().filter(BOX_TRAP_ITEM).nearest().next();
            if (floorTrap != null && floorTrap.validateInteractable() && !ctx.pathing.inMotion() && !ctx.players.getLocal().isAnimating()) {
                status = "Laying trap";
                int trapAm = ctx.inventory.populate().filter(BOX_TRAP_ITEM).population();
                if (floorTrap.click("Lay", "Box trap")) {
                    ctx.sleep(1000);
                    ctx.onCondition(() -> ctx.inventory.populate().filter(BOX_TRAP_ITEM).population() > trapAm, 1200);
                }
                return;
            }
            WorldPoint trapTile = getAvailableTrapLocation();
            if (placedTraps() != trapAmount() && trapTile != null) {
                status = "Checking traps in inventory";
                SimpleItem invTrap = ctx.inventory.populate().filter(BOX_TRAP_ITEM).next();

                if (invTrap != null && trapTile != null) {

                    if (!ctx.players.getLocal().getLocation().equals(trapTile)) {
                        status = "Walking to next spot";
                        ctx.pathing.step(trapTile.getX(), trapTile.getY());
                        ctx.onCondition(() -> ctx.players.getLocal().getLocation().equals(trapTile), 200, 10);
                    }
                    if (ctx.players.getLocal().getLocation().equals(trapTile)) {
                        status = "Setting up trap";
                        setupTrap(invTrap, trapTile);
                    }
                } else {
                    status = "Error -> traps nulled...";
                }
            } else {
                status = "Waiting for action";
                SimpleObject trap = ctx.objects.populate().filter(9382, 9385, 9384).filter(t -> objectInLocation(t.getLocation())).next();
                if (trap != null && trap.validateInteractable() && !ctx.pathing.inMotion() && !ctx.players.getLocal().isAnimating()) {
                    status = "Resetting traps";
                    int chins = getChinCount();
                    if (trap.click("Reset")) {
                        if (trap.getName().equalsIgnoreCase("shaking box") && ctx.onCondition(() -> getChinCount() > chins, 2400)) {
                            chinsGained++;
                        }
                        ctx.onCondition(() -> !trapExistsForTile(trap.getLocation()));
                    }
                }
            }
        } else {
            if (ctx.groundItems.populate().filter(BOX_TRAP_ITEM).population() > 0) {
                SimpleGroundItem trapsGround = ctx.groundItems.populate().filter(BOX_TRAP_ITEM).nearest().next();
                status = "Picking up trap(s)";
                int trapsInv = getTrapCountInv();
                if (trapsGround != null && trapsGround.validateInteractable() && !ctx.pathing.inMotion() && !ctx.players.getLocal().isAnimating()) {
                    trapsGround.click("Take", "Box trap");
                    ctx.onCondition(() -> ctx.inventory.populate().filter("Box trap").population() > trapsInv, 1200);
                }
            } else {
                status = "No traps to pickup";
                trapsPickup = false;
            }
        }
    }

    private boolean objectInLocation(WorldPoint w) {
        for (WorldPoint loc : locs) {
            if (w.distanceTo(loc) == 0) {
                return true;
            }
        }
        return false;
    }

    public WorldPoint getAvailableTrapLocation() {
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

    public int getGainedChins() {
        return chinsGained;
    }

    public int getChinCount() {
        return ctx.inventory.populate().filter("chinchompa", "red chinchompa", "black chinchompa", "crystal chinchompa").population(true);
    }

    public int getTrapCountInv() {
        return ctx.inventory.populate().filter("Box trap").population();
    }

    public void setupTrap(final SimpleItem invTrap, final WorldPoint tile) {
        if (invTrap.click(1)) {
            ctx.sleepCondition(() -> this.trapExistsForTile(tile));
        }
    }

    public boolean trapExistsForTile(final WorldPoint tile) {
        return !ctx.objects.populate().filter(9380, 9382, 9385, 9384).filter(tile).isEmpty();
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
    }

    private String formatTime(long ms) {
        long s = ms / 1000L;
        long m = s / 60L;
        long h = m / 60L;
        s %= 60L;
        m %= 60L;
        h %= 24L;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

}