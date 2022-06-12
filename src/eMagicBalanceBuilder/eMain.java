package eMagicBalanceBuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;

import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.script.Script;


@ScriptManifest(author = "Esmaabi", category = Category.CONSTRUCTION, description = "<br>"
        + "It's fast & cheap construction training method for Zaros.<br> "
        + "Script will build Magical balance 1 in you POH<br><br>"
        + "Before starting script:<br>"
        + "1. You must have enough Air, Fire, Earth and Water runes;<br>"
        + "2. You must have saw and hammer in invenotry;<br>"
        + "3. You must have a Games Room in POH<br>", discord = "Esmaabi#5752",
        name = "eMagicBalanceBuilder", servers = { "Zaros" }, version = "1")

public class eMain extends Script{


    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int currentExp;
    private int count;
    static String status = null;


    @Override
    public void onExecute() {
        System.out.println("Started eMagicBalanceBuilder!");
        startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.CONSTRUCTION);//paint
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.CONSTRUCTION);//paint
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.CONSTRUCTION);// for actions counter by xp drop
        count = 0;
        status = "Setting up config";

        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus(" eMagicBalanceBuilder ");
        this.ctx.updateStatus("----------------------");

    }

    @Override
    public void onProcess() {
        SimpleObject buildSpace = ctx.objects.populate().filter("Elemental balance").filterHasAction("Remove").next();
        //SimpleObject removeBalancer = ctx.objects.populate().filter("Elemental balance space").filterHasAction("Build").next();
        if (buildSpace != null) {
            removeMagicBalancer();
        } else {
            buildMagicBalancer();
        }

        if (currentExp != this.ctx.skills.experience(SimpleSkills.Skills.CONSTRUCTION)) {
            count++;
            currentExp = this.ctx.skills.experience(SimpleSkills.Skills.CONSTRUCTION);
        }
    }


    public void buildMagicBalancer() {
        SimpleObject buildTask = ctx.objects.populate().filter("Elemental balance space").filterHasAction("Build").next();
        SimpleWidget widgetBuild = ctx.widgets.getWidget(458, 4);
        status = "Building Magic Balancer";
        if (buildTask != null && buildTask.validateInteractable() && widgetBuild == null) {
            buildTask.click("Build", "Elemental balance space");
            ctx.onCondition(() -> widgetBuild != null, 1600);
        } else if (widgetBuild != null) {
            status = "Building Magical balance 1";
            System.out.println(widgetBuild);
            if (widgetBuild != null) {
                widgetBuild.click("Build", "Magical balance 1");
                ctx.onCondition(() -> buildTask == null, 1600);
            }
        }
    }

    public void removeMagicBalancer() {
        SimpleObject removeTask = ctx.objects.populate().filter("Elemental balance").filterHasAction("Remove").next();
        status = "Removing Magic Balancer";
            if (removeTask != null && removeTask.validateInteractable()) {
                removeTask.click("Remove", "Elemental balance");
                ctx.onCondition(() -> ctx.dialogue.dialogueOpen(), 1600);
                if (ctx.dialogue.dialogueOpen()) {
                    ctx.dialogue.clickDialogueOption(1);
                    ctx.onCondition(() -> removeTask == null, 1600);
                }
            }
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.currentExp = 0;
        this.count = 0;

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
            } else if (message.contains("the right materials")) {
                ctx.updateStatus(currentTime() + " You are out of runes");
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
        g.fillRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawRect(5, 120, 200, 110);
        g.setColor(PhilippineRed);
        g.drawString("eMagicBalanceBuilder by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.CONSTRUCTION);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.CONSTRUCTION);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Actions made: " + count, 15, 210);
        g.drawString("Status: " + status, 15, 225);
    }

    private String formatTime(long ms) {
        long s = ms / 1000L;
        long m = s / 60L;
        long h = m / 60L;
        s %= 60L;
        m %= 60L;
        h %= 24L;
        return String.format("%02d:%02d:%02d", new Object[] { Long.valueOf(h), Long.valueOf(m), Long.valueOf(s) });
    }

}
