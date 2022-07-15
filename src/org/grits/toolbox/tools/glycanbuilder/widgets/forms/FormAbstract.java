package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public abstract class FormAbstract {

	private MultiPageFormAbstract parent;
	private FormToolkit toolkit;
	private ScrolledForm form;
	private String tabText;

	private ExpandableComposite expCurrent;

	public FormAbstract(MultiPageFormAbstract parent, String tabText) {
		this.parent = parent;
		this.toolkit = parent.getToolkit();
		this.form = toolkit.createScrolledForm(parent.getFolder());
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
		this.form.getBody().setLayout(layout);

		this.tabText = tabText;
	}

	public MultiPageFormAbstract getParent() {
		return this.parent;
	}

	public FormToolkit getToolkit() {
		return this.toolkit;
	}

	public ScrolledForm getForm() {
		return this.form;
	}

	public String getTabText() {
		return this.tabText;
	}

	protected Composite createNewSection(String title) {
		return createNewSection(this.form.getBody(), title);
	}
	protected Composite createNewSection(Composite parent, String title) {
		Section section = this.toolkit.createSection(parent,
				Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED | Section.COMPACT );
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText(title);

		Composite sectionClient = toolkit.createComposite(section);
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
		sectionClient.setLayout(layout);

		section.setClient(sectionClient);

		return sectionClient;
	}

	protected ExpandableComposite createExpandableComposite(Composite parent) {
		final ExpandableComposite exp
			= this.toolkit.createExpandableComposite(parent, ExpandableComposite.TREE_NODE | ExpandableComposite.COMPACT);

		final Composite client = this.toolkit.createComposite(exp);
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 10;
		client.setLayout(layout);
		exp.setClient(client);

		exp.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				if ( e.getState() ) {
					// Close composite previously selected
					if ( expCurrent != null )
						expCurrent.setExpanded(false);
					expCurrent = exp;
				} else {
					expCurrent = null;
				}
				form.reflow(true);
			}
		});

		return exp;
	}

}
