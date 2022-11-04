package eGlassblowingBot;

import simple.hooks.filters.SimpleShop;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.LoopingScript;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.Task;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.Magic;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleNpc;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(author = "Esmaabi", category = Category.CRAFTING,
        description = "<br>Most effective glassblowing bot on Zaros! <br><br><b>Features & recommendations:</b><br><br>" +
        "<ul><li>You must have enough of fire & astral runes, glassblowing pipe & coins in inventory;</li>" +
        "<li>You must wield <b>any air staff</b>;</li>" +
        "<li>You must start near charter trader crewmembers;</li>",
        discord = "Esmaabi#5752",
        name = "eGlassblowingBot", servers = { "Zaros" }, version = "0.1")

public class eMain extends TaskScript implements LoopingScript {

    //vars
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    private int currentExp;
    static String status = null;
    private long lastAnimation = -1;

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

        System.out.println("Started eGlassblowingBot!");



        this.ctx.updateStatus("--------------- " + currentTime() + " ---------------");
        this.ctx.updateStatus("-------------------------------");
        this.ctx.updateStatus("       eGlassblowingBot      ");
        this.ctx.updateStatus("-------------------------------");

        status = "Setting up bot";
        this.startTime = System.currentTimeMillis();
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.CRAFTING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.CRAFTING);
        currentExp = this.ctx.skills.experience(SimpleSkills.Skills.CRAFTING);// for actions counter by xp drop
        count = 0;
        ctx.viewport.angle(270);
        ctx.viewport.pitch(true);

    }

    @Override
    public void onProcess() {
        super.onProcess();

        if (currentExp != this.ctx.skills.experience(SimpleSkills.Skills.CRAFTING)) {
            count++;
            currentExp = this.ctx.skills.experience(SimpleSkills.Skills.CRAFTING);
        }

        if (ctx.magic.spellBook() != Magic.SpellBook.LUNAR) {
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            ctx.game.tab(Game.Tab.INVENTORY);
            ctx.game.tab(Game.Tab.MAGIC);
            status = "Lunar spellbook required!";
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

        if (!ctx.npcs.populate().filter(9317).filterHasAction("Trade").isEmpty()) {

            if (!ctx.inventory.populate().filter(1785, 995, 554, 9075).isEmpty()) {

                if (ctx.inventory.populate().filter(1775).population() == 0 && (ctx.inventory.populate().filter(1783).population() == 0
                        || ctx.inventory.populate().filter(1781).population() == 0)) {
                    if (!ctx.shop.shopOpen()) {
                        status = "Buying supplies";
                        SimpleNpc trader = ctx.npcs.populate().filter("Trader Crewmember").filterHasAction("Trade").nearest().next();
                        if (trader != null && trader.validateInteractable()) {
                            trader.click("Trade", "Trader Crewmember");
                            ctx.onCondition(() -> ctx.shop.shopOpen(), 1600);
                        }

                    }

                    if (ctx.shop.shopOpen()) {

                        if (ctx.inventory.populate().filter(1919, 4527, 4522, 229, 6667, 567, 4542).population() != 0) {
                            status = "Selling crafted products";
                            ctx.shop.sell(4542, SimpleShop.Amount.FIFTY);
                            ctx.onCondition(() -> ctx.inventory.populate().filter(1919, 4527, 4522, 229, 6667, 567, 4542).population() == 0, 1600);

                        } else {

                            if (ctx.inventory.populate().filter(1783).population() == 0) {
                                status = "Buying buckets of sand";
                                ctx.shop.buy(1783, SimpleShop.Amount.TEN);
                                ctx.onCondition(() -> ctx.inventory.populate().filter(1783).population() == 10, 1600);
                            }

                            if (ctx.inventory.populate().filter(1781).population() == 0) {
                                status = "Buying soda ashes";
                                ctx.shop.buy(1781, SimpleShop.Amount.TEN);
                                ctx.onCondition(() -> ctx.inventory.populate().filter(1781).population() == 10, 1600);
                            }

                            if (ctx.inventory.populate().filter(1783).population() == 10 && ctx.inventory.populate().filter(1781).population() == 10) {
                                status = "Closing shop";
                                ctx.shop.closeShop();
                            }
                        }
                    }
                }

                if (ctx.inventory.populate().filter(1775).population() == 0 && ctx.inventory.populate().filter(1783).population() != 0
                        && ctx.inventory.populate().filter(1781).population() != 0) {

                    if (ctx.shop.shopOpen()) {
                        ctx.shop.closeShop();
                    }

                    if (ctx.inventory.populate().filter(1781).population() != 0 && ctx.inventory.populate().filter(1783).population() != 0
                            && ctx.inventory.populate().filter(1775).population() == 0 && !ctx.shop.shopOpen()) {
                        status = "Making molten glass";
                        ctx.magic.castSpellOnce("Superglass Make");
                        ctx.sleep(1200);
                        ctx.onCondition(() -> !ctx.inventory.populate().filter(1775).isEmpty(), 1600);
                    }

                }

                if (ctx.inventory.populate().filter(1775).population() != 0 && !ctx.shop.shopOpen()) {
                    SimpleItem pipe = ctx.inventory.populate().filter(1785).next();
                    SimpleItem moltenGlass = ctx.inventory.populate().filter(1775).next();
                    status = "Glassblowing";

                    if (ctx.players.getLocal().getAnimation() != 884 && (System.currentTimeMillis() > (lastAnimation + 3000))) {

                        if (pipe != null && pipe.validateInteractable() && moltenGlass != null && moltenGlass.validateInteractable() && !ctx.dialogue.dialogueOpen()) {
                            pipe.click(0);
                            ctx.sleep(600);
                            moltenGlass.click(0);
                            ctx.sleep(800);
                        }

                        if (ctx.dialogue.dialogueOpen()) {
                            ctx.keyboard.clickKey(KeyEvent.VK_SPACE);
                            ctx.onCondition(() -> ctx.players.getLocal().isAnimating(), 1600);
                        }

                    } else if (ctx.players.getLocal().isAnimating()) {
                        lastAnimation = System.currentTimeMillis();
                    }
                }
            }

        } else {
                status = "Trader Crewmember not found";
                ctx.updateStatus(currentTime() + " Trader Crewmember not found");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.sleep(2400);
                ctx.stopScript();
        }

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
            } else if (message.contains("don't have enough")) {
                ctx.updateStatus(currentTime() + " Out of runes or coins");
                ctx.updateStatus(currentTime() + " Stopping script");
                ctx.stopScript();
            }
        }
    }

    @Override
    public int loopDuration() {
        return 200;
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
        g.drawString("eGlassblowingBot by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.CRAFTING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.CRAFTING);
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