package eAnglerFisherZaros;

import java.awt.Color;
import java.awt.Graphics;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.simplebot.Game;
import simple.hooks.wrappers.SimpleNpc;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.script.Script;
import simple.hooks.simplebot.teleporter.Teleporter;
import simple.robot.utils.WorldArea;

@ScriptManifest(
        author = "Esmaabi",
        category = Category.FISHING,
        description = "<br>Most effective anglerfish catching bot on Zaros! <br><br><b>Features & recommendations:</b><br><br>" +
                "<ul><li>You must have <b>fishing rod</b> and <b>sandworms</b> in inventory;</li>" +
                "<li>You can start script anywhere;</li>" +
                "<li>Supported special attack with dragon harpoon equipped;</li>" +
                "<li>Included <b>anti-ban</b> features!</li></ul>",
        discord = "Esmaabi#5752",
        name = "eAnglerFisherZaros", servers = { "Zaros" }, version = "2.6")

public class eMain extends Script{
    //coordinates
    private final WorldArea ANGLER = new WorldArea (new WorldPoint(1841,3799, 0), new WorldPoint(1792,3767, 0));
    private final WorldArea ANGLER_BANK = new WorldArea (new WorldPoint(1793,3794, 0), new WorldPoint(1812,3783, 0));

    private static final WorldArea ANGLER_SPOT = new WorldArea (
            new WorldPoint(1839,3781,0),
            new WorldPoint(1829,3781,0),
            new WorldPoint(1822,3767,0),
            new WorldPoint(1834,3768,0),
            new WorldPoint(1841,3774,0));

    //vars
    private Teleporter teleporter;
    private long startTime = 0L;
    private long startingSkillLevel;
    private long startingSkillExp;
    private int count;
    static String status = null;
    boolean firstTeleport;
    boolean fishingState;
    public static State playerState;
    private long lastAnimation = -1;
    public static int randomSleeping(int minimum, int maximum) {
        return (int)(Math.random() * (maximum - minimum)) + minimum;
    }

    public static String currentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    enum State{
        ANTIBAN_ACTIVATED,
        ANTIBAN_DEACTIVATED,
        WAITING,
    }


