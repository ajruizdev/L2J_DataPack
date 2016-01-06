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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.data.xml.impl.MultisellData;
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
	// npc - merchant of abyss
	private static final int NPC_ID = 1151600;
	// abyssal coin
	private static final int ABYSSAL_COIN_ID = 1000001;
	// vitality potion
	private static final int BIRTHDAY_VITALITY_POTION_ID = 22188;

	// list of players who got vita potions from Merchant of Abyss
	private static final Map<Integer, Long> PLAYER_LAST_TIME_GOT_VITA = new HashMap<Integer, Long>();

	/**
	 * Config parameters Price in Abyssal Coins
	 */
	private static final int VITA_POTION_PRICE = 15;
	private static final int VITA_POTION_QUANTITY = 1;
	private static final int CLEAN_UP_KARMA_PRICE = 1;

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
		// if player is not in the list, add it
		if (!PLAYER_LAST_TIME_GOT_VITA.containsKey(player.getObjectId())) {
			PLAYER_LAST_TIME_GOT_VITA.put(player.getObjectId(), 0L);
		}
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
				// player.setKarma(player.getKarma() + 100);
				if (player.getInventory().getInventoryItemCount(
						ABYSSAL_COIN_ID, -1) > 0) {
					return getHtm(player.getHtmlPrefix(), NPC_ID + "-buy.htm");
				} else {
					broadcastNpcSay(npc, Say2.NPC_ALL,
							"No tienes nada que me interese miserable... Muere!");
					npc.doAttack(player);
				}
			}
			break;
		case "buy-a":
			MultisellData.getInstance().separateAndSend(364990002, player, npc,
					false);
			break;
		case "buy-jew-a":
			MultisellData.getInstance().separateAndSend(364990005, player, npc,
					false);
			break;
		case "buy-s":
			MultisellData.getInstance().separateAndSend(364990001, player, npc,
					false);
			break;
		case "buy-jew-s":
			MultisellData.getInstance().separateAndSend(364990004, player, npc,
					false);
			break;
		case "buy-s80":
			MultisellData.getInstance().separateAndSend(364990003, player, npc,
					false);
			break;
		case "buy-jew-s80":
			MultisellData.getInstance().separateAndSend(364990006, player, npc,
					false);
			break;
		case "buy-mounts":
			MultisellData.getInstance().separateAndSend(364990007, player, npc,
					false);
			break;
		case "buy-aga":
			MultisellData.getInstance().separateAndSend(364990008, player, npc,
					false);
			break;
		case "buy-acc":
			MultisellData.getInstance().separateAndSend(364990009, player, npc,
					false);
			break;
		case "vitality":
			return getHtm(player.getHtmlPrefix(), NPC_ID + "-vita.htm");
		case "buy-vita":
			// if player bought vita potion to MOA less than 24 hours ago
			// do not sell anymore
			if ((PLAYER_LAST_TIME_GOT_VITA.get(player.getObjectId()) + 86400000) > System
					.currentTimeMillis()) {
				broadcastNpcSay(npc, Say2.NPC_ALL,
						"No tengo mas pociones por ahora...");
				return "";
			}
			if (player.getInventory()
					.getInventoryItemCount(ABYSSAL_COIN_ID, -1) >= VITA_POTION_PRICE) {
				player.getInventory().destroyItemByItemId("MerchantOfAbyss",
						ABYSSAL_COIN_ID, VITA_POTION_PRICE, player, null);
				player.sendMessage("Has entregado " + VITA_POTION_PRICE
						+ " Moneda Abisal al Mercader del Abismo.");
				player.getInventory().addItem("MerchantOfAbyss",
						BIRTHDAY_VITALITY_POTION_ID, VITA_POTION_QUANTITY,
						player, null);
				// update last time player got vita potion
				PLAYER_LAST_TIME_GOT_VITA.replace(player.getObjectId(),
						System.currentTimeMillis());
				broadcastNpcSay(npc, Say2.NPC_ALL,
						"Trato hecho, aqui tienes...");
			} else {
				broadcastNpcSay(npc, Say2.NPC_ALL,
						"Pretendes engañarme? JAJAJA... Muere!");
				npc.doAttack(player);
			}
			break;
		case "karma":
			if (player.getInventory()
					.getInventoryItemCount(ABYSSAL_COIN_ID, -1) > 0) {
				player.getInventory().destroyItemByItemId("MerchantOfAbyss",
						ABYSSAL_COIN_ID, CLEAN_UP_KARMA_PRICE, player, null);
				player.sendMessage("Has entregado " + CLEAN_UP_KARMA_PRICE
						+ " Moneda Abisal al Mercader del Abismo.");
				player.setKarma(0);
				broadcastNpcSay(npc, Say2.NPC_ALL,
						"Trato hecho... ya no te busca nadie! JAJAJA...");
			} else {
				broadcastNpcSay(npc, Say2.NPC_ALL,
						"Pretendes engañarme? JAJAJA... Muere!");
				npc.doAttack(player);
			}
			break;
		case "who":
			broadcastNpcSay(npc, Say2.NPC_ALL,
					"La respuesta a esa pregunta vale mas que tu vida... Muere!");
			npc.doAttack(player);
			break;

		}
		return super.onAdvEvent(event, npc, player);
	}

	public static void main(String[] args) {
		new MerchantOfAbyss();
	}

}
