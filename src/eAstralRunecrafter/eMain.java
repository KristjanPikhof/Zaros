package eAstralRunecrafter;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.script.Script;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.robot.utils.WorldArea;

@ScriptManifest(author = "Esmaabi", category = Category.RUNECRAFTING, description = "Crafts astral runes in most effective way to train Runecrafting!", discord = "Esmaabi#5752",
        name = "eAstralRunecrafter", servers = { "Zaros" }, version = "1")

public class eMain extends Script{

    private final WorldPoint START_TILE = new WorldPoint(3214, 2798, 0);
    private final WorldPoint SECOND_TILE = new WorldPoint(3214, 2801, 1);
    private final WorldPoint THIRD_TILE = new WorldPoint(3231, 2803, 1);
    private final WorldPoint FOURTH_TILE = new WorldPoint(3253, 2808, 1);
    private final WorldPoint FIFTH_TILE = new WorldPoint(3255, 2802, 1);
    private final WorldPoint SIXT_TILE = new WorldPoint(3262, 2791, 3);
    private final WorldPoint SEVENTH_TILE = new WorldPoint(3262, 2787, 3);
    private final WorldPoint SEVENTH_TILE2 = new WorldPoint(3262, 2783, 3);
    private final WorldPoint EIGHT_TILE = new WorldPoint(3257, 2781, 1);
    private final WorldPoint LAST_TILE = new WorldPoint(3251, 2781, 0);
    private final WorldArea EDGE = new WorldArea(new WorldPoint(33072, 3507, 0), new WorldPoint(3111, 3464, 0));
    private final WorldArea DONOR = new WorldArea(new WorldPoint(1386, 8896, 0), new WorldPoint(1367, 9008, 0));
    private final WorldArea ASTRAL = new WorldArea(new WorldPoint(2137, 3875, 0), new WorldPoint(2170, 3846, 0));



    private Teleporter teleporter;

    private long startTime = 0L;
    private long startingRUNECRAFTINGLevel;
    private long startingRUNECRAFTINGExp;
    private int count;
    static String status = null;
    static int enter = KeyEvent.VK_ENTER;
    static int space = KeyEvent.VK_SPACE;

    static String[] banks = {"Bank chest", "Bank booth"};
    static String[] bankers = {"Emerald Benedict", "Banker"};


    @Override
    public void onExecute() {
        System.out.println("Started eAstralRunecrafter Pro!");
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis(); //paint
        this.startingRUNECRAFTINGLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.RUNECRAFT);
        this.startingRUNECRAFTINGExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);

        count = 0;


        this.ctx.updateStatus("--------------------------");
        this.ctx.updateStatus("  eAstralRunecrafter Pro  ");
        this.ctx.updateStatus("--------------------------");
        //ctx.keyboard.sendKeys("::home");
    }

    @Override
    public void onProcess() {
        if (EDGE.containsPoint(ctx.players.getLocal().getLocation()) || DONOR.containsPoint(ctx.players.getLocal().getLocation()))   {
            if (ctx.inventory.populate().filter(7936).population() == 0) {
                status = "Searching for bank";
                if (ctx.objects.populate().filter("Bank booth").nextNearest() != null) {
                    status = "Bank found (object)";
                    SimpleObject bank = ctx.objects.populate().filter(banks).nextNearest();
                    if (bank != null && bank.validateInteractable()) {
                        bank.click("Last-preset");
                        ctx.sleepCondition(() -> ctx.pathing.inMotion(), 800);
                    }
                } else {
                    status = "Bank not found";
                }
            } else if (ctx.inventory.populate().filter(7936).population() != 0) {
                if (!teleporter.opened()) {
                    ctx.magic.castSpellOnce("Monsters Teleport");
                } else {
                    teleporter.teleportStringPath("Favorites", "Runecrafting: Astral Altar");
                    ctx.onCondition(() -> ASTRAL.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                    count++;
                }
            }

        } else if (ASTRAL.containsPoint(ctx.players.getLocal().getLocation())) {
            if (ctx.inventory.populate().filter(7936).population() == 0) {
                ctx.magic.castSpellOnce("Home Teleport");
            } else {
                SimpleObject altar = ctx.objects.populate().filter(34771).nextNearest();
                if (altar != null && altar.validateInteractable()) {
                    altar.click("Craft-rune", "Altar");
                    //ctx.onCondition(() -> ctx.inventory.populate().filter(9075) != null, 1200);
                }
            }
        } else {
            ctx.stopScript();
            ctx.sendLogout();
        }
    }

    @Override
    public void onTerminate() {
        this.startingRUNECRAFTINGLevel = 0L;
        this.startingRUNECRAFTINGExp = 0L;
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
        g.fillRect(5, 120, 200, 95);
        g.setColor(PhilippineRed);
        g.drawRect(5, 120, 200, 95);
        g.setColor(PhilippineRed);
        g.drawString("eAstralRunecrafter by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentRUNECRAFTINGLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.RUNECRAFT);
        long currentRUNECRAFTINGExp = this.ctx.skills.experience(SimpleSkills.Skills.RUNECRAFT);
        long RUNECRAFTINGLevelsGained = currentRUNECRAFTINGLevel - this.startingRUNECRAFTINGLevel;
        long RUNECRAFTINGExpGained = currentRUNECRAFTINGExp - this.startingRUNECRAFTINGExp;
        long RUNECRAFTINGexpPhour = (int)((RUNECRAFTINGExpGained * 3600000D) / runTime);
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingRUNECRAFTINGLevel + " (+" + RUNECRAFTINGLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentRUNECRAFTINGLevel, 15, 180);
        g.drawString("Exp gained: " + RUNECRAFTINGExpGained + " (" + (RUNECRAFTINGexpPhour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Runs completed: " + count + " time(s)", 15, 210);


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
