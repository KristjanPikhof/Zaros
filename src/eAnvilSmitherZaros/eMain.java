package eAnvilSmitherZaros;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.LoopingScript;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.Task;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ScriptManifest(author = "Esmaabi", category = Category.SMITHING,
        description = "<br>Most effective ::dzone or Varrock west anvil smithing bot on Zaros! <br><br><b>Features & recommendations:</b><br><br>" +
        "<ul><li>You must have set <b>Last-preset</b> to hammer + bars;</li>" +
        "<li>You must start <b>with bars in inventory</b>;</li>" +
        "<li>You must start at ::dzone anvil or Varrock west bank;</li>" +
        "<li>Zoom out to <b>see anvil & bank chest</b>;</li>" +
        "<li>Included random sleeping times!</li></ul>",
        discord = "Esmaabi#5752",
        name = "eAnvilSmitherZaros", servers = { "Zaros" }, version = "0.4")

public class eMain extends TaskScript implements LoopingScript {

    //coordinates
    private final WorldArea smithingAreaDonor = new WorldArea (new WorldPoint(1358,8996, 0), new WorldPoint(1371,8982, 0));
    private final WorldArea smithingAreaVarrock = new WorldArea (new WorldPoint(3177, 3449, 0), new WorldPoint(3196, 3418, 0));

    private final WorldPoint anvilLocation = new WorldPoint(1359, 8989, 0);
    private final WorldPoint anvilLocationVarrock = new WorldPoint(3188, 3426, 0);
    private final WorldPoint bankBoothVarrock = new WorldPoint(3186, 3436, 0);

    //vars
    private Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    public int smithingWidget;
    public int sleepTime;
    public int barsinInv;
    static String status = null;
    static String nameOfItem = null;
    public int barsInInventory;
    private long lastAnimation = -1;
    boolean botStarted = false;

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

        System.out.println("Started eAnvilSmitherZaros!");



        this.ctx.updateStatus("--------------- " + currentTime() + " ---------------");
        this.ctx.updateStatus("-------------------------------");
        this.ctx.updateStatus("       eAnvilSmitherZaros      ");
        this.ctx.updateStatus("-------------------------------");

