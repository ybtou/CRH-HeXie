package willow.train.hexie.compat.create;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import willow.train.hexie.api.cab.DrivingConsoleContext;
import willow.train.hexie.blocks.emu.CRH2DriverSeatBlock;

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

    /** World-space right-hand exit, used where the carrier's own frame must place the feet. */
    public static Vec3 right(AbstractContraptionEntity entity, BlockPos seat) {
        Vec3 feet = localRight(entity, seat);
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
     * Physical structures exit one block to the seat's right. The right-hand cell is taken from
     * the seat block's own FACING (it fronts the console), so it stays correct even when the
     * console is not the immediate neighbour the generic scan expects.
     */
    public static Vec3 localRight(AbstractContraptionEntity entity, BlockPos seat) {
        var world = entity.getContraption().getContraptionWorld();
        var state = world.getBlockState(seat);
        var facing = seatFacing(state);
        if (facing == null) facing = DrivingConsoleContext.consoleFacing(seat, world::getBlockState);
        if (facing == null) return null;
        return Vec3.atBottomCenterOf(seat.relative(facing.getClockWise()));
    }

    /** The seat block records which way the driver faces; older seats have no FACING property. */
    private static Direction seatFacing(BlockState state) {
        if (state == null || !state.hasProperty(CRH2DriverSeatBlock.FACING)) return null;
        return state.getValue(CRH2DriverSeatBlock.FACING);
    }
}
