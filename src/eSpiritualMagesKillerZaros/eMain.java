package eSpiritualMagesKillerZaros;


import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimplePrayers;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.hooks.wrappers.*;
import simple.robot.script.Script;
import simple.robot.utils.WorldArea;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@ScriptManifest(author = "Esmaabi", category = Category.MONEYMAKING, description =
        "<br><br>Kills spiritual mages for dragon boots!<br><br>"
        + "<b>Info:</b><br>"
        + "Start <b>anywhere</b>.<br>"
        + "You must have enough of prayer potions.<br> "
        + "Supported home in <b>Edge</b> or in <b>Donor Zone</b>.<br>"
        + "You must setup last-preset correctly.<br>"
        + "Teleports home at 55hp!",
        discord = "Esmaabi#5752",
        name = "eSpiritualMagesKillerZaros", servers = { "Zaros" }, version = "0.1")

public class eMain extends Script{
    //coordinates
    private final WorldArea EDGE = new WorldArea(new WorldPoint(3072, 3507, 0), new WorldPoint(3111, 3464, 0));
    private final WorldArea DONOR = new WorldArea(new WorldPoint(1386, 8896, 0), new WorldPoint(1367, 9008, 0));
    private final WorldPoint bridgePoint = new WorldPoint(2885, 5326, 2);
    //private final WorldPoint bridgeCross = new WorldPoint(2885, 5333, 3);


    private final WorldPoint[] centerRoom = new WorldPoint[] {
            new WorldPoint(2886, 5350, 2),
            new WorldPoint(2886, 5352, 2),
            new WorldPoint(2889, 5353, 2),
            new WorldPoint(2890, 5353, 2),
            new WorldPoint(2892, 5353, 2),
            new WorldPoint(2894, 5352, 2),
            new WorldPoint(2896, 5351, 2),
            new WorldPoint(2897, 5352, 2),
            new WorldPoint(2899, 5353, 2),
            new WorldPoint(2902, 5353, 2),
            new WorldPoint(2904, 5351, 2),
            new WorldPoint(2905, 5351, 2),
            new WorldPoint(2907, 5350, 2),
            new WorldPoint(2909, 5349, 2),
            new WorldPoint(2911, 5348, 2),
            new WorldPoint(2912, 5347, 2),
            new WorldPoint(2914, 5346, 2)
    };

    private static final WorldArea centerRoomArea = new WorldArea (
            new WorldPoint(2924, 5353, 2),
            new WorldPoint(2924, 5340, 2),
            new WorldPoint(2911, 5340, 2),
            new WorldPoint(2910, 5354, 2));

    private static final WorldArea zamorakArea = new WorldArea (new WorldPoint(2887, 5342, 2),
            new WorldPoint(2882, 5342, 2),
            new WorldPoint(2882, 5349, 2),
            new WorldPoint(2880, 5359, 2),
            new WorldPoint(2887, 5370, 2),
            new WorldPoint(2919, 5366, 2),
            new WorldPoint(2940, 5357, 2),
            new WorldPoint(2938, 5344, 2),
            new WorldPoint(2932, 5333, 2),
            new WorldPoint(2915, 5333, 2),
            new WorldPoint(2903, 5344, 2),
            new WorldPoint(2891, 5349, 2),
            new WorldPoint(2889, 5348, 2),
            new WorldPoint(2889, 5342, 2));

    private static final WorldArea godwarsLobby = new WorldArea (
            new WorldPoint(2912, 5299, 2),
            new WorldPoint(2902, 5281, 2),
            new WorldPoint(2858, 5278, 2),
            new WorldPoint(2842, 5301, 2),
            new WorldPoint(2848, 5331, 2),
            new WorldPoint(2866, 5347, 2),
            new WorldPoint(2880, 5349, 2),
            new WorldPoint(2883, 5343, 2),
            new WorldPoint(2888, 5343, 2),
            new WorldPoint(2890, 5349, 2),
            new WorldPoint(2907, 5337, 2),
            new WorldPoint(2915, 5318, 2));

