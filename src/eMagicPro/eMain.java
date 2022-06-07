package eMagicPro;

import java.awt.Color;
import java.awt.Graphics;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;

import simple.hooks.wrappers.SimpleNpc;
import simple.robot.script.Script;


@ScriptManifest(author = "Esmaabi", category = Category.MAGIC, description = "Magic training bot for fast AFK magic xp.<br> You must have required runes and target nearby. Scrip will start splashing target and alching spesific item. <br> Choose spell you want to auto attack, have auto retaliete ON and have alching supplies in inventory.", discord = "Esmaabi#5752",
        name = "eMagicPro", servers = { "Zaros" }, version = "0.7")

public class eMain extends Script{


    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    public final int itemName = 558; //mind rune
    public final int npcName = 1838; //duck



    @Override
    public void onExecute() {
        System.out.println("Started eMagicPro!");
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MAGIC);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);
        count = 0;
        WorldPoint playerLocation = ctx.players.getLocal().getLocation();

        this.ctx.updateStatus("-------------------");
        this.ctx.updateStatus("     eMagicPro     ");
        this.ctx.updateStatus("-------------------");

    }

    @Override
    public void onProcess() {
        if (ctx.players.populate().isEmpty()) {
            if (ctx.players.getLocal().getAnimation() == -1) {
                status = "Starting loop";
                ctx.magic.castSpellOnItem("High Level Alchemy", itemName);

            } else if (ctx.players.getLocal().getAnimation() == 713) {
                SimpleNpc castOn = ctx.npcs.populate().filter(npcName).nearest().next();
                status = "Casting on NPC";
                if (castOn != null && castOn.validateInteractable()) {
                    castOn.click("Attack", "Duck");
                    count++;
                } else {
                    status = "NPC not found";
                    ctx.stopScript();
                }

            } else if (ctx.players.getLocal().getAnimation() == 711) {
                status = "Alching item";
                ctx.magic.castSpellOnItem("High Level Alchemy", itemName);
            }

        } else {
            if (ctx.players.getLocal().getAnimation() == -1) {
                SimpleNpc castOn = ctx.npcs.populate().filter(npcName).nearest().next();
                status = "Casting on NPC";
                if (castOn != null && castOn.validateInteractable()) {
                    castOn.click("Attack", "Duck");
                    count++;
                } else {
                    status = "NPC not found";
                    ctx.stopScript();
                }
            }
        }
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;

        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("Thank You & Good Luck!");
        this.ctx.updateStatus("----------------------");
    }

    @Override
    public void onChatMessage(ChatMessage e) {
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
        g.drawString("eMagicPro by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MAGIC);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillexpPhour = (int)((SkillExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Counted action: " + count + " time(s)", 15, 210);
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
