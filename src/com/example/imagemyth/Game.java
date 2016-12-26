package com.example.imagemyth;

import java.util.Arrays;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;

/**
 * Game�࣬�����Ϸ���߼������� Activity
 * 
 * @version 1.0��������
 * 
 * @version 2.0 ��д����ʾʱ��� ����
 * 
 * @version 3.0
 * @author ACEr
 * @time 2014-5-13 ��д�����ֲ��ŵĴ���
 */
@SuppressLint({ "UseSparseArrays", "HandlerLeak" })
public class Game extends Activity {

	// ����ʱ��ˢ����Ļ��Handler
	private Handler timerHander;

	// ʱ����ʾ��String
	private String timeString;

	// ��
	private Vibrator vibrator;

	// �񶯵�Pattern
	private long[] pattern = { 100, 400 }; // ֹͣ ���� ֹͣ ����;

	// ��Ϸ��ʼ��String
	private String[] startStrings = { "520476189", "049865712", "629047851",
			"967152840", "190687245" };

	// ���ִ���
	private SoundPool gameSoundPool;

	// �����Ƶ�ļ�Map
	private HashMap<Integer, Integer> soundMap;

	// ���ִ�С
	private float volume;

	// ��ϷӮ�ñ��
	private boolean winFlag;
	// ��Ϸ��ͼ
	private GameView gameView;
	// ��ǰ�����X���Ƕ�ά�����±�y
	private int selX;

	// ��ǰ����� Y���Ƕ�ά�����±�x
	private int selY;

	// �������е��±�
	private int arrayIndexX;

	// �������е��±�
	private int arrayIndexY;

