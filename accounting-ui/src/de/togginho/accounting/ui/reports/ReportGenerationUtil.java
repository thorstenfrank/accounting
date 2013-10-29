/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.ui.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.togginho.accounting.AccountingException;
import de.togginho.accounting.Constants;
import de.togginho.accounting.ReportGenerationMonitor;
import de.togginho.accounting.ReportingService;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;
import de.togginho.accounting.util.TimeFrame;

/**
 * @author thorsten
 *
 */
public class ReportGenerationUtil {

	/** */
	private static final Logger LOG = Logger.getLogger(ReportGenerationUtil.class);
	
	private ReportingService reportingService;
	
	private ReportGenerationHandler handler;
	
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
		
    	Calendar from = Calendar.getInstance();
    	from.setTime(timeFrame.getFrom());
    	Calendar until = Calendar.getInstance();
    	until.setTime(timeFrame.getUntil());
    	
    	if (from.get(Calendar.YEAR) == until.get(Calendar.YEAR)) {
    		sb.append(Constants.UNDERSCORE).append(from.get(Calendar.YEAR));
        	if (from.get(Calendar.MONTH) == until.get(Calendar.MONTH)) {
        		int month = from.get(Calendar.MONTH) + 1;
        		if (month < 10) { // prepend a zero to single-digit months
        			sb.append("0"); //$NON-NLS-1$
        		}
        		sb.append(month);
        	}
    	}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param handler
	 */
	private void doExecuteReportGeneration(ReportGenerationHandler handler, Shell shell) {
		this.handler = handler;
		reportingService = AccountingUI.getDefault().getReportingService();
	    if (reportingService == null) {
	    	throw new AccountingException(Messages.ReportGenerationUtil_errorNoReportingService);
	    }
	    
	    ReportFileChooseDialog dlg = new ReportFileChooseDialog(shell, handler.getTargetFileNameSuggestion());
	    if (dlg.show()) {
	    	targetFileName = dlg.getTargetFile();
	    	if (targetFileName != null && confirmOverwrite(shell, targetFileName)) {
				try {
					LOG.info("Starting PDF generation to file " + targetFileName); //$NON-NLS-1$
					ProgressMonitor generation = new ProgressMonitor();
					new ProgressMonitorDialog(shell).run(true, false, generation);
				} catch (Exception e) {
					LOG.error("Error creating PDF", e); //$NON-NLS-1$
					throw new AccountingException(Messages.ReportGenerationUtil_errorGeneratingInvoice, e);
				}
				
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setMessage(Messages.bind(Messages.ReportGenerationUtil_successMsg, targetFileName));
				msgBox.setText(Messages.ReportGenerationUtil_successText);
				msgBox.open();
				
				if (dlg.isOpenAfterExport()) {
					Program p = Program.findProgram("pdf");
					p.execute(targetFileName);
				}
	    	}
	    } else {
	    	LOG.info("PDF export was cancelled"); //$NON-NLS-1$
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
	 *
	 */
	private class ProgressMonitor implements IRunnableWithProgress, ReportGenerationMonitor {
		
		/** */
		private IProgressMonitor monitor;
		
		/**
		 * {@inheritDoc}.
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			this.monitor = monitor;
			
			handler.handleReportGeneration(reportingService, targetFileName, this);
			
			monitor.worked(1);
			monitor.done();
		}
				
		/**
		 * {@inheritDoc}.
		 * @see de.togginho.accounting.ReportGenerationMonitor#startingReportGeneration()
		 */
		@Override
		public void startingReportGeneration() {
			monitor.beginTask(Messages.ExportInvoiceCommand_startingReportGeneration, 5);
			monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.togginho.accounting.ReportGenerationMonitor#loadingTemplate()
		 */
		@Override
		public void loadingTemplate() {
		    monitor.subTask(Messages.ExportInvoiceCommand_loadingTemplate);
		    monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.togginho.accounting.ReportGenerationMonitor#addingReportParameters()
		 */
		@Override
		public void addingReportParameters() {
		    monitor.subTask(Messages.ExportInvoiceCommand_addingReportParameters);
		    monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.togginho.accounting.ReportGenerationMonitor#fillingReport()
		 */
		@Override
		public void fillingReport() {
			monitor.subTask(Messages.ExportInvoiceCommand_fillingReport);
			monitor.worked(1);
		}
		
		/**
		 * {@inheritDoc}.
		 * @see de.togginho.accounting.ReportGenerationMonitor#exportingReport()
		 */
		@Override
		public void exportingReport() {
		    monitor.subTask(Messages.ExportInvoiceCommand_exportingReportToFile);
		    monitor.worked(1);
		}
	}
}
