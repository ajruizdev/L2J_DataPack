/*
 * Copyright (C) 2004-2016 L2J Server
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
package custom.GraciaTeleporters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Castor y Polux AI
 * 
 * @author Blodust
 */
public class GraciaTeleporters extends AbstractNpcAI {
	private static final Logger LOG = LoggerFactory
			.getLogger(GraciaTeleporters.class);
	// NPC
	// Misc
	static final int CASTOR = 1004308;
	static final int POLUX = 1004309;
	static final Location CASTOR_LOC = new Location(-149309, 253015, -141);
	static final Location POLUX_LOC = new Location(-186763, 244068, 2670);

	public GraciaTeleporters() {
		super(GraciaTeleporters.class.getSimpleName(), "custom");
		addStartNpc(CASTOR);
		addFirstTalkId(CASTOR);
		addTalkId(CASTOR);
		addStartNpc(POLUX);
		addFirstTalkId(POLUX);
		addTalkId(POLUX);
		addSpawn(CASTOR, CASTOR_LOC, false, -1);
		addSpawn(POLUX, POLUX_LOC, false, -1);
		LOG.info("Loaded Castor and Polux");
	}

	/*
	 * @Override public String onFirstTalk(L2Npc npc, L2PcInstance player)
	 */

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {

		switch (event) {
		case "Travel":
			if (npc.isInsideRadius(player, 300, false, true)) {
				if (player.getLevel() > 79) {
					player.sendMessage("Saluda a mi hermano.");
					if (npc.getId() == 1004308) {
						player.teleToLocation(-186742, 244167, 2670);
					} else {
						player.teleToLocation(-149359, 253237, -122);
					}
				} else {
					player.sendMessage("Vuelve cuando seas LvL 80 o superior.");
				}
			} else {
				player.sendMessage("Vuelve si deseas viajar.");
			}
			break;
		}

		return super.onAdvEvent(event, npc, player);
	}

	public static void main(String[] args) {
		new GraciaTeleporters();
	}

}
