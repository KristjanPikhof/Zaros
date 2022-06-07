package eAioAgilityBot;

import eAioAgilityBot.tasks.eCanifisR;
import eAioAgilityBot.eGui;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Pathing;
import simple.hooks.wrappers.SimpleGroundItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(author = "Esmaabi", category = Category.AGILITY, description = "AIO agility training on rooftops courses", discord = "Esmaabi#5752",
        name = "eAioAgilityBot", servers = { "Zaros" }, version = "0.1")

public class eMain extends TaskScript {

    private List tasks = new ArrayList();

    public static eAioAgilityBot.eMain.State courseName;
    public String status;
    public long startTime;
    public int startExperience, startMarks;
    public static int count;
    public enum State {
        CANIFIS,
        WAITING,
    }

    @Override
    public void onExecute() {
        tasks.addAll(Arrays.asList(new eCanifisR(ctx)));// Adds our tasks to our {task} list for execution

        System.out.println("Started eAioAgilityBot!");
        this.startExperience = ctx.skills.experience(SimpleSkills.Skills.AGILITY);
        this.startMarks = ctx.inventory.populate().filter(11849).population(true);
        this.startTime = System.currentTimeMillis();
        count = 0;

        eAioAgilityBot.eGui.eGuiDialogue();
        if (eGui.courseName == 0) {
            courseName = eAioAgilityBot.eMain.State.CANIFIS;
        } else {
            courseName = State.WAITING;
        }
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

        if (!ctx.groundItems.populate().filter(11849).filter((i) -> pathing.reachable(i.getLocation())).isEmpty()) {
            final SimpleGroundItem i = ctx.groundItems.nearest().next();
            ctx.updateStatus("Picking up MOG");
            if (i != null && i.validateInteractable()) {
                final int cached = ctx.inventory.populate().filter(11849).population(true);
                if (i.click("Take")) {
                    ctx.onCondition(() -> cached < ctx.inventory.populate().filter(11849).population(true), 250, 12);
                }
            }
            return;
        }
    }

    @Override
    public void onTerminate() {
        courseName = State.WAITING;

    }
    @Override
    public void paint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.BLACK);
        g.fillRect(5, 2, 192, 86);
        g.setColor(Color.decode("#D93B26"));
        g.drawRect(5, 2, 192, 86);
        g.drawLine(8, 24, 194, 24);

        g.setColor(Color.decode("#1C6497"));
        g.drawString("eAioAgilityBot v. " + "0.1", 12, 20);
        g.drawString("Time: " + ctx.paint.formatTime(System.currentTimeMillis() - startTime), 14, 42);
        g.drawString("Status: " + status, 14, 56);
        int totalExp = ctx.skills.experience(SimpleSkills.Skills.AGILITY) - startExperience;
        g.drawString("XP: " + ctx.paint.formatTime(totalExp) + " (" + ctx.paint.valuePerHour(totalExp, startTime) + ")", 14, 70);
        int totalMarks = ctx.inventory.populate().filter(11849).population(true) - startMarks;
        g.drawString("MOG: " + ctx.paint.formatTime(totalMarks) + " (" + ctx.paint.valuePerHour(totalMarks, startTime) + ")", 14, 84);

    }

    @Override
    public void onChatMessage(ChatMessage e) {
    }

}