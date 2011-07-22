package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HybriderPlayer extends Player {

	public HybriderPlayer(Client client) {
		super(client);
		
	}
	
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 50) +(getLeadership() / 2);
	}
	
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 60) + Tools.statCalc(getConstitution(), 30)+ (getLeadership() / 2);		
	}

	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 55) + (getLeadership() / 2);		
	}
	
	public int getMaxStamina(){
		return Tools.statCalc(getStrength(), 90) + (getLeadership() / 2);		
		
	}
	
	@Override
	public float getBaseDamage() {
		return (getLevel() / 6) + (getStrength() / 5) + (getWisdom()/ 4) + (getDexterity() / 3);
	}
	
}