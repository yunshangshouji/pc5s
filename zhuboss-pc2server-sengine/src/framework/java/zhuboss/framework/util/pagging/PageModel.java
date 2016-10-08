package zhuboss.framework.util.pagging;

import java.util.List;

/**
 * 分页model类
 */
public class PageModel<T> {

	private List<T> list;

	/**
	 * 总记录数
	 */
	private int totalRecords;

	/**
	 * 每页记录数
	 */
	private int pageSize = 10;

	@SuppressWarnings("unused")
    private int totalPages;

	/**
	 * 页码
	 */
	private int pageNo;

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public int getTotalPages() {

		if (pageSize < 1) {
			return 0;
		}

		return (totalRecords + (pageSize - 1)) / pageSize;
	}

	
	/**
	 * 用户
	 * @param totalPages
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	
	
	/**
	 * 上一页
	 * 
	 * @return
	 */
	public int getPrePage() {
		return pageNo - 1 < 1 ?1 : pageNo - 1;
	}

	/**
	 * 下一页
	 * 
	 * @return
	 */
	public int getNextPage() {
		return pageNo + 1 > getTotalPages()  ? getTotalPages() 
				: pageNo + 1;
	}

}
