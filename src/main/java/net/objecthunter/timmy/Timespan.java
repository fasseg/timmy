package net.objecthunter.timmy;

import java.util.Date;

public class Timespan {
	private Date date;
	private int startTime;
	private int endTime;
	private int pause;
	private int amount;

	public Timespan() {
		super();
	}

	public Timespan(Date date, int startTime, int endTime, int pause, int amount) {
		super();
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.pause = pause;
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getPause() {
		return pause;
	}

	public int getAmount() {
		return amount;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void setPause(int pause) {
		this.pause = pause;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
