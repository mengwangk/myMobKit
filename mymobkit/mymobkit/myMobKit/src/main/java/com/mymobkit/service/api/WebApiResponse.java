package com.mymobkit.service.api;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;

public abstract class WebApiResponse {

	public static final String PARAM_TO = "To";
	public static final String PARAM_FROM = "From";
	public static final String PARAM_DATE_SENT = "DateSent";
	public static final String PARAM_PAGE = "Page";
	public static final String PARAM_PAGE_SIZE = "PageSize";
	
	private int page = 0;
	private int numPages = 0;
	private int pageSize = 0;
	private int total = 0;
	private int start = 0;
	private int end = 0;

	@Expose
	protected String description = "";
	@Expose
	protected boolean isSuccessful = true;
	@Expose
	protected RequestMethod requestMethod;
	
	protected WebApiResponse(final RequestMethod method){
		this.requestMethod = method;
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getNumPages() {
		return numPages;
	}
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSuccessful() {
		return isSuccessful;
	}
	public void isSuccessful(boolean isSuccess) {
		this.isSuccessful = isSuccess;
	}

}
