package com.cisco.client.model;

import java.util.List;

public class PPTGridData {

	String current;
	String rowCount;
	List<PPTInformation> rows;
	String total;

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public List<PPTInformation> getRows() {
		return rows;
	}

	public void setRows(List<PPTInformation> rows) {
		this.rows = rows;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

}
