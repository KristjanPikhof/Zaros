package eAstralRunecrafter;


import java.awt.Color;
import java.awt.Graphics;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.script.Script;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.robot.utils.WorldArea;

@ScriptManifest(author = "Esmaabi", category = Category.RUNECRAFTING, description =
        "<br><br>Crafts astral runes in most effective way to train Runecrafting!<br><br>"
        + "<b>Features:</b><br>"
        + "Start <b>anywhere</b>.<br>"
        + "You must set <b>last-preset</b> to full inventory of essences.<br> "
        + "Supported home in <b>Edge</b> or in <b>Donor Zone</b>.<br>"
        + "Supported <b>Daeyalt essences</b>!",
        discord = "Esmaabi#5752",
        name = "eAstralRunecrafter", servers = { "Zaros" }, version = "2.3")

public class eMain extends Script{
    //coordinates
    private final WorldArea EDGE = new WorldArea(new WorldPoint(3072, 3507, 0), new WorldPoint(3111, 3464, 0));
    private final WorldArea DONOR = new WorldArea(new WorldPoint(1386, 8896, 0), new WorldPoint(1367, 9008, 0));
    private final WorldArea ASTRAL = new WorldArea(new WorldPoint(2137, 3875, 0), new WorldPoint(2170, 3846, 0));

    //vars
    private Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    private int currentExp;
    boolean firstTeleport;

    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    @Override
    public void onExecute() {
        System.out.println("Started eAstralRunecrafter!");
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.RUNECRAFT);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);// for actions counter by xp drop
        count = 0;
        firstTeleport = false;
        ctx.viewport.angle(180);
        ctx.viewport.pitch(true);

        this.ctx.updateStatus("-------------- " + currentTime() + " --------------");
        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("  eAstralRunecrafter  ");
        this.ctx.updateStatus("----------------------");

    }

    @Override
    public void onProcess() {
        if (!firstTeleport) {
            if (!teleporter.opened()) {
                status = "First teleport to altar";
                ctx.magic.castSpellOnce("Minigame Teleport");
            } else {
                status = "Browsing for altar teleport";
                teleporter.teleportStringPath("Skilling", "Runecrafting: Astral Altar");
                ctx.onCondition(() -> ASTRAL.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                firstTeleport = true;
            }
        } else {
            if (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())) {
                if (ctx.inventory.populate().filter(7936, 24704).isEmpty()) {
                    status = "Bank found";
                    SimpleObject bank = ctx.objects.populate().filter("Bank booth").nearest().next();
                    if (bank != null && bank.validateInteractable()) {
                        status = "Sleeping to bank (anti-ban)";
                        ctx.sleep(randomSleeping(200, 1800));
                        status = "Getting last-preset";
                        bank.click("Last-preset", "Bank booth");
                        ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
                    }
                } else if (!ctx.inventory.populate().filter(7936, 24704).isEmpty()) {
                    status = "Sleeping to teleport (anti-ban)";
                    ctx.sleep(randomSleeping(200, 6700));
                    status = "Teleporting to altar";
                    SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
                    ctx.game.tab(Game.Tab.MAGIC);
                    homeTeleport.click("Runecrafting: Astral Altar", "Home Teleport");
                    ctx.onCondition(() -> ASTRAL.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                }
            } else if (ASTRAL.containsPoint(ctx.players.getLocal().getLocation())) {
                if (ctx.inventory.populate().filter(7936, 24704).isEmpty()) {
                    status = "Sleeping to teleport (anti-ban)";
                    ctx.sleep(randomSleeping(200, 6700));
                    status = "Teleporting to home";
                    ctx.magic.castSpellOnce("Home Teleport");
                    ctx.onCondition(() -> (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation())), 5000);
                } else {
                    status = "Searching for altar";
                    SimpleObject altar = ctx.objects.populate().filter(34771).nearest().next();
                    if (altar != null && altar.validateInteractable()) {
                        status = "Sleeping to craft (anti-ban)";
                        ctx.sleep(randomSleeping(200, 3700));
                        status = "Crafting runes";
                        altar.click("Craft-rune", "Altar");
                        ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
                    }
                }
            }
        }

        if (currentExp != this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT)) {
            count++;
            currentExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);
        }
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        this.firstTeleport = false;

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
        g.drawString("eAstralRunecrafter by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.RUNECRAFT);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Runs completed: " + count + " time(s)", 15, 210);
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