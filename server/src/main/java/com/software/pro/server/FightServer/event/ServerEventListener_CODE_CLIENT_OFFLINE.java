package com.software.pro.server.FightServer.event;


import com.software.pro.server.FightServer.ServerContains;

import com.software_pro.common.channel.ChannelUtils;
import com.software_pro.common.entity.ClientSide;
import com.software_pro.common.entity.Room;
import com.software_pro.common.enums.ClientEventCode;
import com.software_pro.common.enums.ClientRole;
import com.software_pro.common.helper.MapHelper;

public class ServerEventListener_CODE_CLIENT_OFFLINE implements ServerEventListener{

	@Override
	public void call(ClientSide clientSide, String data) {
		
		Room room = ServerContains.getRoom(clientSide.getRoomId());

		if(room != null) {
			String result = MapHelper.newInstance()
								.put("roomId", room.getId())
								.put("exitClientId", clientSide.getId())
								.put("exitClientOwner_name", clientSide.getOwner_name())
								.json();
			for(ClientSide client: room.getClientSideList()) {
				if(client.getRole() == ClientRole.PLAYER){
					if(client.getId() != clientSide.getId()){
						ChannelUtils.pushToClient(client.getChannel(), ClientEventCode.CODE_CLIENT_EXIT, result);
						client.init();
					}
				}
			}
			ServerContains.removeRoom(room.getId());
		}
		ServerContains.CLIENT_SIDE_MAP.remove(clientSide.getId());
	}

}
