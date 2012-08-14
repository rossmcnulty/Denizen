package net.aufdemrand.denizen.commands.core;

import java.util.logging.Level;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.aufdemrand.denizen.commands.AbstractCommand;
import net.aufdemrand.denizen.scripts.ScriptEntry;
import net.citizensnpcs.command.exception.CommandException;

/**
 * Executes a bukkit command
 * 
 * @author Jeremy Schroeder
 */

public class ExecuteCommand extends AbstractCommand {

	/* EXECUTE [ASPLAYER|ASSERVER|ASNPC] '[command with arguments]' */

	/* 
	 * Arguments: [] - Required, () - Optional 
	 * [ASPLAYER|ASSERVER|ASNPC] defines how the command should be executed.
	 * [command with arguments] is the command to run, without a /. Can also
	 *   use <PLAYER> and <WORLD> variables.
	 *   
	 * Example Usage:
	 * EXECUTE ASSERVER 'gamemode <PLAYER> 2'
	 * EXECUTE ASNPC 'toggledownfall'
	 * 
	 */

	enum ExecuteType { ASSERVER, ASDENIZEN, ASPLAYER }

	@Override
	public boolean execute(ScriptEntry theEntry) throws CommandException {

		/* Initialize variables */ 

		String commandtoExecute = null;
		ExecuteType executeType = null;

		if (theEntry.arguments() == null)
			throw new CommandException("...Usage: EXECUTE [ASSERVER|ASNPC|ASPLAYER] '[Command to execute]'");

		/* Match arguments to expected variables */
		for (String thisArg : theEntry.arguments()) {

			/* If argument is ASPLAYER */
			if (thisArg.equalsIgnoreCase("ASPLAYER")) {
				executeType = ExecuteType.ASPLAYER;
				aH.echoDebug("...executing '%s'.", thisArg);
			}

			/* If argument is ASNPC */
			else if (thisArg.equalsIgnoreCase("ASNPC")) {
				executeType = ExecuteType.ASDENIZEN;
				aH.echoDebug("...executing '%s'.", thisArg);
			}

			/* If argument is ASSERVER */
			else if (thisArg.equalsIgnoreCase("ASSERVER")) {
				executeType = ExecuteType.ASSERVER;
				aH.echoDebug("...executing '%s'.", thisArg);
			}

			else {
				aH.echoDebug("...set command to execute.", thisArg);
				commandtoExecute = thisArg
						.replace("<PLAYER>", theEntry.getPlayer().getName())
						.replace("<WORLD>", theEntry.getPlayer().getWorld().getName());
			}

		}	

		/* Execute the command, if all required variables are filled. */
		if (commandtoExecute != null && executeType != null) {

			switch (executeType) {

			case ASPLAYER:
				theEntry.getPlayer().performCommand(commandtoExecute);
				break;

			case ASDENIZEN:
				if (theEntry.getDenizen().getCitizensEntity().getBukkitEntity().getType() == EntityType.PLAYER) {
					((Player) theEntry.getDenizen().getEntity()).setOp(true);
					((Player) theEntry.getDenizen().getEntity()).performCommand(commandtoExecute);
					((Player) theEntry.getDenizen().getEntity()).setOp(false);
				} else throw new CommandException("...cannot EXECUTE ASNPC unless NPC is Human!");
				break;

			case ASSERVER:
				plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commandtoExecute);
			}

			return true;
		}

		return false;
	}

}