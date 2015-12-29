/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.voicedcommandhandlers;

import java.util.Collection;

import com.l2jserver.Config;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Antonio
 *
 */
public class PlayersOnline implements IVoicedCommandHandler {

	private static long LAST_UPDATE = 0;
	private static int OFFLINE_COUNT = 0;
	private static int ONLINE_COUNT = 0;

	private static final String[] _voicedCommands = { "online", "tradeoffline" };

	private void calcOfflineTraders() {
		OFFLINE_COUNT = 0;

		final Collection<L2PcInstance> pjs = L2World.getInstance().getPlayers();
		for (L2PcInstance player : pjs) {
			if (player.isInOfflineMode()) {
				OFFLINE_COUNT++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.l2jserver.gameserver.handler.IVoicedCommandHandler#useVoicedCommand
	 * (java.lang.String,
	 * com.l2jserver.gameserver.model.actor.instance.L2PcInstance,
	 * java.lang.String)
	 */
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar,
			String params) {
		if (command.equals("online")) {
			// update every minute in order to prevent server overload
			if (System.currentTimeMillis() > (LAST_UPDATE + (10 * 1000))) {
				LAST_UPDATE = System.currentTimeMillis();
				ONLINE_COUNT = L2World.getInstance().getAllPlayersCount();

				calcOfflineTraders();
			}

			activeChar.sendMessage("=== JUGADORES ONLINE ===");
			activeChar
					.sendMessage("Jugadores online: "
							+ ((ONLINE_COUNT + OFFLINE_COUNT) * Config.ONLINE_COMMAND_MULTIPLIER));
		}

		if (command.equals("tradeoffline")) {
			// update every minute in order to prevent server overload
			if (System.currentTimeMillis() > (LAST_UPDATE + (60 * 1000))) {
				LAST_UPDATE = System.currentTimeMillis();

				calcOfflineTraders();
			}

			activeChar.sendMessage("=== JUGADORES EN TRADE OFFLINE ===");
			activeChar.sendMessage("Tiendas offline: " + OFFLINE_COUNT);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.l2jserver.gameserver.handler.IVoicedCommandHandler#getVoicedCommandList
	 * ()
	 */
	@Override
	public String[] getVoicedCommandList() {
		return _voicedCommands;
	}

}
