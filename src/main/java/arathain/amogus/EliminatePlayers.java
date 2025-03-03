package arathain.amogus;

import arathain.amogus.command.EliminatePlayersCommand;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

import java.util.ArrayList;
import java.util.UUID;

public class EliminatePlayers implements ModInitializer {
	public static final ArrayList<UUID> bannedUuids = new ArrayList<>();
	public static boolean enabled = true; // Default to enabled

	@Override
	public void onInitialize(ModContainer mod) {
		// Example: Add a default banned UUID (optional)
		bannedUuids.add(UUID.fromString("bc78a0d3-73fd-44c0-b971-a744b36db76a"));

		// Register the command
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
			EliminatePlayersCommand.register(dispatcher);
		});
	}
}