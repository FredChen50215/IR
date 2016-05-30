package com.CrawlerMaven;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
public class FBCrawler {

	private JFrame frmFbcrawler;
	private JTextField textField;
	private List<String> url = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FBCrawler window = new FBCrawler();
					window.frmFbcrawler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FBCrawler() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmFbcrawler = new JFrame();
		frmFbcrawler.setTitle("FBCrawler");
		frmFbcrawler.setBounds(100, 100, 559, 588);
		frmFbcrawler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFbcrawler.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(126, 29, 169, 21);
		frmFbcrawler.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel label = new JLabel("輸入欲查看網址：");
		label.setBounds(10, 30, 127, 18);
		frmFbcrawler.getContentPane().add(label);
		
		JButton btnNewButton = new JButton("加入");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				url.add(textField.getText());

				JTextPane textPane = new JTextPane();
				textPane.setBounds(10, 70, 285, 21);
				frmFbcrawler.getContentPane().add(textPane);
				textPane.setText(textField.getText() + " 添加成功!");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				textPane = null;
			}
		});
		
		btnNewButton.setBounds(337, 28, 87, 23);
		frmFbcrawler.getContentPane().add(btnNewButton);
		
		JButton button = new JButton("確認送出");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					App.main(url);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(10, 120, 87, 23);
		frmFbcrawler.getContentPane().add(button);
		
		JButton btnNewButton_1 = new JButton("清除全部");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				url.clear();
			}
		});
		btnNewButton_1.setBounds(208, 120, 87, 23);
		frmFbcrawler.getContentPane().add(btnNewButton_1);
	}
}
