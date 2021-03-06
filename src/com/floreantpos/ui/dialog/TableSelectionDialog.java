/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.floreantpos.Messages;
import com.floreantpos.POSConstants;
import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.swing.PosButton;
import com.floreantpos.swing.PosScrollPane;
import com.floreantpos.swing.ScrollableFlowPanel;
import com.floreantpos.swing.ShopTableButton;
import com.floreantpos.ui.TitlePanel;

public class TableSelectionDialog extends POSDialog implements ActionListener {

	private DefaultListModel<ShopTableButton> addedTableListModel = new DefaultListModel<ShopTableButton>();
	private Ticket ticket;

	private Map<ShopTable, ShopTableButton> tableButtonMap = new HashMap<ShopTable, ShopTableButton>();

	public TableSelectionDialog() {
		init();
	}

	private void init() {
		setTitle(Messages.getString("TableSelectionDialog.1")); //$NON-NLS-1$
		setPreferredSize(Application.getPosWindow().getSize());

		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("TableSelectionDialog.0")); //$NON-NLS-1$
		add(titlePanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		PosButton posButton = new PosButton(POSConstants.OK);
		posButton.setFocusable(false);
		posButton.addActionListener(this);
		buttonPanel.add(posButton, "w 80!,split 2,align center"); //$NON-NLS-1$

		PosButton btnCancel = new PosButton(POSConstants.CANCEL);
		btnCancel.setFocusable(false);
		btnCancel.addActionListener(this);
		buttonPanel.add(btnCancel, " w 80!"); //$NON-NLS-1$

		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		footerPanel.add(new JSeparator(), BorderLayout.NORTH);
		footerPanel.add(buttonPanel);

		add(footerPanel, BorderLayout.SOUTH);

		ScrollableFlowPanel buttonsPanel = new ScrollableFlowPanel(FlowLayout.LEADING);
		List<ShopTable> tables = ShopTableDAO.getInstance().findAll();

		for (ShopTable shopTable : tables) {
			ShopTableButton tableButton = new ShopTableButton(shopTable);
			tableButton.setPreferredSize(new Dimension(80, TerminalConfig.getTouchScreenButtonHeight()));
			tableButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addTable(e);
				}
			});

			tableButton.update();
			buttonsPanel.add(tableButton);
			tableButtonMap.put(shopTable, tableButton);
		}

		JScrollPane scrollPane = new PosScrollPane(buttonsPanel, PosScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, PosScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(80, 0));
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));

		add(scrollPane, BorderLayout.CENTER);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		int taskBarSize = scnMax.bottom;

		setSize(screenSize.width, screenSize.height - taskBarSize);
	}

	private boolean addTable(ActionEvent e) {
		
		ShopTableButton button = (ShopTableButton) e.getSource();
		int tableNumber = Integer.parseInt(e.getActionCommand());

		ShopTable shopTable = ShopTableDAO.getInstance().getByNumber(tableNumber);

		if (shopTable == null) {
			POSMessageDialog.showError(this, Messages.getString("TableSelectionDialog.2") + e + Messages.getString("TableSelectionDialog.3")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}

		if (ticket != null) {
			shopTable.setServing(false);
			shopTable.setBooked(false);
		}

		if (shopTable.isServing()) {
			POSMessageDialog.showError(this, Messages.getString("TableSelectionDialog.4") + e + Messages.getString("TableSelectionDialog.5")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}

		if (addedTableListModel.contains(button)) {
			addedTableListModel.removeElement(button);
			button.getShopTable().setServing(false);
			button.getShopTable().setBooked(false);
			button.update();
			return true;
		}
		button.getShopTable().setServing(true);
		button.setBackground(Color.red);
		button.setForeground(Color.black);
		this.addedTableListModel.addElement(button);
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (POSConstants.CANCEL.equalsIgnoreCase(actionCommand)) {
			doCancel();
		}
		else if (POSConstants.OK.equalsIgnoreCase(actionCommand)) {
			doOk();
		}
	}

	private void doOk() {
		setCanceled(false);
		dispose();
	}

	private void doCancel() {
		setCanceled(true);
		dispose();
	}

	public void setTitle(String title) {
		super.setTitle(title);
	}

	public void setDialogTitle(String title) {
		super.setTitle(title);
	}

	public List<ShopTable> getTables() {
		Enumeration<ShopTableButton> elements = this.addedTableListModel.elements();
		List<ShopTable> tables = new ArrayList<ShopTable>();

		while (elements.hasMoreElements()) {
			ShopTableButton shopTableButton = (ShopTableButton) elements.nextElement();
			tables.add(shopTableButton.getShopTable());
		}

		return tables;
	}

	public void setTicket(Ticket ticket) {
		if (ticket == null) {
			return;
		}

		this.ticket = ticket;

		List<ShopTable> tables = ShopTableDAO.getInstance().getTables(ticket);
		if (tables == null)
			return;

		for (ShopTable shopTable : tables) {
			ShopTableButton shopTableButton = tableButtonMap.get(shopTable);
			shopTableButton.setBackground(Color.red);
			shopTableButton.setForeground(Color.black);
			shopTableButton.setEnabled(true);

			if (shopTableButton != null) {
				addedTableListModel.addElement(shopTableButton);
			}
		}
	}
}