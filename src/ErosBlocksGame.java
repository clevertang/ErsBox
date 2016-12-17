
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

/*** 用eclipse开发的俄罗斯方块游戏
 * @author clevertang
 *@version 1.1
 * 游戏主类，继承自JFrame,负责游戏的全局控制
 * 1.一个画布类GameCanvas的实例
 *2.一个游戏块ErsBlock的实例
 *一个控制界面ControlPanel的实例
 */
public class ErosBlocksGame extends JFrame {
	//填满一行的分数
	public final static int alinescore=100;
	//升级分数
	public final static int everyLevelscore=2000;
	//最大的级数
	public final static int maxLevel=10;
	//默认级数
	public final static int initialLevel=5;
	//画布类实例
	private GameCanvas canvas;
	//方块类实例
	private ErsBlock block;
	
	private boolean playing=false;
	//控制面板类实例
	private ControlPanel ctrlPanel;
	private JMenuBar bar= new JMenuBar();
	private JMenu
		mGame=new JMenu("游戏"),
		mControl=new JMenu("控制"),
		mHelp=new JMenu("帮助"),
		mInfo=new JMenu("信息");
	private JMenuItem
	 	miNewGame=new JMenuItem("新游戏"),	 	
		miTurnHarder=new JMenuItem("增加难度"),
		miTurnEasier=new JMenuItem("降低难度"),
		miExit=new JMenuItem("退出"),
		miPlay=new JMenuItem("开始"),
		miPause=new JMenuItem("暂停"),
		miResume=new JMenuItem("继续"),
		miStop=new JMenuItem("停止"),
		miSourceInfo=new JMenuItem("版本：1.0"),
		miAuthor=new JMenuItem("作者：clevertang");
	/**游戏主类的构造方法
	 * 
	 * @param title String,窗口标题
	 */
	public ErosBlocksGame(String title){
		super(title);
		setSize(300,500);
		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width-getSize().width)/2,(scrSize.height-getSize().height)/2);
		creatMenu();
		Container container=getContentPane();
		container.setLayout(new BorderLayout(6,0));
		canvas=new GameCanvas(20,12);
		ctrlPanel=new ControlPanel(this);
		container.add(canvas, BorderLayout.CENTER);
		container.add(ctrlPanel, BorderLayout.EAST);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				stopGame();
				System.exit(0);
				
			}
		});
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent ce){
				canvas.fanning();
			}
		});
		show();
		canvas.fanning();
	}
	private void creatMenu(){
		bar.add(mGame);
		bar.add(mControl);
		bar.add(mHelp);
		bar.add(mInfo);
		mGame.add(miNewGame);
		mGame.addSeparator();
		mGame.add(miTurnEasier);
		mGame.addSeparator();
		mGame.add(miTurnHarder);
		mGame.addSeparator();
		mGame.add(miExit);
		mControl.add(miPlay);
		mControl.addSeparator();
		mControl.add(miPause);
		mControl.addSeparator();
		mControl.add(miResume);
		mControl.addSeparator();
		mControl.add(miStop);
		mInfo.add(miAuthor);
		mInfo.add(miSourceInfo);
		setJMenuBar(bar);
		miNewGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				stopGame();
				reset();
				setLevel(5);
			}
		});
		
		miTurnEasier.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int curLevel=getLevel();
				if(curLevel>1) setLevel(curLevel-1);
			}
		});
		miExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});
		miTurnHarder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				int curLevel=getLevel();
				if(curLevel<1) setLevel(curLevel+1);
			}
		});
		miPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				playGame();
			}
		});
		miPause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				pauseGame();
			}
		});
		miResume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				resumeGame();
			}
		});
		miStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				stopGame();
			}
		});
		
	}
	public void reset(){
		ctrlPanel.reset();
		canvas.reset();
	}
	public boolean isPlaying(){
		return playing;
	}
	public ErsBlock getCurBlock(){
		return block;
	}
	public GameCanvas getCanvas(){
		return canvas;
	}
	public void playGame(){
		play();
		ctrlPanel.setPlayButtonEnable(false);
		miPlay.setEnabled(false);
		miResume.setEnabled(true);
	}
	public void pauseGame(){
		if(block!=null) block.pauseMove();
		ctrlPanel.setPauseButtonLabel(false);
		miPause.setEnabled(false);
		miResume.setEnabled(true);
	}
	public void resumeGame(){
		if(block!=null) block.resumeMove();
		ctrlPanel.setPauseButtonLabel(true);
		miPause.setEnabled(true);
		miResume.setEnabled(false);
		ctrlPanel.requestFocus();
	}
	public void stopGame(){
		playing=false;
		if(block!=null) block.stopMove();
		ctrlPanel.setPlayButtonEnable(true);
		miPlay.setEnabled(true);
		miStop.setEnabled(false);
		miResume.setEnabled(false);
		ctrlPanel.setPauseButtonLabel(true);
	}
	public int getLevel(){
		return ctrlPanel.getLevel();
	}
	public void setLevel(int level){
		if(level>0&&level<11) ctrlPanel.setLevel(level);
	}
	public int getScore(){
		if(canvas!=null) return canvas.getScore();
		return 0;
	}
	public int getScoreForLevelUp(){
		if(canvas!=null) return canvas.getScoreForLevelUpdate();
		return 0;
	}
	public boolean levelUp(){
		int curLevel=getLevel();
		if(curLevel<maxLevel){
			setLevel(curLevel+1);
			canvas.resetScoreForLevelUpdate();
			return true;
		}
		return false;
		}
	private void play(){
		reset();
		playing=true;
		Thread thread=new Thread(new game());
		thread.start();
		
	}
	private void reportGameOver(){
		JOptionPane.showMessageDialog(this, "GG");
	}
	private class game implements Runnable{
		public void run(){
			int col=(int)(Math.random()*(canvas.getCols()-3)),
					style=ErsBlock.STYLES[(int)(Math.random()*7)][(int)(Math.random()*4)];
			while(playing){
				if(block!=null){
					if(block.isAlive()){
						try{
							Thread.currentThread();
							Thread.sleep(100);							
						}
						catch(InterruptedException ie){
							ie.printStackTrace();
						}
						continue;
					}
				}
				checkFullLine();
				if(isGameOver()){
					miPlay.setEnabled(true);
					miPause.setEnabled(true);
					miResume.setEnabled(false);
					ctrlPanel.setPlayButtonEnable(true);
					ctrlPanel.setPauseButtonLabel(true);
					reportGameOver();
					return;
				}
				block=new ErsBlock(style,-1,col,getLevel(),canvas);
				block.start();
				col=(int)(Math.random()*(canvas.getCols()-3));
				style=ErsBlock.STYLES[(int)(Math.random()*7)][(int)(Math.random()*4)];
				ctrlPanel.setShowBeforeStyle(style);
			}
		}
		public void checkFullLine(){
			int row;
			for(int i=0;i<canvas.getRows();i++){
				//int row=1
				boolean fullLineColorBox=true;
				for(int j=0;j<canvas.getCols();j++){
					if(!canvas.getBox(i,j).isColorBox()){
						fullLineColorBox=false;
						break;
					}
				}
				if(fullLineColorBox){
					row=i;
					canvas.removeLine(row);
				}
			}
		}
		private boolean isGameOver(){
			for(int i=0;i<canvas.getCols();i++){
				ErsBox box=canvas.getBox(0,i);
				if(box.isColorBox()){
					return true;
				}
			}
			return false;
		}
	}
	public static void main(String[] args){
		new ErosBlocksGame("clevertang的俄罗斯方块游戏");
			
		
	}
}
	
