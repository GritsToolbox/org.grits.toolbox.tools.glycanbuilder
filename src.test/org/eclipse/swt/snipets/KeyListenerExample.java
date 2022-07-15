package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class KeyListenerExample {

	private Display d;

	private Shell s;

	public KeyListenerExample() {

		d = new Display();
		s = new Shell(d);

		s.setSize(250, 200);

		s.setText("A KeyListener Example");
		s.setLayout(new RowLayout());

		final Combo c = new Combo(s, SWT.DROP_DOWN | SWT.BORDER);
		c.add("Lions");
		c.add("Tigers");
		c.add("Bears");
		c.add("Oh My!");

		c.addKeyListener(new KeyListener() {
			String selectedItem = "";

			public void keyPressed(KeyEvent e) {
				System.out.println(e);
				if (c.getText().length() > 0) {
					return;
				}
				String key = Character.toString(e.character);
				String[] items = c.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].toLowerCase().startsWith(key.toLowerCase())) {
						c.select(i);
						selectedItem = items[i];
						return;
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				if (selectedItem.length() > 0)
					c.setText(selectedItem);
				selectedItem = "";
			}
		});
		s.open();
		while (!s.isDisposed()) {
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	public static void main(String args[]) {
		new KeyListenerExample();
	}

}