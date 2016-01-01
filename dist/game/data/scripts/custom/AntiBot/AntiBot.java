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
package custom.AntiBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * Anti Bot system - L2Abyss feature
 * 
 * @author Antonio
 *
 */
public class AntiBot extends AbstractNpcAI {

	private static final Logger LOG = LoggerFactory.getLogger(AntiBot.class);

	// Chance for showing htm after killing a monster:
	private static final int CHANCE = 1;
	// Time for clicking the button before being kicked
	private static final int TIME = 1;

	public AntiBot() {
		super(AntiBot.class.getSimpleName(), "custom");

		for (L2NpcTemplate npc : NpcData.getInstance().getAllNpcOfClassType(
				"L2Monster")) {
			addKillId(npc.getId());
		}

		LOG.info("Loaded Anti Bot system");
	}

	private final class BotKickTask implements Runnable {
		private final L2PcInstance _player;

		public BotKickTask(L2PcInstance player) {
			_player = player;
		}

		@Override
		public void run() {
			if (_player != null) {
				_player.logout(true);
			}
		}
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (getRandom(100) < CHANCE) {
			// Effect :
			if (!killer.getVariables().hasVariable("BOT_KICK_TASK")) {
				killer.getVariables().set(
						"BOT_KICK_TASK",
						ThreadPoolManager.getInstance().scheduleAi(
								new BotKickTask(killer), TIME * 1000 * 60));

				// Anti Bot window:
				return "antibot.htm";
			}
		}

		return super.onKill(npc, killer, isSummon);
	}

	public static void main(String[] args) {
		new AntiBot();
	}

}
