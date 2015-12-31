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
package custom.ExiledDivine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.npc.AbstractNpcAI;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Exiled Divine - L2Abyss feature
 * 
 * @author Antonio
 *
 */
public class ExiledDivine extends AbstractNpcAI {

	private static final Logger LOG = LoggerFactory
			.getLogger(ExiledDivine.class);

	private static final int NPC_ID = 1151602;

	private static final int ABYSSAL_COIN_ID = 1000001;

	private static final int NOBLESSE_BLESSING = 1323; // max lvl 1

	private static final Map<Integer, Boolean> PLAYER_PAID = new HashMap<Integer, Boolean>();

	private static final int[][] MAGE_BUFFS = {
			// skill id - lvl
			{ 1040, 3 }, // shield
			{ 1035, 4 }, // mental shield
			{ 1062, 2 }, // berserker
			{ 1204, 2 }, // wind walk
			{ 1085, 3 }, // acumen
			{ 1078, 6 }, // concentration
			{ 1036, 2 }, // magic barrier
			{ 1045, 6 }, // blessed body
			{ 1048, 6 }, // blessed soul
			{ 1389, 3 }, // greater shield
			{ 1043, 1 }, // holy weapon
			{ 1059, 3 }, // empower
			{ 1303, 2 }, // wild magic
			{ 1354, 1 }, // arcane protection
			{ 273, 1 }, // dance of mystic
			{ 276, 1 }, // dance of concentration
			{ 1257, 3 }, // decrease weight
			{ 1259, 4 }, // resist shock
			{ 1397, 3 }, // clarity
			{ 264, 1 }, // song of earth
			{ 267, 1 }, // song of warding
	};

	private static final int[][] WARRIOR_BUFFS = {
			// skill id - lvl
			{ 1040, 3 }, // shield
			{ 1068, 3 }, // might
			{ 1388, 3 }, // greater might
			{ 1204, 2 }, // wind walk
			{ 1242, 3 }, // death whisper
			{ 1240, 3 }, // guidance
			{ 1036, 2 }, // magic barrier
			{ 1045, 6 }, // blessed body
			{ 1087, 3 }, // agility
			{ 1389, 3 }, // greater shield
			{ 1086, 2 }, // haste
			{ 1044, 3 }, // regeneration
			{ 1077, 3 }, // focus
			{ 1354, 1 }, // arcane protection
			{ 271, 1 }, // dance of warrior
			{ 275, 1 }, // dance of fury
			{ 1257, 3 }, // decrease weight
			{ 1259, 4 }, // resist shock
			{ 1268, 4 }, // vampiric rage
			{ 264, 1 }, // song of earth
			{ 267, 1 }, // song of warding
	};

	public ExiledDivine() {
		super(ExiledDivine.class.getSimpleName(), "custom");

		addStartNpc(NPC_ID);
		addFirstTalkId(NPC_ID);
		addTalkId(NPC_ID);

		LOG.info("Loaded Exiled Divine");
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {

		// set value false for current player if it is not listed
		if (!PLAYER_PAID.containsKey(player.getObjectId())) {
			PLAYER_PAID.put(player.getObjectId(), false);
		}

		// check if npc is casting on other player in order to
		// prevent a target change
		if (npc.isCastingNow()) {
			broadcastNpcSay(npc, Say2.NPC_ALL, "Lo siento " + player.getName()
					+ ", ahora mismo estoy ocupado!");
			return "";
		}

		String html = "";
		if (player.isVip() || PLAYER_PAID.get(player.getObjectId())) {
			html = getHtm(player.getHtmlPrefix(), NPC_ID + ".htm");
		} else {
			html = getHtm(player.getHtmlPrefix(), "novip.htm");
		}

		return html;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {

		if (npc.isInsideRadius(player, 200, false, true)) {
			if (!player.isVip() && !PLAYER_PAID.get(player.getObjectId())) {
				if (!(player.getInventory().getInventoryItemCount(
						ABYSSAL_COIN_ID, -1) >= 1)) {
					return getHtm(player.getHtmlPrefix(), "nocoin.htm");
				}
				switch (event) {
				case "pay1":
					player.getInventory().destroyItemByItemId("ExiledDivine",
							ABYSSAL_COIN_ID, 1, player, null);
					player.sendMessage("Has entregado 1 Moneda Abisal al Divine Exiliado.");
					PLAYER_PAID.replace(player.getObjectId(), true);
					return getHtm(player.getHtmlPrefix(), NPC_ID + ".htm");
				case "pay5":
					return getHtm(player.getHtmlPrefix(), "todo.htm");
				}
				return "";
			}

			switch (event) {
			case "buff_mage":
				castBuffs("mage", npc, player);
				break;
			case "buff_warrior":
				castBuffs("warrior", npc, player);
				break;
			}
			PLAYER_PAID.replace(player.getObjectId(), false);
		}
		return super.onAdvEvent(event, npc, player);
	}

	private void castBuffs(String type, L2Npc npc, L2PcInstance player) {
		// noblesse blessing is shared with both
		npc.setTarget(player);
		npc.doCast(SkillData.getInstance().getSkill(NOBLESSE_BLESSING, 1));

		switch (type) {
		case "mage":
			for (int[] skill : MAGE_BUFFS) {
				SkillData.getInstance().getSkill(skill[0], skill[1])
						.applyEffects(npc, player);
			}
			break;
		case "warrior":
			for (int[] skill : WARRIOR_BUFFS) {
				SkillData.getInstance().getSkill(skill[0], skill[1])
						.applyEffects(npc, player);
			}
			break;
		}

	}

	public static void main(String[] args) {
		new ExiledDivine();
	}
}
