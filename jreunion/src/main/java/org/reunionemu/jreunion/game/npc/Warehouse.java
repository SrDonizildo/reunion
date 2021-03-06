package org.reunionemu.jreunion.game.npc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import org.reunionemu.jreunion.game.HandPosition;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.StashItem;
import org.reunionemu.jreunion.game.StashPosition;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Warehouse extends NpcType {
	
	public Warehouse(int id) {
		super(id);
		//loadFromReference(id);
	}

	/****** Open stash ******/
	public void openStash(Player player) {

		Client client = player.getClient();

		Iterator<StashItem> stashIter = player.getStash().itemListIterator();
		while (stashIter.hasNext()) {
			StashItem stashItem = stashIter.next();	
			client.sendPacket(Type.STASH, player, stashItem);
		
		}
		client.sendPacket(Type.STASH_END);
	}

	/****** Add/Remove single items to/from stash ******/
	public void stashClick(Player player, int pos, int type, int gems, int special) {
		
		Client client = player.getClient();
		
		if(pos == 12){
			client.sendPacket(Type.MSG, "Lime deposit/withdraw not implemented yet.");
			return;
		}
		/*
			if(gems >= 0)
				storeLime(player,pos,gems);
			else
				removeLime(player,pos,gems);
		}*/
		
		HandPosition handPosition = player.getInventory().getHoldingItem();
		StashItem stashItem = null;

		// Withdraw item from stash
		if(handPosition == null){
			stashItem = player.getStash().getItem(pos);
			removeItem(player, stashItem);
		} else { //depositing item stash
			stashItem = new StashItem(new StashPosition(pos), handPosition.getItem());
			storeItem(player,stashItem);
			
		}
	}
	
	/****** Add multiple items to stash ******/
	public void stashPut(Player player, int pos, int type, int gems, int special) {
		
	}
	
	/****** Remove multiple items from stash ******/
	public void stashGet(Player player, int pos, int type, int gems, int special) {
		
	}
	
	public void storeItem(Player player, StashItem stashItem){
		player.getInventory().setHoldingItem(null);
		player.getStash().addItem(stashItem);
		player.getClient().sendPacket(Type.STASH_TO, player, stashItem);
		Logger.getLogger(Warehouse.class).info("Player "+player+" stored item "+stashItem.getItem()+" in the warehouse");
	}
	
	public void removeItem(Player player, StashItem stashItem){
		Item<?> item = stashItem.getItem();
		
		if(item.getEntityId() == -1)
			player.getPosition().getLocalMap().createEntityId(item);
		
		player.getInventory().setHoldingItem(new HandPosition(item));
		player.getStash().removeItem(stashItem);
		player.getClient().sendPacket(Type.STASH_FROM, stashItem);
		Logger.getLogger(Warehouse.class).info("Player "+player+" removed item "+item+" from the warehouse");
	}
	
	public boolean storeLime(Player player, int pos, int limeAmmount){
		StashItem stashItem = player.getStash().getItem(pos);
		Item<?> limeItem = null;
		
		if(stashItem == null){
			ItemType limeItemType = new ItemType(1014);
			limeItem = limeItemType.create();
		} else {
			limeItem = stashItem.getItem();
		}
		
		synchronized(player) {
			if((player.getLime()-limeAmmount) >= 0)
				player.setLime(player.getLime()-limeAmmount);
			else {
				Logger.getLogger(Warehouse.class).error("Player "+player+" is trying to remove "+limeAmmount+" lime. " +
						"Lime available "+player.getLime());
				return false;
			}
		}
		
		limeItem.setGemNumber((limeItem.getGemNumber()*100) + limeAmmount);		
		DatabaseUtils.getDinamicInstance().saveItem(limeItem);
		player.getClient().sendPacket(Type.STASH_TO, player, stashItem, (limeItem.getGemNumber()/100));

		return true;
	}
	
	public void removeLime(Player player, int pos, int lime){
		
	}
}