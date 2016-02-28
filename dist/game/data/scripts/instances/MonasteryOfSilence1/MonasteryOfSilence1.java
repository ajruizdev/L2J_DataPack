/*
 * Copyright (C) 2004-2015 L2J DataPack
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
package instances.MonasteryOfSilence1;

import quests.Q10294_SevenSignsToTheMonasteryOfSilence.Q10294_SevenSignsToTheMonasteryOfSilence;
import quests.Q10295_SevenSignsSolinasTomb.Q10295_SevenSignsSolinasTomb;
import quests.Q10296_SevenSignsPowerOfTheSeal.Q10296_SevenSignsPowerOfTheSeal;
import instances.AbstractInstance;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Monastery of Silence instance zone.
 * @author Adry_85, Antonio
 */
public final class MonasteryOfSilence1 extends AbstractInstance
{
	protected static final class MoSWorld extends InstanceWorld
	{
		protected L2Npc elcadia = null;
	}
	
	// NPCs
	private static final int ELCADIA_INSTANCE = 32787;
	private static final int ERIS_EVIL_THOUGHTS = 32792;
	private static final int RELIC_GUARDIAN = 32803;
	private static final int RELIC_WATCHER1 = 32804;
	private static final int RELIC_WATCHER2 = 32805;
	private static final int RELIC_WATCHER3 = 32806;
	private static final int RELIC_WATCHER4 = 32807;
	private static final int ODD_GLOBE = 32815;
	private static final int TELEPORT_CONTROL_DEVICE1 = 32817;
	private static final int TELEPORT_CONTROL_DEVICE2 = 32818;
	private static final int TELEPORT_CONTROL_DEVICE3 = 32819;
	private static final int TELEPORT_CONTROL_DEVICE4 = 32820;
	// Solina's tomb
	private static final int SOLINASEVLITHOUGHTS = 32793;
	private static final int TELEPORTCONTROLDEVICE1 = 32837;
	private static final int TELEPORTCONTROLDEVICE2 = 32842;
	private static final int TOMBOFTHESAINTNESS = 32843;
	private static final int ALTAROFHALLOWS_STAFF = 32857;
	private static final int ALTAROFHALLOWS_SWORD = 32858;
	private static final int ALTAROFHALLOWS_SCROLL = 32859;
	private static final int ALTAROFHALLOWS_SHIELD = 32860;
	// One Who Seek The Power Of The Seals
	private static final int ETIS_VAN_ETINA = 18949;
	// Skills
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(6725, 1), // Bless the Blood of Elcadia
		new SkillHolder(6728, 1), // Recharge of Elcadia
		new SkillHolder(6730, 1), // Greater Battle Heal of Elcadia
	};
	// Locations
	private static final Location START_LOC = new Location(120710, -86971, -3392);
	private static final Location EXIT_LOC = new Location(115983, -87351, -3397, 0, 0);
	private static final Location CENTRAL_ROOM_LOC = new Location(85794, -249788, -8320);
	private static final Location SOUTH_WATCHERS_ROOM_LOC = new Location(85798, -246566, -8320);
	private static final Location WEST_WATCHERS_ROOM_LOC = new Location(82531, -249405, -8320);
	private static final Location EAST_WATCHERS_ROOM_LOC = new Location(88665, -249784, -8320);
	private static final Location NORTH_WATCHERS_ROOM_LOC = new Location(85792, -252336, -8320);
	private static final Location BACK_LOC = new Location(120710, -86971, -3392);
	// Solina's tomb
	private static final Location START_LOC_SOLINAS_TOMB = new Location(45545, -249423, -6760);
	private static final Location RETURN_TO_ERIS = new Location(120727, -86868, -3392);
	private static final Location RETURN_TO_GUARDIAN = new Location(85937, -249618, -8320);
	private static final Location SAINTNESS = new Location(56033, -252944, -6760);
	private static final Location SAINTNESS_2 = new Location(55955, -250394, -6760);
	// One Who Seek The Power Of The Seals
	private static final Location ETIS_ETINA = new Location(76707, -241022, -10832);
	// NpcString
	private static final NpcStringId[] ELCADIA_DIALOGS_1 =
	{
		NpcStringId.IT_SEEMS_THAT_YOU_CANNOT_REMEMBER_TO_THE_ROOM_OF_THE_WATCHER_WHO_FOUND_THE_BOOK,
		NpcStringId.WE_MUST_SEARCH_HIGH_AND_LOW_IN_EVERY_ROOM_FOR_THE_READING_DESK_THAT_CONTAINS_THE_BOOK_WE_SEEK,
		NpcStringId.REMEMBER_THE_CONTENT_OF_THE_BOOKS_THAT_YOU_FOUND_YOU_CANT_TAKE_THEM_OUT_WITH_YOU,
	};
	private static final NpcStringId[] ELCADIA_DIALOGS_2 =
	{
		// Solina's Tomb
		NpcStringId.TO_REMOVE_THE_BARRIER_YOU_MUST_FIND_THE_RELICS_THAT_FIT_THE_BARRIER_AND_ACTIVATE_THE_DEVICE,
		NpcStringId.THE_GUARDIAN_OF_THE_SEAL_DOESNT_SEEM_TO_GET_INJURED_AT_ALL_UNTIL_THE_BARRIER_IS_DESTROYED,
		NpcStringId.THE_DEVICE_LOCATED_IN_THE_ROOM_IN_FRONT_OF_THE_GUARDIAN_OF_THE_SEAL_IS_DEFINITELY_THE_BARRIER_THAT_CONTROLS_THE_GUARDIANS_POWER
	};
	// Misc
	protected static final int TEMPLATE_ID = 151;
	
	public MonasteryOfSilence1()
	{
		super(MonasteryOfSilence1.class.getSimpleName());
		addFirstTalkId(TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS);
		addStartNpc(ODD_GLOBE, TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS);
		addTalkId(ODD_GLOBE, ERIS_EVIL_THOUGHTS, RELIC_GUARDIAN, RELIC_WATCHER1, RELIC_WATCHER2, RELIC_WATCHER3, RELIC_WATCHER4, TELEPORT_CONTROL_DEVICE1, TELEPORT_CONTROL_DEVICE2, TELEPORT_CONTROL_DEVICE3, TELEPORT_CONTROL_DEVICE4, ERIS_EVIL_THOUGHTS,
				// solina's tomb
				SOLINASEVLITHOUGHTS,
				TELEPORTCONTROLDEVICE1,
				TELEPORTCONTROLDEVICE2,
				TOMBOFTHESAINTNESS,
				ALTAROFHALLOWS_STAFF,
				ALTAROFHALLOWS_SWORD,
				ALTAROFHALLOWS_SCROLL,
				ALTAROFHALLOWS_SHIELD
				);
		addKillId(ETIS_VAN_ETINA);
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, START_LOC, world.getInstanceId(), false);
		spawnElcadia(player, (MoSWorld) world);
		
		final QuestState qs_solinas = player.getQuestState(Q10295_SevenSignsSolinasTomb.class.getSimpleName());
		if (qs_solinas != null && !qs_solinas.isCompleted()) 
		{
			if (qs_solinas.getInt("progress") >= 4)
			{
				openDoor(21100001, player.getInstanceId());
				openDoor(21100010, player.getInstanceId());
				openDoor(21100014, player.getInstanceId());
				openDoor(21100006, player.getInstanceId());
			}
			if (qs_solinas.getInt("tomb") >= 16) 
			{
				qs_solinas.startQuestTimer("open_door", 1000);
			}
			else 
			{
				qs_solinas.set("tomb", 0);
				qs_solinas.set("tomb_opened", 0);
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(Q10294_SevenSignsToTheMonasteryOfSilence.class.getSimpleName());
		final QuestState qs1 = player.getQuestState(Q10295_SevenSignsSolinasTomb.class.getSimpleName());
		
		final InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (!(tmpworld instanceof MoSWorld))
		{
			return null;
		}
		final MoSWorld world = (MoSWorld) tmpworld;
		
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
		switch (event)
		{
			case "TELE2":
			{
				teleportPlayer(player, CENTRAL_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(CENTRAL_ROOM_LOC, world.getInstanceId(), 0);
				startQuestTimer("START_MOVIE", 2000, npc, player);
				break;
			}
			case "EXIT":
			{
				cancelQuestTimer("FOLLOW", npc, player);
				teleportPlayer(player, EXIT_LOC, 0);
				world.elcadia.deleteMe();
				break;
			}
			case "START_MOVIE":
			{
				player.showQuestMovie(24);
				break;
			}
			case "BACK":
			{
				teleportPlayer(player, BACK_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(BACK_LOC, world.getInstanceId(), 0);
				break;
			}
			case "EAST":
			{
				teleportPlayer(player, EAST_WATCHERS_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(EAST_WATCHERS_ROOM_LOC, world.getInstanceId(), 0);
				break;
			}
			case "WEST":
			{
				teleportPlayer(player, WEST_WATCHERS_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(WEST_WATCHERS_ROOM_LOC, world.getInstanceId(), 0);
				break;
			}
			case "NORTH":
			{
				teleportPlayer(player, NORTH_WATCHERS_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(NORTH_WATCHERS_ROOM_LOC, world.getInstanceId(), 0);
				break;
			}
			case "SOUTH":
			{
				teleportPlayer(player, SOUTH_WATCHERS_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(SOUTH_WATCHERS_ROOM_LOC, world.getInstanceId(), 0);
				break;
			}
			case "CENTER":
			{
				teleportPlayer(player, CENTRAL_ROOM_LOC, world.getInstanceId());
				world.elcadia.teleToLocation(CENTRAL_ROOM_LOC, world.getInstanceId(), 0);
				break;
			}
			// solina's tomb
			case "Enter1":
			{
				if (qs1.getInt("seal_removed") != 1)
				{
					qs1.startQuestTimer("StartMovie", 1000);
					teleportPlayer(player, START_LOC_SOLINAS_TOMB, player.getInstanceId());
					world.elcadia.teleToLocation(START_LOC_SOLINAS_TOMB, world.getInstanceId(), 0);
				}
				else
				{
					teleportPlayer(player, SAINTNESS, player.getInstanceId(), false);
					world.elcadia.teleToLocation(SAINTNESS, world.getInstanceId(), 0);
				}
				break;
			}
			case "ReturnToEris":
			{
				teleportPlayer(player, RETURN_TO_ERIS, player.getInstanceId());
				world.elcadia.teleToLocation(RETURN_TO_ERIS, world.getInstanceId(), 0);
				break;
			}
			case "ReturnToGuardian":
			{
				teleportPlayer(player, RETURN_TO_GUARDIAN, player.getInstanceId());
				world.elcadia.teleToLocation(RETURN_TO_GUARDIAN, world.getInstanceId(), 0);
				break;
			}
			case "MoveToSaintess":
			{
				teleportPlayer(player, SAINTNESS, player.getInstanceId());
				world.elcadia.teleToLocation(SAINTNESS, world.getInstanceId(), 0);
				break;
			}
			case "MoveToSaintess2":
			{
				teleportPlayer(player, SAINTNESS_2, player.getInstanceId());
				world.elcadia.teleToLocation(SAINTNESS_2, world.getInstanceId(), 0);
				break;
			}
			// One Who Seek The Power Of The Seals
			case "video":
			{
				cancelQuestTimer("FOLLOW", world.elcadia, player);
				for (L2Npc inpc : inst.getNpcs())
				{
					if (inpc.getObjectId() == npc.getObjectId() || inpc.getObjectId() == world.elcadia.getObjectId())
					{
						inpc.deleteMe();
					}
				}
				player.showQuestMovie(29);
				startQuestTimer("teleport", 60000, npc, player);
				break;
			}
			case "teleport":
			{
				teleportPlayer(player, ETIS_ETINA, player.getInstanceId());
				spawnElcadia(player, world);
				break;
			}
			case "DESPAWN_ELCADIA":
			{
				cancelQuestTimer("FOLLOW", world.elcadia, player);
				world.elcadia.deleteMe();
				break;
			}
			case "SPAWN_ELCADIA":
			{
				spawnElcadia(player, world);
				break;
			}
			case "FOLLOW":
			{
				if (npc.getInstanceId() != player.getInstanceId())
				{
					npc.deleteMe();
					return null;
				}
				
				if ((npc.getInstanceId() == player.getInstanceId()) && !npc.isInsideRadius(player.getLocation(), 600, false, false)) 
				{
					npc.teleToLocation(player.getLocation());
				}
				
				npc.setIsRunning(true);
				npc.getAI().startFollow(player);
				if (qs != null && qs.getCond() == 2) 
				{
					if (getRandom(2) < 1)
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, ELCADIA_DIALOGS_1[getRandom(ELCADIA_DIALOGS_1.length)]);
					}
				}
				else if (qs != null && qs.getCond() == 3)
				{
					if (player.isInCombat())
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.YOUR_WORK_HERE_IS_DONE_SO_RETURN_TO_THE_CENTRAL_GUARDIAN);
					}
				}
				else if (qs1 != null && qs1.getCond() == 1)
				{
					if (getRandom(2) < 1)
					{
						broadcastNpcSay(npc, Say2.NPC_ALL, ELCADIA_DIALOGS_2[getRandom(ELCADIA_DIALOGS_2.length)]);
					}
				}
				
				if (player.isInCombat())
				{
					npc.setTarget(player);
					npc.doCast(BUFFS[getRandom(BUFFS.length)].getSkill());
				}
				startQuestTimer("FOLLOW", 10000, npc, player);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		if (npc.getId() == ODD_GLOBE)
		{
			InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(talker);
			if (world == null || world.getTemplateId() != TEMPLATE_ID) 
			{
				world = new MoSWorld();
			}
			enterInstance(talker, world, "MonasteryOfSilence.xml", TEMPLATE_ID);
		}
		return super.onTalk(npc, talker);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState qs = player.getQuestState(Q10296_SevenSignsPowerOfTheSeal.class.getSimpleName());
		if (qs == null)
		{
			return null;
		}
		
		if (qs.getInt("boss") != 1)
		{
			qs.set("boss", "1");
		}
		
		startQuestTimer("DESPAWN_ELCADIA", 0, npc, player);
		startQuestTimer("SPAWN_ELCADIA", 60000, npc, player);
		
		// start movie
		player.showQuestMovie(30);
		
		return super.onKill(npc, player, isPet);
	}
	
	protected void spawnElcadia(L2PcInstance player, MoSWorld world)
	{
		if (world.elcadia != null)
		{
			world.elcadia.deleteMe();
		}
		world.elcadia = addSpawn(ELCADIA_INSTANCE, player.getX(), player.getY(), player.getZ(), 0, false, 0, false, player.getInstanceId());
		startQuestTimer("FOLLOW", 3000, world.elcadia, player);
	}
}
