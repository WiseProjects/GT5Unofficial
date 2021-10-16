package idealindustrial.util.energy.kinetic.system;

import idealindustrial.tile.impl.connected.ConnectedRotor;
import idealindustrial.tile.interfaces.host.HostMachineTile;
import idealindustrial.tile.interfaces.host.HostTile;
import idealindustrial.tile.interfaces.meta.Tile;
import idealindustrial.util.energy.kinetic.KUConsumer;
import idealindustrial.util.energy.kinetic.KUProducer;
import idealindustrial.util.misc.II_DirUtil;
import idealindustrial.util.misc.II_TileUtil;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KineticSystem {
    protected KUProducer producer;
    protected List<KUConsumer> consumers = new ArrayList<>();
    protected List<ConnectedRotor> rotors = new ArrayList<>();
    protected boolean isValid;
    protected boolean shouldUpdate = false;
    protected int lastSpeed = 0;

    public void update() {
        int requestPower = 0;
        int[] requests = new int[consumers.size()];
        for (int i = 0; i < consumers.size(); i++) {
            KUConsumer consumer = consumers.get(i);
            requests[i] = consumer.getPowerUsage();
            requestPower += requests[i];
        }
//        lastSpeed = producer.getSpeed(0);
//        if (requestPower == 0) {
//            return;
//        }
        float powerModification = ((float) producer.getTotalPower()) / requestPower;
        powerModification = Math.min(powerModification, 1f);
        int calculatedSpeed = producer.getSpeed(requestPower);
        lastSpeed = calculatedSpeed;
        for (int i = 0; i < consumers.size(); i++) {
            KUConsumer consumer = consumers.get(i);
            consumer.supply((int) (powerModification * requests[i]), calculatedSpeed);
        }
    }

    public void invalidate() {
        isValid = false;
        if (rotors.size() > 0) {
            rotors.get(0).sendRotationSpeed(0);
        }
        for (ConnectedRotor rotor : rotors) {
            rotor.onSystemInvalidate();
        }
        producer.setSystem(null);
        rotors.clear();
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    public void setShouldUpdate(boolean shouldUpdate) {
        this.shouldUpdate = shouldUpdate;
    }

    public void sendSpeed() {
        if (rotors.size() > 0) {
            rotors.get(0).sendRotationSpeed(lastSpeed);
        }
    }

    public KineticSystem(HostMachineTile tile, int side) {
        assert tile.getKineticEnergyHandler().getProducer(side) != null;
        producer = tile.getKineticEnergyHandler().getProducer(side);
        constructConnection(tile, new HashSet<>(), side);
        producer.setSystem(this);
        rotors.forEach(c -> c.system =  this);
        isValid = !rotors.isEmpty();
        producer.setSystem(this);
        if (!isValid()) {
            invalidate();
        }
        shouldUpdate = true;
    }

    private boolean constructConnection(HostTile host, Set<ConnectedRotor> rotors, int sideTo) {
        TileEntity tTile = host.getTileEntityAtSide(sideTo);
        HostTile tile = tTile instanceof HostTile ? (HostTile) tTile : null;
        if (tile == null) {
            return false;
        }
        int opSize = II_DirUtil.getOppositeSide(sideTo);
        if (tile instanceof HostMachineTile) {
            HostMachineTile machineTile = (HostMachineTile) tile;
            KUConsumer kuConsumer = machineTile.getKineticEnergyHandler().getConsumer(opSize);
            if (kuConsumer != null && !consumers.contains(kuConsumer)) {
                consumers.add(kuConsumer);
                if (kuConsumer.getNextDirection() != -1) {
                    constructConnection(machineTile, rotors, kuConsumer.getNextDirection());
                    return true;
                }
                return true;
            }
            return false;
        }
        if (tile.getMetaTile() instanceof ConnectedRotor) {
            ConnectedRotor rotor = (ConnectedRotor) tile.getMetaTile();
            if (!rotors.add(rotor)) {
                return false;
            }
            constructConnection(tile, rotors, sideTo);
            this.rotors.add(rotor);
            return true;
        }
        return false;
    }


}
