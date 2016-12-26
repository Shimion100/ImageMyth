package com.example.imagemyth;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class GameView extends View {

	// 获取Game,可以得到Game的所有属性
	private Game game;

	// 显示图片的宽度
	private float imageWidth;

	// 显示图片的高度
	private float imageHeight;

	// 要绘制的图片
	private Bitmap sourceImage;

	// 背景 图片
	private Bitmap bgImage;

	// 选中的Rectangle
	private Rect selectRect;

	// 绘制的有关属性
	public Paint paint;

	public Paint textPaint;

	public Path path;

	public RectF aRectF;

	public GameView(Context context) {
		super(context);
		this.setGame((Game) context);
		this.setBackgroundResource(R.drawable.game_bg);
		sourceImage = BitmapFactory.decodeResource(getResources(),
				R.drawable.audrey_hepburn);
		switch ((int) (Math.random() * 7)) {
		case 0:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.audrey_hepburn);
			break;
		case 1:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.gudaoweiji);
			break;
		case 2:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.haizaiwang);
			break;
		case 3:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.huoyingrenzhe);
			break;
		case 4:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.laola);
			break;
		case 5:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.lufei);
			break;
		case 6:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.street_fighter);
			break;

		default:
			sourceImage = BitmapFactory.decodeResource(getResources(),
					R.drawable.street_fighter);
			break;
		}
		setBgImage(BitmapFactory.decodeResource(getResources(),
				R.drawable.game_bg));
		// TODO Auto-generated constructor stub
		this.paint = new Paint();
		this.textPaint = new Paint();
		textPaint.setColor(Color.GREEN);
		this.path = new Path();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10.0f);
		paint.setColor(getResources().getColor(R.color.main_font_color));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		// 绘制四行三列的图片
		Rect destRect = null;
		int index = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {

				// 要绘制的区域
				destRect = new Rect((int) (j * this.imageWidth),
						(int) (i * this.imageHeight),
						(int) ((j + 1) * this.imageWidth),
						(int) ((i + 1) * this.imageHeight));
				// 第一行不显示图片，显示信息。
				if (i == 0 && j != 2) {
					canvas.drawPath(path, paint);
					canvas.drawText(this.game.getTimeString(),
							this.imageWidth * 0.15f, this.imageHeight * 0.7f,
							this.textPaint);
					continue;
				}

				// 如果不是0则绘制图片，否则绘制背景
				if (this.game.getImageArray()[index] != 0) {
					canvas.drawBitmap(sourceImage, this.game.getImagePartMap()
							.get(this.game.getImageArray()[index]), destRect,
							paint);
				} else {
					canvas.drawBitmap(this.bgImage, destRect, destRect, paint);
				}

				index++;
			}
		}
		super.onDraw(canvas);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		this.imageHeight = h / (4.0f);
		this.imageWidth = w / (3.0f);
		this.getImageRects();

		this.aRectF = new RectF(0, 0, this.imageWidth * 2, this.imageHeight);
		path.addRoundRect(aRectF, 50.0f, 50.0f, Path.Direction.CW);
		textPaint.setTextSize(this.imageHeight * 0.5f);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/*
	 * 从下标1开始将对应的图片放到对应的index中 ，现在和随机数无关，只是按默认顺序生成图片
	 */
	public void getImageRects() {

		int index = 1;

		HashMap<Integer, Rect> imageMap = this.game.getImagePartMap();

		// 总共添加了9个
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				imageMap.put(index++, new Rect((int) (j * this.imageWidth),
						(int) (i * this.imageHeight),
						(int) ((j + 1) * this.imageWidth),
						(int) ((i + 1) * this.imageHeight)));

			}
		}

		// 最后将0添加进去
		imageMap.put(0, new Rect(0, 0, 0, 0));

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果是其他手势而不是按下，调用系统的处理方式，否则则是按下，我来处理
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}
		// 获得 选择的图片的下标，注意是取整的

		// 选中的时候设置当前选中的 坐标，selX和selY
		this.game.setSelX((int) (event.getX() / this.imageWidth));
		this.game.setSelY((int) (event.getY() / this.imageHeight));

		/**
		 * 由于选中的 selX是水平方向的长度，也就是数组的 列 选中的selY是垂直长度，也就是行
		 * 
		 * 要处理左上角的两块
		 */
		if (this.game.getSelY() == 0 && this.game.getSelX() != 2) {

			// 选中的是左上角的块 ，忽略
			return true;
		}
		this.game.switchXY();

		this.game.move();

		invalidate();
		/*
		 * 开始处理事件逻辑，应该用Game的方法来处理
		 */
		return true;
	}

	public void changeSelectRect() {

		this.selectRect = new Rect(
				(int) (this.game.getSelX() * this.imageWidth),
				(int) (this.game.getSelY() * this.imageHeight),
				(int) ((this.game.getSelX() + 1) * this.imageWidth),
				(int) ((this.game.getSelY() + 1) * this.imageHeight));

	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public float getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(float imageWidth) {
		this.imageWidth = imageWidth;
	}

	public float getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(float imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Bitmap getBgImage() {
		return bgImage;
	}

	public void setBgImage(Bitmap bgImage) {
		this.bgImage = bgImage;
	}

	public Rect getSelectRect() {
		return selectRect;
	}

	public void setSelectRect(Rect selectRect) {
		this.selectRect = selectRect;
	}

}
