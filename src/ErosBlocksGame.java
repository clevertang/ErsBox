
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

/*** ��eclipse�����Ķ���˹������Ϸ
 * @author clevertang
 *@version 1.1
 * ��Ϸ���࣬�̳���JFrame,������Ϸ��ȫ�ֿ���
 * 1.һ��������GameCanvas��ʵ��
 *2.һ����Ϸ��ErsBlock��ʵ��
 *һ�����ƽ���ControlPanel��ʵ��
 */
public class ErosBlocksGame extends JFrame {
	//����һ�еķ���
	public final static int alinescore=100;
	//��������
	public final static int everyLevelscore=2000;
	//���ļ���
	public final static int maxLevel=10;
	//Ĭ�ϼ���
	public final static int initialLevel=5;
	//������ʵ��
	private GameCanvas canvas;
	//������ʵ��
	private ErsBlock block;
	
	private boolean playing=false;
	//���������ʵ��
	private ControlPanel ctrlPanel;
	private JMenuBar bar= new JMenuBar();
	private JMenu
		mGame=new JMenu("��Ϸ"),
		mControl=new JMenu("����"),
		mHelp=new JMenu("����"),
		mInfo=new JMenu("��Ϣ");
	private JMenuItem
	 	miNewGame=new JMenuItem("����Ϸ"),	 	
		miTurnHarder=new JMenuItem("�����Ѷ�"),
		miTurnEasier=new JMenuItem("�����Ѷ�"),
		miExit=new JMenuItem("�˳�"),
		miPlay=new JMenuItem("��ʼ"),
		miPause=new JMenuItem("��ͣ"),
		miResume=new JMenuItem("����"),
		miStop=new JMenuItem("ֹͣ"),
		miSourceInfo=new JMenuItem("�汾��1.0"),
		miAuthor=new JMenuItem("���ߣ�clevertang");
	/**��Ϸ����Ĺ��췽��
	 * 
	 * @param title String,���ڱ���
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
		new ErosBlocksGame("clevertang�Ķ���˹������Ϸ");
			
		
	}
}
	
