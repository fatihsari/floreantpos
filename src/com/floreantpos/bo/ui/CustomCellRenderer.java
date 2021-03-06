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
package com.floreantpos.bo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof byte[]) {

			byte[] imageData = (byte[]) value;
			ImageIcon image = new ImageIcon(imageData);
			image = new ImageIcon(image.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
			if (imageData != null) {
				table.setRowHeight(row, 120);
			}
			return new JLabel(image);
		}

		if (value instanceof Color) {
			JLabel lblColor = new JLabel();
			lblColor.setOpaque(true);
			lblColor.setForeground((Color) value);
			lblColor.setBackground((Color) value);
			return lblColor;
		}
		if (value instanceof Date) {
			String pattern = "MM/dd hh:mm a";
		    SimpleDateFormat format = new SimpleDateFormat(pattern);
			value =	format.format((Date) value);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
