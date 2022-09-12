package eMultiPurposeMaker;

import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.script.Script;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;


@ScriptManifest(author = "Esmaabi", category = Category.OTHER, description = "<br>"
        + "It's multi purpose crafting and fletching bot for Zaros. "
        + "You can type material name you want to make items from. "
        + "<br><br>"
        + "Script will offer two working modes:<br> \"<b>Crafting</b>\" or \"<b>Fletching</b>\"<br><br>"
        + "Before starting script:<br>"
        + "1. You must setup <b>Last-preset</b> and use it once;<br>"
        + "2. You must setup item you want to make as <b>Space</b> action;<br>"
        + "3. Start with fresh inventory only material and tool in it;<br>"
        + "4. Start with knife or chisel depending on starting mode.<br>", discord = "Esmaabi#5752",
        name = "eMultiPurposeMakerZaros", servers = { "Zaros" }, version = "0.1")

public class eMain extends Script{


    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    static String status = null;
    static String npcName;
    public static State playerState;
    private long lastAnimation = -1;
    private boolean started;
    public SimpleSkills.Skills skillName;
    public String toolName;
    public String taskName;
    static int space = KeyEvent.VK_SPACE;
    public int itemNameStarted;

    enum State{
        STARTED,
        WAITING,
    }

    @Override
    public void onExecute() {
        System.out.println("Started eMultiPurposeMaker!");
        started = false;
        startTime = System.currentTimeMillis(); //paint
        npcName = null;
        eGui.returnItem = null;
        eGui.returnMode = -1;
        status = "Setting up config";

        this.ctx.updateStatus("------------------------------");
        this.ctx.updateStatus("      eMultiPurposeMaker      ");
        this.ctx.updateStatus("------------------------------");

        //gui
        eGui.eGuiDialogueMode();
        if (eGui.returnMode == 0) {
            playerState = State.STARTED;
            toolName = "chisel";
            taskName = "Crafting";
            skillName = SimpleSkills.Skills.CRAFTING;
            ctx.updateStatus(currentTime() + " crafting task started");
            if (playerState == State.STARTED) {
                eGui.eGuiDialogueItem();
            }
        } else if (eGui.returnMode == 1) {
            playerState = State.STARTED;
            toolName = "knife";
            taskName = "Fletching";
            skillName = SimpleSkills.Skills.FLETCHING;
            ctx.updateStatus(currentTime() + " fletching task started");
            if (playerState == State.STARTED) {
                eGui.eGuiDialogueItem();
            }
        } else {
            playerState = State.WAITING;
        }

        //storing inventory item ID
        itemNameStarted = storedItemId();

        //stats for paint
        this.startingSkillLevel = this.ctx.skills.realLevel(skillName);
        this.startingSkillExp = this.ctx.skills.experience(skillName);

        // if started check
        started = eGui.returnMode != -1 && eGui.returnItem != null && playerState == State.STARTED && itemNameStarted != 0;

    }

    @Override
    public void onProcess() {
        if (started) {

            if (ctx.inventory.populate().filter(toolName).isEmpty()) {
                status = "No " + toolName + " in inventory";
                ctx.updateStatus("No " + toolName + " in inventory");
                ctx.updateStatus("Stopping script");
                ctx.sleep(5000);
                ctx.stopScript();
            }

            if (ctx.inventory.populate().filter(itemNameStarted).population() <= 1) {
                bankingTask();
            } else if (ctx.inventory.populate().filter(itemNameStarted).population() > 1) {
                if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + randomSleeping(2400, 5200)))) {
                    status = taskName;
                    makingTask();
                } else if (ctx.players.getLocal().isAnimating()) {
                    lastAnimation = System.currentTimeMillis();
                }
            } else {
                status = "Banking";
                bankingTask();
            }
        }
    }

    public void bankingTask() {
        SimpleObject bankOpen =  ctx.objects.populate().filter("Bank chest", "Bank booth").filterHasAction("Last-preset").nearest().next();
        if (bankOpen != null && bankOpen.validateInteractable()) {
            status = "Banking";
            int materialInInventory = getInventoryMaterial();
            SimpleWidget compassWidget = ctx.widgets.getWidget(548, 23);
            compassWidget.click(0);
            if (bankOpen.click("Last-preset")) {
                ctx.onCondition(() -> getInventoryMaterial() > materialInInventory, 5000);
            }
        } else {
            status = "Bank not found";
            ctx.updateStatus("Bank not found");
            ctx.updateStatus("Stopping script");
            ctx.sleep(5000);
            ctx.stopScript();
        }
    }
    public int getInventoryMaterial() {
        return ctx.inventory.populate().filter(itemNameStarted).population();
    }
    public void makingTask() {
        SimpleItem materialName = ctx.inventory.populate().filter(itemNameStarted).next();
        SimpleItem makingTool = ctx.inventory.populate().filter(toolName).next();
        if (!ctx.dialogue.dialogueOpen()) {
            if (materialName != null && materialName.validateInteractable()
                    && makingTool != null && makingTool.validateInteractable()) {
                if (makingTool.click("Use")) {
                    ctx.sleep(400);
                    materialName.click(0);
                }
            }
        } else {
            ctx.sleep(400);
            ctx.keyboard.clickKey(space);
        }
    }

    public static int getItem(String... itemName) { //Scans for the name of item instead of exact name and gets itemID
        return ClientContext.instance().inventory.populate()
                .filter(p -> Stream.of(itemName).anyMatch(arr -> p.getName().toLowerCase().contains(arr.toLowerCase())))
                .next().getId();
    }

    public int storedItemId() {
        return ctx.inventory.populate().filter(getItem(eGui.returnItem)).next().getId();
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        npcName = null;
        eGui.returnItem = null;
        eGui.returnMode = -1;

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
        if (started) {
            Color PhilippineRed = new Color(196, 18, 48);
            Color RaisinBlack = new Color(35, 31, 32, 127);
            g.setColor(RaisinBlack);
            g.fillRect(5, 120, 200, 95);
            g.setColor(PhilippineRed);
            g.drawRect(5, 120, 200, 95);
            g.setColor(PhilippineRed);
            g.drawString("eMultiPurposeMaker by Esmaabi", 15, 135);
            g.setColor(Color.WHITE);
            long runTime = System.currentTimeMillis() - this.startTime;
            long currentSkillLevel = this.ctx.skills.realLevel(skillName);
            long currentSkillExp = this.ctx.skills.experience(skillName);
            long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
            long SkillExpGained = currentSkillExp - this.startingSkillExp;
            long SkillexpPhour = (int) ((SkillExpGained * 3600000D) / runTime);
            g.drawString("Runtime: " + formatTime(runTime), 15, 150);
            g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
            g.drawString("Current Level: " + currentSkillLevel, 15, 180);
            g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
            g.drawString("Status: " + status, 15, 210);
        }
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
