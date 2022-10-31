package com.bonlala.fitalent.calendar;


public class Summary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private long uid;
	
	private String sn;

	// 开始的时间戳(s)
	private int startTime;
	
	// 记录的条目数
	private int recordCount;

	// 该条数据的年份
	private short year;

	// 该条数据的月份
	private byte month;

	// 该条数据的日期
	private byte day;
	
	private byte score;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getSN() {
		return sn;
	}

	public void setSN(String sn) {
		this.sn = sn;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public short getYear() {
		return year;
	}

	public void setYear(short year) {
		this.year = year;
	}

	public byte getMonth() {
		return month;
	}

	public void setMonth(byte month) {
		this.month = month;
	}

	public byte getDay() {
		return day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

	public byte getScore() {
		return score;
	}

	public void setScore(byte score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Summary [id=" + id + ", uid=" + uid +", sn=" + sn + ", startTime=" + startTime + ", recordCount="
				+ recordCount + ", year=" + year + ", month=" + month + ", day=" + day + "]";
	}

	

}













