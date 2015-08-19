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
	public String getId() {return id;}
	/**Корректировка id */
	public void setI(String data) {this.id=data;}
	/**Запрос данных об типе*/
	public String getType() {return type;}
	/**Корректировка типа */
	public void setType(String data) {this.type=data;}
	/**Запрос данных об статусе*/
	public int getBusy() {return busy;}
	/**Корректировка статуса */
	public void setBusy(int data) {this.busy=data;}

}
