/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.ui.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.Constants;
import de.tfsw.accounting.reporting.ReportGenerationMonitor;
import de.tfsw.accounting.reporting.ReportingService;
import de.tfsw.accounting.ui.Messages;
import de.tfsw.accounting.ui.prefs.AccountingPreferences;
import de.tfsw.accounting.ui.prefs.ReportingPreferencesConstants;
import de.tfsw.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ReportGenerationUtil {

	/** */
	private static final Logger LOG = Logger.getLogger(ReportGenerationUtil.class);
	private static final Logger MONITOR_LOG = Logger.getLogger(ReportProgressMonitor.class);
	
	private static final String SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
	
	/** */
	private ReportingService reportingService;
	
	/** */
	private ReportGenerationHandler handler;
	
	/** */
	private String targetFileName;
	
	/**
	 * Use the static method.
	 */
	private ReportGenerationUtil() {
		
	}
	
	/**
	 * 
	 * @param handler
	 * @param shell
	 */
	public static void executeReportGeneration(ReportGenerationHandler handler, Shell shell) {
		new ReportGenerationUtil().doExecuteReportGeneration(handler, shell);
	}
	
	/**
	 * Builds a filename suggestion appended by optional timeframe data.
	 * 
	 * <ul>
	 * <li><code>[fileNameBase]</code> if start and end dates are in different years (no modifications made)</li>
	 * <li><code>[fileNameBase]_[year][month]</code> if start and end dates are within the same month and year</li>
	 * <li><code>[fileNameBase]_[year]</code> if start and end dates are within the same year but different months</li>
	 * </ul>
	 * 
	 * @param fileNameBase the "naked" file name suggestion, i.e. without any extensions
	 * 
	 * @param timeFrame an optional timeframe used for building a file name
	 * 
	 * @return the complete filename suggestion
	 */
	public static String appendTimeFrameToFileNameSuggestion(String fileNameBase, TimeFrame timeFrame) {
		if (timeFrame == null) {
			return fileNameBase;
		}
		
		StringBuffer sb = new StringBuffer(fileNameBase);
		
    	if (timeFrame.isInSameYear()) {
    		sb.append(Constants.UNDERSCORE).append(timeFrame.getFromYear());
        	if (timeFrame.isInSameMonth()) {
        		sb.append(String.format("%02d", timeFrame.getFromMonth()));
        	}
    	}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param handler
	 * @param shell
	 */
	private void doExecuteReportGeneration(ReportGenerationHandler handler, Shell shell) {
		this.handler = handler;
		reportingService = ReportingServiceProvider.getReportingServiceProvider().getReportingService();
	    if (reportingService == null) {
	    	throw new AccountingException(Messages.ReportGenerationUtil_errorNoReportingService);
	    }
	    
	    IEclipsePreferences prefs = AccountingPreferences.getAccountingPreferences();
	    StringBuilder sb = new StringBuilder();
		String dir = prefs.get(ReportingPreferencesConstants.LAST_SAVE_DIR, System.getProperty("user.home")); //$NON-NLS-1$
		String fileType = prefs.get(ReportingPreferencesConstants.PREFERRED_FILE_TYPE, ".pdf"); //$NON-NLS-1$
		
		if (dir != null && dir.isEmpty() == false) {
			sb.append(dir);
			if (dir.endsWith(SEPARATOR) == false) {
				sb.append(SEPARATOR);
			}
		}
		sb.append(handler.getTargetFileNameSuggestion());
		sb.append(fileType);
		
		boolean openAfterExport = prefs.getBoolean(ReportingPreferencesConstants.OPEN_AFTER_EXPORT, false);
		
	    ChooseExportTargetDialog dlg = new ChooseExportTargetDialog(shell, sb.toString(), openAfterExport);
	    
	    if (dlg.show()) {
	    	targetFileName = dlg.getTargetFile();
	    	if (targetFileName != null && confirmOverwrite(shell, targetFileName)) {
				try {
					LOG.info("Starting document generation to file " + targetFileName); //$NON-NLS-1$
					ReportProgressMonitor generation = new ReportProgressMonitor();
					new ProgressMonitorDialog(shell).run(true, false, generation);
				} catch (Exception e) {
					LOG.error("Error creating document", e); //$NON-NLS-1$
					throw new AccountingException(Messages.ReportGenerationUtil_errorGeneratingInvoice, e);
				}
				
				// either open the document or show a success message
				if (dlg.isOpenAfterExport() && openFileInExternalProgram()) {
					LOG.debug("Export of document and opening in external program finished successfully"); //$NON-NLS-1$
				} else {
					showSuccessPopup(shell);
				}
				
				// save any changes to defaults to preferences
				if (openAfterExport != dlg.isOpenAfterExport()) {
					prefs.putBoolean(ReportingPreferencesConstants.OPEN_AFTER_EXPORT, dlg.isOpenAfterExport());
				}
				
				File actualFile = new File(targetFileName);
				if (dir.equals(actualFile.getParent()) == false) {
					LOG.info("Save directory changed to " + actualFile.getParent()); //$NON-NLS-1$
					prefs.put(ReportingPreferencesConstants.LAST_SAVE_DIR, actualFile.getParent());
				}
				
				String fileTypeAfter = getTargetFileExtension(true);
				if (fileType.equals(fileTypeAfter) == false) {
					LOG.info("Preferred file type extension changed to " + fileTypeAfter); //$NON-NLS-1$
					prefs.put(ReportingPreferencesConstants.PREFERRED_FILE_TYPE, fileTypeAfter);
				}
				
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					LOG.error("Error saving preferences", e); //$NON-NLS-1$
				}
	    	}
	    } else {
	    	LOG.info("Document export was cancelled"); //$NON-NLS-1$
	    }
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean confirmOverwrite(Shell shell, String fileName) {
		File file = new File(fileName);
		
		if (file.exists()) {
			LOG.warn(String.format("File [%s] exists. Getting confirmation to overwrite", fileName)); //$NON-NLS-1$
			MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			msgBox.setMessage(Messages.bind(Messages.ReportGenerationUtil_confirmOverwriteMsg, targetFileName));
			return msgBox.open() == SWT.OK;
		}
		
		return true;
	}
	
	/**
	 * @param shell
	 */
	private void showSuccessPopup(Shell shell) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		msgBox.setMessage(Messages.bind(Messages.ReportGenerationUtil_successMsg, targetFileName));
		msgBox.setText(Messages.ReportGenerationUtil_successText);
		msgBox.open();
	}
	
	/**
	 * 
	 */
	private boolean openFileInExternalProgram() {
		try {
			final String type = getTargetFileExtension(false);
			Program p = Program.findProgram(type);
			if (p == null) {
				LOG.warn("No program found for file type " + type);
				return false;
			} else {
				p.execute(targetFileName);
				return true;
			}
		} catch (Exception e) {
			LOG.error(String.format("Could not open file [%s] in external program", targetFileName)); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * 
	 * @param includeDot
	 * @return
	 */
	private String getTargetFileExtension(boolean includeDot) {
		return targetFileName.substring(includeDot ? targetFileName.lastIndexOf(".") : targetFileName.lastIndexOf(".") + 1);
	}
	
	/**
	 *
	 */
	private class ReportProgressMonitor implements IRunnableWithProgress, ReportGenerationMonitor {
		/** */
		private IProgressMonitor monitor;
		
		/**
		 * {@inheritDoc}.
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			this.monitor = monitor;
			
			MONITOR_LOG.debug("Calling reporting service...");
			reportingService.generateReport(handler.getReportId(), handler.getModelObject(), targetFileName, this);
						
			monitor.worked(1);
			monitor.done();
		}
				
		/**
		 * {@inheritDoc}.
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#startingReportGeneration()
		 */
		@Override
		public void startingReportGeneration() {
			MONITOR_LOG.debug(String.format("Starting report generation [%s] to file [%s]", handler.getReportId(), targetFileName));
			monitor.beginTask(Messages.ReportProgressMonitor_startingReportGeneration, 5);
			monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#loadingTemplate()
		 */
		@Override
		public void loadingTemplate() {
			MONITOR_LOG.debug("Loading template " + handler.getReportId());
		    monitor.subTask(Messages.ReportProgressMonitor_loadingTemplate);
		    monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#addingReportParameters()
		 */
		@Override
		public void addingReportParameters() {
			MONITOR_LOG.debug("Adding report parameters");
		    monitor.subTask(Messages.ReportProgressMonitor_addingReportParameters);
		    monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#fillingReport()
		 */
		@Override
		public void fillingReport() {
			MONITOR_LOG.debug("Filling report");
			monitor.subTask(Messages.ReportProgressMonitor_fillingReport);
			monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#exportingReport()
		 */
		@Override
		public void exportingReport() {
			MONITOR_LOG.debug("Exporting report to " + targetFileName);
		    monitor.subTask(Messages.ReportProgressMonitor_exportingReportToFile);
		    monitor.worked(1);
		}

		/**
		 * {@inheritDoc}
		 * @see de.tfsw.accounting.reporting.ReportGenerationMonitor#exportFinished()
		 */
		@Override
		public void exportFinished() {
			MONITOR_LOG.debug(String.format("Finished export of report [%s] to file [%s]", handler.getReportId(), targetFileName));
			monitor.subTask(Messages.ReportProgressMonitor_exportFinished);
			monitor.done();
		}
	}
}
