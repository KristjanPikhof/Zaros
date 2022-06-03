package eJadSlayer;

import eJadSlayer.KillTask.State;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.api.ClientContext;

public class BankTask extends Task {

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

        if (ctx.inventory.populate().filter(2301).population() <= 4 || ctx.inventory.populate().filter(2434).population() <= 2 || ctx.inventory.population() == 28 || KillTask.getItem("ranging") == null) {
            //Teleport to sand Crabs
            ctx.updateStatus("Heading to bank to restock supplies.");
            ctx.sleepCondition(() -> KillTask.teleporter.open(), 2500);
            ctx.sleep(KillTask.randomSleep);
            KillTask.teleportToLocation("Monsters", "Sand Crabs");
            ctx.sleepCondition(() -> ctx.players.getLocal().getLocation().getRegionID() == 6710, 3000);

            //Deposit everything and then withdraw supplies
            SimpleObject banker = ctx.objects.populate().filter("Bank Chest", "Bank booth", "Banker").nearest().next(); // Grabs nearest banker at sand crabs

            if (banker != null && banker.validateInteractable() && ctx.players.getLocal().getLocation().getRegionID() != 9808) {
                banker.click(1);
                ctx.sleepCondition(() -> ctx.bank.bankOpen(), 4000); // Clicks first option, currently bank chest or bank booth will work
                ctx.bank.depositInventory();
                ctx.bank.withdraw(2444, 3); // Ranging Potion
                ctx.bank.withdraw(2442, 2); // Super def Potion
                ctx.bank.withdraw(2434, 5); // Pray pots
                if(ctx.bank.populate().filter(2301) != null) {
                    ctx.bank.withdraw(2301, 15); // Pineapple pizzas
                }else if(ctx.bank.populate().filter(385) != null) {
                    ctx.bank.withdraw(385, 15); // Pineapple pizzas
                }

                ctx.bank.closeBank();
            }

            if (ctx.players.getLocal().getLocation().getRegionID() != 9808) {
                //Teleport back to fight caves
                ctx.sleepCondition(() -> KillTask.teleporter.open(), 2500);
                KillTask.teleportToLocation("Minigames", "Fight Caves");
                ctx.sleepCondition(() -> ctx.players.getLocal().getLocation().getRegionID() == 9808, 3000);
            }
        }

        //Go through the entrance.
        //Update playerState
        SimpleObject entrance = ctx.objects.populate().filter(11833).nearest().next(); // Fight Caves door

        if (entrance != null && entrance.validateInteractable())
        {
            ctx.sleepCondition(() -> entrance.click("Enter"), 2500);
            ctx.sleepCondition(() -> ctx.players.getLocal().getLocation().getRegionID() != 9808, 3000);
            KillTask.playerState = State.PRAYERS;
        }
    }

    @Override
    public String status() {
        return "Bank task script initiated.";
    }
}
