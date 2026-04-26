package github.dbr.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import github.dbr.main.FileManager;
import github.dbr.main.InputHandler;
import github.dbr.main.LogData;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -7405560596987878867L;

	private JPanel mainContainer, valueNames, graph, listPanel;

	private JScrollPane valueList;
	private Border bevel, raised;
	private JButton loadFile;

	private ArrayList<JCheckBox> listBoxes = new ArrayList<>();
	private ArrayList<JLabel> listLabels = new ArrayList<>();

	private float upperBound = 1f;
	private float lowerBound = -1f;

	private LogData data = null;

	private InputHandler input;
	
	public MainWindow(int width, int height) {
		bevel = BorderFactory.createLoweredSoftBevelBorder();
		raised = BorderFactory.createRaisedBevelBorder();

		input = new InputHandler() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				resizeGraph(e.getWheelRotation());
			}
		};
		
		setSize(width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		addMouseMotionListener(input);
		addMouseWheelListener(input);
		createComponents();

		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public void createComponents() {
		mainContainer = new JPanel();
		mainContainer.setLayout(null);
		mainContainer.setBounds(0, 0, getWidth(), getHeight());
		add(mainContainer);

		valueNames = new JPanel();
		valueNames.setBounds(0, 0, 300, 682);
		valueNames.setBorder(raised);
		valueNames.setLayout(null);
		mainContainer.add(valueNames);

		valueList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		valueList.setBounds(0, 100, 300, 586);
		valueList.setBorder(raised);
		valueList.setAlignmentY(JScrollPane.RIGHT_ALIGNMENT);
		valueNames.add(valueList);

		listPanel = new JPanel();
		listPanel.setPreferredSize(new Dimension(300, 582));
		listPanel.setLayout(null);

		valueList.setViewportView(listPanel);

		loadFile = new JButton("Load File");
		loadFile.setBounds(10, 10, 100, 20);
		loadFile.setFocusable(false);
		loadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
				fileChooser.setFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);

				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					data = FileManager.loadCSVFile(selectedFile);
					populateValueList();
					repaint();

					upperBound = data.getHighestMax();
					lowerBound = data.getLowestMin();
				}
			}
		});
		valueNames.add(loadFile);

		graph = new JPanel() {
			private static final long serialVersionUID = 4892367226478645068L;

			public void paint(Graphics g) {
				g.clearRect(0, 0, getWidth() - 300, 682);
				super.paint(g);
				drawGraph(g);
			}
		};
		graph.setBounds(300, 0, getWidth() - 300, 682);
		mainContainer.add(graph);
	}

	public void resizeGraph(float zoom) {
		float scale = 1 + zoom * 0.1f;
		
		upperBound = (upperBound > 0) ? upperBound * scale : upperBound / scale;
		lowerBound = (lowerBound > 0) ? lowerBound / scale : lowerBound * scale;
				
		repaint();
	}

	public void drawGraph(Graphics g) {
		g.setColor(Color.black);
		g.drawLine(10, 672, getWidth() - 370, 672);
		g.drawLine(10, 10, 10, 672);

		float spacer = (upperBound - lowerBound) / 10f;
		for (int i = 0; i < 11; i++) {
			g.drawString("" + ((int) ((lowerBound + i * spacer) * 10) / 10f), getWidth() - 362, (int) (677 - i * (662 / 10)));
		}

		drawData(g);
	}

	public void drawData(Graphics g) {
		if (data != null) {
			int dataPointCount = data.getLength();
			float spacing = ((getWidth() - 380) / (float) dataPointCount);

			for (int c = 0; c < data.getCategoryCount(); c++) {
				if (data.isEnabled(c)) {
					for (int i = 0; i < dataPointCount - 1; i++) {
						float currentDataPoint = data.get(i, c);
						float nextDataPoint = data.get(i + 1, c);

						float currentYPos = 672 - ((currentDataPoint - lowerBound) / ((currentDataPoint - lowerBound) + (upperBound - currentDataPoint))) * 662;
						float nextYPos = 672 - ((nextDataPoint - lowerBound) / ((nextDataPoint - lowerBound) + (upperBound - nextDataPoint))) * 662;

						g.setColor(data.getColor(c));
						g.drawLine(10 + (int) (i * spacing), (int) currentYPos, 10 + (int) ((i + 1) * spacing), (int) nextYPos);
					}
				}
			}
		}
	}

	public void populateValueList() {
		listBoxes.clear();
		listLabels.clear();

		listPanel.removeAll();

		int spacing = 30;
		int listHeight = Math.max(582, data.getCategoryCount() * spacing);

		listPanel.setPreferredSize(new Dimension(300, listHeight));

		for (int i = 0; i < data.getCategoryCount(); i++) {
			JCheckBox box = new JCheckBox();
			box.setBounds(10, 10 + spacing * i, 20, 20);
			box.setSelected(true);
			int index = i;
			box.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					data.setEnabled(index, box.isSelected());
					
					if (data != null) {
						upperBound = data.getHighestMax();
						lowerBound = data.getLowestMin();
					}
					
					repaint();
				}
			});
			listBoxes.add(box);
			listPanel.add(box);

			JLabel label = new JLabel(data.getCategory(i));
			label.setBounds(30, 10 + spacing * i, 260, 20);
			label.setForeground(data.getColor(i));
			listLabels.add(label);
			listPanel.add(label);
		}

		valueList.setViewportView(listPanel);
	}

}
