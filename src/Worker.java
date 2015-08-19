import java.util.ArrayList;
import java.util.List;

public class Worker {
	String ID = new String();
	String Prof = new String();
	List<String> Mash = new ArrayList<String>();
	
	List<Integer> Pool = new ArrayList<Integer>();
	int WorkTime=0;//занятость в минутах от начала периода
	
	//int StartTime=0;
	List<Integer> WorkRecord = new ArrayList<Integer>(); //запись о назначеной операции. Хранится id операции
	
	//Операционные переменные:
	//int Busy = 0;
	int WorkWeight = 0;
	int WorkID = -1;
	
	boolean Fin= false;
}
