package zhuboss.pc2server.sengine.clientserver;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TranficStatis {
	private static final long PERIOD_DAY = TimeUnit.DAYS.toMillis(1l); //一天
	static{
		Calendar calendar = Calendar.getInstance(); 
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date date=calendar.getTime(); //第一次执行定时任务的时间
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				TranficStatis.getInstance().map.clear();
			}
		}, date, PERIOD_DAY);
	}
	
	static TranficStatis tranficStatis = new TranficStatis() ;
	public static TranficStatis getInstance(){
		return tranficStatis;
	}
	Map<String,AtomicLong> map = new ConcurrentHashMap<String,AtomicLong>();
	
	public void add(int userId,int size){
		String key = userId+"";
		AtomicLong cnt = map.get(key);
		if(cnt == null){
			cnt = new AtomicLong(size);
			map.put(key, cnt);
		}else{
			cnt.addAndGet(size);
		}
	}
	
	public long get(int userId){
		String key = userId+"";
		 return map.get(key)==null ? 0:map.get(key).longValue();
	}
	
	public Map<String,AtomicLong> getMap(){
		return this.map;
	}
}
