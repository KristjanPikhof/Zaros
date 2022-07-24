package eMasterFarmerTZaros;


import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.Magic;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.script.Script;
import simple.robot.utils.WorldArea;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ScriptManifest(author = "Esmaabi", category = Category.THIEVING, description =
        "<br><br>Steals seeds from Master Farmer at Draynor!<br><br>"
        + "<b>Info:</b><br>"
        + "Start <b>anywhere</b>.<br>"
        + "You must have thieving level 38.<br> "
        + "Supported home in <b>Edge</b> or in <b>Donor Zone</b>.<br>"
        + "Healing as low as 6hp!",
        discord = "Esmaabi#5752",
        name = "eMasterFarmerTZaros", servers = { "Zaros" }, version = "0.7")

public class eMain extends Script{
    //coordinates
    private final WorldArea EDGE = new WorldArea(new WorldPoint(3072, 3507, 0), new WorldPoint(3111, 3464, 0));
    private final WorldArea DONOR = new WorldArea(new WorldPoint(1386, 8896, 0), new WorldPoint(1367, 9008, 0));
    private static final WorldArea DRAYNOR = new WorldArea (
            new WorldPoint(3069, 3267, 0),
            new WorldPoint(3068, 3261, 0),
            new WorldPoint(3068, 3256, 0),
            new WorldPoint(3065, 3253, 0),
            new WorldPoint(3069, 3244, 0),
            new WorldPoint(3081, 3239, 0),
            new WorldPoint(3103, 3239, 0),
            new WorldPoint(3107, 3247, 0),
            new WorldPoint(3102, 3256, 0),
            new WorldPoint(3095, 3255, 0),
            new WorldPoint(3095, 3251, 0),
            new WorldPoint(3087, 3251, 0),
            new WorldPoint(3087, 3256, 0),
            new WorldPoint(3083, 3256, 0),
            new WorldPoint(3083, 3267, 0));

    private static final WorldArea draynorBank = new WorldArea (
            new WorldPoint(3099, 3250, 0),
            new WorldPoint(3099, 3238, 0),
            new WorldPoint(3085, 3238, 0),
            new WorldPoint(3085, 3245, 0),
            new WorldPoint(3080, 3245, 0),
            new WorldPoint(3079, 3247, 0),
            new WorldPoint(3079, 3249, 0),
            new WorldPoint(3079, 3252, 0),
            new WorldPoint(3081, 3254, 0),
            new WorldPoint(3086, 3253, 0),
            new WorldPoint(3086, 3251, 0),
            new WorldPoint(3099, 3251, 0));



