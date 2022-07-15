package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DatabaseIDComposite {

	private FormToolkit m_toolkit;
	private Text m_txtIdPrefix;
	private Text m_txtIdPostfix;
	private Button m_btnOverwriteIds;

	public DatabaseIDComposite(Composite parent, FormToolkit toolkit) {
		this.m_toolkit = toolkit;
		createControls(parent);
	}

	public DatabaseIDComposite(Composite parent) {
		this.m_toolkit = new FormToolkit(parent.getDisplay());
		this.m_toolkit.setBackground(parent.getBackground());
		createControls(parent);
	}

	private void createControls(Composite parent) {
		// 2 column grid
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		parent.setLayout(layout);

		Label lbl;
		// controls for Prefix (label + text)
		lbl = this.m_toolkit.createLabel(parent, "Prefix");
		lbl.setText("Prefix");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		this.m_txtIdPrefix = this.m_toolkit.createText(parent, "", SWT.BORDER);
		this.m_txtIdPrefix.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE, 1, 2));
		this.m_txtIdPrefix.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// controls for Postfix
		lbl = this.m_toolkit.createLabel(parent, "Postfix");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		this.m_txtIdPostfix = this.m_toolkit.createText(parent, "", SWT.BORDER);
		this.m_txtIdPostfix.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE, 1, 2));
		this.m_txtIdPostfix.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// help text
		lbl = this.m_toolkit.createLabel(parent, "");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		lbl = this.m_toolkit.createLabel(parent,
				"Glycan IDs will be generated following the format <prefix>Number<postfix>.\n"+
				"If prefix and postfix are empty the ID will consist of a number alone.",
				SWT.WRAP
			);
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE, 1, 2);
		data.maxWidth = 120;
		lbl.setLayoutData(data);

		// place the checkbox for overwriting
		this.m_btnOverwriteIds = this.m_toolkit.createButton(parent, "Overwrite:", SWT.CHECK);
		this.m_btnOverwriteIds.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 2));

		lbl = this.m_toolkit.createLabel(parent,
				"For glycans with existing IDs generate new IDs as well.",
				SWT.WRAP
			);
		data = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE);
		data.maxWidth = 150;
		lbl.setLayoutData(data);
	}

	public String getIDPrefix() {
		return this.m_txtIdPrefix.getText();
	}

	public String getIDPostfix() {
		return this.m_txtIdPostfix.getText();
	}

	public boolean isOverwriteIDs() {
		return this.m_btnOverwriteIds.getSelection();
	}
}
