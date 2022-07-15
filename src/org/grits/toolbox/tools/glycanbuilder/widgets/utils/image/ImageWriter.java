package org.grits.toolbox.tools.glycanbuilder.widgets.utils.image;

import java.io.File;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageWriter {

	private static String TMP_DIR = "image";
	private static String FILE_NAME_DEFAULT = TMP_DIR+File.separator+"image";

	private ImageLoader m_loader;

	public ImageWriter(Image img) {
		this.m_loader = new ImageLoader();
		this.m_loader.data = new ImageData[] {img.getImageData()};
	}

	/**
	 * Save image as a .png file with default filename.
	 * @return String of filename
	 */
	public String savePNG() {
		String filename = FILE_NAME_DEFAULT+".png";
		this.m_loader.save(filename, SWT.IMAGE_PNG);
		return filename;
	}

	public void savePNG(String filename) {
		this.m_loader.save(filename, SWT.IMAGE_PNG);
	}

	public void saveJPEG(String filename) {
		this.m_loader.save(filename, SWT.IMAGE_JPEG);
	}

	public void saveBMP(String filename) {
		this.m_loader.save(filename, SWT.IMAGE_BMP);
	}

	public void copyToClipboad() {
		String filename = savePNG();
		File fileImage = new File(filename);
		Display display = Display.getCurrent();
		Clipboard cb = new Clipboard(display);
		String[] data = {fileImage.getAbsolutePath()};
		cb.setContents(new Object[] {data}, new Transfer[] {FileTransfer.getInstance()});
		cb.dispose();
	}

}
