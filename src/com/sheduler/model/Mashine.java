package com.scheduler.model;
/** id, Тип и состояние станка (рабочего места)
 *@version 0.2
 * */
public class Mashine {
	/**Идентификатор*/
	private final String id;
	/**Модель*/
	private final String type;
	/**Статус (время в минутах от начала расписания, до которого станок занят)*/
	private int busy;
	/**Счетчик объектов типа Станок*/
	private static long counter;
	public Mashine(String[] stroke){
		id="(id M"+(++counter)+")"+stroke[0];
		type=stroke[0];
	}
	
	
	/**Запрос данных об id */
	public String getId() {return id;}
//	/**Корректировка id */
//	public void setId(String data) {id=data;}
	/**Запрос данных об типе*/
	public String getType() {return type;}
//	/**Корректировка типа */
//	public void setType(String data) {type=data;}
	/**Запрос данных об статусе*/
	public int getBusy() {return busy;}
	/**Корректировка статуса */
	public void setBusy(int data) {busy=data;}
}
