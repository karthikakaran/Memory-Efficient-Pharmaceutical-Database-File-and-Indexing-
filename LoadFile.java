import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.TreeMap;

public class LoadFile {
    public IndividualRecord trecord = new IndividualRecord();
    public File dbFile;
    public RandomAccessFile randomAccess;
    public IndexSortedRecords recs;
    public String tableName;
    
    public LoadFile() {
	}
	public LoadFile(String fileName, String tableName) {
		this.tableName = tableName;
		loadDbFile(fileName);
	}
	public void loadDbFile(String fileNameInp){
        String fileName = fileNameInp;
        String record = null; long position = 0;
        try {
        	recs = new IndexSortedRecords();
        	dbFile = new File(tableName+".db");
        	if (dbFile.exists()) dbFile.delete();
        	dbFile.createNewFile();
            randomAccess = new RandomAccessFile(dbFile, "rw");
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            record = bufferedReader.readLine();
            while((record = bufferedReader.readLine()) != null) {
            	trecord = processFileToLoad(record);
            	makeIndexFileRecords(recs, trecord, position);
            	position = position + trecord.getCompanyName().length() + 22;
            	writeToFile(trecord);
            	createFieldIndexFiles(recs, tableName);
            }
            bufferedReader.close();
            System.out.println("Loaded the file");
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
        }
	}
	public void makeIndexFileRecords(IndexSortedRecords recs, IndividualRecord trec, long position) {
		if(recs.getId().containsKey(trec.getId())){
			return;
		}else{
			recs.getId().put(trec.getId(), position);	
		}
		if(recs.getCompanyName().containsKey(trec.getCompanyName())){
			recs.getCompanyName().get(trec.getCompanyName()).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getCompanyName().put(trec.getCompanyName(), al);
		}
		if(recs.getDrugId().containsKey(trec.getCompanyDetails()[0])){
			recs.getDrugId().get(trec.getCompanyDetails()[0]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getDrugId().put(trec.getCompanyDetails()[0], al);
		}
		if(recs.getTrials().containsKey(trec.getCompanyDetails()[1])){
			recs.getTrials().get(trec.getCompanyDetails()[1]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getTrials().put(trec.getCompanyDetails()[1], al);
		}
		if(recs.getPatients().containsKey(trec.getCompanyDetails()[2])){
			recs.getPatients().get(trec.getCompanyDetails()[2]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getPatients().put(trec.getCompanyDetails()[2], al);
		}
		if(recs.getDosageMg().containsKey(trec.getCompanyDetails()[3])){
			recs.getDosageMg().get(trec.getCompanyDetails()[3]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getDosageMg().put(trec.getCompanyDetails()[3], al);
		}
		if(recs.getReading().containsKey(trec.getCompanyDetails()[4])){
			recs.getReading().get(trec.getCompanyDetails()[4]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getReading().put(trec.getCompanyDetails()[4], al);
		}
		if(recs.getDoubleBlind().containsKey(trec.getCompanyDetails()[5])){
			recs.getDoubleBlind().get(trec.getCompanyDetails()[5]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getDoubleBlind().put(trec.getCompanyDetails()[5], al);
		}
		if(recs.getControlledStudy().containsKey(trec.getCompanyDetails()[6])){
			recs.getControlledStudy().get(trec.getCompanyDetails()[6]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getControlledStudy().put(trec.getCompanyDetails()[6], al);
		}
		if(recs.getGovtFunded().containsKey(trec.getCompanyDetails()[7])){
			recs.getGovtFunded().get(trec.getCompanyDetails()[7]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getGovtFunded().put(trec.getCompanyDetails()[7], al);
		}
		if(recs.getFdaApproved().containsKey(trec.getCompanyDetails()[8])){
			recs.getFdaApproved().get(trec.getCompanyDetails()[8]).add(position);
		} else {
			ArrayList<Long> al = new ArrayList<Long>();
			al.add(position);
 			recs.getFdaApproved().put(trec.getCompanyDetails()[8], al);
		}
		if (trec.getDelFlag().equals("true")){
			if(recs.getDelFlag().containsKey("true"))
				if (recs.getDelFlag().get("true").contains(position)) {
					recs.getDelFlag().get("true").remove(position);}
		}else{
			if(recs.getDelFlag().containsKey("false")){
				if(recs.getDelFlag().get("false").size() > 0)
					recs.getDelFlag().get("false").add(position);
			} else {
				ArrayList<Long> al = new ArrayList<Long>();
				al.add(position);
				recs.getDelFlag().put("false", al);
			}
		}			
	}
	public void createFieldIndexFiles(IndexSortedRecords recs, String tableName) {
		File idIndFile = new File(tableName+".id.ndx");
		writeToIndexFileId(idIndFile, recs.getId());
		File compIndFile = new File(tableName+".company.ndx");
		writeToIndexFile(compIndFile, recs.getCompanyName());
		File drugIdIndFile = new File(tableName+".drug_id.ndx");
		writeToIndexFile(drugIdIndFile, recs.getDrugId());
		File trailIndFile = new File(tableName+".trials.ndx");
		writeToIndexFile(trailIndFile, recs.getTrials());
		File patIndFile = new File(tableName+".patients.ndx");
		writeToIndexFile(patIndFile, recs.getPatients());
		File dosIndFile = new File(tableName+".dosage_mg.ndx");
		writeToIndexFile(dosIndFile, recs.getDosageMg());
		File readIndFile = new File(tableName+".reading.ndx");
		writeToIndexFile(readIndFile, recs.getReading());
		File dbIndFile = new File(tableName+".double_blind.ndx");
		writeToIndexFile(dbIndFile, recs.getDoubleBlind());
		File csIndFile = new File(tableName+".controlled_study.ndx");
		writeToIndexFile(csIndFile, recs.getControlledStudy());
		File gfIndFile = new File(tableName+".govt_funded.ndx");
		writeToIndexFile(gfIndFile, recs.getGovtFunded());
		File faIndFile = new File(tableName+".fda_approved.ndx");
		writeToIndexFile(faIndFile, recs.getFdaApproved());
		File delFlagIndexFile = new File(tableName+".del_flag.ndx");
		writeToIndexFile(delFlagIndexFile, recs.getDelFlag());
	}
	public void writeToIndexFileId(File indexFile, TreeMap<String, Long> tMapId) {
		try {
			FileOutputStream foutStrem = new FileOutputStream(indexFile);
			ObjectOutputStream os = new ObjectOutputStream(foutStrem);
			os.writeObject(tMapId);
			foutStrem.close();
		}catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + indexFile + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + indexFile + "'");                  
        }
	}
	public void writeToIndexFile(File indexFile, TreeMap<String, ArrayList<Long>> tMapEle) {
		try {
			FileOutputStream foutStrem = new FileOutputStream(indexFile);
			ObjectOutputStream os = new ObjectOutputStream(foutStrem);
			os.writeObject(tMapEle);
			foutStrem.close();
		}catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + indexFile + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + indexFile + "'");                  
        }
	}
	public IndividualRecord processFileToLoad(String record){
		if(record.contains("\"")){
			trecord.setId(record.substring(0, record.indexOf(",")));
			trecord.setCompanyName(record.substring(record.indexOf(",")+2, record.lastIndexOf("\"")));
			record = record.substring(record.lastIndexOf("\"")+2, record.length());
			trecord.setCompanyDetails(record.split(","));
		} else{
			trecord.setId(record.substring(0, record.indexOf(",")));
			record = record.substring(record.indexOf(",") + 1);
			trecord.setCompanyName(record.substring(0, record.indexOf(",")));
			record = record.substring(record.indexOf(",") + 1);
			trecord.setCompanyDetails(record.split(","));
		}
		trecord.setDelFlag("false");
		return trecord;
	}
	public void writeToFile(IndividualRecord tr) {
		try {
			randomAccess.write(convertInt2Byte(Integer.parseInt(tr.getId()), 4));
			randomAccess.write(convertInt2Byte(tr.getCompanyName().length(), 1));
			randomAccess.write(convertStringToByte(tr.getCompanyName()));
			randomAccess.write(convertStringToByte(tr.getCompanyDetails()[0]));
			randomAccess.write(convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[1]), 2));
			randomAccess.write(convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[2]), 2));
			randomAccess.write(convertInt2Byte(Integer.parseInt(tr.getCompanyDetails()[3]), 2));
			randomAccess.write(convertFloatToByte(Float.parseFloat(tr.getCompanyDetails()[4])));
			randomAccess.write(convertBooleanToByte(Boolean.parseBoolean(tr.getDelFlag()),Boolean.parseBoolean(tr.getCompanyDetails()[5]),
					Boolean.parseBoolean(tr.getCompanyDetails()[6]),
					Boolean.parseBoolean(tr.getCompanyDetails()[7]),
					Boolean.parseBoolean(tr.getCompanyDetails()[8])));
		} catch (NumberFormatException | IOException e) {
			System.out.println("Error writing or opening db file");
		}
	}
	public byte[] convertInt2Byte(int intValue, int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) (intValue >>> (i * 8));
		}
		return bytes;
	}
	
	public byte[] convertBooleanToByte(boolean delFlag, boolean db, boolean cs, boolean gf, boolean fa) {
		int val = 0;
		if (delFlag) val = 240;
		if (db) val = val + 8;
		if (cs) val = val + 4;
		if (gf) val = val + 2;
		if (fa) val = val + 1;
		return convertInt2Byte(val, 1);
	}
	public byte[] convertStringToByte(String strValue) {
		char[] charArray = strValue.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		return byteArray;
	}
	public byte[] convertFloatToByte(Float floatVal) {
		return ByteBuffer.allocate(4).putFloat(floatVal).array();
	}
}