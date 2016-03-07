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

import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Exiled Divine - L2Abyss feature
 * 
 * @author Antonio
 *
 */
public class ExiledDivine extends AbstractNpcAI {

	private static final Logger LOG = LoggerFactory.getLogger(ExiledDivine.class);

	/**
	 * Config
	 */
	private static final int NPC_ID = 1151602;

	private static final int ABYSSAL_COIN_ID = 1000002;
	private static final int BUFFS_PRICE = 2;

	private static final int MAX_LVL_FREE = 40;
	// in seconds
	private static final int BUFF_DURATION = 3600;
	private static final int DANCE_DURATION = 3600;
	/** - */
	
	private static final Map<Integer, Boolean> PLAYER_PAID = new HashMap<Integer, Boolean>();
	
	private static final Location MERCHANT_OF_ABYSS_LOC = new Location(74890, 118064, -3664, 65281);

	private static final SkillHolder NOBLESSE_BLESSING = new SkillHolder(1323, 1); // max lvl 1
	private static final SkillHolder CHANT_OF_VICTORY = new SkillHolder(1363, 1); // max lvl 1

	private static final SkillHolder[] MAGE_BUFFS = 
	{
		// skill id - lvl
		new SkillHolder(1040, 3), // shield
		new SkillHolder(1035, 4), // mental shield
		new SkillHolder(1062, 2), // berserker
		new SkillHolder(1085, 3), // acumen
		new SkillHolder(1078, 6), // concentration
		new SkillHolder(1389, 3), // greater shield
		new SkillHolder(1043, 1), // holy weapon
		new SkillHolder(1303, 2), // wild magic
		new SkillHolder(1354, 1), // arcane protection
		new SkillHolder(1257, 3), // decrease weight
		new SkillHolder(1259, 4), // resist shock
		new SkillHolder(1397, 3), // clarity
		// improved buffs
		new SkillHolder(1500, 1), // Improved Magic
		new SkillHolder(1501, 1), // Improved Condition
		new SkillHolder(1504, 1), // Improved Movement
		// dances
		new SkillHolder(273, 1), // dance of mystic
		new SkillHolder(276, 1), // dance of concentration
		new SkillHolder(307, 1), // Dance of Aqua Guard
		new SkillHolder(309, 1), // Dance of Earth Guard
		new SkillHolder(365, 1), // Dance of Siren
		// songs
		new SkillHolder(264, 1), // song of earth
		new SkillHolder(267, 1), // song of warding
		new SkillHolder(268, 1), // Song of Wind
		new SkillHolder(265, 1), // Song of Life
		new SkillHolder(304, 1), // Song of Vitality
		new SkillHolder(363, 1), // Song of Meditation
	};

	private static final SkillHolder[] WARRIOR_BUFFS = 
	{
		// skill id - lvl
		new SkillHolder(1388, 3), // greater might
		new SkillHolder(1240, 3), // guidance
		new SkillHolder(1036, 2), // magic barrier
		new SkillHolder(1389, 3), // greater shield
		new SkillHolder(1086, 2), // haste
		new SkillHolder(1044, 3), // regeneration
		new SkillHolder(1354, 1), // arcane protection
		new SkillHolder(1257, 3), // decrease weight
		new SkillHolder(1259, 4), // resist shock
		new SkillHolder(1268, 4), // vampiric rage
		// improved buffs
		new SkillHolder(1499, 1), // Improved Combat
		new SkillHolder(1501, 1), // Improved Condition
		new SkillHolder(1502, 1), // Improved Critical
		new SkillHolder(1503, 1), // Improved Shield Defense
		new SkillHolder(1504, 1), // Improved Movement
		// dances
		new SkillHolder(271, 1), // dance of warrior
		new SkillHolder(275, 1), // dance of fury
		new SkillHolder(310, 1), // Dance of the Vampire
		new SkillHolder(272, 1), // Dance of Inspiration
		new SkillHolder(274, 1), // Dance of Fire
		new SkillHolder(915, 1), // Dance of Berserker
			// songs
		new SkillHolder(264, 1), // song of earth
		new SkillHolder(267, 1), // song of warding
		new SkillHolder(268, 1), // Song of Wind
		new SkillHolder(265, 1), // Song of Life
		new SkillHolder(304, 1), // Song of Vitality
		new SkillHolder(349, 1), // Song of Renewal
	};

