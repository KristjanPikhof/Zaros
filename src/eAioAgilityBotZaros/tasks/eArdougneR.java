package eAioAgilityBotZaros.tasks;

import eAioAgilityBotZaros.eMain;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.Pathing;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class eArdougneR extends Task {

    //Locations
    private static final WorldArea startLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(2665, 3298, 0),
            new WorldPoint(2665, 3293, 0),
            new WorldPoint(2677, 3293, 0),
            new WorldPoint(2676, 3302, 0)
    });
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2669, 3311, 3),
            new WorldPoint(2669, 3298, 3),
            new WorldPoint(2674, 3298, 3),
            new WorldPoint(2674, 3311, 3)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2666, 3317, 3),
            new WorldPoint(2660, 3317, 3),
            new WorldPoint(2660, 3320, 3),
            new WorldPoint(2666, 3320, 3)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2658, 3320, 3),
            new WorldPoint(2658, 3317, 3),
            new WorldPoint(2653, 3317, 3),
            new WorldPoint(2653, 3320, 3)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2651, 3315, 3),
            new WorldPoint(2651, 3310, 3),
            new WorldPoint(2655, 3310, 3),
            new WorldPoint(2655, 3315, 3)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2649, 3310, 3),
            new WorldPoint(2652, 3310, 3),
            new WorldPoint(2652, 3306, 3),
            new WorldPoint(2654, 3305, 3),
            new WorldPoint(2655, 3303, 3),
            new WorldPoint(2657, 3302, 3),
            new WorldPoint(2652, 3299, 3)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2659, 3295, 3),
            new WorldPoint(2659, 3300, 3),
            new WorldPoint(2656, 3300, 3),
            new WorldPoint(2654, 3299, 3),
            new WorldPoint(2655, 3295, 3)
    });

    private static String teleportName = "Agility: Ardougne Rooftop";

    public eArdougneR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.ARDOUGNE;
    }

    @Override
    public void run() {

        final Pathing pathing = ctx.pathing;
        final boolean inMotion = ctx.pathing.inMotion();

        if (!ctx.groundItems.populate().filter(11849).filter((i) -> pathing.reachable(i.getLocation())).isEmpty()) {
            final SimpleGroundItem i = ctx.groundItems.nearest().next();
            eMain.status = "Picking up MOG";
            if (i != null && i.validateInteractable() && !inMotion) {
                final int cached = ctx.inventory.populate().filter(11849).population(true);
                if (i.click("Take")) {
                    ctx.onCondition(() -> cached < ctx.inventory.populate().filter(11849).population(true), 250, 12);
                }
            }
        }

        if (eMain.lastHP > ctx.combat.health()) {
            SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
            eMain.status = "Course failed - starting over";
            if (ctx.game.tab(Game.Tab.MAGIC)) {
                homeTeleport.click(teleportName, "Home Teleport");
                ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 3200);
                eMain.lastHP = ctx.combat.health();
            }
        }

        if (!eMain.firstTeleport) {
            if (!eMain.teleporter.opened()) {
                eMain.status = "Teleporting to agility course";
                ctx.magic.castSpellOnce("Skilling Teleport");
            } else {
                eMain.status = "Browsing for chosen course";
                eMain.teleporter.teleportStringPath("Skilling", teleportName);
                ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 2400);
                ctx.game.tab(Game.Tab.INVENTORY);
                eMain.firstTeleport = true;
                eMain.status = "Setup completed";
            }

        } else {
            if (startLocation.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Climbing wooden beams";
                eMain.lastHP = ctx.combat.health();
                final SimpleObject o = ctx.objects.populate().filter(15608).filterHasAction("Climb-up").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Climb-up", "Wooden Beams")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(15609).filterHasAction("Jump").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Walking on plank";
                final SimpleObject o = ctx.objects.populate().filter(26635).filterHasAction("Walk-on").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Walk-on", "Plank")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(15610).filterHasAction("Jump").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(15611).filterHasAction("Jump").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Balancing steep roof";
                final SimpleObject o = ctx.objects.populate().filter(28912).filterHasAction("Balance-across").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Balance-across", "Steep roof")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(15612).filterHasAction("Jump").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            }
        }
    }

    @Override
    public String status() {
        return "Laps completed: " + eMain.count;
    }
}