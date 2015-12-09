import java.util.InputMismatchException;
import java.util.Scanner;

public class MyDatabase {
	public static void main(String[] args) {
		
		 Scanner in = new Scanner(System.in);
		 String opt = "y", query; String fileName, tableName = "";
		 int choice;
		 boolean importFlag = false;
		 while(opt.equalsIgnoreCase("y")){
			System.out.println("1.Import\n2.Query\n3.Insert\n4.Delete: ");
			try{
				choice = in.nextInt();
				
			    if (importFlag == false && choice == 1)
			    	importFlag = true;
			    else if (importFlag == false && choice != 1){
			    	System.out.println("Import the file first");
			    	choice = 1;
			    	importFlag = true;
			    }
			    
				switch(choice){
					case 1 : System.out.println("Enter filename: ");
							 //fileName = "PHARMA_TRIALS_1000B.csv"; 
							 fileName = in.next();
							 tableName = fileName.substring(0, fileName.lastIndexOf("."));
							 LoadFile loadFile = new LoadFile(fileName, tableName);
							 break;
					case 2 : System.out.println("Enter the query to select from "+tableName+" :");
							 QueryRecord queryRecord = new QueryRecord(tableName);
							 break;
					case 3 : System.out.println("Instructions:\n1. ENLCOSE string values with (') single quotes\n2. Add (;) at end\n3. Dont give spaces in between fields");
							 System.out.print("Enter the insert query: ");
							 System.out.print("\ninsert into table "+tableName+" values");
							 query = in.nextLine();
							 query = in.nextLine();
							 InsertRecord insertRec = new InsertRecord(query, tableName);
							 break;
					case 4 : System.out.print("delete from "+tableName+" where ");
							 DeleteRecord delRec = new DeleteRecord(tableName);
							 break;
					default : break;
				}
				System.out.println("Want to continue? y/n: ");
				opt = in.next();
			} catch(InputMismatchException e){
				System.out.println("Enter proper input");
			}
		}
	}
}
