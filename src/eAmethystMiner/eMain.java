package eAmethystMiner;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.LoopingScript;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.Task;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.utils.WorldArea;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(author = "Esmaabi", category = Category.MINING,
        description = "<br>Most effective amethyst crystal mining bot on Zaros! <br><br><b>Features & recommendations:</b><br><br>" +
        "<ul><li>You must have set <b>Last-preset</b> to empty inventory;</li>" +
        "<li>You must start <b>with pickaxe equipped</b>;</li>" +
        "<li>You must start at mining guild bank near amethyst crystals;</li>" +
        "<li>Zoom out to <b>to maximum</b>;</li>" +
        "<li>Dragon pickaxe special attack supported;</li>" +
        "<li>Included random sleeping included!</li></ul>",
        discord = "Esmaabi#5752",
        name = "eAmethystMiner", servers = { "Zaros" }, version = "0.1")

public class eMain extends TaskScript implements LoopingScript {

    //coordinates
    private final WorldArea miningArea = new WorldArea (new WorldPoint(3043, 9695, 0), new WorldPoint(2993, 9729, 0));

    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    private long lastAnimation = -1;

    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    //Tasks
    List<Task> tasks = new ArrayList<>();

    @Override
    public boolean prioritizeTasks() {
        return true;
    }

    @Override
    public List<Task> tasks() {
        return tasks;
    }

    @Override
    public void onExecute() {

        tasks.addAll(Arrays.asList());

        System.out.println("Started eAmethystMiner!");



        this.ctx.updateStatus("--------------- " + currentTime() + " ---------------");
        this.ctx.updateStatus("-------------------------------");
        this.ctx.updateStatus("       eAmethystMiner      ");
        this.ctx.updateStatus("-------------------------------");

        status = "Setting up bot";
        this.startTime = System.currentTimeMillis();
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MINING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MINING);
        count = 0;
        ctx.viewport.angle(270);
        ctx.viewport.pitch(true);

    }

    @Override
    public void onProcess() {
        super.onProcess();

        if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
                ctx.pathing.running(true);
        }

        if (ctx.combat.getSpecialAttackPercentage() == 100
                    && ctx.equipment.populate().filter("Dragon pickaxe").population() == 1
                    && ctx.players.getLocal().getAnimation() == 643) {
                ctx.sleep(randomSleeping(1200, 12000));
                ctx.combat.toggleSpecialAttack(true);
        }

        if (miningArea.containsPoint(ctx.players.getLocal().getLocation())) {

            if (ctx.inventory.populate().population() == 28) {
                    openingBank();
            } else if (ctx.inventory.populate().population() < 28) {
                if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + randomSleeping(1200, 4600)))) {
                    miningTask();
                } else if (ctx.players.getLocal().isAnimating()) {
                    lastAnimation = System.currentTimeMillis();
                }
            } else {
                ctx.updateStatus(currentTime() + " Unknown error -> restarting");
                status = "Unknown error";
                if (miningArea.containsPoint(ctx.players.getLocal().getLocation())) {
                    openingBank();
                }
            }

        } else {
                status = "Player not in mining area";
                ctx.updateStatus(currentTime() + " Player not in mining area");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.sleep(2400);
                ctx.stopScript();
        }

    }

    public void openingBank() {
        SimpleObject bankChest = ctx.objects.populate().filter("Bank chest").filterHasAction("Last-preset").nearest().next();
        if (bankChest != null && bankChest.validateInteractable() && !ctx.pathing.inMotion()) {
            status = "Banking";
            bankChest.click("Last-preset", "Bank chest");
            ctx.onCondition(() -> ctx.inventory.populate().isEmpty(), 5000);
        }
    }

    public void miningTask() {
        SimpleObject amethystCrystals = ctx.objects.populate().filter("Crystals").filterHasAction("Mine").nearest().next();
        if (amethystCrystals != null && amethystCrystals.validateInteractable() && !ctx.pathing.inMotion()) {
            status = "Mining amethyst crystals";
            amethystCrystals.click("Mine", "Crystals");
            ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
        }
    }



    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        count = 0;


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
            } else if (message.contains("get some amethyst")) {
                count++;
            }
        }
    }

    @Override
    public int loopDuration() {
        return 200;
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
        g.drawString("eAmethystMiner by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MINING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MINING);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillExpPerHour = (int)((SkillExpGained * 3600000D) / runTime);
        long ActionsPerHour = (int) (count / ((System.currentTimeMillis() - this.startTime) / 3600000.0D));
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillExpPerHour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Crystals mined: " + count + " (" + ActionsPerHour + " per/h)", 15, 210);
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