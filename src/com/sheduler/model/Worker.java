import java.util.ArrayList;
import java.util.List;

public class Worker {
	public String ID = new String();
	public String Prof = new String();
	public List<String> Mash = new ArrayList<String>();
	
	public List<Integer> Pool = new ArrayList<Integer>();
	public int WorkTime=0;//занятость в минутах от начала периода
	
	//int StartTime=0;
	public List<Integer> WorkRecord = new ArrayList<Integer>(); //запись о назначеной операции. Хранится id операции
	
	//Операционные переменные:
	//int Busy = 0;
	public int WorkWeight = 0;
	public int WorkID = -1;
	
	public boolean Fin= false;
}