    private static final WorldArea bridgeArea = new WorldArea (
            new WorldPoint(2891, 5336, 2),
            new WorldPoint(2891, 5326, 2),
            new WorldPoint(2879, 5326, 2),
            new WorldPoint(2879, 5336, 2));

    //vars
    private Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillExp;
    private int count;
    static String status = null;
    private int dragonBoots;
    boolean firstTeleport;
    boolean restoreStats;
    boolean bankTask;
    boolean runCenter;

    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    @Override
    public void onExecute() {
        System.out.println("Started eMasterFarmerTZaros!");
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis();
        this.startingSkillExp = this.ctx.skills.totalExperience();
        dragonBoots = 0;
        count = 0;
        firstTeleport = false;
        restoreStats = false;
        bankTask = false;
        runCenter = false;
        ctx.viewport.angle(180);
        ctx.viewport.pitch(true);

        this.ctx.updateStatus("--------------- " + currentTime() + " ---------------");
        this.ctx.updateStatus("-------------------------------");
        this.ctx.updateStatus("   eSpiritualMagesKillerZaros  ");
        this.ctx.updateStatus("-------------------------------");

        if (ctx.skills.realLevel(SimpleSkills.Skills.THIEVING) < 38) {
            status = "Thieving level too low";
            ctx.updateStatus("Stopping script");
            ctx.updateStatus("Thieving level less than 38");
            ctx.stopScript();
        }

    }

