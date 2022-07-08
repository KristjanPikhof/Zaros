package eAioAgilityBot;

import eAioAgilityBot.tasks.eCanifisR;
import eAioAgilityBot.tasks.eSeersR;
import eAioAgilityBot.tasks.ePollnivneachR;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Pathing;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(author = "Esmaabi", category = Category.AGILITY, description = "AIO agility training bot for rooftops courses", discord = "Esmaabi#5752",
        name = "eAioAgilityBotZaros", servers = { "Zaros" }, version = "0.2")

public class eMain extends TaskScript {

    private List tasks = new ArrayList();

    public static eAioAgilityBot.eMain.State courseName;
    public static String status = null;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    public static int startMarks, totalMarks;
    public static int count;
    public enum State {
        CANIFIS,
        SEERS,
        POLLNIVNEACH,
        WAITING,
    }

    @Override
    public void onExecute() {
        tasks.addAll(Arrays.asList(new eCanifisR(ctx), new eSeersR(ctx), new ePollnivneachR(ctx)));// Adds our tasks to our {task} list for execution

        System.out.println("Started eAioAgilityBot!");
        startMarks = ctx.inventory.populate().filter(11849).population(true);
        startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.AGILITY);//paint
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.AGILITY);//paint
        count = 0;
        totalMarks = 0;
        status = "Setting up script";

        eAioAgilityBot.eGui.eGuiDialogue();
        if (eGui.courseName == "Canifis") {
            courseName = eAioAgilityBot.eMain.State.CANIFIS;
        } else if (eGui.courseName == "Seers") {
            courseName = State.SEERS;
        } else if (eGui.courseName == "Pollnivneach") {
            courseName = eAioAgilityBot.eMain.State.POLLNIVNEACH;
        } else {
            courseName = State.WAITING;
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
        super.onProcess();// Needed for the TaskScript to process the tasks
        final Pathing pathing = ctx.pathing;

        if (!pathing.running() && pathing.energyLevel() >= 50) {
            ctx.updateStatus("Turning run ON");
            pathing.running(true);
            ctx.sleep(200);
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
        totalMarks = ctx.inventory.populate().filter(11849).population(true) - startMarks;
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")" + " Current: " + currentSkillLevel, 15, 165);
        //g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("MOG collected: " + totalMarks + " (" + ctx.paint.valuePerHour(totalMarks, startTime) + ")", 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Laps count: " + count, 15, 210);
        g.drawString("Status: " + status, 15, 225);
    }

    @Override
    public void onChatMessage(ChatMessage m) {
        if (m.getMessage() != null) {
            String message = m.getMessage().toLowerCase();
            if (message.contains(ctx.players.getLocal().getName().toLowerCase())) {
                ctx.updateStatus(currentTime() + " Someone asked for you");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            } else if (message.contains("lap count is")) {
                count++;
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