package model;
/** Описание задачи.
 *@version 0.2
 * */
public class Operation {
	/**Идентификатор*/
	private String id;
	/**Требуемая профессия*/
	private String prof;
	/**Требуемая модель станка*/
	private String mash;
	/**Стоимость выполненной работы*/
	private int cost;
	/**времязатраты операции*/
	private int time;
	/**Флаг "ведомой" операции. Ставится, если операция не может быть выполнена до завершения предыдущей*/
	private boolean wait;
	/**Удельный вес операции Стоимость/Время (коп/минуту). Операции с более высоким весом имеют приоритет. 
	 * Для каждой операции в связанной цепочке процессов, удельный вес берется как (Суммарная стоимость/Суммарная длительность)*/
	private int weight;
	/**Время начала выполнения операции*/
	private Integer start;
	/**Количество рабочих, претендующих на данную операцию*/
	private int pretendents;
	
	public Operation(){
		this.time = 1;
	}
	
	/**Запрос данных об id */
	public String getId(){return this.id;}
	/**Запрос данных об prof */
	public String getProf(){return this.prof;}
	/**Запрос данных об mash */
	public String getMash(){return this.mash;}
	/**Запрос данных об cost */
	public int getCost(){return this.cost;}
	/**Запрос данных об time */
	public int getTime(){return this.time;}
	/**Запрос данных об id */
	public boolean getWait(){return this.wait;}
	/**Запрос данных об weight */
	public int getWeight(){return this.weight;}
	/**Запрос данных об start */
	public Integer getStart(){return this.start;}
	/**Запрос данных об pretendents */
	public int getPretendents(){return this.pretendents;}

	/**Корректировка данных id */
	public void setId(String data){this.id=data;}
	/**Корректировка данных prof */
	public void setProf(String data){this.prof=data;}
	/**Корректировка данных mash */
	public void setMash(String data){this.mash=data;}
	/**Корректировка данных cost */
	public void setCost(int data){this.cost=data;}
	/**Корректировка данных time */
	public void setTime(int data){this.time=data;}
	/**Корректировка данных id */
	public void setWait(boolean data){this.wait=data;}
	/**Корректировка данных weight */
	public void setWeight(int data){this.weight=data;}
	/**Корректировка данных start */
	public void setStart(int data){this.start=data;}
	/**Корректировка данных pretendents */
	public void setPretendents(int data){this.pretendents=data;}
	
}
