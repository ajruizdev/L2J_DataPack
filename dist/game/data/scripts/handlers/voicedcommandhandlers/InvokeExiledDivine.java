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
package handlers.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * @author Antonio
 *
 */
public class InvokeExiledDivine implements IVoicedCommandHandler {

	private static final String[] _voicedCommands = { "divine" };

	private static final int EXILED_DIVINE_ID = 1151602;
	
	private static final long RESSURECTION_DELAY = 1200000; // 20 min (ms)

	private static final Map<Integer, ScheduledFuture<?>> PLAYERS_SUMMON = new HashMap<Integer, ScheduledFuture<?>>();
	private static final Map<Integer, Long> NEXT_RESS = new HashMap<Integer, Long>();

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar,
			String params) {

		if (!activeChar.isVip()) {
			activeChar
					.sendMessage("[Divine Exiliado] Alianza Lvl: 0. Puedes aumentar tu nivel de alianza realizando una donacion igual o superior a 5 euros. Visita www.l2abyss.net para mas informacion.");
			return false;
		}

		if (activeChar.isInOlympiadMode()) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado mientras estas en olimpiadas.");
			return false;
		}

		if (activeChar.getPvpFlag() != 0) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado mientras estas en PvP.");
			return false;
		}

		if (activeChar.isOnEvent()) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado mientras estas en TvT.");
			return false;
		}

		if (activeChar.isInCombat()) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado mientras estas en combate.");
			return false;
		}

		if (activeChar.isInDuel()) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado mientras estas en un duelo.");
			return false;
		}

		if (activeChar.getKarma() > 0) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado siendo PK.");
			return false;
		}

		if (activeChar.isInsideZone(ZoneId.SIEGE)) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado en zona de asedio.");
			return false;
		}

		if (activeChar.isInsideZone(ZoneId.PVP)) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado en zona de PvP.");
			return false;
		}

		if (activeChar.isInsideZone(ZoneId.PEACE)) {
			activeChar
					.sendMessage("No puedes invocar al Divine Exiliado en zona de paz.");
			return false;
		}

		if (PLAYERS_SUMMON.get(activeChar.getObjectId()) != null) 
		{
			if (!PLAYERS_SUMMON.get(activeChar.getObjectId()).isDone()) 
			{
				activeChar
						.sendMessage("El Divine Exiliado ya ha sido invocado.");
				return false;
			}
		}

		if (activeChar.isDead()) 
		{
			if (activeChar.getVipLevel() < 2)
			{
				activeChar.sendMessage("Necesitas alianza lvl 2 para que te resucite!");
				return false;
			}
			else if (!canRessurect(activeChar))
			{
				activeChar.sendMessage("El Divine Exiliado puede resucitarte una vez cada 20 minutos!");
				return false;
			}
		}
		
		activeChar.sendMessage("[Divine Exiliado] Alianza Lvl: " + activeChar.getVipLevel() + " hasta el " + getDate(activeChar.getVipExpiryTime()) + ".");

		L2NpcTemplate template;
		template = NpcData.getInstance().getTemplate(EXILED_DIVINE_ID);

		// create custom spawn for this npc
		try {
			L2Spawn spawn;
			spawn = new L2Spawn(template);

			spawn.setLocation(activeChar.getLocation());
			spawn.setAmount(1);
			spawn.setCustom(true);
			spawn.setRespawnDelay(0);

			if (activeChar.getInstanceId() > 0) {
				spawn.setInstanceId(activeChar.getInstanceId());
			} else {
				spawn.setInstanceId(0);
			}

			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();

			final L2Npc npc = spawn.getLastSpawn();
			npc.setTarget(activeChar);
			npc.setIsRunning(true);
			npc.getAI().startFollow(activeChar);
			
			Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), Say2.ALL, npc.getTemplate().getDisplayId(),
					"Para servirte, " + activeChar.getName() + "."));

			int summonTime = 30000; // 30 seconds
			
			if (activeChar.getVipLevel() == 2)
			{
				summonTime = 120000; // 2 min
				if (activeChar.isDead())
				{
					Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), Say2.ALL, npc.getTemplate().getDisplayId(),
							"Deja que te ayude, " + activeChar.getName() + "!"));
					
					activeChar.reviveRequest(activeChar, SkillData.getInstance().getSkill(2049, 1), false, 100);
					NEXT_RESS.put(activeChar.getObjectId(), System.currentTimeMillis() + RESSURECTION_DELAY);
				}
			}
			
			PLAYERS_SUMMON.put(activeChar.getObjectId(),
					ThreadPoolManager.getInstance().scheduleGeneral(
							new DeleteSpawn(npc), summonTime));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private final class DeleteSpawn implements Runnable {

		private final L2Npc _npc;

		public DeleteSpawn(L2Npc npc) {
			_npc = npc;
		}

		@Override
		public void run() {
			Broadcast.toKnownPlayers(_npc, new NpcSay(_npc.getObjectId(),
					Say2.ALL, _npc.getTemplate().getDisplayId(),
					"He de irme, llamame cuando me necesites!"));
			_npc.deleteMe();
			SpawnTable.getInstance().deleteSpawn(_npc.getSpawn(), false);
		}
	}
	
	private static boolean canRessurect(L2PcInstance player)
	{
		return NEXT_RESS.get(player.getObjectId()) == null || NEXT_RESS.get(player.getObjectId()) <= System.currentTimeMillis();
	}
	
	private String getDate(long time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		return dateFormat.format(c.getTime());
	}

	@Override
	public String[] getVoicedCommandList() {

		return _voicedCommands;
	}

}
