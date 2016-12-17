import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
class ErsBlock extends Thread 
{
	 
	public final static int boxes_rows = 4;
	public final static int boxes_cols = 4;
	public final static int flatgene = 3;
	public final static int betweenleveltime = 50;
	private final static int blockkindnum = 7;
	private final static int blockstatusnum = 4;
	public final static int[][] STYLES = {// 共28种状态
		{0x0f00, 0x4444, 0x0f00, 0x4444}, // 长条型的四种状态
		{0x04e0, 0x0464, 0x00e4, 0x04c4}, // 'T'型的四种状态
		{0x4620, 0x6c00, 0x4620, 0x6c00}, // 反'Z'型的四种状态
		{0x2640, 0xc600, 0x2640, 0xc600}, // 'Z'型的四种状态
		{0x6220, 0x1700, 0x2230, 0x0740}, // '7'型的四种状态
		{0x6440, 0x0e20, 0x44c0, 0x8e00}, // 反'7'型的四种状态
		{0x0660, 0x0660, 0x0660, 0x0660}, // 方块的四种状态
	};
	private GameCanvas canvas;
	private ErsBox[][] boxes = new ErsBox[boxes_rows][boxes_cols];
	private int style, y, x, level;
	private boolean pausing = false, moving = true;
	public ErsBlock(int style, int y, int x, int level, GameCanvas canvas) {
		this.style = style;
		this.y = y;
		this.x = x;
		this.level = level;
		this.canvas = canvas;
		int key = 0x8000;
		for (int i = 0; i < boxes.length; i++) 
		{
			for (int j = 0; j < boxes[i].length; j++) 
			{
				boolean isColor = ((style & key) != 0);
				boxes[i][j] = new ErsBox(isColor);
				key >>= 1;
			}
		}

		display();
	}
	public void run()
	{
		while (moving)
		{
			try 
			{
				sleep(betweenleveltime
				        * (ErosBlocksGame.maxLevel - level + flatgene));
			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
			if (!pausing)
				moving = (moveTo(y + 1, x) && moving);
		}
	}
	public void moveLeft() 
	{
		moveTo(y, x - 1);
	}
	public void moveRight()
	{
		moveTo(y, x + 1);
	}
	public void moveDown()
	{
		moveTo(y + 1, x);
	}
	public void turnNext()
	{
		for (int i = 0; i < blockkindnum; i++) 
		{
			for (int j = 0; j < blockstatusnum; j++)
			{
				if (STYLES[i][j] == style)
				{
					int newStyle = STYLES[i][(j + 1) % blockstatusnum];
					turnTo(newStyle);
					return;
				}
			}
		}
	}
	public void pauseMove() 
	{
		pausing = true;
	}

	 
	public void resumeMove()
	{
		pausing = false;
	}
	public void stopMove() 
	{
		moving = false;
	}
	private void earse() {
		for (int i = 0; i < boxes.length; i++) 
		{
			for (int j = 0; j < boxes[i].length; j++)
			{
				if (boxes[i][j].isColorBox())
				{
					ErsBox box = canvas.getBox(i + y, j + x);
					if (box == null)
						continue;
					box.setColor(false);
				}
			}
		}
	}

	private void display() 
	{
		for (int i = 0; i < boxes.length; i++)
		{
			for (int j = 0; j < boxes[i].length; j++)
			{
				if (boxes[i][j].isColorBox())
				{
					ErsBox box = canvas.getBox(y + i, x + j);
					if (box == null) 
						continue;
					box.setColor(true);
				}
			}
		}
	}
	private boolean isMoveAble(int newRow, int newCol)
	{
		earse();
		for (int i = 0; i < boxes.length; i++)
		{
			for (int j = 0; j < boxes[i].length; j++) 
			{
				if (boxes[i][j].isColorBox()) 
				{
					ErsBox box = canvas.getBox(newRow + i, newCol + j);
					if (box == null || (box.isColorBox())) 
					{
						display();
						return false;
					}
				}
			}
		}
		display();
		return true;
	}
	private synchronized boolean moveTo(int newRow, int newCol) {
		if (!isMoveAble(newRow, newCol) || !moving) 
			return false;
		earse();
		y = newRow;
		x = newCol;
		display();
		canvas.repaint();
		return true;
	}

	private boolean isTurnAble(int newStyle)
	{
		int key = 0x8000;
		earse();
		for (int i = 0; i < boxes.length; i++)
		{
			for (int j = 0; j < boxes[i].length; j++) 
			{
				if ((newStyle & key) != 0) 
				{
					ErsBox box = canvas.getBox(y + i, x + j);
					if (box == null || box.isColorBox())
					{
						display();
						return false;
					}
				}
				key >>= 1;
			}
		}
		display();
		return true;
	}
	private boolean turnTo(int newStyle)
	{
		if (!isTurnAble(newStyle) || !moving) 
			return false;
		earse();
		int key = 0x8000;
		for (int i = 0; i < boxes.length; i++)
		{
			for (int j = 0; j < boxes[i].length; j++) 
			{
				boolean isColor = ((newStyle & key) != 0);
				boxes[i][j].setColor(isColor);
				key >>= 1;
			}
		}
		style = newStyle;
		display();
		canvas.repaint();
		return true;
	}
}