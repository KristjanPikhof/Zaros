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

public class eRellekkaR extends Task {

    //Locations
    private final WorldArea startLocation = new WorldArea(new WorldPoint(2619, 3681, 0), new WorldPoint(2629, 3671, 0));
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2621, 3677, 3),
            new WorldPoint(2621, 3671, 3),
            new WorldPoint(2628, 3671, 3),
            new WorldPoint(2627, 3677, 3)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2614, 3669, 3),
            new WorldPoint(2614, 3657, 3),
            new WorldPoint(2623, 3657, 3),
            new WorldPoint(2623, 3669, 3)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2625, 3656, 3),
            new WorldPoint(2625, 3651, 3),
            new WorldPoint(2631, 3651, 3),
            new WorldPoint(2631, 3656, 3)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2642, 3654, 3),
            new WorldPoint(2645, 3654, 3),
            new WorldPoint(2645, 3648, 3),
            new WorldPoint(2638, 3649, 3),
            new WorldPoint(2634, 3661, 3),
            new WorldPoint(2641, 3660, 3)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2651, 3664, 3),
            new WorldPoint(2651, 3656, 3),
            new WorldPoint(2641, 3656, 3),
            new WorldPoint(2641, 3664, 3)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(2662, 3682, 3),
            new WorldPoint(2654, 3682, 3),
            new WorldPoint(2654, 3664, 3),
            new WorldPoint(2663, 3664, 3),
            new WorldPoint(2663, 3680, 3),
            new WorldPoint(2667, 3682, 3),
            new WorldPoint(2667, 3687, 3),
            new WorldPoint(2662, 3687, 3)
    });
    private static final WorldArea lastLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(2645, 3681, 0),
            new WorldPoint(2645, 3669, 0),
            new WorldPoint(2656, 3669, 0),
            new WorldPoint(2656, 3681, 0)
    });

    private static String teleportName = "Agility: Rellekka Rooftop";

    public eRellekkaR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.RELLEKKA;
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
                eMain.status = "Climbing wall";
                eMain.lastHP = ctx.combat.health();
                final SimpleObject o = ctx.objects.populate().filter(14946).filterHasAction("Climb").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Climb", "Rough wall")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14947).filterHasAction("Leap").nearest().next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Leap", "Gap") && !inMotion) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                eMain.status = "Crossing tightrope";
                final SimpleObject o = ctx.objects.populate().filter(14987).filterHasAction("Cross").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Cross", "Tightrope")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14990).filterHasAction("Leap").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Leap", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(14991).filterHasAction("Hurdle").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Hurdle", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                eMain.status = "Crossing tightrope";
                final SimpleObject o = ctx.objects.populate().filter(14992).filterHasAction("Cross").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Cross", "Tightrope")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping on pile of fish";
                final SimpleObject o = ctx.objects.populate().filter(14994).filterHasAction("Jump-in").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump-in", "Pile of fish")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (lastLocation.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
                eMain.status = "Teleporting to start";
                if (ctx.game.tab(Game.Tab.MAGIC)) {
                    homeTeleport.click("Agility: Rellekka Rooftop", "Home Teleport");
                    ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 3200);
                }

            } else if (ctx.players.getLocal().getLocation().getPlane() <= 1 &&
                    !lastLocation.containsPoint(ctx.players.getLocal().getLocation()) &&
                    !startLocation.containsPoint(ctx.players.getLocal().getLocation()) && !inMotion) {
                SimpleWidget homeTeleport = ctx.widgets.getWidget(218, 6);//home teleport
                eMain.status = "Teleporting to start";
                if (ctx.game.tab(Game.Tab.MAGIC)) {
                    homeTeleport.click(teleportName, "Home Teleport");
                    ctx.onCondition(() -> startLocation.containsPoint(ctx.players.getLocal().getLocation()), 3200);
                }
            }
        }
    }

    @Override
    public String status() {
        return "Laps completed: " + eMain.count;
    }
}