    @Override
    public void onExecute() {
        System.out.println("Started eAnglerFisherZaros!");
        this.teleporter = new Teleporter(ctx);
        this.startTime = System.currentTimeMillis(); //paint
        this.startingSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.FISHING);
        this.startingSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.FISHING);
        count = 0;
        firstTeleport = false;
        fishingState = false;
        playerState = State.WAITING;

        eAnglerFisherZaros.eGui.eGuiDialogueMode();
        if (eAnglerFisherZaros.eGui.returnMode == 0) {
            playerState = State.ANTIBAN_ACTIVATED;
            ctx.updateStatus(currentTime() + " Anti-ban enabled");
        } else if (eAnglerFisherZaros.eGui.returnMode == 1) {
            playerState = State.ANTIBAN_DEACTIVATED;
            ctx.updateStatus(currentTime() + " Anti-ban disabled");
        } else {
            playerState = State.WAITING;
        }

        this.ctx.updateStatus("----------------------");
        this.ctx.updateStatus("  eAnglerFisherZaros  ");
        this.ctx.updateStatus("----------------------");

    }

    @Override
    public void onProcess() {
        if (!firstTeleport) {
            if (!teleporter.opened()) {
                status = "First teleport to fishing spot";
                ctx.magic.castSpellOnce("Skilling Teleport");
            } else {
                status = "Browsing for anglerfish tele";
                teleporter.teleportStringPath("Skilling", "Fishing: Anglerfish");
                ctx.onCondition(() -> ANGLER.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                ctx.game.tab(Game.Tab.INVENTORY);
                firstTeleport = true;
                fishingState = false;
                status = "Setup completed";
            }
        } else {
            if (playerState == State.ANTIBAN_ACTIVATED) {
                if (ANGLER.containsPoint(ctx.players.getLocal().getLocation())) {
                    if (ANGLER_BANK.containsPoint(ctx.players.getLocal().getLocation()) && !fishingState) {
                        bankingFish();
                    } else if (ctx.inventory.populate().population() == 2 && !ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()) && fishingState) {
                        fishingAnglersInstant();
                    } else if (ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()) && fishingState) {
                        if (ctx.inventory.populate().population() == 28) {
                            teleportingToBank();
                        } else if (ctx.inventory.populate().population() < 28) {
                            if (ctx.players.getLocal().getAnimation() != 622 && (System.currentTimeMillis() > (lastAnimation + 3000))) {
                                fishingAnglers();
                            } else if (ctx.players.getLocal().getAnimation() == 622) {
                                lastAnimation = System.currentTimeMillis();
                            }
                        }
                    }
                } else {
                    ctx.updateStatus(currentTime() + " Not in Anglers area");
                    ctx.updateStatus(currentTime() + " Stopping script");
                    ctx.stopScript();
                }

            } else if (playerState == State.ANTIBAN_DEACTIVATED) {
                if (ANGLER.containsPoint(ctx.players.getLocal().getLocation())) {
                    if (ANGLER_BANK.containsPoint(ctx.players.getLocal().getLocation()) && !fishingState) {
                        bankingFish();
                    } else if (ctx.inventory.populate().population() == 2 && !ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()) && fishingState) {
                        fishingAnglersInstant();
                    } else if (ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()) && fishingState) {
                        if (ctx.inventory.populate().population() == 28) {
                            teleportingToBankInstant();
                        } else if (ctx.inventory.populate().population() < 28) {
                            if (ctx.players.getLocal().getAnimation() != 622 && (System.currentTimeMillis() > (lastAnimation + 3000))) {
                                fishingAnglersInstant();
                            } else if (ctx.players.getLocal().getAnimation() == 622) {
                                lastAnimation = System.currentTimeMillis();
                            }
                        }
                    }
                } else {
                    ctx.updateStatus(currentTime() + " Not in Anglers area");
                    ctx.updateStatus(currentTime() + " Stopping script");
                    ctx.stopScript();
                }
            }
        }

        if (ctx.combat.getSpecialAttackPercentage() == 100 && ctx.equipment.populate().filter("Dragon harpoon").population() == 1 && ctx.players.getLocal().getAnimation() == 622) {
            ctx.sleep(randomSleeping(1200, 24000));
            ctx.combat.toggleSpecialAttack(true);
        }

        if (ctx.pathing.energyLevel() > 30 && !ctx.pathing.running()) {
            ctx.pathing.running(true);
        }
    }

    public void fishingAnglers() {
        SimpleNpc anglerFish = ctx.npcs.populate().filter(6825).nearest().next();
        if (anglerFish != null && anglerFish.validateInteractable()) {
            status = "Sleeping (anti-ban)";
            ctx.sleep(randomSleeping(1200, 24000));
            status = "Fishing";
            anglerFish.click("Bait", "Rod Fishing spot");
            ctx.onCondition(() -> ctx.players.getLocal().getAnimation() == 622, 2400);
        } else if (anglerFish == null && ANGLER.containsPoint(ctx.players.getLocal().getLocation())) {
            status = "Running to fishing spot";
            takingStepsRandom();
            ctx.sleepCondition(() -> ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()), 2400);
        } else {
            status = "NPC not found";
            ctx.updateStatus(currentTime() + " NPC not found");
            ctx.updateStatus(currentTime() + " Stopping script");
            ctx.stopScript();
        }
    }

    public void fishingAnglersInstant() {
        SimpleNpc anglerFish = ctx.npcs.populate().filter(6825).nearest().next();
        status = "Fishing";
        if (anglerFish != null && anglerFish.validateInteractable()) {
            anglerFish.click("Bait", "Rod Fishing spot");
            ctx.onCondition(() -> ctx.players.getLocal().getAnimation() == 622, 2400);
        } else if (anglerFish == null && ANGLER.containsPoint(ctx.players.getLocal().getLocation())) {
            status = "Running to fishing spot";
            takingStepsRandom();
            ctx.sleepCondition(() -> ANGLER_SPOT.containsPoint(ctx.players.getLocal().getLocation()), 2400);
        } else {
            status = "NPC not found";
            ctx.updateStatus(currentTime() + " NPC not found");
            ctx.updateStatus(currentTime() + " Stopping script");
            ctx.stopScript();
        }
    }

    public void bankingFish() {
        if (ctx.inventory.populate().population() > 2) {
            SimpleObject bank = ctx.objects.populate().filter("Bank booth").nearest().next();
            status = "Finding bank";
            if (bank != null && bank.validateInteractable() && !ctx.bank.bankOpen()) {
                status = "Banking";
                bank.click("Bank", "Bank booth");
                ctx.onCondition(() -> ctx.bank.bankOpen(), 2400);
            } else if (ctx.bank.bankOpen()) {
                status = "Banking";
                ctx.bank.depositAllExcept(307, 13431);
                ctx.sleep(600);
                ctx.bank.closeBank();
                ctx.viewport.angle(randomSleeping(190, 220));
                fishingState = true;
            }
        } else if (ctx.inventory.populate().population() == 2) {
            fishingState = true;
        }
    }

    public void takingStepsRandom() {
        int max = 7;
        int min = 1;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        if (randomNum == 1) {
            ctx.pathing.step(1827, 3771);
        } else if (randomNum == 2) {
            ctx.pathing.step(1836, 3772);
        } else if (randomNum == 3) {
            ctx.pathing.step(1834, 3770);
        } else if (randomNum == 4) {
            ctx.pathing.step(1839, 3776);
        } else if (randomNum == 5) {
            ctx.pathing.step(1824, 3772);
        } else if (randomNum == 6) {
            ctx.pathing.step(1830, 3771);
        } else {
            ctx.pathing.step(1826, 3771);
        }
    }

    public void teleportingToBank() {
        status = "Sleeping (anti-ban)";
        ctx.sleep(randomSleeping(1200, 24000));
        status = "Teleporting to bank";
        if (!ANGLER_BANK.containsPoint(ctx.players.getLocal().getLocation())) {
                ctx.game.tab(Game.Tab.MAGIC);
                SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
                if (homeTeleport.click("Fishing: Anglerfish", "Home Teleport")) {
                    ctx.onCondition(() -> ANGLER_BANK.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                    ctx.game.tab(Game.Tab.INVENTORY);
                    fishingState = false;
                }
        }
    }

    public void teleportingToBankInstant() {
        status = "Teleporting to bank";
        SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
        ctx.game.tab(Game.Tab.MAGIC);
        homeTeleport.click("Fishing: Anglerfish", "Home Teleport");
        ctx.onCondition(() -> ANGLER_BANK.containsPoint(ctx.players.getLocal().getLocation()), 2400);
        ctx.game.tab(Game.Tab.INVENTORY);
        fishingState = false;
    }

    @Override
    public void onTerminate() {
        this.startingSkillLevel = 0L;
        this.startingSkillExp = 0L;
        this.count = 0;
        this.firstTeleport = false;
        this.lastAnimation = -1;
        this.fishingState = false;
        playerState = null;

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
            } else if (message.contains("catch a anglerfish")) {
                count++;
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
        g.drawString("eAnglerFisherZaros by Esmaabi", 15, 135);
        g.setColor(Color.WHITE);
        long runTime = System.currentTimeMillis() - this.startTime;
        long currentSkillLevel = this.ctx.skills.realLevel(SimpleSkills.Skills.FISHING);
        long currentSkillExp = this.ctx.skills.experience(SimpleSkills.Skills.FISHING);
        long SkillLevelsGained = currentSkillLevel - this.startingSkillLevel;
        long SkillExpGained = currentSkillExp - this.startingSkillExp;
        long SkillExpPerHour = (int)((SkillExpGained * 3600000D) / runTime);
        long FishPerHour = (int) (count / ((System.currentTimeMillis() - this.startTime) / 3600000.0D));
        g.drawString("Runtime: " + formatTime(runTime), 15, 150);
        g.drawString("Starting Level: " + this.startingSkillLevel + " (+" + SkillLevelsGained + ")", 15, 165);
        g.drawString("Current Level: " + currentSkillLevel, 15, 180);
        g.drawString("Exp gained: " + SkillExpGained + " (" + (SkillExpPerHour / 1000L) + "k" + " xp/h)", 15, 195);
        g.drawString("Fish caught: " + count + " (" + FishPerHour + " fish/h)", 15, 210);
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