package owmii.powah.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import owmii.powah.Powah;
import owmii.powah.block.cable.CableNet;
import owmii.powah.forge.compat.curios.CuriosCompat;
import owmii.powah.forge.data.DataEvents;
import owmii.powah.lib.util.Wrench;

@Mod(Powah.MOD_ID)
public class PowahForge {
    public PowahForge() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Powah.MOD_ID, modEventBus);

        Powah.init();
        modEventBus.addListener(DataEvents::gatherData);
        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
            if (event.getUseBlock() == Event.Result.DENY) {
                return;
            }
            if (Wrench.removeWithWrench(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ChunkEvent.Unload event) -> {
            if (event.getLevel() instanceof Level level) {
                CableNet.removeChunk(level, event.getChunk());
            }
        });

        if (FMLEnvironment.dist.isClient()) {
            try {
                Class.forName("owmii.powah.forge.client.PowahForgeClient").getMethod("init").invoke(null);
            } catch (Exception exception) {
                throw new RuntimeException("Failed to run powah forge client init", exception);
            }
        }

        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.init();
        }
    }
}
