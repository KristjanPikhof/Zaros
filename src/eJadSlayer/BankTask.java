package eJadSlayer;

import eJadSlayer.KillTask.State;
import simple.hooks.filters.SimplePrayers;
import simple.hooks.filters.SimpleSkills;
import simple.hooks.wrappers.SimpleItem;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;
import simple.hooks.simplebot.teleporter.Teleporter;

import java.util.stream.Stream;

import static eJadSlayer.KillTask.teleporter;

public class BankTask extends Task {

    static final WorldArea jadEntrance = new WorldArea(new WorldPoint(2429, 5170, 0),
            new WorldPoint(2436, 5184, 0),
            new WorldPoint(2462, 5184, 0),
            new WorldPoint(2450, 5161, 0));

    private void drinkRangePot() {
        ctx.updateStatus("Sipping potions before entering");
        SimpleItem rangePot = ctx.inventory.populate().filter(new String[] { "Ranging potion(4)", "Ranging potion(3)", "Ranging potion(2)", "Ranging potion(1)" }).next();
        if (rangePot != null && ctx.skills.level(SimpleSkills.Skills.RANGED) <= ctx.skills.realLevel(SimpleSkills.Skills.RANGED)) {
            rangePot.click("Drink");
            ctx.sleep(1000);
        } else {
        ctx.updateStatus("Ranging potion not found");
        }
    }

    private void drinkDefencePot() {
        SimpleItem defPot = ctx.inventory.populate().filter(new String[] { "Super defence(4)", "Super defence(3)", "Super defence(2)", "Super defence(1)" }).next();
        if (defPot != null && ctx.skills.level(SimpleSkills.Skills.DEFENCE) <= ctx.skills.realLevel(SimpleSkills.Skills.DEFENCE)) {
            ctx.updateStatus("Sipping defence potions before entering");
            defPot.click("Drink");
            ctx.sleep(1000);
        } else {
            ctx.updateStatus("Defence potion not found");
        }
    }

    private void teleportToTzhaar() {
        if (!teleporter.opened() && !jadEntrance.containsPoint(ctx.players.getLocal().getLocation())) {
            ctx.magic.castSpellOnce("Minigame Teleport");
            ctx.onCondition(() -> teleporter.opened(), 1200);
        } else {
            ctx.updateStatus("Teleporting to Fight Cave");
            teleporter.teleportStringPath("Minigame Teleport", "Fight Cave");
            ctx.onCondition(() -> jadEntrance.containsPoint(ctx.players.getLocal().getLocation()), 1200);
        }
    }

    private void openBank() {
        SimpleObject bankChest = ctx.objects.populate().filter("Bank chest").nearest().next();
        if (bankChest != null && bankChest.validateInteractable() && !ctx.bank.bankOpen()) {
            bankChest.click("Use", "Bank chest");
            ctx.sleepCondition(() -> ctx.bank.bankOpen(), 2400);
            ctx.bank.depositInventory();
        }
    }

    private void banking() {
            if (ctx.inventory.isEmpty() && ctx.bank.bankOpen()) {
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
        //ctx.updateStatus("Started task: Banking");
        if (!jadEntrance.containsPoint(ctx.players.getLocal().getLocation())) {
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
        return "Bank task script initiated.";
    }
}
