package eMagicPro;

import java.awt.Color;
import java.awt.Graphics;

import eJadSlayer.ExchangeTask;
import net.runelite.api.ChatMessageType;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.queries.SimpleEntityQuery;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;

import simple.hooks.wrappers.SimpleNpc;
import simple.robot.script.Script;


@ScriptManifest(author = "Esmaabi", category = Category.MAGIC, description = "Magic training bot for fast AFK magic xp.<br> You must have required runes and target nearby. Scrip will start splashing target and alching specific item. <br> Choose spell you want to auto attack, have auto retaliate activated and required alching supplies in inventory.", discord = "Esmaabi#5752",
        name = "eMagicPro", servers = { "Zaros" }, version = "1.5")

public class eMain extends Script{


    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int currentExp;
    private int count;
    static String status = null;
    public final int itemName = 558; //mind rune
    static String[] npcName;
    public static eMain.State playerState;
    private long lastAnimation = -1;

    enum State{
        SPLASHING,
        ALCHING,
        WAITING,
    }

    @Override
    public void onExecute() {
        System.out.println("Started eMagicPro!");
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MAGIC);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);
        count = 0;
        npcName = null;
        eGui.returnSuicide = -1;
        eGui.returnMode = -1;
        status = "Setting up config";

        this.ctx.updateStatus("-------------------");
        this.ctx.updateStatus("     eMagicPro     ");
        this.ctx.updateStatus("-------------------");

        //gui
        eGui.eGuiDialogueMode();
        if (eGui.returnMode == 0) {
            playerState = State.SPLASHING;
            if (playerState == State.SPLASHING) {
                eGui.eGuiDialogueTarget();
            }
        } else if (eGui.returnMode == 1) {
            playerState = State.ALCHING;
            if (playerState == State.ALCHING) {
                eGui.eGuiDialogueTarget();
                eGui.eGuiDialogueSuicide();
            }
        } else if (eGui.returnMode == -1) {
            playerState = State.WAITING;
        }

        if (eGui.returnNpc == 0) {
            npcName = new String[]{"Duck"};
        } else if (eGui.returnNpc == 1) {
            npcName = new String[]{"Rat"};
        } else if (eGui.returnNpc == 2) {
            npcName = new String[]{"Man"};
        } else if (eGui.returnNpc == 3) {
            npcName = new String[]{"Woman"};
        } else if (eGui.returnNpc == 4) {
            npcName = new String[]{"Goblin"};
        } else if (eGui.returnNpc == 5) {
            npcName = new String[]{"Imp"};
        } else if (eGui.returnNpc == 6) {
            npcName = new String[]{"Chicken"};
        } else if (eGui.returnNpc == 7) {
            npcName = new String[]{"Cow"};
        } else if (eGui.returnNpc == -1) {
            npcName = null;
        }

    }

    @Override
    public void onProcess() {

        if (playerState == State.ALCHING || playerState == State.SPLASHING) {
            if (currentExp != this.ctx.skills.experience(SimpleSkills.Skills.MAGIC)) {
                count++;
                currentExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);
            }
        }

        if (playerState == State.ALCHING) {

            if (ctx.players.population() == 1) {
                if (!ctx.players.getLocal().isAnimating()) {
                    alchingItem();
                } else if (ctx.players.getLocal().getAnimation() == 713) {
                    splashingNpc();
                } else {
                    alchingItem();
                }
            } else if (ctx.players.population() > 1 && eGui.returnSuicide == 0) {
                if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 2000))) {
                    ctx.updateStatus("Players around -> starting anti-ban");
                    splashingNpc();
                } else if (ctx.players.getLocal().isAnimating()) {
                    lastAnimation = System.currentTimeMillis();
                }
            } else if (ctx.players.population() > 1 && eGui.returnSuicide == 1) {
                if (!ctx.players.getLocal().isAnimating()) {
                    alchingItem();
                } else if (ctx.players.getLocal().getAnimation() == 713) {
                    splashingNpc();
                } else {
                    alchingItem();
                }
            }

        } else if (playerState == State.WAITING) {
            ctx.updateStatus("Please choose alching or splashing");

        } else if (playerState == State.SPLASHING) {
            if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 2000))) {
                splashingNpc();
            } else if (ctx.players.getLocal().isAnimating()) {
                lastAnimation = System.currentTimeMillis();
            }
        }
    }

    public void splashingNpc() {
        SimpleNpc castOn = ctx.npcs.populate().filter(npcName).nearest().next();
        status = "Casting on NPC";
        if (castOn != null && castOn.validateInteractable()) {
            castOn.click("Attack");
        } else {
            status = "NPC not found";
            ctx.updateStatus("NPC not found");
            ctx.updateStatus("Stopping script");
            ctx.stopScript();
        }
    }

    public void alchingItem() {
            status = "Alching item";
            ctx.magic.castSpellOnItem("High Level Alchemy", itemName);
    }
    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        playerState = null;
        npcName = null;
        eGui.returnSuicide = -1;

        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("Thank You & Good Luck!");
        this.ctx.updateStatus("----------------------");
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        //System.out.println(chatMessage.getMessage());
        //System.out.println(chatMessage.getType());

        if (chatMessage.getType() == ChatMessageType.PUBLICCHAT
                && chatMessage.getMessage().contains(new String(ctx.players.getLocal().getName()))) {
            ctx.updateStatus("Stopping script");
            ctx.updateStatus("Someone asked for you");
            ctx.stopScript();
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
