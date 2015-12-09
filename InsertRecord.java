import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class InsertRecord {
    
	public File dbFile;
    public RandomAccessFile randomAccess;
    public LoadFile lf = new LoadFile();
    public IndividualRecord frecord = new IndividualRecord();
    public QueryRecord qRec = new QueryRecord();
    public String tableName;
    public IndividualRecord trecord = new IndividualRecord();
    
    public InsertRecord(String query, String tableName) {
    	this.tableName = tableName;
    	try{
	    	query = query.substring(query.indexOf("(")+1,query.indexOf(")"));
			String id = query.substring(0, query.indexOf(","));
			query = query.substring(id.length() + 1);
			if (id.contains("'"))
				id = id.substring(1, id.length()-1);
			trecord.setId(id);
			
			String compName = query.substring(1, query.indexOf("'",2));
			trecord.setCompanyName(compName);
			query = query.substring(query.indexOf("'",2) + 2);
			
			String[] details = query.split(",");
			String[] tempDetails = {"0","0","0","0","0","0","0","0","0"}; int i = 0;
			for (String string : details) {
				if (string.contains("'"))
					tempDetails[i] = string.substring(1, string.length()-1).trim();
				else
					tempDetails[i] = string.trim();
				i++;
			}
			trecord.setCompanyDetails(tempDetails);
			trecord.setDelFlag("false");
			
			int returnFlag = readRecords(trecord, true);
			if (returnFlag == 0){
				appendToFile(trecord);
				returnFlag = readRecords(trecord, false);
				System.out.println("Record Inserted");
			}				
    	} catch(NullPointerException | NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e){ e.printStackTrace();
			System.out.println("Enter proper query, ENLCOSE values with (') single quotes, Add (;) at end, Dont give spaces in between fields");
		}
	}
    public int readRecords(IndividualRecord trecord, boolean firstFlag){
    	try {
        	long position = 0;
        	IndexSortedRecords recs = new IndexSortedRecords();
        	//randomAccess.close();
        	dbFile = new File(tableName+".db");
        	randomAccess = new RandomAccessFile(dbFile, "rw");
            FileInputStream finStrem = new FileInputStream(dbFile);
			while(position <= randomAccess.length() - 22){
            	fetchDbRecords(position);
            	if(frecord.getId().equals(trecord.getId()) && frecord.getDelFlag().equals("false") && firstFlag == true){
            		System.out.println("ID already exists");
            		return 1;
            	}
            	if(frecord.getId().equals(trecord.getId()) && frecord.getDelFlag().equals("true")){}
            	else
            		lf.makeIndexFileRecords(recs, frecord, position);
            	position = position + frecord.getCompanyName().length() + 22;
            }
        	lf.createFieldIndexFiles(recs, tableName);
        	randomAccess.close(); finStrem.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file");               
        }
		return 0;
    }
    public void appendToFile(IndividualRecord trecord){
        try {
        	long position = 0;
        	dbFile = new File(tableName+".db");
            randomAccess = new RandomAccessFile(dbFile, "rw");
            position = randomAccess.length();
            randomAccess.seek(position);
            writeToDBFile(trecord);
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file");                  
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
			String delFlag = String.format("%8s", Integer.toBinaryString(bol & 0xFF)).substring(0,4);
			if(delFlag.equalsIgnoreCase("1111"))
				frecord.setDelFlag("true");
			else
				frecord.setDelFlag("false");
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
			frecord.setId(String.valueOf(id));
			frecord.setCompanyName(cname);
			String compDet[] = {String.valueOf(drug_id), String.valueOf(trials), String.valueOf(patients), String.valueOf(dosage), String.valueOf(reading), String.valueOf(db), String.valueOf(cs), String.valueOf(gf), String.valueOf(fa)};
			frecord.setCompanyDetails(compDet);
			
		} catch (Exception e) {
			System.out.println("Error reading file " +dbFile);
		}
	}
    public void writeToDBFile(IndividualRecord tr) {
		try {
			randomAccess.write(lf.convertInt2Byte(Integer.parseInt(tr.getId()), 4));
			randomAccess.write(lf.convertInt2Byte(tr.getCompanyName().length(), 1));
			randomAccess.write(lf.convertStringToByte(tr.getCompanyName()));
			randomAccess.write(lf.convertStringToByte(tr.getCompanyDetails()[0]));
			randomAccess.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[1]), 2));
			randomAccess.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[2]), 2));
			randomAccess.write(lf.convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[3]), 2));
			randomAccess.write(lf.convertFloatToByte(Float.parseFloat(tr.getCompanyDetails()[4])));
			randomAccess.write(lf.convertBooleanToByte(Boolean.parseBoolean(tr.getDelFlag()),Boolean.parseBoolean(tr.getCompanyDetails()[5]),
					Boolean.parseBoolean(tr.getCompanyDetails()[6]),
					Boolean.parseBoolean(tr.getCompanyDetails()[7]),
					Boolean.parseBoolean(tr.getCompanyDetails()[8])));
			randomAccess.close();
		} catch (NumberFormatException | IOException e) {
			System.out.println("Error reading file " +dbFile);
		}
	}
}