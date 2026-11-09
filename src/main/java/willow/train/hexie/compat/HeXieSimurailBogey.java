package willow.train.hexie.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

/**
 * Puts a head car's support cell on Simurail's physics bogey inside a Sable sub-level.
 *
 * Simurail only builds its pivot and axles when the block entity is owned by a server
 * sub-level (PhysicsBogeyBlockEntity.init), which is exactly the case that needs them:
 * the plot has to be pulled along the rail graph instead of sitting on it. In the open
 * world the same cell keeps HeXie's own bogey block, so nothing changes there.
 */
public final class HeXieSimurailBogey {
    private static Boolean available;

    private HeXieSimurailBogey() {}

    public static boolean available() {
        Boolean cached = available;
        if (cached == null) {
            cached = ModList.get().isLoaded("simurail");
            available = cached;
        }
        return cached;
    }

    /** Recognises either Simurail bogey block by registry name, without resolving its classes. */
    public static boolean isPhysicsBogey(BlockState state) {
        if (!available()) return false;
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!"simurail".equals(id.getNamespace())) return false;
        return "physics_bogey".equals(id.getPath()) || "unpowered_physics_bogey".equals(id.getPath());
    }

    /** Returns whether the cell now carries a physics bogey. */
    public static boolean place(Level level, BlockPos pos, Direction facing) {
        if (level.isClientSide || !available()) return false;
        var state = Loaded.state(facing);
        if (!level.setBlock(pos, state, Block.UPDATE_ALL)) return false;
        Loaded.configure(level, pos);
        return true;
    }

    /** Only resolved once the mod guard above has passed. */
    private static final class Loaded {
        private static BlockState state(Direction facing) {
            return com.crystaelix.simurail.content.SimurailBlocks.UNPOWERED_PHYSICS_BOGEY.get()
                    .defaultBlockState()
                    .setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING, facing);
        }

        private static void configure(Level level, BlockPos pos) {
            if (!(level.getBlockEntity(pos) instanceof com.crystaelix.simurail.content.bogey.PhysicsBogeyBlockEntity bogey))
                return;
            var options = bogey.getOptions();
            options.type = new com.crystaelix.simurail.api.bogey.BogeySubtype(
                    new com.crystaelix.simurail.api.bogey.BogeyType(
                            willow.train.hexie.init.bogeys.BogeyStylesInit.CRHBOGEY,
                            willow.train.hexie.init.bogeys.BogeySizesInit.DT206));
            // setOptions clears the cached bogey data and syncs the new subtype to clients.
            bogey.setOptions(options);
        }
    }
}
