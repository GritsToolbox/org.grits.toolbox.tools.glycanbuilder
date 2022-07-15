package org.grits.toolbox.tools.glycanbuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class TestTableWrapLayout {

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("Test Sections with TableWrapLayout");
//		shell.setLayout(new GridLayout(1, false));
		shell.setLayout(new FillLayout());

		TestTableWrapLayout test = new TestTableWrapLayout();
		test.createControl(shell);

		shell.pack();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private FormToolkit toolkit;
	private ScrolledForm form;

	private void createControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
//		form = toolkit.createScrolledForm(parent);
		Composite body = parent;

		TableWrapLayout layout;
		layout = new TableWrapLayout();
		layout.numColumns = 2;
		body.setLayout(layout);

		Composite sectionClient = createSection(body, "Section 1");
		layout = new TableWrapLayout();
		sectionClient.setLayout(layout);

		Label lbl = toolkit.createLabel(sectionClient,
				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				, SWT.WRAP);
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.maxWidth = 50;
		lbl.setLayoutData(data);
		lbl = toolkit.createLabel(sectionClient,
				"test 1"
				, SWT.WRAP);
		data = new TableWrapData(TableWrapData.FILL_GRAB);
		lbl.setLayoutData(data);

		sectionClient = createSection(body, "Section 2");
		lbl = toolkit.createLabel(sectionClient,
				"b"
				, SWT.WRAP);
		data = new TableWrapData(TableWrapData.FILL_GRAB);
		lbl.setLayoutData(data);
		lbl = toolkit.createLabel(sectionClient,
				"test 2"
				, SWT.WRAP);
		data = new TableWrapData(TableWrapData.FILL_GRAB);
		lbl.setLayoutData(data);

		data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.colspan = 2;
		sectionClient = createSectionWithTableWrapLayout(body, "TableWrapLayout + RowLayout", data);
//		sectionClient = createSectionWithColumnLayout(body, "Section 3", data);

		Composite inner = this.toolkit.createComposite(sectionClient, SWT.NONE);
		data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.maxWidth = 150;
		inner.setLayoutData(data);
//		TableWrapLayout rowLayout = new TableWrapLayout();
//		rowLayout.numColumns = 2;
		RowLayout rowLayout = new RowLayout();
//		rowLayout.type = SWT.VERTICAL;
		rowLayout.wrap = true;
		inner.setLayout(rowLayout);
		for ( int i=0; i<10; i++ ) {
			Button btn = this.toolkit.createButton(inner, "Button #"+i, SWT.PUSH);
//			btn.setLayoutData(new TableWrapData(TableWrapData.FILL));
		}

		data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.colspan = 2;
		sectionClient = createSectionWithColumnLayout(body, "ColumnLayout", data);

		inner = this.toolkit.createComposite(sectionClient, SWT.NONE);
		ColumnLayout colLayout = new ColumnLayout();
		colLayout.maxNumColumns = 10;
		inner.setLayout(colLayout);
		for ( int i=0; i<10; i++ ) {
			Button btn = this.toolkit.createButton(inner, "Button #"+i, SWT.PUSH);
		}

		data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.colspan = 2;
		sectionClient = createSectionWithTableWrapLayout(body, "Nested 1", data);
		sectionClient = createSection(sectionClient, "Nested 1-1");

		data = new TableWrapData(TableWrapData.FILL_GRAB);
		data.colspan = 2;
		sectionClient = createSectionWithTableWrapLayout(body, "Nested 2", data);
		sectionClient = createSection(sectionClient, "Nested 2-1");

	}

	private Composite createSection(Composite parent, String title) {
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		return createSectionWithTableWrapLayout(parent, title, data);
	}

	private Composite createSectionWithTableWrapLayout(Composite parent, String title, Object layoutData) {
		Composite client = createSection(parent, title, layoutData);
		TableWrapLayout layout = new TableWrapLayout();
//		layout.numColumns = 1;
		client.setLayout(layout);
		return client;
	}

	private Composite createSectionWithColumnLayout(Composite parent, String title, Object layoutData) {
		Composite client = createSection(parent, title, layoutData);
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
		client.setLayout(layout);
		return client;
	}

	private Composite createSection(Composite parent, String title, Object layoutData) {
		Section section = toolkit.createSection(parent,
				Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
//				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED );
		section.setLayoutData(layoutData);
		section.setText(title);

//		section.addExpansionListener(new ExpansionAdapter() {
//			public void expansionStateChanged(ExpansionEvent e) {
//				form.reflow(true);
//			}
//		});

		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);

		return sectionClient;
	}
}