    @Override
    public void onProcess() {

        if (!firstTeleport) {
            status = "First teleport to home";
            if (!EDGE.containsPoint(ctx.players.getLocal().getLocation()) || !DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                if (ctx.magic.castHomeTeleport()) {
                    ctx.onCondition(() ->
                            (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())), 5000);
                    firstTeleport = true;
                }
            }

        } else {
            if (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                if (!restoreStats) {
                    status = "Restoring Stats";
                    SimpleObject healingBox = ctx.objects.populate().filter(60003).nearest().next();
                    if (healingBox != null && healingBox.validateInteractable()) {
                        ctx.sleep(randomSleeping(200, 1800));
                        if (healingBox.click("Heal", "Rejuvenation box")) {
                            ctx.onCondition(() -> (ctx.skills.level(SimpleSkills.Skills.HITPOINTS) > 55 && ctx.skills.level(SimpleSkills.Skills.PRAYER) >= 37), 5000);
                            restoreStats = true;
                        }
                    }
                } else if (!bankTask) {
                    status = "Banking";
                    SimpleObject bank = ctx.objects.populate().filter("Bank booth").nearest().next();
                    if (bank != null && bank.validateInteractable()) {
                        ctx.sleep(randomSleeping(200, 1800));
                        status = "Getting last-preset";
                        bank.click("Last-preset", "Bank booth");
                        ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
                        bankTask = true;
                    }
                } else {
                    status = "Teleporting to God Wars";
                    if (!teleporter.opened()) {
                        status = "Opening teleporter";
                        ctx.magic.castSpellOnce("Dungeons Teleport");
                    } else {
                        status = "Browsing for teleport";
                        teleporter.teleportStringPath("Dungeons", "Godwars Dungeon");
                        ctx.onCondition(() -> godwarsLobby.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                    }
                }
            } else if (godwarsLobby.containsPoint(ctx.players.getLocal().getLocation())) {
                status = "Running to bridge";
                bankTask = false;
                restoreStats = false;
                runCenter = false;

                if (ctx.combat.health() < 55) {
                    status = "Low HP. Teleporting home";
                    if (ctx.magic.castHomeTeleport()) {
                        ctx.onCondition(() ->
                                (EDGE.containsPoint(ctx.players.getLocal().getLocation()) ||
                                        DONOR.containsPoint(ctx.players.getLocal().getLocation())), 5000);
                    }
                }

                if (ctx.prayers.points() > 20 && !ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MAGIC)) {
                    ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MAGIC, true);
                    ctx.game.tab(Game.Tab.INVENTORY);
                }

                if (!bridgeArea.containsPoint(ctx.players.getLocal().getLocation())) {
                    if (ctx.pathing.step(bridgePoint)) {
                        ctx.onCondition(() -> bridgeArea.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                    }
                } else if (bridgeArea.containsPoint(ctx.players.getLocal().getLocation())) {
                    status = "Crossing bridge";
                    final SimpleObject o = ctx.objects.populate().filter(26518).filter((i) ->
                            ctx.pathing.reachable(i.getLocation())).filterHasAction("Climb-off").nearest().next();
                    if (/*o == null && */o.validateInteractable()) {
                        o.click("Climb-off");
                        ctx.onCondition(() -> ctx.pathing.inMotion() && ctx.players.getLocal().isAnimating(), 5000);
                    }
                }
            } else if (zamorakArea.containsPoint(ctx.players.getLocal().getLocation())) {

                if (!runCenter) {
                    status = "Taking steps to center";
                    if (!centerRoomArea.containsPoint(ctx.players.getLocal().getLocation())) {
                        ctx.pathing.walkPath(centerRoom);
                    }

                    if (centerRoomArea.containsPoint(ctx.players.getLocal().getLocation())) {
                        runCenter = true;
                    }

                } else {

                    if (ctx.prayers.points() > 20 && !ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MAGIC)) {
                        ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MAGIC, true);
                        ctx.game.tab(Game.Tab.INVENTORY);
                    }

                    if (ctx.prayers.points() <= 20) {
                        final SimpleItem pPotion = ctx.inventory.populate().filter(Pattern.compile("Prayer potion\\(\\d+\\)")).next();
                        final SimpleItem rPotion = ctx.inventory.populate().filter(Pattern.compile("Super restore\\(\\d+\\)")).next();
                        final int cached = ctx.prayers.points();
                        status = "Restoring prayer points";
                        if (pPotion != null) {
                            if (pPotion != null && pPotion.click("Drink")) {
                                ctx.onCondition(() -> ctx.prayers.points() > cached, 250, 12);
                            }
                        } else if (rPotion != null) {
                            if (rPotion != null && rPotion.click("Drink")) {
                                ctx.onCondition(() -> ctx.prayers.points() > cached, 250, 12);
                            }
                        }
                    }

                    if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
                        status = "Turning running ON";
                        ctx.pathing.running(true);
                    }

                    if (!ctx.players.getLocal().inCombat() || ctx.players.getLocal().getInteracting() == null) {
                        SimpleNpc fm = ctx.npcs.populate().filter("Spiritual mage").filter((n) ->
                                n.getInteracting() != null && n.getInteracting().equals(ctx.players.getLocal()) && n.inCombat()).nearest().next();
                        SimpleNpc npc = fm != null ? fm : ctx.npcs.populate().filter("Spiritual mage").nearest().next();
                        if (npc == null) {
                            return;
                        }
                        status = "Fighting spiritual mages";
                        npc.click("Attack");
                        ctx.onCondition(() -> ctx.players.getLocal().inCombat(), 250, 12);

                        if (npc.getAnimation() == 836) {
                            count++;
                        }
                    }

                    if (ctx.combat.health() < 55) {
                        status = "Low HP. Teleporting home";
                        if (ctx.magic.castHomeTeleport()) {
                            ctx.onCondition(() ->
                                    (EDGE.containsPoint(ctx.players.getLocal().getLocation()) ||
                                            DONOR.containsPoint(ctx.players.getLocal().getLocation())), 5000);
                        }
                    }

