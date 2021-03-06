package com.software.pro.server.FightServer.event;

import com.software.pro.server.FightServer.ServerContains;
import com.software_pro.common.channel.ChannelUtils;
import com.software_pro.common.entity.ClientSide;
import com.software_pro.common.entity.Room;
import com.software_pro.common.entity.WebData;
import com.software_pro.common.enums.ClientEventCode;
import com.software_pro.common.enums.RoomStatus;
import com.software_pro.common.enums.RoomType;
import org.nico.noson.Noson;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerEventListener_CODE_ROOM_CREATE implements ServerEventListener{

	@Override
	public void call(ClientSide clientSide, String data) {

		//创建一个新的房间
		Room room = new Room(ServerContains.getServerId());
		room.setStatus(RoomStatus.BLANK);                                //空闲
		room.setType(RoomType.PVP);
		room.setRoomOwner(clientSide.getOwner_name());                   //房主
		room.getClientSideMap().put(clientSide.getId(), clientSide);     //存map
		room.getClientSideList().add(clientSide);             //房主加入房间
		room.setCurrentSellClient(clientSide.getId());        //当前庄主
		room.setCreateTime(System.currentTimeMillis());
		room.setLastFlushTime(System.currentTimeMillis());
		
		clientSide.setRoomId(room.getId());
		ServerContains.addRoom(room);

		//将room推回客户端
		System.out.println(clientSide.getId()+"创建了房间:"+room.getId());
		//这边加入后直接在房间服务器取出群发, 所以不用每个客户端都塞入
		try {
			ServerContains.Server_Room_Data.get(room.getId()).put(new WebData("join_room_type", 1)); //表示房主加入房间
			ServerContains.Server_Room_Data.get(room.getId()).put(new WebData("join_room_client_id", String.valueOf(clientSide.getId())));
			ServerContains.Server_Room_Data.get(room.getId()).put(new WebData("join_room_client_name", clientSide.getOwner_name()));
			ServerContains.Server_Room_Data.get(room.getId()).put(new WebData("join_room_client_role", clientSide.getRole().toString()));
		}
		//以上内容为补充
		catch (Exception e) {
			e.printStackTrace();
		}
		ChannelUtils.pushToClient(clientSide.getChannel(), ClientEventCode.CODE_ROOM_CREATE_SUCCESS, Noson.reversal(room));
	}

	



}
