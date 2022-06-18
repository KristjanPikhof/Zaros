package eJadSlayer;

import eJadSlayer.KillTask.State;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.wrappers.SimpleItem;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

import static eJadSlayer.KillTask.teleporter;

public class BankTask extends Task {

    public static String[] defencePotions = new String[] { "Super defence(1)", "Super defence(2)", "Super defence(3)", "Super defence(4)" };
    public static String[] rangingPotions = new String[] { "Ranging potion(1)", "Ranging potion(2)", "Ranging potion(3)", "Ranging potion(4)" };
    public static String[] prayerPotions = new String[] { "Prayer potion(1)", "Prayer potion(2)", "Prayer potion(3)", "Prayer potion(4)" };
    static final WorldArea jadEntrance = new WorldArea(new WorldPoint[] {
            new WorldPoint(2450, 5160, 0),
            new WorldPoint(2456, 5179, 0),
            new WorldPoint(2440, 5187, 0),
            new WorldPoint(2422, 5172, 0)});

    private void drinkRangePot() {
        ctx.updateStatus("Sipping potions before entering");
        SimpleItem rangePot = ctx.inventory.populate().filter(new String[] { "Ranging potion(1)", "Ranging potion(2)", "Ranging potion(3)", "Ranging potion(4)" }).next();
        if (rangePot != null && ctx.skills.level(SimpleSkills.Skills.RANGED) <= ctx.skills.realLevel(SimpleSkills.Skills.RANGED)) {
            rangePot.click("Drink");
            ctx.sleep(1000);
        } else if (rangePot == null) {
            ctx.updateStatus("Ranging potion not found");
            ctx.updateStatus("Starting bank task");
            openBank();
        } else {
            enterCave();
        }
    }

    private void drinkDefencePot() {
        SimpleItem defPot = ctx.inventory.populate().filter(new String[] { "Super defence(1)", "Super defence(2)", "Super defence(3)", "Super defence(4)" }).next();
        if (defPot != null && ctx.skills.level(SimpleSkills.Skills.DEFENCE) <= ctx.skills.realLevel(SimpleSkills.Skills.DEFENCE)) {
            ctx.updateStatus("Sipping defence potions before entering");
            defPot.click("Drink");
            ctx.sleep(1000);
        } else if (defPot == null) {
            ctx.updateStatus("Defence potion not found");
            ctx.updateStatus("Starting bank task");
            openBank();
        } else {
            enterCave();
        }
    }

    private void teleportToTzhaar() {
        if (!teleporter.opened() && !jadEntrance.containsPoint(ctx.players.getLocal().getLocation())
                && ctx.players.getLocal().getLocation().getRegionID() == 9551) {
            ctx.sleep(KillTask.randomSleep);
            ctx.magic.castSpellOnce("Minigame Teleport");
            ctx.onCondition(() -> teleporter.opened(), 1200);
        } else if (teleporter.opened()) {
            ctx.updateStatus("Teleporting to Fight Cave");
            teleporter.teleportStringPath("Minigame Teleport", "Fight Cave");
            ctx.onCondition(() -> jadEntrance.containsPoint(ctx.players.getLocal().getLocation()), 1200);
        }
    }

    private void openBank() {
        if (ctx.inventory.populate().filter(2301).population() <= 4
                || ctx.inventory.populate().filter(prayerPotions).population() <= 2
                || ctx.inventory.populate().filter(rangingPotions).population() <= 1
                || ctx.inventory.populate().filter(defencePotions).population() <= 1
                || ctx.inventory.population() == 28) {
            SimpleObject bankChest = ctx.objects.populate().filter("Bank chest").nearest().next();
            if (bankChest != null && bankChest.validateInteractable() && !ctx.bank.bankOpen()) {
                bankChest.click("Use", "Bank chest");
                ctx.sleepCondition(() -> ctx.bank.bankOpen(), 2400);
                ctx.bank.depositInventory();
            }
        } else {
            enterCave();
        }
    }

    private void banking() {
            if (ctx.bank.bankOpen()) {
                if (ctx.inventory.populate().filter(2444).population() != 2) {
                    ctx.bank.withdraw(2444, 2);
                } else if (ctx.inventory.populate().filter(2442).population() != 2) {
                    ctx.bank.withdraw(2442, 2);
                } else if (ctx.inventory.populate().filter(2434).population() != 5) {
                    ctx.bank.withdraw(2434, 5);
                } else if (ctx.inventory.populate().filter(2301).population() != 18) {
                    ctx.bank.withdraw(2301, 18);
                } else {
                    ctx.bank.closeBank();
                }
            }
    }

    private void enterCave() {
        if (ctx.skills.level(SimpleSkills.Skills.RANGED) <= ctx.skills.realLevel(SimpleSkills.Skills.RANGED)) {
            drinkRangePot();
        } else if (ctx.skills.level(SimpleSkills.Skills.DEFENCE) <= ctx.skills.realLevel(SimpleSkills.Skills.DEFENCE)) {
            drinkDefencePot();
        } else if (ctx.inventory.populate().filter(defencePotions).population() <= 1
                || ctx.inventory.populate().filter(rangingPotions).population() <= 1) {
            openBank();
        } else {
            SimpleObject entranceJad = ctx.objects.populate().filter(11833).nearest().next(); // Fight Caves door
            if (entranceJad != null && entranceJad.validateInteractable()) {
                ctx.onCondition(() -> entranceJad.click("Enter", "Cave entrance"), 2400);
                ctx.sleepCondition(() -> ctx.players.getLocal().getLocation().getRegionID() != 9808, 3000);
                KillTask.playerState = State.PRAYERS;
            }
        }
    }

    public BankTask(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return KillTask.playerState == State.BANKING;
    }

    @Override
    public void run() {
        if (!jadEntrance.containsPoint(ctx.players.getLocal().getLocation())) {
            ctx.sleep(KillTask.randomSleep);
            teleportToTzhaar();
        } else if (ctx.inventory.populate().filter(2301).population() <= 4
                || ctx.inventory.populate().filter(2434).population() <= 2
                || ctx.inventory.population() == 28) {
            openBank();
            if (ctx.inventory.isEmpty() && ctx.bank.bankOpen()) {
                banking();
            }
        } else {
            if (ctx.bank.bankOpen()) {
                ctx.bank.closeBank();
            } else {
                enterCave();
            }
        }
    }

    @Override
    public String status() {
        return "Bank task script initiated";
    }
}
