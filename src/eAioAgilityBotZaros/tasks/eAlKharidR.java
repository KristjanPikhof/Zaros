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

public class eAlKharidR extends Task {

    //Locations
    private static final WorldArea startLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(3278, 3190, 0),
            new WorldPoint(3268, 3190, 0),
            new WorldPoint(3268, 3200, 0),
            new WorldPoint(3278, 3200, 0)
    });
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3270, 3194, 3),
            new WorldPoint(3270, 3189, 3),
            new WorldPoint(3272, 3189, 3),
            new WorldPoint(3272, 3183, 3),
            new WorldPoint(3271, 3183, 3),
            new WorldPoint(3271, 3179, 3),
            new WorldPoint(3276, 3179, 3),
            new WorldPoint(3276, 3184, 3),
            new WorldPoint(3279, 3184, 3),
            new WorldPoint(3279, 3189, 3),
            new WorldPoint(3277, 3189, 3),
            new WorldPoint(3277, 3194, 3)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3274, 3175, 3),
            new WorldPoint(3274, 3160, 3),
            new WorldPoint(3264, 3160, 3),
            new WorldPoint(3264, 3175, 3)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3282, 3177, 3),
            new WorldPoint(3282, 3159, 3),
            new WorldPoint(3304, 3159, 3),
            new WorldPoint(3304, 3170, 3),
            new WorldPoint(3294, 3170, 3),
            new WorldPoint(3294, 3168, 3),
            new WorldPoint(3288, 3168, 3),
            new WorldPoint(3288, 3177, 3)
    });

    private static final WorldArea thirdHouseSmall = new WorldArea (new WorldPoint[] {
            new WorldPoint(3304, 3159, 3),
            new WorldPoint(3294, 3159, 3),
            new WorldPoint(3294, 3169, 3),
            new WorldPoint(3304, 3169, 3)
    });
    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3312, 3167, 1),
            new WorldPoint(3312, 3159, 1),
            new WorldPoint(3320, 3159, 1),
            new WorldPoint(3320, 3167, 1)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3319, 3180, 2),
            new WorldPoint(3311, 3180, 2),
            new WorldPoint(3313, 3173, 2),
            new WorldPoint(3319, 3173, 2)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3319, 3188, 3),
            new WorldPoint(3311, 3188, 3),
            new WorldPoint(3311, 3179, 3),
            new WorldPoint(3319, 3179, 3)
    });

    private static final WorldArea seventhHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3307, 3189, 3),
            new WorldPoint(3302, 3184, 3),
            new WorldPoint(3295, 3191, 3),
            new WorldPoint(3300, 3196, 3)
    });

    private static final WorldArea lastLocation = new WorldArea (new WorldPoint[] {
            new WorldPoint(3297, 3189, 0),
            new WorldPoint(3303, 3195, 0),
            new WorldPoint(3299, 3198, 0),
            new WorldPoint(3292, 3193, 0)
    });

    private static String teleportName = "Agility: Al-Kharid Rooftop";

    public eAlKharidR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.ALKHARID;
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
                final SimpleObject o = ctx.objects.populate().filter(11633).filterHasAction("Climb").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Climb", "Rough wall")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Crossing tightrope";
                final SimpleObject o = ctx.objects.populate().filter(14398).filterHasAction("Cross").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Cross", "Tightrope")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Swinging cable";
                final SimpleObject o = ctx.objects.populate().filter(14402).filterHasAction("Swing-across").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Swing-across", "Cable")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }
            } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Teeth-gripping zip line";
                if (ctx.pathing.step(new WorldPoint(3299, 3163, 3)) || thirdHouseSmall.containsPoint(ctx.players.getLocal().getLocation())) {
                    final SimpleObject o = ctx.objects.populate().filter(14403).filterHasAction("Teeth-grip").next();
                    if (o != null && o.validateInteractable()) {
                        if (o.click("Teeth-grip", "Zip line")) {
                            ctx.onCondition(() -> inMotion, 1200);
                        }
                    }
                }

            } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Swinging tropical tree";
                final SimpleObject o = ctx.objects.populate().filter(14404).filterHasAction("Swing-across").next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Swing-across", "Tropical tree")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Climbing roof top beams";
                final SimpleObject o = ctx.objects.populate().filter(11634).filterHasAction("Climb").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Climb", "Roof top beams")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Crossing tightrope";
                final SimpleObject o = ctx.objects.populate().filter(14409).filterHasAction("Cross").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Cross", "Tightrope")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (seventhHouse.containsPoint(ctx.players.getLocal().getLocation())) {
                eMain.status = "Jumping gap";
                final SimpleObject o = ctx.objects.populate().filter(14399).filterHasAction("Jump").nearest().next();
                if (o != null && o.validateInteractable() && !inMotion) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> inMotion, 1200);
                    }
                }

            } else if (lastLocation.containsPoint(ctx.players.getLocal().getLocation())) {
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