    //vars
    private Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    private int currentExp;
    boolean firstTeleport;
    boolean stunned;

    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    @Override
    public void onExecute() {
        System.out.println("Started eMasterFarmerTZaros!");
        status = "Setting up script";
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis();
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.THIEVING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.THIEVING);
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.THIEVING);// for actions counter by xp drop
        count = 0;
        firstTeleport = false;
        stunned = false;
        ctx.viewport.angle(180);
        ctx.viewport.pitch(true);

        this.ctx.updateStatus("--------------- " + currentTime() + " ---------------");
        this.ctx.updateStatus("------------------------");
        this.ctx.updateStatus("   eMasterFarmerTZaros  ");
        this.ctx.updateStatus("------------------------");

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
            if (!teleporter.opened()) {
                status = "First teleport to Draynor";
                ctx.magic.castSpellOnce("Cities Teleport");
            } else {
                status = "Browsing for Draynor teleport";
                teleporter.teleportStringPath("Cities", "Draynor");
                ctx.onCondition(() -> DRAYNOR.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                ctx.game.tab(Game.Tab.INVENTORY);
                firstTeleport = true;
            }
        } else {
            if (ctx.combat.health() > 6 && DRAYNOR.containsPoint(ctx.players.getLocal().getLocation())) {
                SimpleNpc masterFarmer = ctx.npcs.populate().filter(5730).nearest().next();
                SimpleObject bank = ctx.objects.populate().filter("Bank booth").nearest().next();
                if (ctx.inventory.populate().population() < 28) {
                    status = "Thieving for seeds";
                    if (masterFarmer != null && masterFarmer.validateInteractable() && !stunned) {
                        status = "Thieving for seeds";
                        masterFarmer.click("Pickpocket", "Master Farmer");
                    }

                    if (stunned) {
                        status = "Stunned";
                        ctx.sleep(3900);
                        stunned = false;
                    }

                    if (masterFarmer == null) {
                        status = "Taking steps to farmer";
                        ctx.pathing.step(3081, 3251);
                    }

                } else {
                    status = "Banking";

                    if (draynorBank.containsPoint(ctx.players.getLocal().getLocation())) {

                        if (bank != null && bank.validateInteractable() && !ctx.bank.bankOpen()) {
                            bank.click("Bank", "Bank booth");
                            ctx.onCondition(() -> ctx.bank.bankOpen(), 5000);

                        } else if (ctx.bank.bankOpen()) {
                            ctx.bank.depositInventory();
                            ctx.sleep(randomSleeping(200, 2200));
                            ctx.bank.closeBank();
                        }

                        if (bank == null) {
                            status = "Taking steps to bank";
                            ctx.pathing.step(3088, 3248);
                        }

                    } else {
                        ctx.pathing.step(3088, 3248);
                        ctx.onCondition(() -> draynorBank.containsPoint(ctx.players.getLocal().getLocation()), 1200);
                    }
                }
            } else if (ctx.combat.health() <= 6 && EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                status = "Restoring HP";
                SimpleObject healingBox = ctx.objects.populate().filter(60003).nearest().next();
                if (healingBox != null && healingBox.validateInteractable() && ctx.combat.health() <= 6) {
                    ctx.sleep(randomSleeping(200, 1800));
                    healingBox.click("Heal", "Rejuvenation box");
                    ctx.onCondition(() -> ctx.combat.health() > 6, 5000);
                }
            } else if (ctx.combat.health() > 6 && EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                status = "Teleporting to Draynor";
                ctx.game.tab(Game.Tab.MAGIC);
                stunned = false;
                SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);
                if (homeTeleport.click("Draynor", "Home Teleport")) {
                    ctx.onCondition(() -> DRAYNOR.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                    ctx.game.tab(Game.Tab.INVENTORY);
                }
            } else if (ctx.combat.health() <= 6 && DRAYNOR.containsPoint(ctx.players.getLocal().getLocation())) {
                ctx.magic.castHomeTeleport();
                ctx.onCondition(() -> (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())), 5000);
            } else if (!DRAYNOR.containsPoint(ctx.players.getLocal().getLocation()) || !EDGE.containsPoint(ctx.players.getLocal().getLocation()) || !DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                status = "Lost, teleporting back";
                ctx.game.tab(Game.Tab.MAGIC);
                stunned = false;
                SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);
                    if (homeTeleport.click("Draynor", "Home Teleport")) {
                        ctx.onCondition(() -> DRAYNOR.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                        ctx.game.tab(Game.Tab.INVENTORY);
                    }
            }
        }

        if (currentExp != this.ctx.skills.experience(SimpleSkills.Skills.THIEVING)) {
            count++;
            currentExp = this.ctx.skills.experience(SimpleSkills.Skills.THIEVING);
        }

        if (!ctx.pathing.running() && ctx.pathing.energyLevel() >= 50) {
            ctx.updateStatus("Turning run ON");
            ctx.pathing.running(true);
            ctx.sleep(200);
        }

        if (ctx.magic.spellBook() != Magic.SpellBook.MODERN) {
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            status = "Normal spellbook required!";
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.updateStatus("Stopping script");
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.updateStatus("and start script over!");
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.updateStatus("Please change spellbook to normal");
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.sleep(10000);
            ctx.stopScript();
        }
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void onTerminate() {
        status = "Shutting down";
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        this.firstTeleport = false;
        this.stunned = false;

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
            } else if (message.contains("have been stunned!")) {
                stunned = true;
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
        g.drawString("eMasterFarmerT for Zaros by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.THIEVING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.THIEVING);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Stole seeds: " + count + " time(s)", 15, 210);
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