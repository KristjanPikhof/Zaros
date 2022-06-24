package eJadSlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleBank;
import simple.hooks.filters.SimplePrayers.Prayers;
import simple.hooks.filters.SimpleSkills.Skills;
import simple.hooks.queries.SimpleEntityQuery;
import simple.hooks.queries.SimplePlayerQuery;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.LoopingScript;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.Task;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.hooks.wrappers.*;
import simple.robot.api.ClientContext;

@ScriptManifest(author = "Overload, reworked by Esmaabi", category = Category.MINIGAMES, description = "Please disable all plugins.<br>Start anywhere with:<br> 1) ranged setup &<br> 2) ranging potions(4), Super defence potions(4) and Prayer potions(4) in bank.",
        name = "Jad Slayer v2", servers = {"Zaros"}, version = "2.0", discord = "Esmaabi#5752" )

public class KillTask extends TaskScript implements LoopingScript {

    //Time
    private long startTime = 0L;

    //Stats
    private int jadKilled = 0;
    private int playerDeath = 0;
    static int space = KeyEvent.VK_SPACE;
    static int VK_1 = KeyEvent.VK_1;

    //private int fireCape = ctx.inventory.populate().filter("Fire cape").population();

    //Configs
    private static final Random r = new Random();
    static int randomSleep = r.nextInt((2000 - 1500) + 1) + 1500;
    static Teleporter teleporter;
    private BufferedImage backgroundImage;
    boolean jadHasBeenKilled = false;
    boolean prayerRigour = false;

    //NPC
    private SimpleNpc jad;
    private SimpleNpc healerInteractingNearest;

    //State Machine
    enum State {
        BANKING,
        PRAYERS,
        EXCHANGE_CAPES,
        WAITING
    }

    //Tasks
    List<Task> tasks = new ArrayList<Task>();

    public static State playerState;

    @Override
    public boolean prioritizeTasks() {
        return true;
    }

    @Override
    public List<Task> tasks() {
        return tasks;
    }

    @Override
    public void paint(Graphics g) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double onyxGained = (((double) jadKilled * 8064) * 2) / 300000;

        String onyxFormatted = decimalFormat.format(onyxGained);

        g.drawImage(backgroundImage, 0, 190, 185, 140, null);

