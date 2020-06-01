package org.deltaverse.saf;

import org.json.JSONObject;

import java.io.Serializable;

public class Data_Holder implements Serializable {
	private JSONObject data_Object;
	public Data_Holder(JSONObject data_Object){
		this.data_Object = data_Object;
	}

	public JSONObject getData_Object() {
		return data_Object;
	}

	public void setData_Object(JSONObject data_Object) {
		this.data_Object = data_Object;
	}
}
