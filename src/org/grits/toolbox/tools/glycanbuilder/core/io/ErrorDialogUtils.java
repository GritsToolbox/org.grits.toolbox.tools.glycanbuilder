package org.grits.toolbox.tools.glycanbuilder.core.io;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

public class ErrorDialogUtils {
	public static MultiStatus createMultiStatus(String msg, Throwable t) {
		if ( t == null )
			return null;

		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = t.getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "org.grits.toolbox.tools.glycanbuilder", stackTrace.toString());
			childStatuses.add(status);
		}

		MultiStatus ms = new MultiStatus("org.grits.toolbox.tools.glycanbuilder",
				IStatus.ERROR, childStatuses.toArray(new Status[] {}), t.toString(), t);
		return ms;
	}
}
