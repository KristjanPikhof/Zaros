package eAioAgilityBot.tasks;

import eAioAgilityBot.eMain;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.simplebot.Game;
import simple.hooks.simplebot.Pathing;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class eSeersR extends Task {
    public eSeersR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.SEERS;
    }

    @Override
    public void run() {

        final Pathing pathing = ctx.pathing;
        final boolean inMotion = ctx.pathing.inMotion();

        if (!ctx.groundItems.populate().filter(11849).filter((i) -> pathing.reachable(i.getLocation())).isEmpty()) {
            final SimpleGroundItem i = ctx.groundItems.nearest().next();
            ctx.updateStatus("Picking up MOG");
            eMain.status = "Picking up MOG";
            if (i != null && i.validateInteractable() && !inMotion) {
                final int cached = ctx.inventory.populate().filter(11849).population(true);
                if (i.click("Take")) {
                    ctx.onCondition(() -> cached < ctx.inventory.populate().filter(11849).population(true), 250, 12);
                }
            }
        }

        if (startLocation.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Climbing wall";
            final SimpleObject o = ctx.objects.populate().filter("Wall").filterHasAction("Climb-up").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Climb-up", "Wall")) {
                    //ctx.onCondition(() -> ctx.players.getLocal().getLocation().getPlane() != 0, 250, 14);
                    ctx.onCondition(() -> inMotion, 1200);
                }
            }
        //} else if (pathing.reachable(new WorldPoint(3349, 2968, 1))) {
        } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            if (pathing.reachable(new WorldPoint(2721, 3493, 3))) {
                final SimpleObject o = ctx.objects.populate().filter("Gap").filterHasAction("Jump").nearest().next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Jump", "Gap")) {
                        //ctx.onCondition(() -> !pathing.reachable(new WorldPoint(3349, 2968, 1)), 250, 14);
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            }
        //} else if (pathing.reachable(new WorldPoint(3355, 2976, 1))) {
        } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            if (pathing.reachable(new WorldPoint(2710, 3490, 2))) {
                final SimpleObject o = ctx.objects.populate().filter("Tightrope").filterHasAction("Cross").next();
                eMain.status = "Crossing tightrope";
                if (o != null && o.validateInteractable()) {
                    if (o.click("Cross", "Tightrope")) {
                        //ctx.onCondition(() -> !pathing.reachable(new WorldPoint(3355, 2976, 1)), 250, 14);
                        ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                    }
                }
            }
            //} else if (pathing.reachable(new WorldPoint(3362, 2977, 1))) {
        } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping gap";
            if (pathing.reachable(new WorldPoint(2710, 3477, 2))) {
                final SimpleObject o = ctx.objects.populate().filter("Gap").filterHasAction("Jump").next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Jump", "Gap")) {
                        //ctx.onCondition(() -> !pathing.reachable(new WorldPoint(3362, 2977, 1)), 250, 14);
                        ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                    }
                }
            }
        //} else if (pathing.reachable(new WorldPoint(3368, 2976, 1))) {
        } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping gap";
            if (pathing.reachable(new WorldPoint(2702, 3470, 3))) {
                final SimpleObject o = ctx.objects.populate().filter("Gap").filterHasAction("Jump").next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Jump", "Gap")) {
                        //ctx.onCondition(() -> !pathing.reachable(new WorldPoint(3368, 2976, 1)), 250, 14);
                        ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                    }
                }
            }
        //} else if (pathing.reachable(new WorldPoint(3366, 2982, 1))) {
        } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping from edge";
            if (pathing.reachable(new WorldPoint(2702, 3465, 2))) {
                final SimpleObject o = ctx.objects.populate().filter("Edge").filterHasAction("Jump").next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Jump", "Edge")) {
                        //ctx.onCondition(() -> !pathing.reachable(new WorldPoint(3368, 2976, 1)), 250, 14);
                        ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                    }
                }
            }
        } else if (lastLocation.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
            eMain.status = "Teleporting to start";
            if (ctx.game.tab(Game.Tab.MAGIC)) {
                homeTeleport.click("Agility: Seers Rooftop", "Home Teleport");
                ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 4200);
            }
        }
    }

    @Override
    public String status() {
        return "Completed lap: " + eMain.count;
    }


    //Locations

    private final WorldArea startLocation = new WorldArea(new WorldPoint(2724, 3491, 0), new WorldPoint(2732, 3486, 0));
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2731, 3498, 3),
            new WorldPoint(2731, 3489, 3),
            new WorldPoint(2720, 3489, 3),
            new WorldPoint(2720, 3498, 3)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2702, 3499, 2),
            new WorldPoint(2702, 3486, 2),
            new WorldPoint(2714, 3486, 2),
            new WorldPoint(2716, 3500, 2)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2708, 3483, 2),
            new WorldPoint(2708, 3475, 2),
            new WorldPoint(2717, 3475, 2),
            new WorldPoint(2717, 3483, 2)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2697, 3478, 3),
            new WorldPoint(2697, 3468, 3),
            new WorldPoint(2718, 3468, 3),
            new WorldPoint(2718, 3474, 3),
            new WorldPoint(2706, 3474, 3),
            new WorldPoint(2706, 3478, 3)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2689, 3467, 2),
            new WorldPoint(2689, 3457, 2),
            new WorldPoint(2704, 3457, 2),
            new WorldPoint(2704, 3467, 2)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3366, 2987, 2),
            new WorldPoint(3354, 2987, 2),
            new WorldPoint(3354, 2979, 2),
            new WorldPoint(3366, 2979, 2)
    });

    private static final WorldArea seventhHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3355, 2996, 2),
            new WorldPoint(3356, 2990, 2),
            new WorldPoint(3371, 2989, 2),
            new WorldPoint(3371, 2996, 2)
    });

    private static final WorldArea eightHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3355, 3006, 2),
            new WorldPoint(3355, 2999, 2),
            new WorldPoint(3363, 2999, 2),
            new WorldPoint(3363, 3006, 2)
    });

    private static final WorldArea lastLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(2701, 3468, 0),
            new WorldPoint(2701, 3454, 0),
            new WorldPoint(2716, 3454, 0),
            new WorldPoint(2715, 3469, 0)
    });
}