	private int[] imageArray = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private HashMap<Integer, Rect> imagePartMap;
	// ���ƶ��߼��Ķ�ά����
	private int[][] moveArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.init();
		gameView = new GameView(this);
		this.setContentView(gameView);
		gameView.requestFocus();
		new Thread(new TimeThread(this)).start();
	}

	/**
	 * ����ʼ������
	 */
	public void init() {

		this.timerHander = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					timeString = msg.getData().getString("msg");
					gameView.invalidate();
					break;
				default:
					break;
				}

				super.handleMessage(msg);
			}

		};

		// ����������������
		String startString = this.startStrings[(int) (Math.random() * 5)];
		// ��ԶҪ��3�ǿ�ŵ�0
		this.imageArray[0] = 3;
		for (int i = 0; i < startString.length(); i++) {
			this.imageArray[i + 1] = Integer.parseInt(startString.charAt(i)
					+ "");
		}
		System.out.println(Arrays.toString(imageArray));

		// ��ʼ����ά���鲢��ֵ
		this.moveArray = new int[4][3];
		this.moveArray[0][0] = -1;
		this.moveArray[0][1] = -1;
		this.moveArray[0][2] = this.imageArray[0];
		int index = 1;
		for (int i = 1; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				this.moveArray[i][j] = this.imageArray[index++];
			}
		}
		this.setImagePartMap(new HashMap<Integer, Rect>());

		this.selX = 2;
		this.selY = 0;
		/*
		 * ��ʼ�����ֲ���
		 */
		this.initSoundPool();
		/*
		 * ��ʼ����
		 */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

	}

	// �߼��ƶ�
	public void move() {
		// ��ǰѡ���˶�ά�����е��±�Ϊ��arrayIndexX,arrayIndexY�ķ���
		// ��ʼ�ж��Ƿ�����ƶ�

		System.out.println("===" + this.arrayIndexX + "===" + this.arrayIndexY);

		/**
		 * ע�������� �ƶ�
		 */

		if (this.arrayIndexX - 1 >= 0
				&& this.moveArray[this.arrayIndexX - 1][this.arrayIndexY] == 0) {
			// ���������ƶ�һ ��
			if (this.arrayIndexX == 1 && this.arrayIndexY != 2
					|| this.arrayIndexX == 0) {
				// �����ƶ�
				vibrator.vibrate(pattern, -1); // �ظ����������pattern
												// ���ֻ����һ�Σ�index��Ϊ-1
				return;
			} else {
				// �ƶ�������
				this.moveArray[this.arrayIndexX - 1][this.arrayIndexY] = this.moveArray[this.arrayIndexX][this.arrayIndexY];
				this.moveArray[this.arrayIndexX][this.arrayIndexY] = 0;
				System.out.println("���� �ƶ�֮�� ");
				this.gameSoundPool.play(this.soundMap.get(1), this.volume,
						this.volume, 1, 0, 1.0f);
			}

		} else if (this.arrayIndexX + 1 <= 3
				&& this.moveArray[this.arrayIndexX + 1][this.arrayIndexY] == 0) {
			// ���������ƶ�
			this.moveArray[this.arrayIndexX + 1][this.arrayIndexY] = this.moveArray[this.arrayIndexX][this.arrayIndexY];
			this.moveArray[this.arrayIndexX][this.arrayIndexY] = 0;

			System.out.println("���� �ƶ�֮�� ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else if (this.arrayIndexY - 1 >= 0
				&& this.moveArray[this.arrayIndexX][this.arrayIndexY - 1] == 0) {
			// ���������ƶ�
			if (this.arrayIndexY == 2 && this.arrayIndexX == 0) {
				// �����ƶ�
				vibrator.vibrate(pattern, -1); // �ظ����������pattern
												// ���ֻ����һ�Σ�index��Ϊ-1
				return;
			}
			this.moveArray[this.arrayIndexX][this.arrayIndexY - 1] = this.moveArray[arrayIndexX][this.arrayIndexY];
			this.moveArray[arrayIndexX][this.arrayIndexY] = 0;
			System.out.println("����  �ƶ�֮�� ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else if (this.arrayIndexY + 1 <= 2
				&& this.moveArray[this.arrayIndexX][this.arrayIndexY + 1] == 0) {
			// ���������ƶ�
			this.moveArray[this.arrayIndexX][this.arrayIndexY + 1] = this.moveArray[arrayIndexX][this.arrayIndexY];
			this.moveArray[arrayIndexX][this.arrayIndexY] = 0;
			System.out.println("�����ƶ�֮�� ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else {
			// �����ƶ�
			vibrator.vibrate(pattern, -1); // �ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1
			return;

		}
		this.syn();
		if (this.judgeWin()) {
			// Ӯ��
			this.winFlag = true;
			this.gameSoundPool.play(this.soundMap.get(2), this.volume,
					this.volume, 1, 0, 1.0f);

		} else {
			this.winFlag = false;

		}

	}

	public void initSoundPool() {
		// ��ʼ��SoundPool
		this.gameSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		this.soundMap = new HashMap<Integer, Integer>();
		// ��������Ƶ�ļ�����HashMap��
		this.soundMap.put(1,
				this.gameSoundPool.load(this, R.raw.launch_upmenu1, 1));
		this.soundMap.put(2, this.gameSoundPool.load(this, R.raw.goal_1, 1));
		AudioManager aManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		// ��ȡ��ǰ������С
		this.volume = aManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void switchXY() {
		this.arrayIndexX = this.selY;

		this.arrayIndexY = this.selX;
	}

	/*
	 * ����ά����� �仯ͬ����һά������
	 */
	public void syn() {
		int index = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 0 && j != 2) {
					continue;
				} else {
					this.imageArray[index++] = this.moveArray[i][j];
				}
			}
		}
	}

	public boolean judgeWin() {
		int index = 0;
		for (int i = 0; i < 10; i++) {
			if (i != this.imageArray[index++]) {
				return false;
			}
		}
		return true;
	}

	public GameView getGameView() {
		return gameView;
	}

	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}

	public int[] getImageArray() {
		return imageArray;
	}

	public void setImageArray(int[] imageArray) {
		this.imageArray = imageArray;
	}

	public int[][] getMoveArray() {
		return moveArray;
	}

	public void setMoveArray(int[][] moveArray) {
		this.moveArray = moveArray;
	}

	public HashMap<Integer, Rect> getImagePartMap() {
		return imagePartMap;
	}

	public void setImagePartMap(HashMap<Integer, Rect> imagePartMap) {
		this.imagePartMap = imagePartMap;
	}

	public int getSelX() {
		return selX;
	}

	public void setSelX(int selX) {
		this.selX = selX;
	}

	/**
	 * @return the winFlag
	 */
	public boolean isWinFlag() {
		return winFlag;
	}

	/**
	 * @param winFlag
	 *            the winFlag to set
	 */
	public void setWinFlag(boolean winFlag) {
		this.winFlag = winFlag;
	}

	public int getSelY() {
		return selY;
	}

	public void setSelY(int selY) {
		this.selY = selY;
	}

	public int getArrayIndexX() {
		return arrayIndexX;
	}

	public void setArrayIndexX(int arrayIndexX) {
		this.arrayIndexX = arrayIndexX;
	}

	public int getArrayIndexY() {
		return arrayIndexY;
	}

	public void setArrayIndexY(int arrayIndexY) {
		this.arrayIndexY = arrayIndexY;
	}

	public Handler getTimerHander() {
		return timerHander;
	}

	public void setTimerHander(Handler timerHander) {
		this.timerHander = timerHander;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

}