	public ExiledDivine() 
	{
		super(ExiledDivine.class.getSimpleName(), "custom");

		addStartNpc(NPC_ID);
		addFirstTalkId(NPC_ID);
		addTalkId(NPC_ID);

		LOG.info("Loaded Exiled Divine");
	}

	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) 
	{
		// set value false for current player if it is not listed
		if (!PLAYER_PAID.containsKey(player.getObjectId())) 
		{
			PLAYER_PAID.put(player.getObjectId(), false);
		}

		// check if npc is casting on other player in order to
		// prevent a target change
		if (npc.isCastingNow()) 
		{
			broadcastNpcSay(npc, Say2.NPC_ALL, "Lo siento " + player.getName() + ", ahora mismo estoy ocupado!");
			return "";
		}

		String html = "novip.htm";
		
		if (player.getVipLevel() == 2)
		{
			html = "vip2.htm";
		}
		else if (player.getVipLevel() == 1 || (player.getLevel() < MAX_LVL_FREE) || PLAYER_PAID.get(player.getObjectId()))
		{
			html = "vip1.htm";
		}

		return html;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) 
	{
		if (!npc.isInsideRadius(player, 200, false, true)) 
		{
			return "";
		}

		switch (event) 
		{
			case "pay":
			{
				if (!hasCoins(player))
				{
					return "nocoin.htm";
				}
				player.getInventory().destroyItemByItemId(ExiledDivine.class.getSimpleName(), ABYSSAL_COIN_ID, BUFFS_PRICE, player, npc);
				player.sendMessage("Has entregado " + BUFFS_PRICE + " Fragmentos Abisales al Divine Exiliado.");
				PLAYER_PAID.replace(player.getObjectId(), true);
				
				return "vip1.htm";
			}
			case "teleport":
			{
				player.teleToLocation(MERCHANT_OF_ABYSS_LOC);
				return "";
			}
			case "info":
			{
				return "info.htm";
			}
			// vip lvl 2 features
			case "shop":
			{	
				if (player.getVipLevel() < 2)
				{
					return "";
				}
				((L2MerchantInstance) npc).showBuyWindow(player, 11516021);
				return "";
			}
			// vip lvl 1, 2 or by payment
			case "buff_mage":
			{
				if (!player.isVip() && !hasPaid(player) && !(player.getLevel() < MAX_LVL_FREE))
				{
					return "";
				}
				castBuffs(MAGE_BUFFS, npc, player);
				break;
			}
			case "buff_warrior":
			{
				if (!player.isVip() && !hasPaid(player) && !(player.getLevel() < MAX_LVL_FREE))
				{
					return "";
				}
				castBuffs(WARRIOR_BUFFS, npc, player);
				break;
			}
			case "buff_mage_pet":
			{
				if (player.getVipLevel() < 2)
				{
					return "";
				}
				if (player.getSummon() != null)
				{
					castBuffs(MAGE_BUFFS, npc, player.getSummon());
				}
				break;
			}
			case "buff_warrior_pet":
			{
				if (player.getVipLevel() < 2)
				{
					return "";
				}
				if (player.getSummon() != null)
				{
					castBuffs(WARRIOR_BUFFS, npc, player.getSummon());
				}
				break;
			}
		}
		PLAYER_PAID.replace(player.getObjectId(), false);
		
		return super.onAdvEvent(event, npc, player);
	}

	private void castBuffs(SkillHolder[] buffType, L2Npc npc, L2Playable effected) 
	{
		final L2PcInstance player = (L2PcInstance)(effected.isPlayer() ? effected : ((L2Summon)effected).getOwner());
		
		if (effected.isPlayer())
		{
			// noblesse blessing is shared by both mages and warriors
			npc.setTarget(effected);
			npc.doSimultaneousCast(NOBLESSE_BLESSING.getSkill());
		}

		for (SkillHolder sh : buffType) 
		{
			if (sh.getSkill().isDance() && effected.isSummon())
			{
				continue; // summons are not affected by dances/songs...
			}
			
			if (sh.getSkill().isDance()) 
			{
				sh.getSkill().applyEffects(npc, effected, true, DANCE_DURATION);
			} 
			else 
			{
				sh.getSkill().applyEffects(npc, effected, true, BUFF_DURATION);
			}
		}
		
		// player vip lvl 2
		if (player.getVipLevel() == 2)
		{
			CHANT_OF_VICTORY.getSkill().applyEffects(npc, effected, true, BUFF_DURATION);
		}
	}
	
	private boolean hasCoins(L2PcInstance player)
	{
		return (player.getInventory().getInventoryItemCount(ABYSSAL_COIN_ID, -1) >= BUFFS_PRICE);
	}
	
	private boolean hasPaid(L2PcInstance player)
	{
		return PLAYER_PAID.get(player.getObjectId()) != null && PLAYER_PAID.get(player.getObjectId());
	}

	public static void main(String[] args) 
	{
		new ExiledDivine();
	}
}
