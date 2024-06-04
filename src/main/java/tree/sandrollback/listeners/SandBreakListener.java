package tree.sandrollback.listeners;

import com.palmergames.bukkit.towny.TownyAPI;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tree.sandrollback.SandRollbackPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SandBreakListener implements Listener {
    private final FoliaLib foliaLib = new FoliaLib(SandRollbackPlugin.getInstance());
    private final Map<Location, BlockState> blocksToRollback = new HashMap<>();
    private WrappedTask rollbackTask;
    public SandBreakListener() {
        start();
    }

    private void start() {
        rollbackTask = foliaLib.getImpl().runTimerAsync(() -> {
            // Perform block state updates
            for (Location location : new HashSet<>(blocksToRollback.keySet())) {
                BlockState state = blocksToRollback.remove(location);
                if (state != null) {
                    foliaLib.getImpl().runAtLocation(location, blocks -> {
                        state.update(true, true);  // if you want to apply physics to sand change to false
                    });
                }
            }
        }, 1, SandRollbackPlugin.getInstance().getRollbackTime());
    }


    public void shutdown() {
        if (rollbackTask != null && !rollbackTask.isCancelled()) {
            rollbackTask.cancel();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // Check if event block is happening out of town claims
        if (TownyAPI.getInstance().isWilderness(block.getLocation())) {
            if (block.getType() == Material.SAND) {
                BlockState blockState = block.getState();
                blocksToRollback.put(block.getLocation(), blockState);

                Block topBlock = getTopBlockAboveSand(block);

                if (topBlock != null) {
                    BlockState topBlockState = topBlock.getState();
                    blocksToRollback.put(topBlock.getLocation(), topBlockState);
                }
            }
        }
    }
    // Method to find the top block above a given sand block
    private Block getTopBlockAboveSand(Block sandBlock) {
        // Start by getting the block directly above the sand block
        Block topBlock = sandBlock.getWorld().getBlockAt(sandBlock.getLocation().clone().add(0, 1, 0));
        // Continue to move upwards while the block type is not air
        while (topBlock.getType() != Material.AIR) {
            // Goes to the next block on top of other one
            topBlock = topBlock.getWorld().getBlockAt(topBlock.getLocation().clone().add(0, 1, 0));
        }
        // If an air block is found then it returns the block just below it
        return topBlock.getType() == Material.AIR ? topBlock.getRelative(0, -1, 0) : null;
    }

}

