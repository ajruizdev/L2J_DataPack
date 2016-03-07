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
package handlers.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import com.l2jserver.commons.database.pool.impl.ConnectionFactory;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class AdminVip implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_set_vip",
		"admin_set_vip_offline",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		L2Object targetChar = activeChar.getTarget();
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("admin_set_vip"))
		{
			if (targetChar == null)
			{
				activeChar.sendMessage("You must target the player before using this command.");
				return false;
			}
			
			if (st.countTokens() != 2)
			{
				activeChar.sendMessage("Usage: //set_vip <vip_level> <num_days>");
				return false;
			}
						
			try
			{
				int vipLvl = Math.max(0, Byte.parseByte(st.nextToken()));
				long expiryDate = System.currentTimeMillis() + (Math.max(0, Byte.parseByte(st.nextToken())) * 86400000L);
				
				if ((targetChar instanceof L2PcInstance) && !((L2PcInstance) targetChar).isInOfflineMode())
				{
					((L2PcInstance) targetChar).setVip(vipLvl, expiryDate);
					
					((L2PcInstance) targetChar).sendMessage("[Divine Exiliado] Alianza Lvl: " + vipLvl + " hasta el " + getDate(expiryDate) + ".");
					activeChar.sendMessage("[" + targetChar.getName() + "] VIP set lvl " + vipLvl + ". Expiry date: " + getDate(expiryDate) + ".");
				}
				else
				{
					activeChar.sendMessage("Wrong target.");
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Wrong Number Format: values allowed from -128 to 127.");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_vip_offline"))
		{
			if (st.countTokens() != 3)
			{
				activeChar.sendMessage("Usage: //set_vip_offline <player_name> <vip_level> <num_days>");
				return false;
			}
			
			String charName = st.nextToken();
			if (!playerExists(charName))
			{
				activeChar.sendMessage("Player " + charName + " does not exist!");
				return false;
			}
			
			try
			{
				int vipLvl = Math.max(0, Byte.parseByte(st.nextToken()));
				long expiryDate = System.currentTimeMillis() + (Math.max(0, Byte.parseByte(st.nextToken())) * 86400000L);
				
				try (Connection con = ConnectionFactory.getInstance().getConnection()) 
				{
					try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET vip_level=?, vip_expiry_time=? WHERE char_name=?"))
					{
						ps.setInt(1, vipLvl);
						ps.setLong(2, expiryDate);
						ps.setString(3, charName);
						ps.execute();
						
						activeChar.sendMessage("[" + charName + "] VIP set lvl " + vipLvl + ". Expiry date: " + getDate(expiryDate) + ".");
					}
				} 
				catch (Exception e) 
				{
					activeChar.sendMessage("Could not set vip player " + charName + ".");
				}
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Wrong Number Format: values allowed from -128 to 127.");
			}
		}
		return true;
	}
	
	private boolean playerExists(String charName)
	{
		int numChars = 0;
		
		try (Connection con = ConnectionFactory.getInstance().getConnection()) 
		{
			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE char_name=?"))
			{
				ps.setString(1, charName);
				ResultSet result = ps.executeQuery();
				while (result.next())
				{
					numChars++;
				}
			}
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return numChars != 0 ? true : false;
	}
	
	private String getDate(long time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		return dateFormat.format(c.getTime());
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
