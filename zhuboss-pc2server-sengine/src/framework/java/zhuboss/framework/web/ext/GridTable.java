package zhuboss.framework.web.ext;

import java.util.List;

public class GridTable<T> {
	private List<T> rows;
	private Integer total = 0;
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
}