        Color PhilippineRed = new Color(150, 15, 38);
        Color AndroidGreen = new Color(1, 135, 134, 127);
        Color DarkerYellow = new Color(120, 105, 20);
        long runTime = System.currentTimeMillis() - this.startTime;
        g.setColor(Color.BLACK);
        g.drawString("Overload - Jad Slayer", 35, 230);
        g.drawString("-> reworked by Esmaabi", 35, 240);
        g.drawString("Uptime: " + formatTime(runTime), 35, 255);
        g.drawString("TzTok-Jad killcount: " + jadKilled, 35, 270);
        g.setColor(PhilippineRed);
        g.drawString("Deaths: " + playerDeath, 35, 285);
        g.setColor(AndroidGreen);
        g.drawString("Onyx Gained: " + onyxFormatted, 35, 300);
        g.setColor(DarkerYellow);
        g.drawString("Status: " + playerState, 35, 315);
    }

    @Override
    public void onChatMessage(ChatMessage m) {
        String message = m.getMessage().toLowerCase();
             if (message.contains("victorious!") && message != null) {
                 jadHasBeenKilled = true;
             }
    }

    @Override
    public void onExecute() {

        // Adds our tasks to our {task} list for execution
        tasks.addAll(Arrays.asList(new BankTask(ctx)));

        //paint
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(KillTask.class.getResourceAsStream("parchment.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //config
        teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis();
        jadHasBeenKilled = false;
        playerState = State.WAITING;
        ctx.viewport.angle(180);
        ctx.viewport.pitch(true);

        //main gui
        ExchangeTask.ExchangeDialogue();
        if (ExchangeTask.returnValue == 1) {
            playerState = State.EXCHANGE_CAPES;
        } else if (ExchangeTask.returnValue == 0) {
            playerState = State.BANKING;
        } else if (playerState == null) {
            playerState = State.WAITING;
        } else {
            playerState = State.WAITING;
        }

        //prayer gui
        if (ExchangeTask.returnValue == 0) {
            ExchangeTask.prayerOptions();
            if (ExchangeTask.returnPrayer == 0) {
                prayerRigour = false;
            } else if (ExchangeTask.returnPrayer == 1) {
                prayerRigour = true;
            } else {
                prayerRigour = false;
            }
        }
    }

    @Override
    public void onProcess() {

        super.onProcess();

        if (playerState == State.EXCHANGE_CAPES) {
            ctx.updateStatus("Task: Cape exchange");
            exchangeCapes();
        } else if (jad == null && BankTask.jadEntrance.containsPoint(ctx.players.getLocal().getLocation())
                && playerState != State.WAITING && playerState != State.EXCHANGE_CAPES) {
            playerState = State.BANKING;
        } else if (playerState == State.WAITING) {
            ctx.updateStatus("Choose mode from menu");
        } else {
            playerState = State.PRAYERS;
            SimpleEntityQuery<SimpleNpc> bossEnemyDetect = ctx.npcs.populate().filter("Tztok-Jad").filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals(ctx.players.getLocal().getName()));
            jad = bossEnemyDetect.next();
            if (jad != null) {
                attackJad();
            }
            if (ctx.combat.health() == 0) {
                playerDeathDetect();
            }
            if (jadHasBeenKilled) {
                jadDeathDetect();
            }
        }
    }

    private void playerDeathDetect() {
        ctx.updateStatus("Player has died, starting banking functions.");
        playerDeath++;
        ctx.sleepCondition(() -> ctx.combat.health() > 0, 6000); // Wait until respawn
        playerState = State.BANKING;
    }

    private void jadDeathDetect() {
        ctx.updateStatus("TzHaar-Jad is killed");
        jadKilled++;
        disablePrayers();
        ctx.sleepCondition(() -> jad == null, 6000); // Wait until jad is not existing
        jadHasBeenKilled = false;
        playerState = State.BANKING;
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub

    }

    public void eatTask() {
        //Food handling
        String[] foodItems = {"Shark", "Lobster", "Monkfish", "pineapple", "pizza", "fish"};
        SimpleItem food = getItem(foodItems);

        if (ctx.combat.healthPercent() <= 40 && food != null && ctx.combat.health() > 0) {
            ctx.updateStatus("Restoring player HP.");
            food.click("Eat");
        }

        //Prayer handling
        String[] restores = {"restore", "sanfew", "prayer"};
        SimpleItem restore = getItem(restores);

        if (ctx.prayers.points() <= 15 && restore != null) {
            ctx.updateStatus("Restoring prayer.");
            restore.click("Drink");
        }

        //Stat pot handling
        String[] rangePotItems = {"ranging", "bastion"};
        SimpleItem rangePotion = getItem(rangePotItems);
        String[] defPotItems = {"Super defence", "defence"};
        SimpleItem defencePotion = getItem(defPotItems);

        if (ctx.skills.level(Skills.RANGED) == ctx.skills.realLevel(Skills.RANGED) + 9 && rangePotion != null) {
            ctx.updateStatus("Sipping ranging potion.");
            rangePotion.click("Drink");
        }

        if (ctx.skills.level(Skills.DEFENCE) == ctx.skills.realLevel(Skills.RANGED) + 14 && defencePotion != null) {
            ctx.updateStatus("Sipping defence potion.");
            defencePotion.click("Drink");
        }

    }

    public static SimpleItem getItem(String... itemName) { //Credits to trester I think? Scans for general name of item instead of having to write every exact match.
        return ClientContext.instance().inventory.populate()
                .filter(p -> Stream.of(itemName).anyMatch(arr -> p.getName().toLowerCase().contains(arr.toLowerCase())))
                .next();
    }

    public void attackJad() {
        if (jad != null && jad.validateInteractable()) {

            switch (jad.getAnimation()) {
                case 2656:
                    protectFromMagic();
                    attackHealers();
                    eatTask();
                    prayerFlick();
                    refocusJad();
                    distanceCheck();
                    ctx.viewport.turnTo(jad);
                    specialAttack();
                    runningEnable();
                    break;
                case 2652:
                    protectFromRange();
                    attackHealers();
                    eatTask();
                    prayerFlick();
                    refocusJad();
                    distanceCheck();
                    ctx.viewport.turnTo(jad);
                    specialAttack();
                    runningEnable();
                    break;
                case 2655:
                    protectFromMelee();
                    distanceSelfFromBoss();
                    prayerFlick();
                    ctx.viewport.turnTo(jad);
                    specialAttack();
                    runningEnable();
                    break;

                default:
                    prayerFlick();
                    distanceSelfFromBoss();
                    specialAttack();
            }
        }
    }

    private void protectFromMelee() {
        playerState = State.PRAYERS;
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MELEE, true);
        ctx.prayers.prayer(Prayers.STEEL_SKIN, true);
        ctx.prayers.prayer(Prayers.EAGLE_EYE, true);
        ctx.updateStatus("Melee detected. Distancing player from jad.");
    }

    private void protectFromRange() {
        playerState = State.PRAYERS;
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MISSILES, true);
    }

    void protectFromMagic() {
        playerState = State.PRAYERS;
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MAGIC, true);
    }

    private void prayerFlick() {

        if (!prayerRigour) {
            if (ctx.players.getLocal().getAnimation() == -1) {
                ctx.prayers.prayer(Prayers.STEEL_SKIN, true);
                ctx.prayers.prayer(Prayers.EAGLE_EYE, true);
            } else {
                ctx.prayers.prayer(Prayers.EAGLE_EYE, false);
            }
        } else {
            if (ctx.players.getLocal().getAnimation() == -1) {
                ctx.prayers.prayer(Prayers.RIGOUR, true);
            } else {
                ctx.prayers.prayer(Prayers.RIGOUR, false);
            }
        }
    }

    private void distanceCheck() {
        if (ctx.players.getLocal().getLocation().distanceTo(ctx.npcs.populate().filter("TzTok-Jad").nearest().next().getLocation()) <= 3) {
            ctx.updateStatus("Too close to boss, stepping away.");
            distanceSelfFromBoss();
        }
    }

    private void refocusJad() {
        //Check if healers are interacting with jad
        SimpleEntityQuery<SimpleNpc> healerInteractingWithJadDetect = ctx.npcs.populate().filter("Yt-HurKot").filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals("TzTok-Jad"));
        SimpleNpc healerInteractingWithJad = healerInteractingWithJadDetect.nearest().next();

        //Check if player is interacting with healers
        SimplePlayerQuery<SimplePlayer> playerInteractingWithHealerDetect = ctx.players.populate().filter(ctx.players.getLocal().getName()).filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals("Yt-HurKot"));
        SimplePlayer playerInteractingWithHealer = playerInteractingWithHealerDetect.nearest().next();

        //Check if player is interacting with jad
        SimplePlayerQuery<SimplePlayer> playerInteractingWithJadDetect = ctx.players.populate().filter(ctx.players.getLocal().getName()).filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals("TzTok-Jad"));
        SimplePlayer playerInteractingWithJad = playerInteractingWithJadDetect.nearest().next();

        //if healers are not interacting with jad && the player is NOT interacting with the healers A&& not interacting with Jad, attack jad instead.
        //OR if player isn't already interacting with jad && the healers are null, attack jad.
        if (healerInteractingWithJad == null && playerInteractingWithHealer == null && playerInteractingWithJad == null
                && ctx.npcs.populate().filter("Yt-HurKot").nearest().next() == null || playerInteractingWithJad == null
                && ctx.npcs.populate().filter("Yt-HurKot").nearest().next() == null && ctx.pathing.inMotion() == false) {
            if (jad.validateInteractable()) {
                ctx.updateStatus("Refocusing on jad. Left clicking now.");
                jad.click("attack");
            }
        }
    }

    public void attackHealers() {

        SimpleEntityQuery<SimpleNpc> healerDetect = ctx.npcs.populate().filter("Yt-HurKot").filter(n -> n.getInteracting() != null && n.getInteracting().getName().equals("TzTok-Jad"));

        //Check if healers are interacting with jad and hit them off.
        if (healerDetect != null && !healerDetect.isEmpty()) {
            healerInteractingNearest = healerDetect.nearest().next();
            if (healerInteractingNearest != null) {
                if (healerInteractingNearest.validateInteractable()) {
                    ctx.updateStatus("Grabbing aggro from the healers.");
                    healerInteractingNearest.click("attack");
                }
            }
        }
    }

    public void disablePrayers() {
        ctx.updateStatus("Disabling prayers");
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MAGIC, false);
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MISSILES, false);
        ctx.prayers.prayer(Prayers.PROTECT_FROM_MELEE, false);
        ctx.prayers.prayer(Prayers.STEEL_SKIN, false);
        ctx.prayers.prayer(Prayers.EAGLE_EYE, false);
        ctx.prayers.prayer(Prayers.RIGOUR, false);
    }

    public void distanceSelfFromBoss() {
        WorldPoint enemyLoc = jad.getLocation();
        WorldPoint newLoc = new WorldPoint(enemyLoc.getX(), enemyLoc.getY() - 4, 0); // -4 tiles away from the boss.

        if (!ctx.players.getLocal().getLocation().equals(newLoc)) {
            ctx.updateStatus("Getting to perfect distance");
            ctx.pathing.step(newLoc);
        }
    }

    private void specialAttack() {
        if (ctx.combat.getSpecialAttackPercentage() >= 55 && ctx.equipment.populate().filter(861, 12926, 20558) != null) {
            ctx.combat.toggleSpecialAttack(true);
        }
    }

    private void runningEnable() {
        if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
            ctx.pathing.running(true);
        }
    }

    public void exchangeCapes() {
        SimpleNpc fireCapeNPC = ctx.npcs.populate().filter(2180).nearest().next();
        SimpleObject banker = ctx.objects.populate().filter("Bank Chest").nearest().next(); // Grabs nearest banker at sand crabs

        if (ctx.inventory.populate().filter(6570).population() == 0) {
            if (banker != null && banker.validateInteractable() && !ctx.bank.bankOpen()) {
                ctx.updateStatus("Refilling on fire capes");
                banker.click("Use");
                ctx.onCondition(() -> ctx.bank.bankOpen(), 2400);
            } else if (ctx.bank.bankOpen()) {
                ctx.bank.depositInventory();
                ctx.sleep(800);
                ctx.bank.withdraw(6570, SimpleBank.Amount.ALL); // Fire Cape
                ctx.sleep(800);
                ctx.bank.closeBank();
                ctx.onCondition(() -> ctx.inventory.population() >= 1, 2400);
            } else if (ctx.bank.bankOpen() && ctx.bank.populate().filter(6570).population() == 0 && ctx.inventory.populate().filter(6570).population() == 0) {
                ctx.updateStatus("No fire capes detected in bank, time to kill Jad.");
                ctx.bank.closeBank();
                ctx.sleep(1000);
                playerState = State.BANKING;
            }
        } else {
            if (fireCapeNPC.validateInteractable() && fireCapeNPC != null && !ctx.dialogue.dialogueOpen()) {
                fireCapeNPC.click("Exchange fire cape");
                ctx.sleepCondition(() -> ctx.pathing.inMotion(), 2400);
            } else if (ctx.dialogue.dialogueOpen()) {
                ctx.dialogue.clickDialogueOption(1);
                ctx.keyboard.clickKey(space);
                ctx.onCondition(() -> !ctx.dialogue.dialogueOpen(), 800);
            }
        }
    }


    @Override
    public int loopDuration() {
        // TODO Auto-generated method stub
        return 150;
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