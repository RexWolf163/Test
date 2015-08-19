package com.scheduler.model;
/** id, Тип и состояние станка (рабочего места)
 *@version 0.2
 * */
public class Mashine {
	/**Идентификатор*/
	private String id = new String();
	/**Модель*/
	private String type = new String();
	/**Статус (время в минутах от начала расписания, до которого станок занят)*/
	private int busy = 0;
	
	/**Запрос данных об id */
	public String get_id() {return id;}
	/**Корректировка id */
	public void set_id(String data) {this.id=data;}
	/**Запрос данных об типе*/
	public String get_type() {return type;}
	/**Корректировка типа */
	public void set_type(String data) {this.type=data;}
	/**Запрос данных об статусе*/
	public int get_busy() {return busy;}
	/**Корректировка статуса */
	public void set_busy(int data) {this.busy=data;}

}

