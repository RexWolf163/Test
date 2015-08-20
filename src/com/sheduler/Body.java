package com.sheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Mashine;
import model.Operation;
import model.Worker;

public class Body {
	public static ArrayList<Mashine> Models = new ArrayList<Mashine>();		
	public static ArrayList<Worker> Workers = new ArrayList<Worker>();		
	public static ArrayList<Operation> Operations = new ArrayList<Operation>();
	public static BufferedReader br;
	public static List<String> Records = new ArrayList<String>();
	public static int Current =0;
	public static int WorkTime=0;
	public static Date date1 = new Date();
	public static Date date2 = new Date();
	
	public static int Loading(boolean debag){//Загрузка данных из файлов
		int result=0;
		
		String[] files = {"mashines.txt","workers.txt","Operations.txt"}; //список файлов для обработки
		String[] s = {"",""}; //оперативная строковая переменная
		
		int b=0;
		int WaitNum=0;//количество "ожидающих" операций (просто для статистики)
		
		for (String Load:files){
			try {
				String sCurrentLine;
				int i=0;
				b+=1;		
				br = new BufferedReader(new FileReader(Load));
				
				while ((sCurrentLine = br.readLine()) != null) {
					i+=1;
					
					s=sCurrentLine.split("\t");
					if (s[0].length()!=0) {
						switch (b){
							case 1:{
								i-=1;
								for (int k = 1; k <= Integer.valueOf(s[1]); k++){
									i+=1;
									Models.add(new Mashine());
									Models.get(i-1).setType(s[0]);
									Models.get(i-1).setId("(id M"+i+")"+s[0]);
									}
								break;
							}
							case 2:{
								Workers.add(new Worker());
								Workers.get(i-1).setId("(id W"+i+") "+s[0]);
								Workers.get(i-1).setProf(s[1]);
								for (String mash:s[2].split("`")){
									Workers.get(i-1).addMash(mash);
								}
								break;
							}
							case 3:{
								Operations.add(new Operation());
								Operations.get(i-1).setId("(id Op"+i+") "+s[0]);
								Operations.get(i-1).setProf(s[2]);
								Operations.get(i-1).setMash(s[1]);
								Operations.get(i-1).setCost(Integer.valueOf(s[4]));
								Operations.get(i-1).setTime(Integer.valueOf(s[3]));
								Operations.get(i-1).setWeight(Math.round(100*Operations.get(i-1).getCost()/Operations.get(i-1).getTime()));
								if (s.length==6)
									if (s[5].equals("#")){
										WaitNum+=1;
										Operations.get(i-1).setWait(true);
										Operations.get(i-2).setWeight(Math.round((Operations.get(i-2).getWeight()+Operations.get(i-1).getWeight())/2));
										Operations.get(i-1).setWeight(Operations.get(i-2).getWeight());
									}
								
								break;
							}
						}
						
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Файл данных не найден");
				System.exit(0);
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(0);
				}
			}
		}
		//немного информации
		if (debag)
			{System.out.println("Загружены данные:"
				+ "\nОборудование:");
			for (Mashine Model : Models) {
				System.out.println(Model.getId()+" - "+Model.getType());}
			System.out.println("Работники:");
			for (Worker Worker : Workers) {
				System.out.println(Worker.getId()+" ("+Worker.getProf()+" - "+Worker.getMash()+")");}
			System.out.println("Поставленные задачи:");
			for (Operation Operation : Operations) {
				System.out.println(Operation.getId()+" ("+Operation.getProf()+" - "+Operation.getMash()+"/ "+Operation.getTime()+"мин - "+Operation.getCost() +"рублей)");}
			}
		result=WaitNum;
		return result;
	}
	
	public static void Check(){// проверка соответствий названий агрегатов и профессий
		int i=0;
		int j=0;
		for (Worker Worker:Workers)
			for (String mash:Worker.getMash()){
				i=1;
				for (Mashine Model:Models)
					if (Model.getType().equals(mash)) i=0;
				if(i==1) {System.out.println("Оборудование указаное в записи "+Worker.getId()+" "+Worker.getMash()+" в базе данных не обнаружено");
					j=1;
				}
		}
		for (Operation Operation:Operations){
			i=1;
			for (Mashine Model:Models)
				if (Model.getType().equals(Operation.getMash())) i=0;
			if(i==1) {System.out.println("Оборудование указаное в записи "+Operation.getId()+" "+Operation.getMash()+" в базе данных не обнаружено");
				j=1;
			}
		}
		for (Operation Operation:Operations){
			i=1;
			for (Worker Worker:Workers)
				if (Worker.getProf().equals(Operation.getProf())) i=0;
			if(i==1) {System.out.println("Профессия указаная в записи "+Operation.getId()+" "+Operation.getProf()+" среди персонала не встречается");
				j=1;
			}
		}
		for (Operation Operation:Operations){
			if (Operation.getWait()) {
				if (i==1) {
					i=2;
					System.out.println("Обнаружена цепочка длиной более двух операций");
					j=1;
					break;
				}
				i=1;
			}
			else i=0;
		}
		
		if (j==0) System.out.println("Проблемы не обнаружены");
		else {
			System.out.println("Расчет окончен по ошибке в данных");
			System.exit(0);
		}		
	}
	
	public static Date RDate(boolean debag){//считываем дату с клавиатуры

		br=new BufferedReader(new InputStreamReader(System.in));
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyPattern("dd.MM.yyyy");
		Date date= new Date();
		try {
			String h="";
			if (!debag) h=br.readLine();
			else h="1.1.1";
			
			try {				
				date= format.parse(h);
			} catch (ParseException e) {
				//e.printStackTrace();
				System.out.println("Неправильно введена дата");
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} 
		return date;

	}
	
	public static void FillPool(Worker Wor){//Заполнение Пула работников задачами
        /*Распределения работы меж работников:
         * Тезис: Максимальная прибыль достигается при максимальной загруженности всех работников и станков.
         		Соответственно необходимо обеспечить равномерное распределение задач по персоналу.
         		Этого можно достигнуть, если наименее загруженный работник имеет приоритетное право на решение задачи. 
         
         Алгоритм:
         Сперва каждый работник собирает себе операции на выполнение без каких либо ограничений.
         		Номер операции ставится в Пул работника. Время операции суммируется к занятости работника. Количество претендентов 
         		на операцию увеличивается. Затем выбирается работник с занятостью больше чем у других. Перебираются его операции. 
         		У первой операции с большим чем 1 количеством претендентов снижается кол-во претендентов и она убирается из пула 
         		работника. Ее время вычитается из занятости. 
         		Данный пункт повторяется до тех пор, пока не будет достигнута "1" в претендентах у всех операций.
         */
		int i=0;
		for (Operation Op:Operations){
			if (Wor.getProf().equals(Op.getProf())){
				if (Wor.getMash().indexOf(Op.getMash())!=-1){
					Wor.setWorkTime(Wor.getWorkTime()+Op.getTime());
					Op.setPretendents(Op.getPretendents()+1);
					Wor.addPool(i);
				}
			}
		i+=1;	
		}
		
	}
	
	public static boolean DropPool(Worker Wor){//Сброс лишней операции из пула
		boolean bool=true;
		for (int IDOp:Wor.getPool()){
			//System.out.println(IDOp +"-"+Operations.get(IDOp).Pretendents);
			if (Operations.get(IDOp).getPretendents()>1){
				//System.out.println("!"+Wor.getWorkTime());
				bool=false;
				Wor.setWorkTime(Wor.getWorkTime()-Operations.get(IDOp).getTime());
				//System.out.println("!"+Wor.getWorkTime());

				Wor.removePool(Wor.getPool().indexOf(IDOp));
				Operations.get(IDOp).setPretendents(Operations.get(IDOp).getPretendents()-1);
				break;
			}
		}
		return bool;
	}
	
	public static void WorkersDuty(){
        /*Распределения работы меж работников:
         * Тезис: Максимальная прибыль достигается при максимальной загруженности всех работников и станков.
         		Соответственно необходимо обеспечить равномерное распределение задач по персоналу.
         		Этого можно достигнуть, если наименее загруженный работнки имеет приоритетное право на решение задачи. 
         
         Алгоритм:
         Сперва каждый работник собирает себе операции на выполнение без каких либо ограничений.
         		Номер операции ставится в Пул работника. Время операции суммируется к занятости работника. Количество претендентов 
         		на операцию увеличивается. Затем выбирается работник с занятостью больше чем у других. Перебираются его операции. 
         		У первой операции с большим чем 1 количеством претендентов снижается кол-во претендентов и она убирается из пула 
         		работника. Ее время вычитается из занятости. 
         		Данный пункт повторяется до тех пор, пока не будет достигнута "1" в претендентах у всех операций.
         */
        int BiggerTime = 0;
        int BiggerID=-1;
        int i=0;
        // первичное распределение операций
        for (Worker Wor:Workers){
        	FillPool(Wor);
        	if (BiggerTime<Wor.getWorkTime()){
        		BiggerTime=Wor.getWorkTime();
        		BiggerID=i;
        	}
        	i+=1;
        }
        
        //отказ от спорных операций
        boolean fin=false;
        while(!fin){
        	//проверка количества претендентов на операцию
        	fin=true;
        	for (Operation Op:Operations){
        		if (Op.getPretendents()>1) fin=false;
        		//System.out.println(Op.getId()+"*"+Op.Pretendents);
        	}
        	Workers.get(BiggerID).setFin(DropPool(Workers.get(BiggerID))); //сброс операции от самого занятого
        	BiggerTime=0;//и обнуление переменнных
       		BiggerID=-1;
        	i=0;
            for (Worker Wor:Workers){//ищем следующего "жадину"
            	if ((!Wor.getFin())&&(BiggerTime<Wor.getWorkTime())){
            		BiggerTime=Wor.getWorkTime();
            		BiggerID=i;
            	}
            	i+=1;
            }
        }		
	}
	
	public static int RefreshWork(){
		int j=0;
		int TimeStamp=WorkTime+1;
		boolean bool=true;
		boolean skip=false;
		for (Worker Wor:Workers){
				if ((Wor.getWorkTime()<=Current)&&(Wor.getWorkId()!=-1)){
					Wor.removePool(Wor.getPool().indexOf(Wor.getWorkId()));
					Wor.setWorkId(-1);
				}
				j=0;
				bool=true;
				
				while((bool)&&(Wor.getWorkTime()<=Current)){
					if (j==Wor.getPool().size()) break;
					skip=false;
					//System.out.println("Check2");
					if (Operations.get(Wor.getPool().get(j)).getWait()){
						//System.out.println("Check3 ==>"+Operations.get(Wor.getPool().get(j)-1).getStart()+" * "+Operations.get(Wor.getPool().get(j)-1).getStart()+"=="+Operations.get(Wor.getPool().get(j)-1).getTime());
						skip=true;
						if ((Operations.get(Wor.getPool().get(j)-1).getStart()!=null)&&(Current>=(Operations.get(Wor.getPool().get(j)-1).getStart()+Operations.get(Wor.getPool().get(j)-1).getTime()))){
							skip=false;
							//System.out.println("Check4");
						}
					}		
					
					if (!skip){
						for (Mashine Mod:Models){
							//System.out.println("Srch:"+Wor.getId()+Mod.getId()+"for"+Operations.get(Wor.getPool().get(j)).getId()+"    :"+Current);
							if ((Mod.getBusy()<=Current)&&(Mod.getType().equals(Operations.get(Wor.getPool().get(j)).getMash()))){
								//System.out.println("Find");
								Wor.setWorkTime(Operations.get(Wor.getPool().get(j)).getTime()+Current);
								Mod.setBusy(Operations.get(Wor.getPool().get(j)).getTime()+Current);
								Wor.setWorkId(Wor.getPool().get(j));
								Operations.get(Wor.getPool().get(j)).setStart(Current);
								Records.add(DateTrans(Current)+" по "+DateTrans(Wor.getWorkTime())+" =>"+Wor.getId()+" занимает "+Mod.getId()+ " для "+ Operations.get(Wor.getPool().get(j)).getId());
								//System.out.println(Records.get(Records.size()-1));
								bool=false;
								//if (TimeStamp>Wor.getWorkTime()) TimeStamp=Wor.WorkTime;
								break;
							}
						}
					}
					j+=1;
				}
			//}
		}
		for (Worker Wor:Workers){
			if((Wor.getWorkId()!=-1)&&(Wor.getPool().size()>0)&&(TimeStamp>Wor.getWorkTime())) TimeStamp=Wor.getWorkTime();
		}
		
		return TimeStamp;
	}
	
	public static void MashinesDuty(){
		/*раскидываем операции по машинам.
		
		Алгоритм:
		Для каждого работника набор операций выстраивается по снижению веса (сортировка по пузырьковому алгоритму)
		
		После этого работнику назначается станок. При недоступности станка, берется следующая операция в пуле. Проверяется
		наличие зависимости ("цепочки")
		*/
		int j=0;
		int W=-1;
		for (Worker Wor:Workers){
			W=-1;
			for (int i=0;i < Wor.getPool().size();i++){
				if ((W!=-1)&&(W<Operations.get(Wor.getPool().get(i)).getWeight())){
					j=Wor.getPool().get(i-1);
					Wor.setPool(i-1,Wor.getPool().get(i));
					Wor.setPool(i, j);
					i-=2;
					if (i<0)i=0;
				}
				W=Operations.get(Wor.getPool().get(i)).getWeight();
			}
		}
		
		//Очистка переменных от распределения, для учета
		for (Worker Wor:Workers){
			j=0;
			Wor.setWorkTime(0);
			Wor.setWorkId(-1);
		}
		
		//сортировка завершена.
		boolean bool=false;
		int step=0;
		while (step<100000){
			step+=1;//страховка от вечного цикла
			Current=RefreshWork();
			bool=true;
			for (Worker Wor:Workers)
				if (Wor.getPool().size()>0) {
					bool=false;
					break;
				}
			if ((bool)||(Current>WorkTime)) break;
		}
	}
	
	public static String DateTrans(int M){//переведем абстрактные минуты в точное время. Считаем началом дня 8:00, конец 17:00, а обед 12:00-13:00
		int Days=Math.floorDiv(M, 8*60);
		int Hours=Math.floorDiv(M-Days*8*60, 60)+8;
		int Minutes=M-Days*8*60-(Hours-8)*60;
		if (Hours>=12)Hours+=1;

		Calendar NDate = Calendar.getInstance();
		
		NDate.setTime(date1);
		NDate.add(Calendar.DAY_OF_MONTH, Days);
		NDate.add(Calendar.HOUR, Hours);
		NDate.add(Calendar.MINUTE, Minutes);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyyг ");	
		return (sdf.format(NDate.getTime()));

	}
	
	public static void main(String[] args) {
		
		//Загрузка данных из файлов
		int WaitNum=Loading(false);
		
		System.out.println("\nНайдено "+WaitNum +" сцепленных операций"
				+ "\n--------------------------------------------------\n"
				+ "Проверка корректности данных");	
		
		// проверка соответствий названий агрегатов и профессий
		Check();

		System.out.println("Проверка корректности данных завершена"
				+ "\n--------------------------------------------------\n\n\n"
				+ "Введите дату начала расчета в формате ДД.ММ.ГГГГ");
		
		//Делаем запрос на диапазон расчета
		
        
		date1=RDate(false);
		
        System.out.println("\nВведите дату конца расчета");
        
        date2=RDate(false);
        
        if (date2.getTime()<date1.getTime()){
        	System.out.println("Конец расчета должен быть позже начала!");
        	System.exit(0);
        }
        //наверняка есть способ проще, но я его не знаю. так что будем считать количество дней "вручную"

        long Days = 1+Math.abs(date1.getTime()-date2.getTime())/(1000*60*60*24);
        System.out.println("\nПланирование производится на "+Days+" дней ("+Days*8*60+" рабочих минут)"
        		+ "\n\n");
        
        WorkTime = (int) Days*8*60;//рабочий период (пригодится)
        
        /*Сортируем рабочих по количеству Станков а операции по относительной стоимости минуты. 
        Таким образом приоритет получат более дорогие операции для более узких спецов
        Последнее важно, так как широкий спец найдет альтернативное занятие легче узкого
        */
		int W=0;
        Worker j=new Worker();
			W=-1;
			for (int i=0;i < Workers.size();i++){
				//System.out.println(W+" "+i+Wor.Pool);
				if ((W!=-1)&&(W>Workers.get(i).getMash().size())){
					j=Workers.get(i-1);
					Workers.set(i-1,Workers.get(i));
					Workers.set(i, j);
					i-=2;
					if (i<0)i=0;
				}
				W=Workers.get(i).getMash().size();
			}

	        Operation z=new Operation();
				W=-1;
				for (int i=0;i < Operations.size();i++){
					//System.out.println(W+" "+i+Wor.Pool);
					if ((W!=-1)&&(W<Operations.get(i).getWeight())){
						z=Operations.get(i-1);
						Operations.set(i-1,Operations.get(i));
						Operations.set(i, z);
						i-=2;
						if (i<0)i=0;
					}
					W=Operations.get(i).getWeight();
				}
				//for(Operation Wor:Operations) System.out.println(Wor.getId()+"\t"+Wor.getWeight());

        
        
        WorkersDuty();//распределяем задачи меж сотрудников
        MashinesDuty();//расставляем работников по местам
      

        
        //************
        
        File file = new File("PLAN.txt");
        
        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
     
            try {
            	out.println("Расписание составляется на период с "+DateTrans(0)+" по "+DateTrans(WorkTime-1));
            	out.println("");
                for (String R:Records){
                	System.out.println(R);
                	out.println(R);
                }
                System.out.println("Не выполнено:");
                out.println("-------------------------------------------------------------");
                out.println("Не выполнено:");
                for(Operation Op:Operations){
                	if (((Op.getStart()+Op.getTime())>WorkTime)||(Op.getStart()==null)){
                		System.out.println(Op.getId()+"\tДолжно начаться "+DateTrans(Op.getStart())+"\tСтоимость"+Op.getCost()+"\tДлительность"+Op.getTime()+"мин");
                		out.println(Op.getId()+"\tДолжно начаться "+DateTrans(Op.getStart())+"\tСтоимость"+Op.getCost()+"\tДлительность"+Op.getTime()+"мин");
                	}
                }
            	
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
	}
}
