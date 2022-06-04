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

import java.util.stream.Stream;

public class BankTask extends Task {

    private static final WorldArea jadEntrance = new WorldArea(new WorldPoint(2429, 5170, 0),
            new WorldPoint(2436, 5184, 0),
            new WorldPoint(2462, 5184, 0),
            new WorldPoint(2450, 5161, 0));

    public static SimpleItem getItem(String... itemName) { //Credits to trester I think? Scans for general name of item instead of having to write every exact match.
        return ClientContext.instance().inventory.populate()
                .filter(p -> Stream.of(itemName).anyMatch(arr -> p.getName().toLowerCase().contains(arr.toLowerCase())))
                .next();
    }

    String[] rangePotItems = { "ranging", "bastion" };
    SimpleItem rangePotion = getItem(rangePotItems);
    String[] defPotItems = { "Super defence", "defence" };
    SimpleItem defencePotion = getItem(defPotItems);

    public void disablePrayers() {
        ctx.updateStatus("Disabling prayers");
        ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MAGIC, false);
        ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MISSILES, false);
        ctx.prayers.prayer(SimplePrayers.Prayers.PROTECT_FROM_MELEE, false);
        ctx.prayers.prayer(SimplePrayers.Prayers.STEEL_SKIN, false);
        ctx.prayers.prayer(SimplePrayers.Prayers.EAGLE_EYE, false);
    }

    public BankTask(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return KillTask.playerState == State.BANKING || ctx.players.getLocal().getLocation().getRegionID() == 9808 && KillTask.playerState != null && KillTask.playerState != State.EXCHANGE_CAPES;
    }

    @Override
    public void run() {
        banking();
    }

    void banking() {
        ctx.updateStatus("Bank task activated");

        if (KillTask.playerState != State.EXCHANGE_CAPES || KillTask.playerState != State.BANKING) {
            KillTask.playerState = State.BANKING;
        }

        boolean playerReady;
        if (ctx.inventory.populate().filter(2301).population() <= 4 || ctx.inventory.populate().filter(2434).population() <= 1 || ctx.inventory.population() == 28 || KillTask.getItem("ranging") == null) {
            ctx.updateStatus("Heading to bank to restock supplies.");
            ctx.sleep(KillTask.randomSleep);
            disablePrayers();
            playerReady = false;
            if (!jadEntrance.containsPoint(ctx.players.getLocal().getLocation()) && !playerReady) {
                ctx.sleepCondition(() -> KillTask.teleporter.open(), 2400);
                KillTask.teleportToLocation("Minigames", "Fight Caves");
            } else if (jadEntrance.containsPoint(ctx.players.getLocal().getLocation()) && !playerReady) {
                //Deposit everything and then withdraw supplies
                SimpleObject banker = ctx.objects.populate().filter("Bank chest").nearest().next(); // Grabs the nearest bank chest at TzHaar
                if (banker != null && banker.validateInteractable() && !ctx.bank.bankOpen()) {
                    banker.click("Use", "Bank chest");
                    ctx.sleepCondition(() -> ctx.bank.bankOpen(), 2400); // Clicks first option, currently bank chest or bank booth will work
                } else if (ctx.bank.bankOpen()) {
                    ctx.onCondition(() -> ctx.bank.depositInventory(), 2400);
                    ctx.bank.withdraw(2444, 2); // Ranging Potion
                    ctx.bank.withdraw(2442, 2); // Super def Potion
                    ctx.bank.withdraw(2434, 5); // Pray pots
                    if (ctx.bank.populate().filter(2301) != null) {
                        ctx.bank.withdraw(2301, 18); // Pineapple pizza
                        ctx.bank.closeBank();
                        ctx.onCondition(() -> !ctx.bank.bankOpen(), 2400);
                    } else {
                        playerReady = true;
                    }
                }
            }
        } else {
            playerReady = true;
            if (playerReady && ctx.bank.bankOpen()) {
                ctx.bank.closeBank();
                ctx.onCondition(() -> !ctx.bank.bankOpen(), 2400);
            } else if (playerReady && !ctx.bank.bankOpen()) {
                //Go through the entrance.
                //Update playerState
                SimpleObject entranceJad = ctx.objects.populate().filter(11833).nearest().next(); // Fight Caves door
                if (entranceJad != null && entranceJad.validateInteractable()) {
                    if (ctx.skills.level(SimpleSkills.Skills.RANGED) <= ctx.skills.realLevel(SimpleSkills.Skills.RANGED) && rangePotion != null
                            && ctx.skills.level(SimpleSkills.Skills.DEFENCE) > ctx.skills.realLevel(SimpleSkills.Skills.DEFENCE) && defPotItems != null) {
                        ctx.updateStatus("Sipping potions.");
                        rangePotion.click("Drink");
                        ctx.onCondition(() -> ctx.skills.level(SimpleSkills.Skills.RANGED) <= ctx.skills.realLevel(SimpleSkills.Skills.RANGED), 800);
                        ctx.sleep(600);
                        defencePotion.click("Drink");
                        ctx.onCondition(() -> ctx.skills.level(SimpleSkills.Skills.DEFENCE) > ctx.skills.realLevel(SimpleSkills.Skills.DEFENCE), 800);
                    } else {
                        ctx.sleepCondition(() -> entranceJad.click("Enter", "Cave entrance"), 2400);
                        ctx.sleepCondition(() -> ctx.players.getLocal().getLocation().getRegionID() != 9808, 3000);
                        KillTask.playerState = State.PRAYERS;
                    }
                }
            }
        }
    }

    @Override
    public String status() {
        return "Bank task script initiated.";
    }
}
