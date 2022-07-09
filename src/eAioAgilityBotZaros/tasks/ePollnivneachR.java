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

public class ePollnivneachR extends Task {

    //Locations
    private final WorldArea startLocation = new WorldArea(new WorldPoint(3347, 2957, 0), new WorldPoint(3353, 2964, 0));
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3345, 2969, 1),
            new WorldPoint(3345, 2963, 1),
            new WorldPoint(3349, 2962, 1),
            new WorldPoint(3353, 2963, 1),
            new WorldPoint(3352, 2970, 1),
            new WorldPoint(3345, 2970, 1)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3356, 2977, 1),
            new WorldPoint(3356, 2972, 1),
            new WorldPoint(3351, 2972, 1),
            new WorldPoint(3351, 2977, 1),
            new WorldPoint(3356, 2977, 1)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3359, 2980, 1),
            new WorldPoint(3363, 2980, 1),
            new WorldPoint(3363, 2976, 1),
            new WorldPoint(3359, 2976, 1)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3365, 2977, 1),
            new WorldPoint(3365, 2973, 1),
            new WorldPoint(3371, 2973, 1),
            new WorldPoint(3371, 2977, 1)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3364, 2984, 1),
            new WorldPoint(3364, 2987, 1),
            new WorldPoint(3371, 2987, 1),
            new WorldPoint(3371, 2984, 1),
            new WorldPoint(3370, 2981, 1),
            new WorldPoint(3365, 2981, 1),
            new WorldPoint(3365, 2983, 1),
            new WorldPoint(3366, 2983, 1),
            new WorldPoint(3366, 2984, 1)
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
            new WorldPoint(3361, 3000, 0),
            new WorldPoint(3361, 2997, 0),
            new WorldPoint(3367, 2997, 0),
            new WorldPoint(3367, 3000, 0)
    });

    public ePollnivneachR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.POLLNIVNEACH;
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

        if (startLocation.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Climbing basket";
            final SimpleObject o = ctx.objects.populate().filter(14935).filterHasAction("Climb-on").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Climb-on", "Basket")) {
                    ctx.onCondition(() -> inMotion, 1200);
                }
            }
        } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping market stall";
            final SimpleObject o = ctx.objects.populate().filter(14936).filterHasAction("Jump-on").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump-on", "Market stall")) {
                    ctx.onCondition(() -> inMotion, 1200);
                }
            }
        } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            final SimpleObject o = ctx.objects.populate().filter(14937).filterHasAction("Grab").next();
            eMain.status = "Grabbing banner";
            if (o != null && o.validateInteractable()) {
                if (o.click("Grab", "Banner")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }
        } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Leaping gap";
            final SimpleObject o = ctx.objects.populate().filter(14938).filterHasAction("Leap").next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Leap", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping to tree";
            final SimpleObject o = ctx.objects.populate().filter(14939).filterHasAction("Jump-to").next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump-to", "Tree")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Climbing wall";
            final SimpleObject o = ctx.objects.populate().filter(14940).filterHasAction("Climb").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Climb", "Rough wall")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Crossing monkeybars";
            //"Monkeybars"
            final SimpleObject o = ctx.objects.populate().filter(14941).filterHasAction("Cross").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Cross", "Monkeybars")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (seventhHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping on tree";
            final SimpleObject o = ctx.objects.populate().filter(14944).filterHasAction("Jump-on").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump-on", "Tree")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (eightHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            eMain.status = "Jumping to tree";
            final SimpleObject o = ctx.objects.populate().filter(14945).filterHasAction("Jump-to").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump-to", "Drying line")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (lastLocation.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
            SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
            eMain.status = "Teleporting to start";
            if (ctx.game.tab(Game.Tab.MAGIC)) {
                homeTeleport.click("Agility: Pollnivneach Rooftop", "Home Teleport");
                ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 3200);
            }
        }
    }

    @Override
    public String status() {
        return "Completed lap: " + eMain.count;
    }
}