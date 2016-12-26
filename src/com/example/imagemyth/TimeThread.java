package com.example.imagemyth;

import android.os.Bundle;
import android.os.Message;

public class TimeThread implements Runnable {

	private int minute;

	private int second;

	private boolean flag;

	private Game game;

	public TimeThread(Game game) {
		super();
		this.game = game;
	}

	@Override
	public void run() {

		flag = true;

		this.minute = 0;

		this.second = 0;

		while (flag) {

			this.second++;

			if (this.second == 60) {
				this.minute++;
				this.second = 0;
			}

			Bundle bundle = new Bundle();

			bundle.putString("msg", this.getString());

			Message message = new Message();

			message.setData(bundle);

			message.what = 0;

			game.getTimerHander().sendMessage(message);

			// 如果游戏赢 了 则停止计时
			if (this.game.isWinFlag()) {
				this.flag = false;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}
	}

	public String getString() {
		StringBuilder builder = new StringBuilder();

		if (this.minute < 10) {
			builder.append("0");
		}

		builder.append(this.minute);

		builder.append(" : ");

		if (this.second < 10) {
			builder.append("0");
		}

		builder.append(this.second);

		return builder.toString();
	}

	/**
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @param minute
	 *            the minute to set
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * @return the second
	 */
	public int getSecond() {
		return second;
	}

	/**
	 * @param second
	 *            the second to set
	 */
	public void setSecond(int second) {
		this.second = second;
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

}
