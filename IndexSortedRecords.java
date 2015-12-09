import java.util.ArrayList;
import java.util.TreeMap;

public class IndexSortedRecords {
	private TreeMap<String, Long> id;
	private TreeMap<String, ArrayList<Long>> companyName;
	private TreeMap<String, ArrayList<Long>> drugId;
	private TreeMap<String, ArrayList<Long>> trials;
	private TreeMap<String, ArrayList<Long>> patients;
	private TreeMap<String, ArrayList<Long>> dosageMg;
	private TreeMap<String, ArrayList<Long>> reading;
	private TreeMap<String, ArrayList<Long>> doubleBlind;
	private TreeMap<String, ArrayList<Long>> controlledStudy;
	private TreeMap<String, ArrayList<Long>> govtFunded;
	private TreeMap<String, ArrayList<Long>> fdaApproved;
	private TreeMap<String, ArrayList<Long>> delFlag;
	
	public IndexSortedRecords() {
		id = new TreeMap<String, Long>();
		companyName = new TreeMap<String, ArrayList<Long>>();
		drugId = new TreeMap<String, ArrayList<Long>>();
		trials = new TreeMap<String, ArrayList<Long>>();
		patients = new TreeMap<String, ArrayList<Long>>();
		dosageMg = new TreeMap<String, ArrayList<Long>>();
		reading = new TreeMap<String, ArrayList<Long>>();
		dosageMg = new TreeMap<String, ArrayList<Long>>();
		reading = new TreeMap<String, ArrayList<Long>>();
		doubleBlind = new TreeMap<String, ArrayList<Long>>();
		controlledStudy = new TreeMap<String, ArrayList<Long>>();
		govtFunded = new TreeMap<String, ArrayList<Long>>();
		fdaApproved = new TreeMap<String, ArrayList<Long>>();
		delFlag = new TreeMap<String, ArrayList<Long>>();
	}

	public TreeMap<String, Long> getId() {
		return id;
	}

	public void setId(TreeMap<String, Long> id) {
		this.id = id;
	}

	public TreeMap<String, ArrayList<Long>> getCompanyName() {
		return companyName;
	}

	public void setCompanyName(TreeMap<String, ArrayList<Long>> companyName) {
		this.companyName = companyName;
	}

	public TreeMap<String, ArrayList<Long>> getDrugId() {
		return drugId;
	}

	public void setDrugId(TreeMap<String, ArrayList<Long>> drugId) {
		this.drugId = drugId;
	}

	public TreeMap<String, ArrayList<Long>> getTrials() {
		return trials;
	}

	public void setTrials(TreeMap<String, ArrayList<Long>> trials) {
		this.trials = trials;
	}

	public TreeMap<String, ArrayList<Long>> getPatients() {
		return patients;
	}

	public void setPatients(TreeMap<String, ArrayList<Long>> patients) {
		this.patients = patients;
	}

	public TreeMap<String, ArrayList<Long>> getDosageMg() {
		return dosageMg;
	}

	public void setDosageMg(TreeMap<String, ArrayList<Long>> dosageMg) {
		this.dosageMg = dosageMg;
	}

	public TreeMap<String, ArrayList<Long>> getReading() {
		return reading;
	}

	public void setReading(TreeMap<String, ArrayList<Long>> reading) {
		this.reading = reading;
	}

	public TreeMap<String, ArrayList<Long>> getDoubleBlind() {
		return doubleBlind;
	}

	public void setDoubleBlind(TreeMap<String, ArrayList<Long>> doubleBlind) {
		this.doubleBlind = doubleBlind;
	}

	public TreeMap<String, ArrayList<Long>> getControlledStudy() {
		return controlledStudy;
	}

	public void setControlledStudy(TreeMap<String, ArrayList<Long>> controlledStudy) {
		this.controlledStudy = controlledStudy;
	}

	public TreeMap<String, ArrayList<Long>> getGovtFunded() {
		return govtFunded;
	}

	public void setGovtFunded(TreeMap<String, ArrayList<Long>> govtFunded) {
		this.govtFunded = govtFunded;
	}

	public TreeMap<String, ArrayList<Long>> getFdaApproved() {
		return fdaApproved;
	}

	public void setFdaApproved(TreeMap<String, ArrayList<Long>> fdaApproved) {
		this.fdaApproved = fdaApproved;
	}

	public TreeMap<String, ArrayList<Long>> getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(TreeMap<String, ArrayList<Long>> delFlag) {
		this.delFlag = delFlag;
	}
}	