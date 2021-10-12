package idealindustrial.util.energy.kinetic.system;

import idealindustrial.tile.interfaces.host.HostMachineTile;
import idealindustrial.util.energy.electric.system.CableSystem;
import idealindustrial.util.energy.kinetic.KineticEnergyHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KineticSystemHandler {

    protected static List<KineticSystem> systems = new ArrayList<>();

    private static long timer = 1;

    public static void onTick() {
        for (Iterator<KineticSystem> iterator = systems.iterator(); iterator.hasNext(); ) {
            KineticSystem system = iterator.next();
            if (system.isValid()) {
                system.update();
            }
            else {
                iterator.remove();
            }
        }
        if (timer++ % 4 == 0) {
            systems.forEach(s ->s.sendSpeed());
        }
        int a = 0;
    }

    /**
     * creates energy system from producer tile
     * @param tile - producer tile
     * @param side - side of tile with producer
     */
    public static void initSystem(HostMachineTile tile, int side) {
        try {
            systems.add(new KineticSystem(tile, side));
        }
        catch (Throwable e) {
            e.printStackTrace();//same as top one
        }
    }
}
