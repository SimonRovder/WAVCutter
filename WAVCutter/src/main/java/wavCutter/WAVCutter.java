package main.java.wavCutter;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;


public class WAVCutter extends JFrame implements ActionListener, Runnable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField source;
	private JTextField destination;
	private JButton sourceButton;
	private JButton destinationButton;
	private JCheckBox sameAsSource;
	private JTextArea rawText;
	private JScrollPane rawTextScroller;
	private JScrollPane tableScroller;
	private JTable table;
	private JLabel status;

	private boolean continueStill;
	private boolean parsed;
	
	private File destinationF;
	private File sourceF;
	private boolean sameAsSourceB;
	private JButton stop;
	private JButton start;
	private JButton generate;
	
	private Song[] songs;
	
	public static void main(String args[]){
		new WAVCutter();
	}
	
	public WAVCutter(){
		super("WAVCutter");
		Song.launcher();
		this.setLayout(null);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setUpComponents();
		this.setVisible(true);
		this.parsed = false;
	}
	
	private void setUpComponents(){
		Container c = this.getContentPane();
		Tools.MyLabel(20, 20, 300, 20, "Source WAV File:", c);
		this.source = Tools.MyTextField(20, 50, 200, 20, c, false);
		this.sourceButton = Tools.MyButton(230, 50, 120, 20, "Browse", this, c);
		Tools.MyLabel(20, 80, 300, 20, "Destination Folder:", c);
		this.destination = Tools.MyTextField(20, 110, 200, 20, c, false);
		this.destinationButton = Tools.MyButton(230, 110, 120, 20, "Browse", this, c);
		this.sameAsSource = Tools.MyCheckBox(20, 140, 300, 20, "Destination same as Source", this, c);
		this.generate = Tools.MyButton(20, 170, 330, 30, "Generate a preview of the cut-up", this, c);
		this.start = Tools.MyButton(20, 210, 160, 30, "CUT!", this, c);
		this.stop = Tools.MyButton(190, 210, 160, 30, "STOP!", this, c);
		this.stop.setEnabled(false);
		this.rawText = new JTextArea();
		this.rawTextScroller = new JScrollPane(this.rawText);
		Tools.MyLabel(360, 20, 420, 20, "Enter the raw data here:", c);
		this.rawTextScroller.setBounds(360, 50, 420, 200);
		this.rawTextScroller.setVisible(true);
		c.add(this.rawTextScroller);
		this.sameAsSourceB = false;
		this.continueStill = true;
		this.status = Tools.MyLabel(20, 550, 600, 20, "STATUS: Idle", c);
	}
	
	private void createTable(){
		try{
			this.getContentPane().remove(this.tableScroller);
		}catch(Exception ex){
			
		}
		Object[][] data = new Object[this.songs.length][3];
		Object columns[] = {"Start", "End", "Name of Song"};
		int i = 0;
		while(i < this.songs.length){
			data[i][0] = this.songs[i].getStartS();
			data[i][1] = this.songs[i].getEndS();
			data[i][2] = this.songs[i].getName();
			i++;
		}
		
		this.table = new JTable(data, columns){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.getColumnModel().getColumn(0).setPreferredWidth(100);
		this.table.getColumnModel().getColumn(1).setPreferredWidth(100);
		this.table.getColumnModel().getColumn(2).setPreferredWidth(540);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.tableScroller = new JScrollPane(this.table);
		this.tableScroller.setBounds(20,280,760,240);
		this.add(this.tableScroller);
	}
	
	@Override
	public void run() {
		try{
			for(Song s : this.songs){
				if(!this.continueStill){
					throw new Exception();
				}else{
					this.status.setText("STATUS: Cutting song: " + s.getName());
					try{
						this.copyAudio(this.sourceF, this.destinationF.getAbsolutePath() + "/" + s.getName() + ".wav", s.getStart(), s.getEnd());
					}catch(Exception ex){
						this.error("Could not cut file! The following error occured:\n" + ex.getMessage());
						throw new Exception();
					}
				}
			}
		}catch(Exception e){
			this.alert("Cutting has been cancelled.");
		}
		exitThreadMode();
		this.status.setText("STATUS: Idle");
	}
	
	private void copyAudio(File file, String destinationFileName, int startSecond, int endSecond) throws Exception{
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		int secondsToCopy = endSecond - startSecond;
		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
		AudioFormat format = fileFormat.getFormat();
		inputStream = AudioSystem.getAudioInputStream(file);
		int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
		inputStream.skip(startSecond * bytesPerSecond);
		long framesOfAudioToCopy = secondsToCopy * (int)format.getFrameRate();
		shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
		File destinationFile = new File(destinationFileName);
		AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.generate){
			this.totalParser();
		}
		if(e.getSource()==this.sourceButton){
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("WAVE FILES", "wav", "wave");
			fc.setFileFilter(filter);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            	this.sourceF = fc.getSelectedFile();
            	this.source.setText(this.sourceF.toString());
            	try{
            		this.tableScroller.setVisible(false);
            	}catch(Exception ex){
            		
            	}
				this.parsed = false;
            	if(this.sameAsSourceB){
    				this.destination.setText(this.sourceF.getParent());
    			}
            }
		}
		if(e.getSource()==this.destinationButton){
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            	this.destinationF = fc.getSelectedFile();
            	this.destination.setText(this.destinationF.toString());
            }
		}
		if(e.getSource() == this.sameAsSource){
			this.sameAsSourceB = !this.sameAsSourceB;
			this.destinationButton.setEnabled(!this.sameAsSourceB);
			if(this.sourceF != null){
				this.destination.setText(this.sourceF.getParent());
				this.destinationF = new File(this.sourceF.getParent());
			}else{
				this.destination.setText("Select a source file.");
			}
		}
		if(e.getSource() == this.start){
			if(this.destinationF == null){
				this.error("Please select a destination folder!");
				return;
			}
			if(!this.parsed){
				if(!this.totalParser()){
					this.error("Could not parse text!");
					return;
				}
			}
			this.enterThreadMode();
			this.continueStill = true;
			(new Thread(this)).start();
		}
		if(e.getSource() == this.stop){
			this.continueStill = false;
		}
	}
	
	private void alert(String string) {
		JOptionPane.showMessageDialog(this, string, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	private void error(String string) {
		JOptionPane.showMessageDialog(this, string, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void enterThreadMode(){
		this.generate.setEnabled(false);
		this.start.setEnabled(false);
		this.stop.setEnabled(true);
		this.sourceButton.setEnabled(false);
		this.destinationButton.setEnabled(false);
		this.sameAsSource.setEnabled(false);
	}
	
	private void exitThreadMode(){
		this.generate.setEnabled(true);
		this.start.setEnabled(true);
		this.stop.setEnabled(false);
		this.sourceButton.setEnabled(true);
		this.destinationButton.setEnabled(!this.sameAsSource.isSelected());
		this.sameAsSource.setEnabled(true);
	}
	
	private boolean totalParser(){
		String[] str = this.rawText.getText().split("[\n]");
		this.songs = new Song[str.length];
		for(int i = 0; i < str.length; i++){
			this.songs[i] = new Song(str[i]);
		}
		for(int i = 0; i < str.length-1; i++){
			this.songs[i].setEnd(this.songs[i+1].getStart());
		}
		
		int badOnes = 0;
		for(int i = 0; i < this.songs.length; i++){
			if(Song.isValid(this.songs[i])){
				this.songs[i] = null;
				badOnes++;
			}
		}
		Song[] temp = new Song[this.songs.length - badOnes];
		int i = 0;
		for(Song song : this.songs){
			if(song != null){
				temp[i] = song;
				i++;
			}
		}
		this.songs = temp;
		
		String resp = null;
		if(this.songs[this.songs.length - 1].getEnd() == 0){
			resp = JOptionPane.showInputDialog(null, "Enter the time at which the last song ends:", "End Time", 1);
			if(resp != null){
				try{
					this.songs[this.songs.length - 1].setEnd(Song.parseDigits(resp)-5);
					if(this.songs[this.songs.length - 1].getEnd() <= this.songs[this.songs.length - 1].getStart()){
						this.error("The entered time is smaller than the starting time of the last song! Please enter a valid one.");
						return false;
					}
				}catch(NumberFormatException ex){
					this.error("Invalid time index entered. Please write the time in the following formats:\nHH:MM:SS or\nMM:SS ");
					return false;
				}
				
			}else{
				return false;
			}
		}
		
		
		this.createTable();
		this.parsed = true;
		return true;
	}
}
