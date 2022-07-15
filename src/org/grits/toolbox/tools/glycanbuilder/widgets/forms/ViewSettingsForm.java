package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasWithToolBarComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.DisplaySettingsDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GeneralIconProvider;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GlycanIconProvider;

public class ViewSettingsForm extends FormAbstract {

	private static final String[] SCALE_PERSENT = {"400%", "300%", "200%", "150%", "100%", "67%", "50%", "33%", "25%"};
	private static final double[] SCALE_VALUE = {4.00d, 3.00d, 2.00d, 1.50d, 1.00d, 0.67d, 0.50d, 0.33d, 0.25d};

	private Button btnReset;
	private Map<String, Button> mapNotationToBtn;
	private Map<String, Button> mapDisplayStyleToBtn;
	private Button btnShowCollapse;
	private Button btnShowMass;
	private Button btnShowRedend;
	private Button btnShowID;
	private Button btnDisplayChange;
	private int iOrientation;
	private Button btnDisplayOrientation;
	private Combo cmbDisplayChangeScale;
	private Button btnShowPager;

	public ViewSettingsForm(MultiPageFormAbstract parent, String tabText) {
		super(parent, tabText);
		this.mapNotationToBtn = new HashMap<>();
		this.mapDisplayStyleToBtn = new HashMap<>();
		createControl();
	}

	private void createControl() {
		getForm().setText(Messages.getString("ViewSettings.title")); //$NON-NLS-1$

		// Reset button
		Button btn = getToolkit().createButton(this.getForm().getBody(),
				Messages.getString("ViewSettings.default"), //$NON-NLS-1$
				SWT.PUSH);
		this.btnReset = btn;

		// Notation format
		Composite sectionClient = this.createNewSection(Messages.getString("ViewSettings.format")); //$NON-NLS-1$
		List<String> lNotations = new ArrayList<>();
		lNotations.add(GraphicOptionsSWT.NOTATION_SNFG);
		lNotations.add(GraphicOptionsSWT.NOTATION_CFG);
		lNotations.add(GraphicOptionsSWT.NOTATION_CFGBW);
		lNotations.add(GraphicOptionsSWT.NOTATION_CFGLINK);
		lNotations.add(GraphicOptionsSWT.NOTATION_UOXF);
		lNotations.add(GraphicOptionsSWT.NOTATION_UOXFCOL);
		lNotations.add(GraphicOptionsSWT.NOTATION_TEXT);
		for ( final String notation : lNotations ) {
			Button button = getToolkit().createButton(sectionClient,
					Messages.getString("ViewSettings.format_"+notation), //$NON-NLS-1$
					SWT.RADIO);
			this.mapNotationToBtn.put(notation, button);
		}

		// Notation style
		sectionClient = this.createNewSection(Messages.getString("ViewSettings.style")); //$NON-NLS-1$
		List<String> lStyles = new ArrayList<>();
		lStyles.add(GraphicOptionsSWT.DISPLAY_COMPACT);
		lStyles.add(GraphicOptionsSWT.DISPLAY_NORMAL);
		lStyles.add(GraphicOptionsSWT.DISPLAY_NORMALINFO);
		lStyles.add(GraphicOptionsSWT.DISPLAY_CUSTOM);
		for ( final String style : lStyles ) {
			Button button = getToolkit().createButton(sectionClient,
					Messages.getString("ViewSettings.style_"+style), //$NON-NLS-1$
					SWT.RADIO);
			this.mapDisplayStyleToBtn.put(style, button);
		}

		// Show features section
		sectionClient = this.createNewSection(Messages.getString("ViewSettings.show")); //$NON-NLS-1$
		/// Collapse antennae
		Button button = getToolkit().createButton(sectionClient,
				Messages.getString("ViewSettings.show_collapse"), //$NON-NLS-1$
				SWT.CHECK);
		this.btnShowCollapse = button;

		/// Mass info
		button = getToolkit().createButton(sectionClient,
				Messages.getString("ViewSettings.show_mass"), //$NON-NLS-1$
				SWT.CHECK);
		this.btnShowMass = button;

		/// Reducing end
		button = getToolkit().createButton(sectionClient,
				Messages.getString("ViewSettings.show_redend"), //$NON-NLS-1$
				SWT.CHECK);
		this.btnShowRedend = button;

		/// ID
		button = getToolkit().createButton(sectionClient,
				Messages.getString("ViewSettings.show_id"), //$NON-NLS-1$
				SWT.CHECK);
		this.btnShowID = button;

		// Display Setting section
		sectionClient = this.createNewSection( Messages.getString("ViewSettings.display") ); //$NON-NLS-1$
		sectionClient.setLayout(new GridLayout(2, false));
		/// Display change
		button = getToolkit().createButton(sectionClient, null, SWT.FLAT);
		button.setImage(GeneralIconProvider.getChangeDisplayIcon());
		button.setToolTipText(Messages.getString("ViewSettings.display_change")); //$NON-NLS-1$
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		this.btnDisplayChange = button;

		/// Change orientation
		this.iOrientation = GraphicOptionsSWT.RL;
		button = getToolkit().createButton(sectionClient, null, SWT.FLAT);
		button.setImage(GeneralIconProvider.getOrientationIcon(this.iOrientation));
		button.setToolTipText(Messages.getString("ViewSettings.display_orientation")); //$NON-NLS-1$
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		this.btnDisplayOrientation = button;

		/// Change scale
		getToolkit().createLabel(sectionClient,
				Messages.getString("ViewSettings.display_scale") //$NON-NLS-1$
			);

		Combo cmb = new Combo(sectionClient, SWT.READ_ONLY);
		cmb.setItems(SCALE_PERSENT);
		cmb.select(4); // 100%
		this.cmbDisplayChangeScale = cmb;

		/// Pager
		button = getToolkit().createButton(sectionClient,
				Messages.getString("ViewSettings.display_pager"), //$NON-NLS-1$
				SWT.CHECK);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		this.btnShowPager = button;
	}

