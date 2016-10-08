package zhuboss.framework.util.pagging;

import java.util.List;

public class WwwPageModel<T> extends PageModel<T> {
	
	int start,end;
	
	public WwwPageModel(List<T> list, 
			int totalRecords, 
			int pageSize,
			int showPages, 
			int curPageNo) {
		
		this.setList(list);
		this.setTotalRecords(totalRecords);
		this.setPageSize(pageSize);
		this.setPageNo(curPageNo);
		cal(showPages);
		
	}
	
	private void cal(int showPages){
		boolean lEnd = false, rEnd =false;
		final int SHOW_PAGES_HALF_1 = showPages/2;
		final int SHOW_PAGES_HALF_2 = showPages%2 ==0 ? (SHOW_PAGES_HALF_1-1) : SHOW_PAGES_HALF_1;
		int curPageNo = this.getPageNo();
		int totalPage = this.getTotalPages();
		if(totalPage==0) totalPage = 1; //避免在页面显示第0页，没有记录也是一页
		
		if(curPageNo > SHOW_PAGES_HALF_1){
			start=curPageNo - SHOW_PAGES_HALF_1;
		}else{
			start = 1;
			lEnd = true;
		}
		//right
		if(totalPage > curPageNo + SHOW_PAGES_HALF_2){
			end = curPageNo + SHOW_PAGES_HALF_2;
		}else{
			end = totalPage;
			rEnd = true;
		}
		//
		if(end - start < showPages - 1){
			if(lEnd == false){
				start = (end - showPages > 0)? (end - showPages) : 1;
			}else if(rEnd == false){
				end = (start + showPages -1  > totalPage) ? totalPage : (start + showPages -1);
			}
		}
	}
	
	public static void main(String[] args){
		WwwPageModel test = new WwwPageModel(null,1000,10,10,8);
		System.out.println(test.getStart());
		System.out.println(test.getEnd());
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

	
}
