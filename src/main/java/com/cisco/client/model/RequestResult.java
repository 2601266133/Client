package com.cisco.client.model;

import java.util.List;

public class RequestResult {

	public static final String FAILED = "Failed";
	public static final String SUCCESS = "Success";
	public static final String CODE_NOT_FOUND = "404";
	public static final String CODE_ERROR = "500";
	public static final String CODE_SUCCESS = "200";

	String result;
	String context;
	String code;
	PPTInformation pptInfo;
	List<PPTInformation> pptInfos;

	public RequestResult() {

	}

	public RequestResult(String result, String context, String code) {
		this.result = result;
		this.context = context;
		this.code = code;
	}

	public RequestResult(String result, String context, String code, PPTInformation pptInfo) {
		this.result = result;
		this.context = context;
		this.code = code;
		this.pptInfo = pptInfo;
	}

	public RequestResult(String result, String context, String code, List<PPTInformation> pptInfos) {
		this.result = result;
		this.context = context;
		this.code = code;
		this.pptInfos = pptInfos;
	}

	public List<PPTInformation> getPptInfos() {
		return pptInfos;
	}

	public void setPptInfos(List<PPTInformation> pptInfos) {
		this.pptInfos = pptInfos;
	}

	public PPTInformation getPptInfo() {
		return pptInfo;
	}

	public void setPptInfo(PPTInformation pptInfo) {
		this.pptInfo = pptInfo;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
