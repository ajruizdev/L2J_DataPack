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
package custom.MerchantOfAbyss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Merchant Of Abyss - L2Abyss feature
 * 
 * @author Antonio
 *
 */
public class MerchantOfAbyss extends AbstractNpcAI {

	// logger
	private static final Logger LOG = LoggerFactory
			.getLogger(MerchantOfAbyss.class);
	// npc
	private static final int NPC_ID = 1151600;
	// abyssal coin
	private static final int ABYSSAL_COIN_ID = 1000001;

	/**
	 * @param name
	 * @param descr
	 */
	public MerchantOfAbyss() {
		super(MerchantOfAbyss.class.getSimpleName(), "custom");

		addStartNpc(NPC_ID);
		addFirstTalkId(NPC_ID);
		addTalkId(NPC_ID);

		LOG.info("Loaded Merchant Of Abyss");
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		broadcastNpcSay(npc, Say2.NPC_ALL,
				"Habla rapido y cuida tus palabras, pues te pueden costar la vida...");
		String html = getHtm(player.getHtmlPrefix(), NPC_ID + ".htm");
		return html;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
		case "buy":
			if (npc.isInsideRadius(player, 300, false, true)) {
				player.setKarma(500);
				if (player.getInventory().getInventoryItemCount(
						ABYSSAL_COIN_ID, -1) > 0) {
					broadcastNpcSay(
							npc,
							Say2.NPC_ALL,
							"No tengo nada que vender por ahora miserable mortal. Vuelve en otro momento...");
				} else {
					broadcastNpcSay(npc, Say2.NPC_ALL,
							"No tienes nada que me interese miserable... Muere!");
					npc.doAttack(player);
				}
			}
			break;
		case "who":
			npc.doAttack(player);
			break;

		}
		return super.onAdvEvent(event, npc, player);
	}

	public static void main(String[] args) {
		new MerchantOfAbyss();
	}

}
