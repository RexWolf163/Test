package com.scheduler.model;
/** id, Тип и состояние станка (рабочего места)
 *@version 0.2
 * */
public class Mashine {
	/**Идентификатор*/
	private String id;
	/**Модель*/
	private String type;
	/**Статус (время в минутах от начала расписания, до которого станок занят)*/
	private int busy;
	
	/**Запрос данных об id */
	public String getId() {return id;}
	/**Корректировка id */
	public void setId(String data) {this.id=data;}
	/**Запрос данных об типе*/
	public String getType() {return type;}
	/**Корректировка типа */
	public void setType(String data) {this.type=data;}
	/**Запрос данных об статусе*/
	public int getBusy() {return busy;}
	/**Корректировка статуса */
	public void setBusy(int data) {this.busy=data;}

}

