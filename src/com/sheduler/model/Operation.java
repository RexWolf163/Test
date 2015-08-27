package com.scheduler.model;
/** Описание задачи.
 *@version 0.2
 * */
public class Operation {
	/**Идентификатор*/
	private final String id;
	/**Требуемая профессия*/
	private final String prof;
	/**Требуемая модель станка*/
	private final String mash;
	/**Стоимость выполненной работы*/
	private final int cost;
	/**времязатраты операции*/
	private final int time;
	/**Флаг "ведомой" операции. Ставится, если операция не может быть выполнена до завершения предыдущей*/
	private final boolean wait;
	/**Удельный вес операции Стоимость/Время (коп/минуту). Операции с более высоким весом имеют приоритет. 
	 * Для каждой операции в связанной цепочке процессов, удельный вес берется как (Суммарная стоимость/Суммарная длительность)*/
	private int weight;
	/**Время начала выполнения операции*/
	private Integer start;
	/**Количество рабочих, претендующих на данную операцию*/
	private int pretendents;
	/**Счетчик объектов типа Станок*/
	private static long counter;
	
	public Operation(String[] stroke){
		id="(id Op"+(++counter)+") "+stroke[0];
		prof=stroke[2];
		mash=stroke[1];
		cost=Integer.valueOf(stroke[4]);
		time=Integer.valueOf(stroke[3]);
		weight=Math.round(100*cost/time);
		wait=((stroke.length==6)&&(stroke[5].equals("#"))) ? true : false;		
	}
	
	/**Запрос данных об id */
	public String getId(){return id;}
	/**Запрос данных об prof */
	public String getProf(){return prof;}
	/**Запрос данных об mash */
	public String getMash(){return mash;}
	/**Запрос данных об cost */
	public int getCost(){return cost;}
	/**Запрос данных об time */
	public int getTime(){return time;}
	/**Запрос данных об id */
	public boolean getWait(){return wait;}
	/**Запрос данных об weight */
	public int getWeight(){return weight;}
	/**Запрос данных об start */
	public Integer getStart(){return start;}
	/**Запрос данных об pretendents */
	public int getPretendents(){return pretendents;}

	/**Корректировка данных weight */
	public void setWeight(int data){weight=data;}
	/**Корректировка данных start */
	public void setStart(int data){start=data;}
	/**Корректировка данных pretendents */
	public void setPretendents(int data){pretendents=data;}
	
}
