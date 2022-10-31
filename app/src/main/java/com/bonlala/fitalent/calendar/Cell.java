package com.bonlala.fitalent.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Cell {
	private static final String TAG = Cell.class.getSimpleName();
	private Rect mBound = null;
	private int mDayOfMonth = 1; // from 1 to 31
	/**
	 * 描述：有记录的时候，起时时间
	 */
	private int startTime;
	
	private int dateTime;

	private String dateStr;

	/**
	 * 描述：睡眠结果: 0-->无睡眠; 1-->bad; 2-->General; 3-->good; 4-->supper
	 */
	private int score;
	
	/**
	 * 描述：该天 是否有 暂停
	 */
	private boolean isPause;
	private int dx, dy;
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
	protected Paint mCirclePaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public int getScore() {
		return score;
	}

	public Cell(int dayOfMon, Rect rect, float textSize, boolean bold) {
		mDayOfMonth = dayOfMon;
		mBound = rect;
		mPaint.setTextSize(textSize/* 26f */);
		mPaint.setColor(Color.WHITE);
		mCirclePaint.setStyle(Paint.Style.FILL);
		// Typeface font = Typeface.create(SleepConfig.SLEEPFONT,Typeface.NORMAL);
		// mPaint.setTypeface(font);
		if (bold)
			mPaint.setFakeBoldText(true);
		
		String text = (mDayOfMonth < 10) ? ("0" + String.valueOf(mDayOfMonth)) : String.valueOf(mDayOfMonth);
		dx = (int) mPaint.measureText(text) / 2;
		dy = (int) ((mPaint.descent() - mPaint.ascent()) / 3f);
	}

	private String week;

	public Cell(String week, Rect rect, float textSize, boolean bold) {
		this.week = week;
		mBound = rect;
		mPaint.setTextSize(textSize/* 26f */);
		mPaint.setColor(Color.WHITE);
		// Typeface font = Typeface.create(SleepConfig.SLEEPFONT,Typeface.NORMAL);
		// mPaint.setTypeface(font);
		if (bold)
			mPaint.setFakeBoldText(true);
		
		dx = (int) mPaint.measureText(week) / 2;
		dy = (int) ((mPaint.descent()-mPaint.ascent()) / 2.6f);
		mDayOfMonth = -1;
	}

	public Cell(int dayOfMon, Rect rect, float textSize) {
		this(dayOfMon, rect, textSize, false);
	}

	protected void draw(Canvas canvas) {
		if (mDayOfMonth == -1) {
			canvas.drawText(week, mBound.centerX() - dx, mBound.centerY() + dy, mPaint);
		} else if (mDayOfMonth < 10) {
			canvas.drawText("0" + String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy, mPaint);
		} else {
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy, mPaint);
		}
	}

	public int getDayOfMonth() {
		return mDayOfMonth;
	}

	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y);
	}

	public Rect getBound() {
		return mBound;
	}

	@Override
	public String toString() {
		return String.valueOf(mDayOfMonth) + "(" + mBound.toString() + ")";
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public int getDateTime() {
		return dateTime;
	}

	public void setDateTime(int dateTime) {
		this.dateTime = dateTime;
	}

	

}




