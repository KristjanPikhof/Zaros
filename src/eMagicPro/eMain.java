package eMagicPro;

import java.awt.Color;
import java.awt.Graphics;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;

import simple.hooks.wrappers.SimpleNpc;
import simple.robot.api.ClientContext;
import simple.robot.script.Script;


@ScriptManifest(author = "Esmaabi", category = Category.MAGIC, description = "<br>"
        + "It's fast & safe magic training method script for Zaros. "
        + "You can splash chosen target and alch specified item. "
        + "It's possible to select anti-ban option during setup.<br><br>"
        + "Script will offer two working modes:<br> \"<b>Only Splashing</b>\" or \"<b>Alch & Splash</b>\"<br><br>"
        + "Before starting script:<br>"
        + "1. You must select autocast spell from combat tab;<br>"
        + "2. You must magic attack: <b>-65</b> & auto retaliate activated;<br>"
        + "3. Also have required runes in inventory (and items if alching).<br>", discord = "Esmaabi#5752",
        name = "eMagicProZaros", servers = { "Zaros" }, version = "3.1")

public class eMain extends Script{


    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int currentExp;
    private int count;
    static String status = null;
    static String npcName;
    public static State playerState;
    private long lastAnimation = -1;
    private boolean started;

    enum State{
        SPLASHING,
        ALCHING,
        WAITING,
    }

    @Override
    public void onExecute() {
        System.out.println("Started eMagicPro!");
        started = false;
        startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.MAGIC);//paint
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);//paint
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.MAGIC);// for actions counter by xp drop
        count = 0;
        npcName = null;
        eGui.returnItem = null;
        eGui.returnSuicide = -1;
        eGui.returnMode = -1;
        status = "Setting up config";

        this.ctx.updateStatus("---------------------");
        this.ctx.updateStatus("      eMagicPro      ");
        this.ctx.updateStatus("---------------------");

        //gui choosing gaming mode
        eGui.eGuiDialogueMode();
        if (eGui.returnMode == 0) {
            playerState = State.SPLASHING;
            ctx.updateStatus(currentTime() + " Starting splashing task");
            if (playerState == State.SPLASHING) {
                eGui.eGuiDialogueTarget();
            }
        } else if (eGui.returnMode == 1) {
            playerState = State.ALCHING;
            ctx.updateStatus(currentTime() + " Starting alching task");
            if (playerState == State.ALCHING) {
                eGui.eGuiDialogueTarget();
                eGui.eGuiDialogueItem();
                eGui.eGuiDialogueSuicide();
            }
        } else if (eGui.returnMode == -1) {
            playerState = State.WAITING;
        }

        // choosing NPC
        if (eGui.returnNpc != null) {
            npcName = eGui.returnNpc;
        } else {
            npcName = null;
        }

        // if script started
        started = eGui.returnMode != -1 && npcName != null;

    }

    @Override
    public void onProcess() {
        if (started) {
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
                    status = "Anti-ban activated";
                    if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 3000))) {
                        ctx.updateStatus(currentTime() + " Players around -> anti-ban");
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
                ctx.updateStatus(currentTime() + " Please choose task");

            } else if (playerState == State.SPLASHING) {
                if (!ctx.players.getLocal().isAnimating() && (System.currentTimeMillis() > (lastAnimation + 3000))) {
                    splashingNpc();
                } else if (ctx.players.getLocal().isAnimating()) {
                    lastAnimation = System.currentTimeMillis();
                }
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
            ctx.updateStatus(currentTime() + " NPC not found");
            ctx.updateStatus(currentTime() + " Stopping script");
            ctx.stopScript();
        }
    }

    public void alchingItem() {
        if (ctx.inventory.populate().filter(getItem(eGui.returnItem)).population() != 0) {
            ctx.updateStatus(currentTime() + " Out of items to alch");
            ctx.updateStatus(currentTime() + " Changing task");
            playerState = State.SPLASHING;
        } else {
            status = "Alching item";
            ctx.magic.castSpellOnItem("High Level Alchemy", getItem(eGui.returnItem));
        }
    }

    public static int getItem(String... itemName) { //Scans for the name of item instead of exact name and gets itemID
        return ClientContext.instance().inventory.populate()
                .filter(p -> Stream.of(itemName).anyMatch(arr -> p.getName().toLowerCase().contains(arr.toLowerCase())))
                .next().getId();
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        npcName = null;
        eGui.returnItem = null;
        eGui.returnSuicide = -1;
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
