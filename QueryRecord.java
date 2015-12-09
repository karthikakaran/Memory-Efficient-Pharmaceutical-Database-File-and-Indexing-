import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class QueryRecord {
	
	public IndexSortedRecords recs = new IndexSortedRecords();
	public File dbFile;
    public RandomAccessFile randomAccess;
    public LoadFile lf = new LoadFile();
    public IndividualRecord trecord = new IndividualRecord();
    public String selectStar;    
    public String[] selectVals = {"0","0","0","0","0","0","0","0","0","0","0"};
    public String tableName;
    Scanner in = new Scanner(System.in);
    
    public QueryRecord() {
		
	}
	public QueryRecord(String tableName) {
		try{
			this.tableName = tableName;
			System.out.println("\nEnter * or fieldnames seperated by commas without space(like id,company,reading,..): ");
			selectStar = in.next();
			String fieldfName = null;
			String operation = null;
			String valueToDel = null;
			System.out.println("Enter the WHERE Field Name: ");
			fieldfName = in.next(); 
			System.out.println("Enter Operation < or > or <= or >= or = : ");
			operation = in.next();
			System.out.println("Enter Value: ");
			valueToDel = in.next();
			System.out.println("Do you want to perform NOT operation y/n:");
			if(in.next().equalsIgnoreCase("Y")){
				System.out.println("select "+selectStar+" from "+tableName+" where "+fieldfName+" NOT "+operation+" "+valueToDel+";");
				readFromIndexFileIdNotOper(fieldfName.trim(), operation.trim(), valueToDel.trim());
			}
			else{
				System.out.println("select "+selectStar+" from "+tableName+" where "+fieldfName+" "+operation+" "+valueToDel+";");
				readFromIndexFileId(fieldfName.trim(), operation.trim(), valueToDel.trim());
			}
		} catch(NullPointerException | StringIndexOutOfBoundsException e){
			System.out.println("Enter proper query (or) ENLCOSE values with (') single quotes");
		}
	}
	private void readFromIndexFileIdNotOper(String fieldfName, String operation, String valueToDel) {
		String fileName = "PHARMA_TRIALS_1000B."+fieldfName+".ndx", record;
		long position = 0;
		try {
			dbFile = new File(tableName+".db");
		    randomAccess = new RandomAccessFile(dbFile, "rw");
			FileInputStream finStrem = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(finStrem);
			if (fieldfName.equals("id")){
				TreeMap<String, Long> tMap = new TreeMap<String, Long>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, Long>) is.readObject();
					Set<String> setKey = tMap.keySet();
					Iterator iter = setKey.iterator();
					while (iter.hasNext()) {
						String iterNextValue = (String) iter.next();
						if (!iterNextValue.equalsIgnoreCase(valueToDel)) {
								fetchDbRecords(tMap.get(iterNextValue));
						}
					}
				}
			} else {
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>)is.readObject();
					if (operation.equals("=")){
						Set<String> setKey = tMap.keySet();
						Iterator iter = setKey.iterator();
						while (iter.hasNext()) {
							String iterNextValue = (String) iter.next();
							if (!iterNextValue.equalsIgnoreCase(valueToDel)) {
								for (long j : tMap.get(iterNextValue)) {
									fetchDbRecords(j);
								}
							}
						}
					} else {
						Set<String> setKey = tMap.keySet();
						Iterator iter = setKey.iterator();
						while (iter.hasNext()) {
							String tIterNextValue = "0", tValueToDel = "0"; Boolean flag = false;
							   String iterNextValue = (String) iter.next();
							   String fIter = iterNextValue;
							   if(fieldfName.equalsIgnoreCase("reading")){
								   tIterNextValue = iterNextValue;
								   tValueToDel = valueToDel;
								   if(iterNextValue.contains("."))
									   tIterNextValue = iterNextValue.substring(0,iterNextValue.indexOf('.')) + iterNextValue.substring(iterNextValue.indexOf('.') + 1);
								   if(valueToDel.contains("."))
									   tValueToDel = valueToDel.substring(0,valueToDel.indexOf('.')) + valueToDel.substring(valueToDel.indexOf('.') + 1);
							   }
							   if (operation.equals("<")){
								   if (fieldfName.equalsIgnoreCase("reading")){
									   if (Integer.parseInt(tIterNextValue) >= Integer.parseInt(tValueToDel))
										   flag = true;
								   }
								   else if (Integer.parseInt(iterNextValue) >= Integer.parseInt(valueToDel))
									   flag = true;
							   } else if(operation.equals(">")) {
								   if (fieldfName.equalsIgnoreCase("reading")){
									   if (Integer.parseInt(tIterNextValue) <= Integer.parseInt(tValueToDel))
										   flag = true;
								   }
								   else if (Integer.parseInt(iterNextValue) <= Integer.parseInt(valueToDel))
									   flag = true;
							   }
							   else if(operation.equals(">=")) {
								   if (fieldfName.equalsIgnoreCase("reading")){
									   if (Integer.parseInt(tIterNextValue) < Integer.parseInt(tValueToDel))
										   flag = true;
								   }
								   else if (Integer.parseInt(iterNextValue) < Integer.parseInt(valueToDel))
									   flag = true;
							   }
							   else if(operation.equals("<=")) {
								   if (fieldfName.equalsIgnoreCase("reading")){
									   if (Integer.parseInt(tIterNextValue) > Integer.parseInt(tValueToDel))
										   flag = true;
								   }
								   else if (Integer.parseInt(iterNextValue) > Integer.parseInt(valueToDel))
									   flag = true;
							   }
							   if (flag == true){
								   if (fieldfName.equalsIgnoreCase("reading")){
									   for (long j : tMap.get(fIter)){
										   fetchDbRecords(j);
									   }
								   } else {
									   for (long j : tMap.get(iterNextValue)){
										   fetchDbRecords(j);
									   }
								   }
							   }   
						}
					}
				}
			}
			finStrem.close();
		}catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        } catch(ClassNotFoundException | IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");             
        }
	}
	public void readFromIndexFileId(String fieldfName, String operation, String valueToDel) {
		
		String fileName = tableName+"."+fieldfName+".ndx", record;
		long position = 0;
	
		try {
			dbFile = new File(tableName+".db");
		    randomAccess = new RandomAccessFile(dbFile, "rw");
			FileInputStream finStrem = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(finStrem);
			if (fieldfName.equals("id")){
				TreeMap<String, Long> tMap = new TreeMap<String, Long>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, Long>) is.readObject();
					Set<String> setKey = tMap.keySet();
					Iterator iter = setKey.iterator();
					while (iter.hasNext()) {
						String iterNextValue = (String) iter.next();
						if (iterNextValue.equalsIgnoreCase(valueToDel)) {
							fetchDbRecords(tMap.get(valueToDel));
						}
					}
				}
			} else {
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>)is.readObject();
					if (operation.equals("=")){
						Set<String> setKey = tMap.keySet();
						Iterator iter = setKey.iterator();
						while (iter.hasNext()) {
							String iterNextValue = (String) iter.next();
							if (iterNextValue.equalsIgnoreCase(valueToDel)) {
								for (long j : tMap.get(iterNextValue)) {
									fetchDbRecords(j);
								}
							}
						}
					} else {
						Set<String> setKey = tMap.keySet();
						Iterator iter = setKey.iterator();
						while (iter.hasNext()) {
						   String tIterNextValue = "0", tValueToDel = "0"; Boolean flag = false;
						   String iterNextValue = (String) iter.next();
						   String fIter = iterNextValue;
						   if(fieldfName.equalsIgnoreCase("reading")){
							   tIterNextValue = iterNextValue;
							   tValueToDel = valueToDel;
							   if(iterNextValue.contains("."))
								   tIterNextValue = iterNextValue.substring(0,iterNextValue.indexOf('.')) + iterNextValue.substring(iterNextValue.indexOf('.') + 1);
							   if(valueToDel.contains("."))
								   tValueToDel = valueToDel.substring(0,valueToDel.indexOf('.')) + valueToDel.substring(valueToDel.indexOf('.') + 1);
						   }
						   if (operation.equals("<")){
							   if (fieldfName.equalsIgnoreCase("reading")){
								   if (Integer.parseInt(tIterNextValue) < Integer.parseInt(tValueToDel))
									   flag = true;
							   }
							   else if (Integer.parseInt(iterNextValue) < Integer.parseInt(valueToDel))
								   flag = true;
						   } else if(operation.equals(">")) {
							   if (fieldfName.equalsIgnoreCase("reading")){
								   if (Integer.parseInt(tIterNextValue) > Integer.parseInt(tValueToDel))
									   flag = true;
							   }
							   else if (Integer.parseInt(iterNextValue) > Integer.parseInt(valueToDel))
								   flag = true;
						   }
						   else if(operation.equals(">=")) {
							   if (fieldfName.equalsIgnoreCase("reading")){
								   if (Integer.parseInt(tIterNextValue) >= Integer.parseInt(tValueToDel))
									   flag = true;
							   }
							   else if (Integer.parseInt(iterNextValue) >= Integer.parseInt(valueToDel))
								   flag = true;
						   }
						   else if(operation.equals("<=")) {
							   if (fieldfName.equalsIgnoreCase("reading")){
								   if (Integer.parseInt(tIterNextValue) <= Integer.parseInt(tValueToDel))
									   flag = true;
							   }
							   else if (Integer.parseInt(iterNextValue) <= Integer.parseInt(valueToDel))
								   flag = true;
						   }
						   if (flag == true){
							   if (fieldfName.equalsIgnoreCase("reading")){
								   for (long j : tMap.get(fIter)){
									   fetchDbRecords(j);
								   }
							   } else {
								   for (long j : tMap.get(iterNextValue)){
									   fetchDbRecords(j);
								   }
							   }
						   }
						}
					}
				}
			}
			finStrem.close();
		}catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        } catch(IOException | ClassNotFoundException ex) {
            System.out.println("Error reading file '" + fileName + "'");             
        }
	}
	public void fetchDbRecords(long pos_file) {
		try {
			int size, id, trials, patients, dosage;
			float reading;
			String cname, drug_id;
			byte[] idb = new byte[50];
			byte[] drug = new byte[6];
			byte[] flt = new byte[4];
			byte bol, double_blind_mask = 0x08, controlled_study_mask = 0x04, govt_funded_mask = 0x02, fda_approved_mask = 0x01;
			boolean db = false, cs = false, gf = false, fa = false;
			randomAccess.seek(pos_file);
			randomAccess.read(idb, 0, 4);
			id = byteArray2Int(idb, 4);
			randomAccess.read(idb, 0, 1);
			size = byteArray2Int(idb, 1);
			byte[] cn = new byte[size];
			randomAccess.read(cn, 0, size);
			cname = byte2String (cn);
			randomAccess.read(drug, 0, 6);
			drug_id = byte2String (drug);
			randomAccess.read(idb, 0, 2);
			trials = byteArray2Int(idb, 2);
			randomAccess.read(idb, 0, 2);
			patients = byteArray2Int(idb, 2);
			randomAccess.read(idb, 0, 2);
			dosage = byteArray2Int(idb, 2);
			randomAccess.read(flt, 0, 4);
			reading = byteArray2Float(flt);
			bol = randomAccess.readByte();
			String delFlag = String.format("%8s", Integer.toBinaryString(bol & 0xFF)).substring(0,4); 
			boolean delString;
			if(delFlag.equalsIgnoreCase("1111"))
				delString = true;
			else
				delString = false;
			if ((bol & double_blind_mask) > 0) {
				db = true;
			}
			if ((bol & controlled_study_mask) > 0) {
				cs = true;
			}
			if ((bol & govt_funded_mask) > 0) {
				gf = true;
			}
			if ((bol & fda_approved_mask) > 0) {
				fa = true;
			}
			if (!selectStar.equals("*") && delString == false){
				selectVals = selectStar.split(",");
				for (String item : selectVals) {
					if (item.equalsIgnoreCase("id"))
						System.out.print(id + " ");
					else if (item.equalsIgnoreCase("company"))
						System.out.print(cname + " ");
					else if (item.equalsIgnoreCase("drug_id"))
						System.out.print(drug_id + " ");
					else if (item.equalsIgnoreCase("trials"))
						System.out.print(trials + " ");
					else if (item.equalsIgnoreCase("patients"))
						System.out.print(patients + " ");
					else if (item.equalsIgnoreCase("dosage_mg"))
						System.out.print(dosage + " ");
					else if (item.equalsIgnoreCase("reading"))
						System.out.print(reading + " ");
					else if (item.equalsIgnoreCase("double_blind"))
						System.out.print(db + " ");
					else if (item.equalsIgnoreCase("controlled_study"))
						System.out.print(cs + " ");
					else if (item.equalsIgnoreCase("govt_funded"))
						System.out.print(gf + " ");
					else if (item.equalsIgnoreCase("fda_approved"))
						System.out.print(fa + " ");
				}
				System.out.println();
			}
			else if (selectStar.equals("*") && delString == false)
				System.out.println(id + " " + cname + " " + drug_id + " " + trials+ " " + patients + " " + dosage + " " + reading + " " + db+ " " + cs + " " + gf + " " + fa);
			//else
				//System.out.println("No such records");
		} catch (Exception e) {
			System.out.println("Error reading file " +dbFile);
		}
	}
	public static int byteArray2Int(byte[] bytes, int k) {
		if (k == 1) {
			return (bytes[0] & 0xFF);
		} else if (k == 2) {
			return (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
		} else {
			return bytes[3] << 24 | (bytes[2] & 0xFF) << 16
					| (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
		}
	}

	public static float byteArray2Float(byte[] bytes) {
		float f = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
		return f;
	}

	public String byte2String (byte[] bytes) {
		String s = new String(bytes);
		return s;
	}
}