package com.cisco.client.model;

import java.util.LinkedHashMap;
import java.util.List;

public class PPTGridData {

	String current;
	String rowCount;
	List<LinkedHashMap> rows;
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

	public List<LinkedHashMap> getRows() {
		return rows;
	}

	public void setRows(List<LinkedHashMap> rows) {
		this.rows = rows;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

}
