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
 * Game类，完成游戏的逻辑，及主 Activity
 * 
 * @version 1.0基本功能
 * 
 * @version 2.0 重写了显示时间的 代码
 * 
 * @version 3.0
 * @author ACEr
 * @time 2014-5-13 重写了音乐播放的代码
 */
@SuppressLint({ "UseSparseArrays", "HandlerLeak" })
public class Game extends Activity {

	// 根据时间刷新屏幕的Handler
	private Handler timerHander;

	// 时间显示的String
	private String timeString;

	// 震动
	private Vibrator vibrator;

	// 振动的Pattern
	private long[] pattern = { 100, 400 }; // 停止 开启 停止 开启;

	// 游戏初始的String
	private String[] startStrings = { "520476189", "049865712", "629047851",
			"967152840", "190687245" };

	// 音乐处理
	private SoundPool gameSoundPool;

	// 存放音频文件Map
	private HashMap<Integer, Integer> soundMap;

	// 音乐大小
	private float volume;

	// 游戏赢得标记
	private boolean winFlag;
	// 游戏视图
	private GameView gameView;
	// 当前点击的X，是二维数组下表y
	private int selX;

	// 当前点击的 Y，是二维数组下表x
	private int selY;

	// 在数组中的下标
	private int arrayIndexX;

	// 在数组中的下标
	private int arrayIndexY;

	private int[] imageArray = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private HashMap<Integer, Rect> imagePartMap;
	// 做移动逻辑的二维数组
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
	 * 做初始化工作
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

		// 对数组进行随机排序
		String startString = this.startStrings[(int) (Math.random() * 5)];
		// 永远要把3那块放到0
		this.imageArray[0] = 3;
		for (int i = 0; i < startString.length(); i++) {
			this.imageArray[i + 1] = Integer.parseInt(startString.charAt(i)
					+ "");
		}
		System.out.println(Arrays.toString(imageArray));

		// 初始化二维数组并赋值
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
		 * 初始化音乐播放
		 */
		this.initSoundPool();
		/*
		 * 初始化震动
		 */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

	}

	// 逻辑移动
	public void move() {
		// 当前选中了二维数组中的下标为：arrayIndexX,arrayIndexY的方块
		// 开始判断是否可以移动

		System.out.println("===" + this.arrayIndexX + "===" + this.arrayIndexY);

		/**
		 * 注意是行列 移动
		 */

		if (this.arrayIndexX - 1 >= 0
				&& this.moveArray[this.arrayIndexX - 1][this.arrayIndexY] == 0) {
			// 可以向上移动一 行
			if (this.arrayIndexX == 1 && this.arrayIndexY != 2
					|| this.arrayIndexX == 0) {
				// 不可移动
				vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
												// 如果只想震动一次，index设为-1
				return;
			} else {
				// 移动即交换
				this.moveArray[this.arrayIndexX - 1][this.arrayIndexY] = this.moveArray[this.arrayIndexX][this.arrayIndexY];
				this.moveArray[this.arrayIndexX][this.arrayIndexY] = 0;
				System.out.println("向上 移动之后 ");
				this.gameSoundPool.play(this.soundMap.get(1), this.volume,
						this.volume, 1, 0, 1.0f);
			}

		} else if (this.arrayIndexX + 1 <= 3
				&& this.moveArray[this.arrayIndexX + 1][this.arrayIndexY] == 0) {
			// 可以向下移动
			this.moveArray[this.arrayIndexX + 1][this.arrayIndexY] = this.moveArray[this.arrayIndexX][this.arrayIndexY];
			this.moveArray[this.arrayIndexX][this.arrayIndexY] = 0;

			System.out.println("向下 移动之后 ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else if (this.arrayIndexY - 1 >= 0
				&& this.moveArray[this.arrayIndexX][this.arrayIndexY - 1] == 0) {
			// 可以向左移动
			if (this.arrayIndexY == 2 && this.arrayIndexX == 0) {
				// 不可移动
				vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
												// 如果只想震动一次，index设为-1
				return;
			}
			this.moveArray[this.arrayIndexX][this.arrayIndexY - 1] = this.moveArray[arrayIndexX][this.arrayIndexY];
			this.moveArray[arrayIndexX][this.arrayIndexY] = 0;
			System.out.println("向左  移动之后 ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else if (this.arrayIndexY + 1 <= 2
				&& this.moveArray[this.arrayIndexX][this.arrayIndexY + 1] == 0) {
			// 可以向右移动
			this.moveArray[this.arrayIndexX][this.arrayIndexY + 1] = this.moveArray[arrayIndexX][this.arrayIndexY];
			this.moveArray[arrayIndexX][this.arrayIndexY] = 0;
			System.out.println("向右移动之后 ");
			this.gameSoundPool.play(this.soundMap.get(1), this.volume,
					this.volume, 1, 0, 1.0f);
		} else {
			// 不可移动
			vibrator.vibrate(pattern, -1); // 重复两次上面的pattern 如果只想震动一次，index设为-1
			return;

		}
		this.syn();
		if (this.judgeWin()) {
			// 赢了
			this.winFlag = true;
			this.gameSoundPool.play(this.soundMap.get(2), this.volume,
					this.volume, 1, 0, 1.0f);

		} else {
			this.winFlag = false;

		}

	}

	public void initSoundPool() {
		// 初始化SoundPool
		this.gameSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		this.soundMap = new HashMap<Integer, Integer>();
		// 将连个音频文件放入HashMap中
		this.soundMap.put(1,
				this.gameSoundPool.load(this, R.raw.launch_upmenu1, 1));
		this.soundMap.put(2, this.gameSoundPool.load(this, R.raw.goal_1, 1));
		AudioManager aManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		// 获取当前音量大小
		this.volume = aManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void switchXY() {
		this.arrayIndexX = this.selY;

		this.arrayIndexY = this.selX;
	}

	/*
	 * 将二维数组的 变化同步到一维数组中
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