        status = "Setting up bot";
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis();
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.SMITHING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.SMITHING);
        botStarted = false;
        barsInInventory = 0;
        count = 0;
        ctx.viewport.angle(270);
        ctx.viewport.pitch(true);

        barsinInv = getItem("bar");

        if (barsinInv == 0) {
            ctx.updateStatus("Bars in inventory NOT FOUND");
            status = "Bars in inventory NOT FOUND";
            ctx.sleep(4200);
            status = "Stopping script";
            ctx.updateStatus("Stopping script");
            ctx.stopScript();
        } else {
            ctx.updateStatus("Bars in inventory found");
            status = "Bars in inventory found";
        }

        eGui.eGuiDialogueTarget();
        if (eGui.returnItem == "Sword") {
            nameOfItem = "swords";
            ctx.updateStatus("Making " + nameOfItem);
            smithingWidget = 10;
            barsInInventory = 0;
            botStarted = true;
        } else if (eGui.returnItem == "Platebody") {
            nameOfItem = "platebodies";
            ctx.updateStatus("Making " + nameOfItem);
            smithingWidget = 22;
            barsInInventory = 4;
            botStarted = true;
        } else if (eGui.returnItem == "Dart tips") {
            nameOfItem = "dart tips";
            ctx.updateStatus("Making " + nameOfItem);
            smithingWidget = 29;
            barsInInventory = 0;
            botStarted = true;
        } else {
            status = "Waiting for GUI options";
        }


    }

    @Override
    public void onProcess() {
        super.onProcess();

        if (botStarted) {

            if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
                ctx.pathing.running(true);
            }

            if (smithingAreaVarrock.containsPoint(ctx.players.getLocal().getLocation()) ||
                    smithingAreaDonor.containsPoint(ctx.players.getLocal().getLocation())) {

                if (ctx.inventory.populate().filter(barsinInv).population() <= barsInInventory) {
                    if (smithingAreaVarrock.containsPoint(ctx.players.getLocal().getLocation())) {
                        openingBankVarrock();
                    } else if (smithingAreaDonor.containsPoint(ctx.players.getLocal().getLocation())) {
                        openingBank();
                    }

                } else if (ctx.inventory.populate().filter(barsinInv).population() > barsInInventory) {
                    if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 4000))) {
                        if (smithingAreaVarrock.containsPoint(ctx.players.getLocal().getLocation())) {
                            smithingTaskVarrock();
                        } else if (smithingAreaDonor.containsPoint(ctx.players.getLocal().getLocation())) {
                            smithingTask();
                        }
                    } else if (ctx.players.getLocal().isAnimating()) {
                        lastAnimation = System.currentTimeMillis();
                    }

                } else {
                    ctx.updateStatus(currentTime() + " Unknown error -> restarting");
                    status = "Unknown error";
                    if (smithingAreaVarrock.containsPoint(ctx.players.getLocal().getLocation())) {
                        openingBankVarrock();
                    } else if (smithingAreaDonor.containsPoint(ctx.players.getLocal().getLocation())) {
                        openingBank();
                    }
                }

            } else {
                status = "Player not in smithing area";
                ctx.updateStatus(currentTime() + " Player not in smithing area");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.sleep(2400);
                ctx.stopScript();
            }

        }
    }

    public void openingBank() {
        SimpleObject bankChest = ctx.objects.populate().filter("Bank chest").filterHasAction("Last-preset").nearest().next();
        if (bankChest != null && bankChest.validateInteractable() && !ctx.pathing.inMotion()) {
            sleepTime = randomSleeping(1200, 10200);
            status = "Sleeping to bank (" + sleepTime + "ms)";
            ctx.sleep(sleepTime);
            status = "Refilling supplies";
            bankChest.click("Last-preset", "Bank chest");
            ctx.onCondition(() -> ctx.inventory.populate().filter(barsinInv).population() > barsInInventory, 5000);
        }
    }

    public void openingBankVarrock() {
        SimpleObject bankBooth = ctx.objects.populate().filter("Bank booth").nearest(bankBoothVarrock).next();
        if (bankBooth != null && bankBooth.validateInteractable() && !ctx.pathing.inMotion()) {
            sleepTime = randomSleeping(1200, 10200);
            status = "Sleeping to bank (" + sleepTime + "ms)";
            ctx.sleep(sleepTime);
            status = "Refilling supplies";
            bankBooth.click("Last-preset", "Bank booth");
            ctx.onCondition(() -> ctx.inventory.populate().filter(barsinInv).population() > barsInInventory, 5000);
        }
    }

    public void smithingTask() {
        SimpleObject anvil = ctx.objects.populate().filter("Anvil").nearest(anvilLocation).next();
        SimpleWidget widgetScreen = ctx.widgets.getWidget(312, 0);
        SimpleWidget smithingItem = ctx.widgets.getWidget(312, smithingWidget);
        if (anvil != null && anvil.validateInteractable() && widgetScreen == null && !ctx.pathing.inMotion()) {
            status = "Clicking anvil";
            anvil.click("Smith", "Anvil");
            ctx.sleepCondition(() -> ctx.pathing.inMotion(), 5000);
        } else if (widgetScreen != null && smithingItem != null && !ctx.players.getLocal().isAnimating()) {
            status = "Making " + nameOfItem;
            smithingItem.click(0);
            ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
        }
    }

    public void smithingTaskVarrock() {
        SimpleObject anvil = ctx.objects.populate().filter("Anvil").nearest(anvilLocationVarrock).next();
        SimpleWidget widgetScreen = ctx.widgets.getWidget(312, 0);
        SimpleWidget smithingItem = ctx.widgets.getWidget(312, smithingWidget);
        if (anvil != null && anvil.validateInteractable() && widgetScreen == null && !ctx.pathing.inMotion()) {
            status = "Clicking anvil";
            anvil.click("Smith", "Anvil");
            ctx.onCondition(() -> ctx.pathing.inMotion(), 5000);
        } else if (widgetScreen != null && smithingItem != null && !ctx.players.getLocal().isAnimating()) {
            status = "Making " + nameOfItem;
            smithingItem.click(0);
            ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 5000);
        }
    }

    public static int getItem(String... itemName) { //Scans for the name of item instead of exact name and gets itemID
        return ClientContext.instance().inventory.populate()
                .filter(p -> Stream.of(itemName).anyMatch(arr -> p.getName().toLowerCase().contains(arr.toLowerCase())))
                .next().getId();
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
            } else if (message.contains("you hammer the")) {
                count++;
            }
        }
    }

    @Override
    public int loopDuration() {
        return 150;
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
        g.drawString("eAnvilSmitherZaros by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.SMITHING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.SMITHING);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillExpPerHour = (int)((SkillExpGained * 3600000D) / runTime);
        long ActionsPerHour = (int) (count / ((System.currentTimeMillis() - this.startTime) / 3600000.0D));
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillExpPerHour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Actions made: " + count + " (" + ActionsPerHour + " per/h)", 15, 210);
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