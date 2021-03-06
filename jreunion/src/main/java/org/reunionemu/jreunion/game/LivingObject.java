package org.reunionemu.jreunion.game;

import org.apache.log4j.Logger;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.game.quests.ExperienceQuest;
import org.reunionemu.jreunion.game.quests.QuestState;
import org.reunionemu.jreunion.game.quests.objective.Objective;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Tools;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public abstract class LivingObject extends WorldObject {

	private LivingObject target;
	
	private Position targetPosition;
	
	private String name;
	
	private long hp;

	private long maxHp;

	private int level;
	
	private int dmgType;

	public Position getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Position targetPosition) {
		this.targetPosition = targetPosition;
	}
	
	public int getPercentageHp() {
		
		double percentageHp = this.getHp() * 100 / this.getMaxHp();
		if (percentageHp > 0 && percentageHp < 1) {
			percentageHp = 1;
		}
		return (int) percentageHp;		
	}
	
	public void walk(Position position, boolean running) {

		setIsRunning(running);
		synchronized(this) {
			setPosition(position);
			setTargetPosition(position.clone());			
		}
		getInterested().sendPacket(Type.WALK, this, position);
				
	}
	
	public int getDmgType() {
		return dmgType;
	}

	public void setDmgType(int dmgType) {
		this.dmgType = dmgType;
	}
	
	private boolean running;

	public void setIsRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	public LivingObject() {
		super();
	}

	public long getHp() {
		return hp;
	}

	public int getLevel() {
		return level;
	}

	public long getMaxHp() {
		return maxHp;
	}

	public LivingObject getTarget() {
		return target;
	}

	public void loadFromReference(int id) {

	}

	public void setHp(long hp) {
		this.hp = Tools.between(hp, 0l, getMaxHp());		
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHp(long maxHp) {
		this.maxHp = maxHp;
	}

	public void setTarget(LivingObject target) {
		this.target = target;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String livingObjectName){
		this.name = livingObjectName;
	}
	
	public void getsAttacked(Player player, long damage){
		
		player.addAttack(damage);
		
		//Cursed quest Boss packet
		if(this instanceof Npc){
			Npc<?> npc = (Npc<?>)this;
			if(npc.getType() instanceof Mob){
				//Mob mob = (Mob)((Npc<?>)this).getType();
				QuestState questState = player.getQuestState();
			
				if(questState != null){
					Quest quest = questState.getQuest();
					if(quest instanceof ExperienceQuest){
						Objective objective = quest.getObjective(npc.getType().getTypeId());
						if(objective != null){
								if(questState.getProgression(objective.getId()) == (objective.getAmmount()-1)){
									if(!npc.isBoss()){
										player.getClient().sendPacket(Type.QT, "king "+this.getEntityId()+" 1");
										npc.setBoss();
									}
								}
						}
					}
				}
			}
		}
		
		long newHp = Tools.between(getHp() - damage, 0l, getMaxHp());				
		
		if (newHp <= 0) {
			Logger.getLogger(LivingObject.class).info("Player "+player+" killed npc "+this);
			if(this instanceof Npc){
					((Npc<?>)this).kill(player);
			}
		} else {
			setHp(newHp);
		}
		this.getInterested().sendPacket(Type.ATTACK_VITAL, this);
	}
}