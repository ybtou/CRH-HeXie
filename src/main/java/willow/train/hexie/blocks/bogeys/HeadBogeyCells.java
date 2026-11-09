package willow.train.hexie.blocks.bogeys;

import net.minecraft.world.level.block.state.BlockState;

/**
 * The support cell a head car hangs its running gear on. HeXie mounts its own bogey
 * block in the open world; inside a Sable sub-level the same cell carries Simurail's
 * physics bogey so the plot follows the rail. Both count as the same structure member.
 */
public final class HeadBogeyCells {
    private HeadBogeyCells() {}

    public static boolean isSupport(BlockState state) {
        if (state.getBlock() instanceof HexieBogeyBlock<?>) return state.getValue(HexieBogeyBlock.HEAD_MOUNTED);
        return willow.train.hexie.compat.HeXieSimurailBogey.isPhysicsBogey(state);
    }
}
