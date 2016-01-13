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
package custom.PaganTemple;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * @author Antonio
 *
 */
public class PaganTemple extends AbstractNpcAI {

	public PaganTemple() {
		super(PaganTemple.class.getSimpleName(), "custom");
		addSpawnId(22136); // Doorman Zombie
		addSpawnId(22137); // Penance Guard
		addSpawnId(22138); // Chapel Guard
	}

	@Override
	public String onSpawn(L2Npc npc) {
		npc.setIsNoRndWalk(true);
		return super.onSpawn(npc);
	}

	public static void main(String[] args) {
		new PaganTemple();
	}
}
