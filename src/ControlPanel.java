import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

class ControlPanel extends JPanel {
    private JTextField
            tfLevel = new JTextField("" + 5),
            tfScore = new JTextField("0");
    private JButton
            btPlay = new JButton("开始"),
            btPause = new JButton("暂停"),
            btStop = new JButton("停止"),
            btTurnLevelUp = new JButton("增加");
    btTurnLevelDown =new

    JButton("降低难度");

    private JPanel showbefore = new JPanel(new BorderLayout());
    private ShowBeforePanel plShowBeforeBlock = new ShowBeforePanel();
    private JPanel plInfo = new JPanel(new GridLayout(4, 1));
    private JPanel plButton = new JPanel(new GridLayout(5, 1));
    private Timer timer;
    private ErosBlocksGame game;

    public ControlPanel(final ErosBlocksGame game) {
        setLayout(new GridLayout(3, 1, 0, 4));
        this.game = game;
        showbefore.add(new JLabel("下一个方块"), BorderLayout.NORTH);
        showbefore.add(plShowBeforeBlock);
        plInfo.add(new JLabel("等级"));
        plInfo.add(tfLevel);
        plInfo.add(new JLabel("得分"));
        plInfo.add(tfScore);
        tfLevel.setEditable(false);
        tfScore.setEditable(false);
        plButton.add(btPlay);
        plButton.add(btPause);
        plButton.add(btStop);
        plButton.add(btTurnLevelUp);
        plButton.add(btTurnLevelDown);
        add(showbefore);
        add(plInfo);
        add(plButton);
        addKeyListener(new ControlKeyListener());
        btPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                game.playGame();
            }
        });
        btPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (btPause.getText().equals(new String("暂停"))) {
                    game.pauseGame();
                } else {
                    game.resumeGame();
                }
            }
        });
        btStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                game.stopGame();
            }
        });
        btTurnLevelUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    int level = Integer.parseInt(tfLevel.getText());
                    if (level < 10)
                        tfLevel.setText("" + (level + 1));
                } catch (NumberFormatException e) {
                }
                requestFocus();
            }
        });
        btTurnLevelDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    int level = Integer.parseInt(tfLevel.getText());
                    if (level > 1)
                        tfLevel.setText("" + (level - 1));
                } catch (NumberFormatException e) {
                }
                requestFocus();
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ce) {
                plShowBeforeBlock.fanning();
            }
        });
        timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                tfScore.setText("" + game.getScore());
                int scoreForLevelUpdate =
                        game.getScoreForLevelUp();
                if (scoreForLevelUpdate >= ErosBlocksGame.everyLevelscore
                        && scoreForLevelUpdate > 0)
                    game.levelUp();
            }
        });
        timer.start();
    }

    public void setShowBeforeStyle(int style) {
        plShowBeforeBlock.setStyle(style);
    }

    public int getLevel() {
        int level = 0;
        try {
            level = Integer.parseInt(tfLevel.getText());
        } catch (NumberFormatException e) {
        }
        return level;
    }

    public void setLevel(int level) {
        if (level > 0 && level < 11)
            tfLevel.setText("" + level);
    }

    public void setPlayButtonEnable(boolean enable) {
        btPlay.setEnabled(enable);
    }

    public void setPauseButtonLabel(boolean pause) {
        btPause.setText(pause ? "暂停" : "继续");
    }

    public void reset() {
        tfScore.setText("0");
        plShowBeforeBlock.setStyle(0);
    }

    public void fanning() {
        plShowBeforeBlock.fanning();
    }

    private class ShowBeforePanel extends JPanel {
        private Color backColor = Color.darkGray, frontColor = Color.red;
        private ErsBox[][] boxes = new ErsBox[ErsBlock.boxes_rows][ErsBlock.boxes_cols];
        private int style, boxWidth, boxHeight;
        private boolean isTiled = false;

        public ShowBeforePanel() {
            for (int i = 0; i < boxes.length; i++) {
                for (int j = 0; j < boxes[i].length; j++)
                    boxes[i][j] = new ErsBox(false);
            }
        }

        public ShowBeforePanel(Color backColor, Color frontColor) {
            this();
            this.backColor = backColor;
            this.frontColor = frontColor;
        }


        public void setStyle(int style) {
            this.style = style;
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!isTiled)
                fanning();
            int key = 0x8000;
            for (int i = 0; i < boxes.length; i++) {
                for (int j = 0; j < boxes[i].length; j++) {
                    Color color = (((key & style) != 0) ? frontColor : backColor);
                    g.setColor(color);
                    g.fill3DRect(j * boxWidth, i * boxHeight,
                            boxWidth, boxHeight, true);
                    key >>= 1;
                }
            }
        }

        public void fanning() {
            boxWidth = getSize().width / ErsBlock.boxes_cols;
            boxHeight = getSize().height / ErsBlock.boxes_rows;
            isTiled = true;
        }
    }

    private class ControlKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            if (!game.isPlaying())
                return;

            ErsBlock block = game.getCurBlock();
            switch (ke.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    block.moveDown();
                    break;
                case KeyEvent.VK_LEFT:
                    block.moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    block.moveRight();
                    break;
                case KeyEvent.VK_UP:
                    block.turnNext();
                    break;
                default:
                    break;
            }
        }
    }
}
