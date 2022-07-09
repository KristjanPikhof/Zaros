package eAioAgilityBot.tasks;

import eAioAgilityBot.eMain;
import eAioAgilityBot.eGui;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.simplebot.Pathing;
import simple.hooks.wrappers.SimpleGroundItem;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class eCanifisR extends Task {

    private final WorldArea START = new WorldArea(new WorldPoint(3502, 3484, 0), new WorldPoint(3510, 3490, 0));
    private static final WorldArea firstHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3504, 3490, 2),
            new WorldPoint(3504, 3494, 2),
            new WorldPoint(3503, 3499, 2),
            new WorldPoint(3509, 3499, 2),
            new WorldPoint(3512, 3496, 2),
            new WorldPoint(3512, 3494, 2),
            new WorldPoint(3509, 3493, 2),
            new WorldPoint(3509, 3490, 2)
    });

    private static final WorldArea secondHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3505, 3503, 2),
            new WorldPoint(3505, 3507, 2),
            new WorldPoint(3503, 3508, 2),
            new WorldPoint(3496, 3508, 2),
            new WorldPoint(3495, 3506, 2),
            new WorldPoint(3495, 3503, 2)
    });

    private static final WorldArea thirdHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3493, 3506, 2),
            new WorldPoint(3488, 3506, 2),
            new WorldPoint(3488, 3503, 2),
            new WorldPoint(3485, 3503, 2),
            new WorldPoint(3484, 3501, 2),
            new WorldPoint(3484, 3497, 2),
            new WorldPoint(3493, 3498, 2),
            new WorldPoint(3494, 3500, 2),
            new WorldPoint(3494, 3506, 2)
    });

    private static final WorldArea fourthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3481, 3501, 3),
            new WorldPoint(3481, 3490, 3),
            new WorldPoint(3473, 3491, 3),
            new WorldPoint(3473, 3500, 3),
            new WorldPoint(3475, 3500, 3),
            new WorldPoint(3476, 3501, 3)
    });

    private static final WorldArea fifthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3476, 3488, 2),
            new WorldPoint(3485, 3488, 2),
            new WorldPoint(3485, 3484, 2),
            new WorldPoint(3482, 3484, 2),
            new WorldPoint(3482, 3480, 2),
            new WorldPoint(3476, 3481, 2)
    });

    private static final WorldArea sixthHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3490, 3480, 3),
            new WorldPoint(3489, 3478, 3),
            new WorldPoint(3487, 3478, 3),
            new WorldPoint(3487, 3467, 3),
            new WorldPoint(3500, 3468, 3),
            new WorldPoint(3503, 3471, 3),
            new WorldPoint(3505, 3471, 3),
            new WorldPoint(3505, 3478, 3),
            new WorldPoint(3498, 3478, 3),
            new WorldPoint(3498, 3480, 3)
    });

    private static final WorldArea seventhHouse = new WorldArea (new WorldPoint[] {
            new WorldPoint(3508, 3484, 2),
            new WorldPoint(3517, 3484, 2),
            new WorldPoint(3517, 3477, 2),
            new WorldPoint(3513, 3475, 2),
            new WorldPoint(3513, 3473, 2),
            new WorldPoint(3509, 3474, 2),
            new WorldPoint(3508, 3478, 2),
            new WorldPoint(3507, 3479, 2),
            new WorldPoint(3507, 3482, 2)
    });


    public eCanifisR(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean condition() {
        return eMain.courseName == eMain.State.CANIFIS;
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

        if (START.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Climbing tree";
            final SimpleObject o = ctx.objects.populate().filter("Tall tree").filterHasAction("Climb").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Climb", "Tall tree")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (firstHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            final SimpleObject o = ctx.objects.populate().filter(14844).filterHasAction("Jump").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (secondHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            final SimpleObject o = ctx.objects.populate().filter(14845).filterHasAction("Jump").next();
            eMain.status = "Jumping gap";
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (thirdHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            final SimpleObject o = ctx.objects.populate().filter(14848).filterHasAction("Jump").next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (fourthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            final SimpleObject o = ctx.objects.populate().filter(14846).filterHasAction("Jump").next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (fifthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Pole-Vault jump";
            final SimpleObject o = ctx.objects.populate().filter(14894).filterHasAction("Vault").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Vault", "Pole-vault")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }

        } else if (sixthHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            if (ctx.pathing.step(new WorldPoint(3502, 3476, 3))) {
                final SimpleObject o = ctx.objects.populate().filter(14847).filterHasAction("Jump").nearest().next();
                if (o != null && o.validateInteractable()) {
                    if (o.click("Jump", "Gap")) {
                        ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                    }
                }
            }

        } else if (seventhHouse.containsPoint(ctx.players.getLocal().getLocation())) {
            eMain.status = "Jumping gap";
            final SimpleObject o = ctx.objects.populate().filter(14897).filterHasAction("Jump").nearest().next();
            if (o != null && o.validateInteractable()) {
                if (o.click("Jump", "Gap")) {
                    ctx.onCondition(() -> ctx.pathing.inMotion(), 1200);
                }
            }
        }
    }


    @Override
    public String status() {
        return "Completed lap: " + eMain.count;
    }
}