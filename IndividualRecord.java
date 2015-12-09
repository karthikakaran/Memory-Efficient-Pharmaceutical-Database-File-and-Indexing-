public class IndividualRecord {
	private String id;
	private String companyName;
	private String companyDetails[];
	private String delFlag;	
	
	public IndividualRecord() {
	
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String[] getCompanyDetails() {
		return companyDetails;
	}
	public void setCompanyDetails(String[] companyDetails) {
		this.companyDetails = companyDetails;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
}