                    if (!ctx.players.getLocal().inCombat() || ctx.players.getLocal().getInteracting() == null &&
                            ctx.groundItems.populate().filter(11840).population() > 0) {
                        final int cached = ctx.inventory.filter(11840).population();
                        if (ctx.inventory.getFreeSlots() > 0) {
                            SimpleGroundItem item = ctx.groundItems.populate().filter(11840).nearest().next();
                            if (item != null) {
                                status = "Looting dragon boots";
                                item.click("Take");
                                ctx.onCondition(() ->
                                        ctx.inventory.filter(11840).population() > cached, 5000);
                                dragonBoots++;
                            }
                        }
                    }

                    if ((ctx.skills.level(SimpleSkills.Skills.STRENGTH) <= (ctx.skills.realLevel(SimpleSkills.Skills.STRENGTH) + 8)) &&
                            !ctx.inventory.populate().filter(Pattern.compile("Super strength\\(\\d+\\)")).isEmpty()) {
                        status = "Drinking strength potion";
                        final SimpleItem potion = ctx.inventory.populate().filter(Pattern.compile("Super strength\\(\\d+\\)")).next();
                        int cached = ctx.skills.level(SimpleSkills.Skills.STRENGTH);
                        if (potion != null && potion.click("Drink")) {
                            ctx.onCondition(() -> ctx.skills.level(SimpleSkills.Skills.STRENGTH) > cached, 250, 12);
                        }
                    }

                    if ((ctx.skills.level(SimpleSkills.Skills.ATTACK) <= (ctx.skills.realLevel(SimpleSkills.Skills.ATTACK) + 8)) &&
                            !ctx.inventory.populate().filter(Pattern.compile("Super attack\\(\\d+\\)")).isEmpty()) {
                        status = "Drinking attack potion";
                        final SimpleItem potion = ctx.inventory.populate().filter(Pattern.compile("Super attack\\(\\d+\\)")).next();
                        int cached = ctx.skills.level(SimpleSkills.Skills.ATTACK);
                        if (potion != null && potion.click("Drink")) {
                            ctx.onCondition(() -> ctx.skills.level(SimpleSkills.Skills.ATTACK) > cached, 250, 12);
                        }
                    }
                }
            } // end of killin task
        } // end of overall tasks
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void onTerminate() {
        this.startingSkillExp = 0L;
        dragonBoots = 0;
        count = 0;
        firstTeleport = false;
        restoreStats = false;
        bankTask = false;
        runCenter = false;


        this.ctx.updateStatus("-------------- " + currentTime() + " --------------");
        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("Thank You & Good Luck!");
        this.ctx.updateStatus("----------------------");
    }

    @Override
    public void onChatMessage(ChatMessage m) {
        if (m.getMessage() != null) {
            String message = m.getMessage().toLowerCase();
            if (message.contains(ctx.players.getLocal().getName().toLowerCase())) {
                ctx.updateStatus(currentTime() + " Someone asked for you");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Color PhilippineRed = new Color(196, 18, 48);
        Color RaisinBlack = new Color(35, 31, 32, 127);
        g.setColor(RaisinBlack);
        g.fillRect(5, 120, 205, 110);
        g.setColor(PhilippineRed);
        g.drawRect(5, 120, 205, 110);
        g.setColor(PhilippineRed);
        g.drawString("eSpiritualMagesKiller by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillExp = this.ctx.skills.totalExperience();
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        long gpPerHour = (long) (ctx.paint.valuePerHour(dragonBoots, startTime) * 1.65);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 165);
        g.drawString("Dragon boots looted: " + dragonBoots + " (" + ctx.paint.valuePerHour(dragonBoots, startTime) + " per/h)", 15, 180);
        g.drawString("Mages killed: " + count + " (" + ctx.paint.valuePerHour(count, startTime) + " per/h)", 15, 195);
        g.drawString("Money: ~" + gpPerHour + "M gp/h", 15, 210);
        g.drawString("Status: " + status, 15, 225);
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