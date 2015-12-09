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

public class DeleteRecord {
	
	public IndexSortedRecords recs = new IndexSortedRecords();
	public File dbFile;
    public RandomAccessFile randomAccess;
    public LoadFile lf = new LoadFile();
    public QueryRecord qRec = new QueryRecord();
    public IndividualRecord trecord = new IndividualRecord();
    public String tableName;
    public ArrayList<Long> delPosList = new ArrayList<Long>();
    Scanner in = new Scanner(System.in);
    
	public DeleteRecord(String tableName) {
		try{
			this.tableName = tableName;
			String fieldfName = null;
			String operation = null;
			String valueToDel = null;
			System.out.println("\nEnter Field Name: ");
			fieldfName = in.next();
			System.out.println("Enter Operation < or > or <= or >= or = : ");
			operation = in.next();
			System.out.println("Enter Value: ");
			valueToDel = in.next();
			System.out.println("Do you want to perform NOT operation y/n:");
			if(in.next().equalsIgnoreCase("Y")){
				System.out.println("delete from "+tableName+" where "+fieldfName+" NOT "+operation+" "+valueToDel+";");
				readFromIndexFileIdNotOper(fieldfName.trim(), operation.trim(), valueToDel.trim());
			} else{
				System.out.println("delete from "+tableName+" where "+fieldfName+" "+operation+" "+valueToDel+";");
				readFromIndexFileId(fieldfName.trim(), operation.trim(), valueToDel.trim());
			}
			
			setDelFlagInFile(delPosList);
			System.out.println("Deleted records");
		} catch(NullPointerException | StringIndexOutOfBoundsException e){
			System.out.println("Enter proper query, ENLCOSE values with (') single quotes");
		}
	}
	private void readFromIndexFileIdNotOper(String fieldfName, String operation, String valueToDel) {
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
        } catch(IOException | ClassNotFoundException ex) {
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
        } catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");           
        } catch (ClassNotFoundException e) {
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
			id = convertByteArray2Int(idb, 4); 
			removePosFromIndexFiles("id", String.valueOf(id), pos_file);
			randomAccess.read(idb, 0, 1);
			size = convertByteArray2Int(idb, 1);
			byte[] cn = new byte[size];
			randomAccess.read(cn, 0, size);
			cname = byte2String (cn);
			removePosFromIndexFiles("company", cname, pos_file);
			randomAccess.read(drug, 0, 6);
			drug_id = byte2String (drug);
			removePosFromIndexFiles("drug_id", drug_id, pos_file);
			randomAccess.read(idb, 0, 2);
			trials = convertByteArray2Int(idb, 2);
			removePosFromIndexFiles("trials", String.valueOf(trials), pos_file);
			randomAccess.read(idb, 0, 2);
			patients = convertByteArray2Int(idb, 2);
			removePosFromIndexFiles("patients", String.valueOf(patients), pos_file);
			randomAccess.read(idb, 0, 2);
			dosage = convertByteArray2Int(idb, 2);
			removePosFromIndexFiles("dosage_mg", String.valueOf(dosage), pos_file);
			randomAccess.read(flt, 0, 4);
			reading = convertByteArray2Float(flt);
			removePosFromIndexFiles("reading", String.valueOf(reading), pos_file);
			bol = randomAccess.readByte();
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
			String delFlag = String.format("%8s", Integer.toBinaryString(bol & 0xFF)).substring(0,4), delString;
			if(delFlag.equalsIgnoreCase("1111"))
				delString = "true";
			else
				delString = "false";
			removePosFromIndexFiles("del_flag", String.valueOf(delString), pos_file);
			removePosFromIndexFiles("double_blind", String.valueOf(db), pos_file);
			removePosFromIndexFiles("controlled_study", String.valueOf(cs), pos_file);
			removePosFromIndexFiles("govt_funded", String.valueOf(gf), pos_file);
			removePosFromIndexFiles("fda_approved", String.valueOf(fa), pos_file);
			delPosList.add(pos_file);
			lf.createFieldIndexFiles(recs, tableName);
		} catch (Exception e) {
			System.out.println("Error reading file '" + dbFile + "'"); 
		}
	}
	 public void setDelFlagInFile(ArrayList<Long> pos_file){
		 byte[] idb = new byte[50];	 byte[] drug = new byte[6]; byte[] flt = new byte[4];
		 int size, id, trials, patients, dosage;float reading; String drug_id;
		 byte bol, double_blind_mask = 0x08, controlled_study_mask = 0x04, govt_funded_mask = 0x02, fda_approved_mask = 0x01;
		 boolean db = false, cs = false, gf = false, fa = false, delBool;
		 IndividualRecord tr = new IndividualRecord();
		 try{
			File dbFileD = new File(tableName+".db");
			RandomAccessFile randomAccessD = new RandomAccessFile(dbFileD, "rw");
			File dbTemp = new File("temp"+tableName+".db");
			dbTemp.createNewFile();
			RandomAccessFile ranTemp = new RandomAccessFile(dbTemp, "rw");
			long pos = 0; String cname;
			while(pos <= randomAccessD.length() - 22){
				randomAccess.seek(pos);
				randomAccess.read(idb, 0, 4);
				id = QueryRecord.byteArray2Int(idb, 4);
				randomAccess.read(idb, 0, 1);
				size = QueryRecord.byteArray2Int(idb, 1);
				byte[] cn = new byte[size];
				randomAccess.read(cn, 0, size);
				cname = qRec.byte2String (cn);
				randomAccess.read(drug, 0, 6);
				drug_id = qRec.byte2String (drug);
				randomAccess.read(idb, 0, 2);
				trials = QueryRecord.byteArray2Int(idb, 2);
				randomAccess.read(idb, 0, 2);
				patients = QueryRecord.byteArray2Int(idb, 2);
				randomAccess.read(idb, 0, 2);
				dosage = QueryRecord.byteArray2Int(idb, 2);
				randomAccess.read(flt, 0, 4);
				reading = QueryRecord.byteArray2Float(flt);
				bol = randomAccess.readByte();
				if (pos_file.contains(pos)) {
					delBool = true;
				} else {
					String delFlag = String.format("%8s", Integer.toBinaryString(bol & 0xFF)).substring(0,4); 
					if(delFlag.equalsIgnoreCase("1111"))
						delBool = true;
					else
						delBool = false;
				}
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
				
				tr.setId(String.valueOf(id));
				tr.setCompanyName(cname);
				String compDet[] = {String.valueOf(drug_id), String.valueOf(trials), String.valueOf(patients), String.valueOf(dosage), String.valueOf(reading), String.valueOf(db), String.valueOf(cs), String.valueOf(gf), String.valueOf(fa)};
				tr.setCompanyDetails(compDet);
				tr.setDelFlag(String.valueOf(delBool));
				//if(delBool == true)
				//	lf.makeIndexFileRecords(recs, tr, pos);
				pos = pos + cname.length() + 22;
				ranTemp.write(lf.convertInt2Byte(Integer.parseInt(tr.getId()), 4));
				ranTemp.write(lf.convertInt2Byte(tr.getCompanyName().length(), 1));
				ranTemp.write(lf.convertStringToByte(tr.getCompanyName()));
				ranTemp.write(lf.convertStringToByte(tr.getCompanyDetails()[0]));
				ranTemp.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[1]), 2));
				ranTemp.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[2]), 2));
				ranTemp.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[3]), 2));
				ranTemp.write(lf.convertFloatToByte(Float.parseFloat(tr.getCompanyDetails()[4])));
				ranTemp.write(lf.convertBooleanToByte(Boolean.parseBoolean(tr.getDelFlag()),Boolean.parseBoolean(tr.getCompanyDetails()[5]),
						Boolean.parseBoolean(tr.getCompanyDetails()[6]),
						Boolean.parseBoolean(tr.getCompanyDetails()[7]),
						Boolean.parseBoolean(tr.getCompanyDetails()[8])));
			}
			
			randomAccessD.close();
			ranTemp.close();
			dbFileD.delete();
			dbTemp.renameTo(dbFileD);
			
		} catch(FileNotFoundException ex) {
	            System.out.println("Unable to open file");                
	    } catch(IOException ex) {
	            System.out.println("Error reading file");                  
	    }
    }
	
	public static int convertByteArray2Int(byte[] bytes, int k) {
		if (k == 1) {
			return (bytes[0] & 0xFF);
		} else if (k == 2) {
			return (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
		} else {
			return bytes[3] << 24 | (bytes[2] & 0xFF) << 16
					| (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
		}
	}

	public static float convertByteArray2Float(byte[] bytes) {
		float f = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
		return f;
	}

	public String byte2String (byte[] bytes) {
		String s = new String(bytes);
		return s;
	}
	public void removePosFromIndexFiles(String fieldfName, String iterNextValue, long position) {
		try {
			String fileName = tableName+"."+fieldfName+".ndx";
			FileInputStream finStrem = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(finStrem);
			
			if (fieldfName.equals("id")){
				TreeMap<String, Long> tMap = new TreeMap<String, Long>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, Long>) is.readObject();
					recs.setId(tMap);
					if(recs.getId().containsKey(iterNextValue)){
						if (recs.getId().containsValue(position)){
							recs.getId().remove(iterNextValue);
						}
					}
				}			
			} else if (fieldfName.equals("company")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setCompanyName(tMap);
					if(recs.getCompanyName().containsKey(iterNextValue)){
						if (recs.getCompanyName().get(iterNextValue).contains(position))recs.getCompanyName().get(iterNextValue).remove(position);
							//recs.getCompanyName().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("drug_id")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setDrugId(tMap);
					if(recs.getDrugId().containsKey(iterNextValue)){
						if (recs.getDrugId().get(iterNextValue).contains(position))recs.getDrugId().get(iterNextValue).remove(position);
							//recs.getDrugId().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("trials")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setTrials(tMap);
					if(recs.getTrials().containsKey(iterNextValue)){
						if (recs.getTrials().get(iterNextValue).contains(position))recs.getTrials().get(iterNextValue).remove(position);
					//		recs.getTrials().get(iterNextValue).clear();}
					}
				}
			} else if (fieldfName.equals("patients")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setPatients(tMap);
					if(recs.getPatients().containsKey(iterNextValue)){
						if (recs.getPatients().get(iterNextValue).contains(position))recs.getPatients().get(iterNextValue).remove(position);
							//recs.getPatients().get(iterNextValue).clear();}
					}
				}
			} else if (fieldfName.equals("dosage_mg")){
					TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
					while(finStrem.available() > 0){
						tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
						recs.setDosageMg(tMap);
						if(recs.getDosageMg().containsKey(iterNextValue)){
							if (recs.getDosageMg().get(iterNextValue).contains(position))recs.getDosageMg().get(iterNextValue).remove(position);
								//recs.getDosageMg().get(iterNextValue).clear(); }
						}
					}
			} else if (fieldfName.equals("reading")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setReading(tMap);
					if(recs.getReading().containsKey(iterNextValue)){
						if (recs.getReading().get(iterNextValue).contains(position))recs.getReading().get(iterNextValue).remove(position);
							//recs.getReading().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("double_blind")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setDoubleBlind(tMap);
					if(recs.getDoubleBlind().containsKey(iterNextValue)){
						if (recs.getDoubleBlind().get(iterNextValue).contains(position))recs.getDoubleBlind().get(iterNextValue).remove(position);
							//recs.getDoubleBlind().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("controlled_study")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setControlledStudy(tMap);
					if(recs.getControlledStudy().containsKey(iterNextValue)){
						if (recs.getControlledStudy().get(iterNextValue).contains(position))recs.getControlledStudy().get(iterNextValue).remove(position);
							//recs.getControlledStudy().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("govt_funded")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setGovtFunded(tMap);
					if(recs.getGovtFunded().containsKey(iterNextValue)){
						if (recs.getGovtFunded().get(iterNextValue).contains(position))recs.getGovtFunded().get(iterNextValue).remove(position);
							//recs.getGovtFunded().get(iterNextValue).clear(); }
					}
				}
			} else if (fieldfName.equals("fda_approved")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setFdaApproved(tMap);
					if(recs.getFdaApproved().containsKey(iterNextValue)){
						if (recs.getFdaApproved().get(iterNextValue).contains(position)){
							recs.getFdaApproved().get(iterNextValue).remove(position);
							//recs.getFdaApproved().get(iterNextValue).clear(); 
						}
					}
				}
			} else if (fieldfName.equals("del_flag")){
				TreeMap<String, ArrayList<Long>> tMap = new TreeMap<String, ArrayList<Long>>();
				while(finStrem.available() > 0){
					tMap = (TreeMap<String, ArrayList<Long>>) is.readObject();
					recs.setDelFlag(tMap);
					
					if (iterNextValue.equals("false")){
						if(recs.getDelFlag().containsKey("false"))
							if (recs.getDelFlag().get("false").contains(position)) {
								recs.getDelFlag().get("false").remove(position);}
						if(recs.getDelFlag().containsKey("true")){
							if(recs.getDelFlag().get("true").size() > 0)
								recs.getDelFlag().get("true").add(position);
						} else {
							ArrayList<Long> al = new ArrayList<Long>();
							al.add(position);
							recs.getDelFlag().put("true", al);
						}
					}
				}
		   }
			finStrem.close(); is.close();
	  }catch(ClassNotFoundException | IOException e){
		  System.out.println("Error reading "+fieldfName);
	  }
	}
}