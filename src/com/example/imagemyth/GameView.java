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

	// ��ȡGame,���Եõ�Game����������
	private Game game;

	// ��ʾͼƬ�Ŀ��
	private float imageWidth;

	// ��ʾͼƬ�ĸ߶�
	private float imageHeight;

	// Ҫ���Ƶ�ͼƬ
	private Bitmap sourceImage;

	// ���� ͼƬ
	private Bitmap bgImage;

	// ѡ�е�Rectangle
	private Rect selectRect;

	// ���Ƶ��й�����
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

		// �����������е�ͼƬ
		Rect destRect = null;
		int index = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {

				// Ҫ���Ƶ�����
				destRect = new Rect((int) (j * this.imageWidth),
						(int) (i * this.imageHeight),
						(int) ((j + 1) * this.imageWidth),
						(int) ((i + 1) * this.imageHeight));
				// ��һ�в���ʾͼƬ����ʾ��Ϣ��
				if (i == 0 && j != 2) {
					canvas.drawPath(path, paint);
					canvas.drawText(this.game.getTimeString(),
							this.imageWidth * 0.15f, this.imageHeight * 0.7f,
							this.textPaint);
					continue;
				}

				// �������0�����ͼƬ��������Ʊ���
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
	 * ���±�1��ʼ����Ӧ��ͼƬ�ŵ���Ӧ��index�� �����ں�������޹أ�ֻ�ǰ�Ĭ��˳������ͼƬ
	 */
	public void getImageRects() {

		int index = 1;

		HashMap<Integer, Rect> imageMap = this.game.getImagePartMap();

		// �ܹ������9��
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				imageMap.put(index++, new Rect((int) (j * this.imageWidth),
						(int) (i * this.imageHeight),
						(int) ((j + 1) * this.imageWidth),
						(int) ((i + 1) * this.imageHeight)));

			}
		}

		// ���0��ӽ�ȥ
		imageMap.put(0, new Rect(0, 0, 0, 0));

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ������������ƶ����ǰ��£�����ϵͳ�Ĵ���ʽ���������ǰ��£���������
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}
		// ��� ѡ���ͼƬ���±꣬ע����ȡ����

		// ѡ�е�ʱ�����õ�ǰѡ�е� ���꣬selX��selY
		this.game.setSelX((int) (event.getX() / this.imageWidth));
		this.game.setSelY((int) (event.getY() / this.imageHeight));

		/**
		 * ����ѡ�е� selX��ˮƽ����ĳ��ȣ�Ҳ��������� �� ѡ�е�selY�Ǵ�ֱ���ȣ�Ҳ������
		 * 
		 * Ҫ�������Ͻǵ�����
		 */
		if (this.game.getSelY() == 0 && this.game.getSelX() != 2) {

			// ѡ�е������ϽǵĿ� ������
			return true;
		}
		this.game.switchXY();

		this.game.move();

		invalidate();
		/*
		 * ��ʼ�����¼��߼���Ӧ����Game�ķ���������
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
