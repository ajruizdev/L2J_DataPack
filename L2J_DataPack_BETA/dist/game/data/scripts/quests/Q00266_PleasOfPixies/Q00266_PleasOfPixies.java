/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00266_PleasOfPixies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.enums.PcRace;
import com.l2jserver.gameserver.enums.QuestSound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Pleas of Pixies (266)
 * @author xban1x
 */
public final class Q00266_PleasOfPixies extends Quest
{
	// NPC
	private static final int PIXY_MURIKA = 31852;
	// Items
	private static final int PREDATORS_FANG = 1334;
	// Monsters
	//@formatter:off
	private static final Map<Integer,List<ItemHolder>> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(20537, Arrays.asList(new ItemHolder(10, 2))); // Elder Red Keltir
		MONSTERS.put(20525, Arrays.asList(new ItemHolder(5, 2), new ItemHolder(10, 3))); // Gray Wolf
		MONSTERS.put(20534, Arrays.asList(new ItemHolder(6, 1))); // Red Keltir
		MONSTERS.put(20530, Arrays.asList(new ItemHolder(8, 1))); // Young Red Keltir
	}
	// Rewards
	private static final Map<Integer,List<ItemHolder>> REWARDS = new HashMap<>();
	static
	{
		REWARDS.put(0, Arrays.asList(new ItemHolder(1337, 1), new ItemHolder(3032, 1))); // Emerald, Recipe: Spiritshot D
		REWARDS.put(1, Arrays.asList(new ItemHolder(2176, 1), new ItemHolder(1338, 1))); // Recipe: Leather Boots, Blue Onyx
		REWARDS.put(2, Arrays.asList(new ItemHolder(1339, 1), new ItemHolder(1061, 1))); // Onyx, Greater Healing Potion
		REWARDS.put(3, Arrays.asList(new ItemHolder(1336, 1), new ItemHolder(1060, 1))); // Glass Shard, Lesser Healing Potion
	}
	//@formatter:on
	// Misc
	private static final int MIN_LVL = 3;
	
	private Q00266_PleasOfPixies(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(PIXY_MURIKA);
		addTalkId(PIXY_MURIKA);
		addKillId(MONSTERS.keySet());
		registerQuestItems(PREDATORS_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equalsIgnoreCase("31852-04.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			final int chance = getRandom(10);
			for (ItemHolder mob : MONSTERS.get(npc.getId()))
			{
				if (chance < mob.getId())
				{
					st.giveItems(PREDATORS_FANG, mob.getCount());
					if (st.getQuestItemsCount(PREDATORS_FANG) >= 100)
					{
						st.setCond(2, true);
					}
					else
					{
						st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = getNoQuestMsg(player);
		if (st != null)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = (player.getRace() == PcRace.Elf) ? (player.getLevel() >= MIN_LVL) ? "31852-03.htm" : "31852-02.htm" : "31852-01.htm";
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "31852-05.html";
							break;
						}
						case 2:
						{
							if (st.getQuestItemsCount(PREDATORS_FANG) >= 100)
							{
								final int chance = getRandom(100);
								int reward;
								if (chance < 2)
								{
									reward = 0;
									st.playSound(QuestSound.ITEMSOUND_QUEST_JACKPOT);
								}
								else if (chance < 20)
								{
									reward = 1;
								}
								else if (chance < 45)
								{
									reward = 2;
								}
								else
								{
									reward = 3;
								}
								for (ItemHolder item : REWARDS.get(reward))
								{
									st.rewardItems(item.getId(), item.getCount());
								}
								st.exitQuest(true, true);
								htmltext = "31852-06.html";
							}
							break;
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Q00266_PleasOfPixies(266, Q00266_PleasOfPixies.class.getSimpleName(), "Pleas of Pixies");
	}
}
