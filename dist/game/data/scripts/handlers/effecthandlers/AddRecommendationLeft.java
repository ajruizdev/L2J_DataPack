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
package handlers.effecthandlers;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExVoteSystemInfo;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * @author Antonio
 *
 */
public class AddRecommendationLeft extends AbstractEffect {

	private final int _amount;

	/**
	 * @param attachCond
	 * @param applyCond
	 * @param set
	 * @param params
	 */
	public AddRecommendationLeft(Condition attachCond, Condition applyCond,
			StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);

		_amount = params.getInt("amount", 0);
		if (_amount == 0) {
			_log.warning(getClass().getSimpleName()
					+ ": amount parameter is missing or set to 0. id:"
					+ set.getInt("id", -1));
		}
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public void onStart(BuffInfo info) {
		L2PcInstance target = info.getEffected() instanceof L2PcInstance ? (L2PcInstance) info
				.getEffected() : null;
		if (target != null) {
			int addRecommendationsLeft = _amount;

			if (addRecommendationsLeft > 0) {
				target.setRecomLeft(target.getRecomLeft()
						+ addRecommendationsLeft);

				SystemMessage sm = SystemMessage
						.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_RECOMMENDATIONS);
				sm.addInt(addRecommendationsLeft);
				target.sendPacket(sm);
				target.sendPacket(new UserInfo(target));
				target.sendPacket(new ExVoteSystemInfo(target));
			} else {
				L2PcInstance player = info.getEffector() instanceof L2PcInstance ? (L2PcInstance) info
						.getEffector() : null;
				if (player != null) {
					player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
				}
			}
		}
	}

}
