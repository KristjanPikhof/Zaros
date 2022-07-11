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

public class eVarrockR extends Task {

    //Locations
    private static final WorldArea startLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(3243, 3420, 0),
            new WorldPoint(3243, 3408, 0),
            new WorldPoint(3211, 3409, 0),
            new WorldPoint(3212, 3422, 0),
            new WorldPoint(3228, 3423, 0),
            new WorldPoint(3229, 3420, 0)
    });
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3221, 3409, 3),
            new WorldPoint(3213, 3409, 3),
            new WorldPoint(3213, 3421, 3),
            new WorldPoint(3221, 3421, 3)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3209, 3413, 3),
            new WorldPoint(3209, 3416, 3),
            new WorldPoint(3210, 3418, 3),
            new WorldPoint(3208, 3421, 3),
            new WorldPoint(3202, 3421, 3),
            new WorldPoint(3199, 3416, 3),
            new WorldPoint(3203, 3412, 3)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3199, 3418, 1),
            new WorldPoint(3199, 3415, 1),
            new WorldPoint(3191, 3415, 1),
            new WorldPoint(3191, 3418, 1)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3199, 3407, 3),
            new WorldPoint(3190, 3407, 3),
            new WorldPoint(3190, 3401, 3),
            new WorldPoint(3199, 3401, 3)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3190, 3387, 3),
            new WorldPoint(3190, 3381, 3),
            new WorldPoint(3181, 3381, 3),
            new WorldPoint(3181, 3400, 3),
            new WorldPoint(3201, 3400, 3),
            new WorldPoint(3201, 3405, 3),
            new WorldPoint(3210, 3405, 3),
            new WorldPoint(3210, 3395, 3),
            new WorldPoint(3196, 3387, 3)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3217, 3404, 3),
            new WorldPoint(3217, 3392, 3),
            new WorldPoint(3234, 3392, 3),
            new WorldPoint(3234, 3404, 3),
            new WorldPoint(3220, 3406, 3)
    });

    private static final WorldArea seventhHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3241, 3410, 3),
            new WorldPoint(3235, 3410, 3),
            new WorldPoint(3235, 3402, 3),
            new WorldPoint(3241, 3402, 3)
    });

    private static final WorldArea eightHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3241, 3410, 3),
            new WorldPoint(3235, 3410, 3),
            new WorldPoint(3235, 3418, 3),
            new WorldPoint(3241, 3418, 3)
    });


    private static String teleportName = "Agility: Varrock Rooftop";

    public eVarrockR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.VARROCK;
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
                ctx.viewport.angle(90);
                final SimpleObject o = ctx.objects.populate().filter(14412).filterHasAction("Climb").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Climb", "Rough wall")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Crossing clothes line";
                final SimpleObject o = ctx.objects.populate().filter(14413).filterHasAction("Cross").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Cross", "Clothes line")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14414).filterHasAction("Leap").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Leap", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Balancing wall";
                final SimpleObject o = ctx.objects.populate().filter(14832).filterHasAction("Balance").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Balance", "Wall")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14833).filterHasAction("Leap").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Leap", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14834).filterHasAction("Leap").nearest().next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Leap", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Leaping gap";
                final SimpleObject o = ctx.objects.populate().filter(14835).filterHasAction("Leap").nearest().next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Leap", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (seventhHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Crossing ledge";
                final SimpleObject o = ctx.objects.populate().filter(14836).filterHasAction("Hurdle").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Hurdle", "Ledge")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (eightHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping off edge";
                final SimpleObject o = ctx.objects.populate().filter(14841).filterHasAction("Jump-off").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump-off", "Edge")) {
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