	public void saveSettingsToWorkspace(BuilderWorkspaceSWT bws) {
		// Notation
		for ( String notation : mapNotationToBtn.keySet() ) {
			Button btn = mapNotationToBtn.get(notation);
			if ( !btn.getSelection() )
				continue;
			bws.setNotation(notation);
			GlycanIconProvider.setNotation(notation);
		}
		// Style
		for ( String display : mapDisplayStyleToBtn.keySet() ) {
			Button btn = mapDisplayStyleToBtn.get(display);
			if ( btn.getSelection() )
				bws.setDisplay(display);
		}
		// Features
		bws.getGraphicOptions().COLLAPSE_MULTIPLE_ANTENNAE = btnShowCollapse.getSelection();
		bws.getGraphicOptions().SHOW_MASSES = btnShowMass.getSelection();
		bws.getGraphicOptions().SHOW_REDEND = btnShowRedend.getSelection();
		bws.getGraphicOptions().SHOW_ID = btnShowID.getSelection();
		// Orientation
		bws.getGraphicOptions().ORIENTATION = iOrientation;
		// Scale
		bws.getGraphicOptions().setScale(SCALE_VALUE[cmbDisplayChangeScale.getSelectionIndex()]);
	}

	public void loadSettingsFromWorkspace(BuilderWorkspaceSWT bws) {
		// Notation
		for ( String notation : mapNotationToBtn.keySet() ) {
			Button btn = mapNotationToBtn.get(notation);
			btn.setSelection(false);
			if ( notation.equals( bws.getGraphicOptions().NOTATION ) )
				btn.setSelection(true);
		}
		// Style
		for ( String display : mapDisplayStyleToBtn.keySet() ) {
			Button btn = mapDisplayStyleToBtn.get(display);
			btn.setSelection(false);
			if ( display.equals( bws.getGraphicOptions().DISPLAY ) )
				btn.setSelection(true);
		}
		// Features
		btnShowCollapse.setSelection(bws.getGraphicOptions().COLLAPSE_MULTIPLE_ANTENNAE);
		btnShowMass.setSelection(bws.getGraphicOptions().SHOW_MASSES);
		btnShowRedend.setSelection(bws.getGraphicOptions().SHOW_REDEND);
		btnShowID.setSelection(bws.getGraphicOptions().SHOW_ID);
		// Orientation
		iOrientation = bws.getGraphicOptions().ORIENTATION;
		btnDisplayOrientation.setImage(GeneralIconProvider.getOrientationIcon(iOrientation));
		// Scale
		double curScale = bws.getGraphicOptions().SCALE;
		// Find nearest value
		int inx = 0;
		double minDiff = Double.MAX_VALUE;
		for ( int i=0; i<SCALE_VALUE.length; i++ ) {
			double scale = SCALE_VALUE[i];
			double diff = Math.abs( curScale - scale );
			if ( diff < minDiff ) {
				minDiff = diff;
				inx = i;
			}
		}
		cmbDisplayChangeScale.select(inx);
	}

	private void setDefaultToWorkspace(BuilderWorkspaceSWT bws) {
		// Notation
		bws.setNotation(GraphicOptionsSWT.NOTATION_SNFG);
		// Style
		bws.setDisplay(GraphicOptionsSWT.DISPLAY_NORMALINFO);
		// Features
		bws.getGraphicOptions().COLLAPSE_MULTIPLE_ANTENNAE = true;
		bws.getGraphicOptions().SHOW_MASSES = true;
		bws.getGraphicOptions().SHOW_REDEND = true;
		bws.getGraphicOptions().SHOW_ID = false;
		// Orientation
		bws.getGraphicOptions().ORIENTATION = GraphicOptionsSWT.RL;
		// Scale
		bws.getGraphicOptions().setScale(1.d);
	}

