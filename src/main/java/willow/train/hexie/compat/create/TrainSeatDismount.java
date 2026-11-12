package willow.train.hexie.compat.create;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import willow.train.hexie.api.cab.DrivingConsoleContext;

/** Exit one block away from the operation point; the side depends on the carrier. */
public final class TrainSeatDismount {
    private TrainSeatDismount() {}

    public static Vec3 behind(AbstractContraptionEntity entity, BlockPos seat) {
        Vec3 feet = localBehind(entity, seat);
        if (feet == null) return null;
        return entity instanceof SnapshotCarriageEntity car
                ? CarriagePlayerSupport.frame(car, 1).world(feet)
                : entity.toGlobalVector(feet, 1);
    }

    public static Vec3 localBehind(AbstractContraptionEntity entity, BlockPos seat) {
        var world = entity.getContraption().getContraptionWorld();
        var facing = DrivingConsoleContext.consoleFacing(seat, world::getBlockState);
        return facing == null ? null : Vec3.atBottomCenterOf(seat.relative(facing.getOpposite()));
    }

    /**
     * Physical structures exit one block to the seat's right. The seat fronts the console,
     * so the right-hand cell is {@code facing.getClockWise()} while still leaving the desk.
     */
    public static Vec3 localRight(AbstractContraptionEntity entity, BlockPos seat) {
        var world = entity.getContraption().getContraptionWorld();
        var facing = DrivingConsoleContext.consoleFacing(seat, world::getBlockState);
        return facing == null ? null : Vec3.atBottomCenterOf(seat.relative(facing.getClockWise()));
    }
}
