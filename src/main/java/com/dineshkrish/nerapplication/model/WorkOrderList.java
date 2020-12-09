package com.dineshkrish.nerapplication.model;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderList {

	private List<WorkOrder> workorders;

	public WorkOrderList() {
		workorders = new ArrayList<>();
	}

	public List<WorkOrder> getWorkorders() {
		return workorders;
	}

	public void setWorkorders(List<WorkOrder> workorders) {
		this.workorders = workorders;
	}

}