	public void addCanvasUpdateListener(GlycanCanvasWithToolBarComposite canvas) {

		this.addChangeViewSettingsListener(canvas.getGlycanCanvas().getBuilderWorkspace());

		// Add listener for updating view
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.updateView();
			}
		};

		this.btnReset.addSelectionListener(listener);
		for (Button btn : this.mapNotationToBtn.values()) {
			btn.addSelectionListener(listener);
		}
		for (Button btn : this.mapDisplayStyleToBtn.values()) {
			btn.addSelectionListener(listener);
		}
		this.btnShowCollapse.addSelectionListener(listener);
		this.btnShowMass.addSelectionListener(listener);
		this.btnShowRedend.addSelectionListener(listener);
		this.btnShowID.addSelectionListener(listener);
		this.btnDisplayChange.addSelectionListener(listener);
		this.btnDisplayOrientation.addSelectionListener(listener);
		this.cmbDisplayChangeScale.addSelectionListener(listener);

		this.btnShowPager.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.getGlycanCanvas().showPager( btnShowPager.getSelection() );
				canvas.updateView();
			}
		});

		// Updates orientation icons
		listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.getShortcutToolBar().updateOrientationIcon();
			}
		};
		this.btnReset.addSelectionListener(listener);
		this.btnDisplayOrientation.addSelectionListener(listener);
		canvas.getShortcutToolBar().getChangeOrientationItem().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnDisplayOrientation.setImage(GeneralIconProvider.getOrientationIcon(
						canvas.getGlycanCanvas().getBuilderWorkspace().getGraphicOptions().ORIENTATION
					));
			}
		});

		// Add mouse wheel listener to GlycanCanvas
		canvas.getGlycanCanvas().addMouseWheelListener( new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent e) {
				// Ctrl + mouse wheel
				if ( (e.stateMask & SWT.MODIFIER_MASK) != SWT.MOD1 )
					return;
				int inx = cmbDisplayChangeScale.getSelectionIndex();
				if ( e.count > 0 ) // Scroll up
					inx--;
				else // Scroll down
					inx++;
				if ( inx < 0 )
					inx = 0;
				if ( inx > cmbDisplayChangeScale.getItemCount() - 1 )
					inx = cmbDisplayChangeScale.getItemCount() - 1;
				cmbDisplayChangeScale.select(inx);
				canvas.getGlycanCanvas().getBuilderWorkspace().getGraphicOptions().setScale(SCALE_VALUE[inx]);
				canvas.updateView();
			}
		});
	}

	private void addChangeViewSettingsListener(BuilderWorkspaceSWT bws) {

		Button btn;

		// Reset
		btn = this.btnReset;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDefaultToWorkspace(bws);
				loadSettingsFromWorkspace(bws);
				GlycanIconProvider.setNotation(bws.getGraphicOptions().NOTATION);
			}

		});

		// For notation
		for ( final String notation : this.mapNotationToBtn.keySet() ) {
			btn = this.mapNotationToBtn.get(notation);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					bws.setNotation(notation);
					GlycanIconProvider.setNotation(notation);
				}
			});
		}

		// For display style
		for ( final String style : this.mapDisplayStyleToBtn.keySet() ) {
			btn = this.mapDisplayStyleToBtn.get(style);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					bws.setDisplay(style);
				}
			});
		}

		// For show settings
		btn = this.btnShowCollapse;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bws.getGraphicOptions().COLLAPSE_MULTIPLE_ANTENNAE = ((Button)e.widget).getSelection();
			}
		});

		btn = this.btnShowMass;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bws.getGraphicOptions().SHOW_MASSES
					= ((Button)e.widget).getSelection();
			}
		});

		btn = this.btnShowRedend;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bws.getGraphicOptions().SHOW_REDEND = ((Button)e.widget).getSelection();
			}
		});

		btn = this.btnShowID;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bws.getGraphicOptions().SHOW_ID = ((Button)e.widget).getSelection();
			}
		});

		btn = this.btnDisplayChange;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DisplaySettingsDialog dialog = new DisplaySettingsDialog(
						((Button)e.widget).getShell(),
						bws.getGraphicOptions()
					);
				dialog.open();
				// Change display style selection
				for ( final String style : mapDisplayStyleToBtn.keySet() ) {
					Button btnStyle = mapDisplayStyleToBtn.get(style);
					// Change selection
					if ( bws.getGraphicOptions().DISPLAY.equals(style) )
						btnStyle.setSelection(true);
					else
						btnStyle.setSelection(false);
				}

			}
		});

		btn = this.btnDisplayOrientation;
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int orien = bws.getGraphicOptions().ORIENTATION;
				if ( orien == GraphicOptionsSWT.RL )
					orien = GraphicOptionsSWT.BT;
				else if ( orien == GraphicOptionsSWT.BT )
					orien = GraphicOptionsSWT.LR;
				else if ( orien == GraphicOptionsSWT.LR )
					orien = GraphicOptionsSWT.TB;
				else if ( orien == GraphicOptionsSWT.TB )
					orien = GraphicOptionsSWT.RL;
				bws.getGraphicOptions().ORIENTATION = orien;
				((Button)e.widget).getImage().dispose();
				((Button)e.widget).setImage(GeneralIconProvider.getOrientationIcon(orien));
			}
		});

		this.cmbDisplayChangeScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = ((Combo)e.getSource()).getSelectionIndex();
				bws.getGraphicOptions().setScale(SCALE_VALUE[selection]);
			}
		});
	}
}
