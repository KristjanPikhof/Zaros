package eAioAgilityBotZaros;

import eAioAgilityBotZaros.tasks.*;
import simple.hooks.filters.SimpleEquipment;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.LoopingScript;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.Magic;
import simple.hooks.simplebot.Pathing;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleWidget;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ScriptManifest(author = "Esmaabi", category = Category.AGILITY, description =
        "<br>AIO agility training bot for rooftops courses<br><br>" +
                "<b>Start anywhere</b> and <b>select preferred</b> course in GUI.<br><br>" +
                "You <b>must</b> be using <b>normal spellbook</b>.<br>" +
                "Supported mark of grace pickup- or ring of wealth banking method.<br><br>" +
                "Supported courses:<br>" +
                "Al-Kharid, Varrock, Canifis, Seers,<br> Pollnivneach, Rellekka, Ardougne",
        discord = "Esmaabi#5752",
        name = "eAioAgilityBotZaros", servers = { "Zaros" }, version = "2")

public class eMain extends TaskScript  implements LoopingScript {

    private List tasks = new ArrayList();

    public static eAioAgilityBotZaros.eMain.State courseName;
    public static String status = null;
    public static Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    public static int startMarks, totalMarks;
    public static int count;
    public static boolean firstTeleport;
    public static long lastHP;
    private long lastAnimation;
    boolean ringOfWealthEqupped = false;

    @Override
    public int loopDuration() {
        return 600;
    }

    public enum State {
        ALKHARID,
        VARROCK,
        CANIFIS,
        SEERS,
        POLLNIVNEACH,
        RELLEKKA,
        ARDOUGNE,
        WAITING,
    }

    @Override
    public void onExecute() {
        tasks.addAll(Arrays.asList(
                new eAlKharidR(ctx),
                new eVarrockR(ctx),
                new eCanifisR(ctx),
                new eSeersR(ctx),
                new ePollnivneachR(ctx),
                new eRellekkaR(ctx),
                new eArdougneR(ctx)
        ));// Adds our tasks to our {task} list for execution

        //settings
        System.out.println("Started eAioAgilityBot!");
        startMarks = ctx.inventory.populate().filter(11849).population(true);
        startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.AGILITY);//paint
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.AGILITY);//paint
        teleporter = new Teleporter(ctx);
        lastHP = ctx.combat.health();
        count = 0;
        totalMarks = 0;
        firstTeleport = false;
        status = "Setting up script";
        lastAnimation = System.currentTimeMillis() + 20000;

        //GUI
        eAioAgilityBotZaros.eGui.eGuiDialogue();
        if (Objects.equals(eGui.courseName, "Al-Kharid Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.ALKHARID;
        } else if (Objects.equals(eGui.courseName, "Varrock Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.VARROCK;
        } else if (Objects.equals(eGui.courseName, "Canifis Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.CANIFIS;
        } else if (Objects.equals(eGui.courseName, "Seers Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.SEERS;
        } else if (Objects.equals(eGui.courseName, "Pollnivneach Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.POLLNIVNEACH;
        } else if (Objects.equals(eGui.courseName, "Rellekka Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.RELLEKKA;
        } else if (Objects.equals(eGui.courseName, "Ardougne Rooftop")) {
            courseName = eAioAgilityBotZaros.eMain.State.ARDOUGNE;
        } else {
            courseName = eAioAgilityBotZaros.eMain.State.WAITING;
        }

        // viewport settings
        ctx.viewport.angle(0);
        ctx.viewport.pitch(true);

        //counting marks of grace method
        SimpleItem ringOfWealth = ctx.equipment.getEquippedItem(SimpleEquipment.EquipmentSlot.RING);
        if (ringOfWealth != null && ringOfWealth.getName().contains("wealth")) {
            ringOfWealthEqupped = true;
            totalMarks = 0;
            ctx.updateStatus("Ring of wealth equipped");
            ctx.updateStatus("Adjusting marks of grace counting method");
        } else {
            ringOfWealthEqupped = false;
            ctx.updateStatus("Ring of wealth not equipped");
            ctx.updateStatus("Adjusting marks of grace counting method");
        }
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public List tasks() {
        // TODO Auto-generated method stub
        return tasks;
    }

    @Override
    public boolean prioritizeTasks() {
        // TODO Auto-generated method stub
        return true;
    }

    // This method is not needed as the TaskScript class will call it, itself
    @Override
    public void onProcess() {
        // Can add anything here before tasks have been ran
        super.onProcess();
        final Pathing pathing = ctx.pathing;

        if (!pathing.running() && pathing.energyLevel() >= 50) {
            ctx.updateStatus("Turning run ON");
            pathing.running(true);
            ctx.sleep(200);
        }

        if (!ctx.pathing.inMotion() && (System.currentTimeMillis() > (lastAnimation + 20000))) {
            SimpleWidget compassWidget = ctx.widgets.getWidget(548, 23);
            compassWidget.click(0);
        } else if (ctx.pathing.inMotion()) {
            lastAnimation = System.currentTimeMillis();
        }

        if (ctx.combat.health() <= 6) {
            ctx.updateStatus("Low HP, sleeping to recover");
            status = "Low HP, sleeping";
            ctx.sleep(60000);
        }

        if (ctx.magic.spellBook() != Magic.SpellBook.MODERN && courseName != State.WAITING) {
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

    @Override
    public void onTerminate() {
        courseName = State.WAITING;
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        count = 0;
        totalMarks = 0;
    }

    @Override
    public void paint(Graphics g) {

        if (!ringOfWealthEqupped) {
            totalMarks = ctx.inventory.populate().filter(11849).population(true) - startMarks;
        }

        Color PhilippineRed = new Color(196, 18, 48);
        Color RaisinBlack = new Color(35, 31, 32, 127);
        g.setColor(RaisinBlack);
        g.fillRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawString("eAioAgilityBot by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.AGILITY);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.AGILITY);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Current Level: " + currentSkillLevel + " (" + "+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 180);
        g.drawString("MOG collected: " + totalMarks + " (" + ctx.paint.valuePerHour(totalMarks, startTime) + " per/h)", 15, 195);
        g.drawString("Laps count: " + count + " (" + ctx.paint.valuePerHour(count, startTime) + " laps/h)", 15, 210);
        g.drawString("Status: " + status, 15, 225);
    }

    public static int getIntFromChat(String chatMessageMOG) {
        Pattern ints = Pattern.compile("\\d+");
        Matcher makeMatch = ints.matcher(chatMessageMOG);
        makeMatch.find();
        String result = makeMatch.group();
        return Integer.parseInt(result);
    }

    @Override
    public void onChatMessage(ChatMessage m) {
        if (m.getMessage() != null) {
            String message = m.getMessage().toLowerCase();
            boolean bankedMOG = message.contains("x marks of grace to your bank");
            if (message.contains(ctx.players.getLocal().getName().toLowerCase())) {
                ctx.updateStatus(currentTime() + " Someone asked for you");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            } else if (message.contains("lap count is")) {
                count++;
            } else if (bankedMOG && ringOfWealthEqupped) {
                String newMessage = m.getFormattedMessage();
                totalMarks += getIntFromChat(newMessage);
            }
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