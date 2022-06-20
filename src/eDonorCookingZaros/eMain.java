package eDonorCookingZaros;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.script.Script;
import simple.robot.utils.WorldArea;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static eDonorCookingZaros.eGui.eGuiDialogueTarget;

@ScriptManifest(
        author = "Esmaabi",
        category = Category.COOKING,
        description = "<br>Most effective ::dzone cooking bot on Zaros! <br><br><b>Features & recommendations:</b><br><br>" +
                "<ul><li>You must have set <b>Last-preset</b> to the same as Gui fish;</li>" +
                "<li>You must start @ ::dzone;</li>" +
                "<li>Zoom out to <b>maximum</b>;</li>" +
                "<li>Included <b>anti-ban</b> features!</li></ul>",
        discord = "Esmaabi#5752",
        name = "eDonorCookingZaros", servers = { "Zaros" }, version = "1.2")

public class eMain extends Script{

    //coordinates
    private final WorldArea COOKING = new WorldArea (new WorldPoint(1358,8996, 0), new WorldPoint(1371,8982, 0));

    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    private long lastAnimation = -1;
    static String fishName;
    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }



    @Override
    public void onExecute() {
        System.out.println("Started eDonorCookingZaros!");
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.COOKING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.COOKING);
        count = 0;
        fishName = null;
        ctx.viewport.angle(180);
        ctx.viewport.pitch(true);

        // choosing NPC
        eGuiDialogueTarget();
        if (eGui.returnItem != null) {
            fishName = eGui.returnItem;
        } else {
            fishName = null;
        }
        this.ctx.updateStatus("-------------- " + currentTime() + " --------------");
        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("  eDonorCookingZaros  ");
        this.ctx.updateStatus("----------------------");

    }

    @Override
    public void onProcess() {
        if (COOKING.containsPoint(ctx.players.getLocal().getLocation())) {
            if (ctx.inventory.populate().filter(fishName).population() == 0) {
                status = "Starting banking task";
                openingBank();
            } else if (ctx.inventory.populate().filter(fishName).population() > 0) {
                status = "Cooking " + fishName.toLowerCase();
                if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 3000))) {
                    cookingTask();
                } else if (ctx.players.getLocal().isAnimating()) {
                    lastAnimation = System.currentTimeMillis();
                }
            } else {
                ctx.updateStatus(currentTime() + " Error -> Restart script");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.sleep(2400);
                ctx.stopScript();
            }

            if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
                ctx.pathing.running(true);
            }

        } else {
            status = "Player not in cooking area";
            ctx.updateStatus(currentTime() + " Player not in cooking area");
            ctx.updateStatus(currentTime() + " Stopping script");
            ctx.sleep(2400);
            ctx.stopScript();
        }
    }
    public void cookingTask() {
        SimpleObject cookingFire = ctx.objects.populate().filter(4265).nearest().next();
        SimpleItem fishInv = ctx.inventory.populate().filter(fishName).next();
        if (fishInv != null && cookingFire != null && fishInv.validateInteractable() && cookingFire.validateInteractable() && !ctx.players.getLocal().isAnimating()) {
            status = "Sleeping to cook (anti-ban)";
            ctx.sleep(randomSleeping(800, 16400));
            status = "Cooking " + fishName.toLowerCase();
            fishInv.click(0);
            ctx.sleep(randomSleeping(600, 1200));
            cookingFire.click("Use");
            ctx.onCondition(() -> ctx.dialogue.dialogueOpen(), 6200);
            if (ctx.dialogue.dialogueOpen()) {
                ctx.dialogue.clickDialogueOption(1);
                ctx.sleepCondition(() -> ctx.players.getLocal().isAnimating(), randomSleeping(6200, 12600));
            }
        }
    }

    public void openingBank() {
        SimpleObject bankChest = ctx.objects.populate().filter("Bank chest").filterHasAction("Last-preset").nearest().next();
        if (bankChest != null && bankChest.validateInteractable()) {
            status = "Sleeping to bank (anti-ban)";
            ctx.sleep(randomSleeping(6200, 16400));
            status = "Refilling " + fishName.toLowerCase();
            bankChest.click("Last-preset", "Bank chest");
            ctx.sleepCondition(() -> ctx.inventory.populate().filter(fishName).population() > 0, randomSleeping(4200, 12600));
        }
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        this.lastAnimation = -1;
        fishName = null;

        this.ctx.updateStatus("-------------- " + currentTime() + " --------------");
        this.ctx.updateStatus("---------------------------------");
        this.ctx.updateStatus("      Thank You & Good Luck!     ");
        this.ctx.updateStatus("---------------------------------");
    }

    @Override
    public void onChatMessage(ChatMessage m) {
        if (m.getMessage() != null) {
            String message = m.getMessage().toLowerCase();
            if (message.contains(ctx.players.getLocal().getName().toLowerCase())) {
                ctx.updateStatus(currentTime() + " Someone asked for you");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            } else if (message.contains("successfully cook")) {
                count++;
            }
        }
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
        g.drawString("eDonorCookingZaros by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.COOKING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.COOKING);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillExpPerHour = (int)((SkillExpGained * 3600000D) / runTime);
        long FishPerHour = (int) (count / ((System.currentTimeMillis() - this.startTime) / 3600000.0D));
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillExpPerHour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Fish cooked: " + count + " (" + FishPerHour + " fish/h)", 15, 210);
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