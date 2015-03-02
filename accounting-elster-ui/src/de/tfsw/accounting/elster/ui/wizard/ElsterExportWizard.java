/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.elster.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.time.YearMonth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import de.tfsw.accounting.elster.ElsterDTO;
import de.tfsw.accounting.elster.ui.ElsterUI;

/**
 * @author Thorsten Frank
 *
 */
public class ElsterExportWizard extends Wizard {
	
	/**
	 * 
	 */
	private static final String USER_HOME = "user.home";

	/**
	 * 
	 */
	private static final String LAST_KNOWN_EXPORT_DIRECTORY = "lastKnownExportDirectory";
	
	/**
	 * 
	 */
	private static final String SEPARATOR = System.getProperty("file.separator"); //$NON-NLS-1$
	
	/**
	 * 
	 */
	private ElsterDTO elsterDTO;
	
	/**
	 * 
	 */
	private String targetFileName;
	
	/**
	 * 
	 */
	public ElsterExportWizard() {
		super();
		setHelpAvailable(true);
		setWindowTitle(Messages.ElsterExportWizard_Title);
		setNeedsProgressMonitor(true);
		
		this.elsterDTO = ElsterUI.getDefault().getElsterService().getElsterDTO(YearMonth.now().minusMonths(1));
		
		initTargetFileName();
	}
	
	/**
	 * 
	 */
	private void initTargetFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getLastKnownSaveDir());
		sb.append("ustva_");
		sb.append(elsterDTO.getTimeFrameYear());
		sb.append(elsterDTO.getTimeFrameMonth());
		this.targetFileName = sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	private String getLastKnownSaveDir() {
		String dir = ElsterUI.getPreferences().get(LAST_KNOWN_EXPORT_DIRECTORY, System.getProperty(USER_HOME));
		return dir.endsWith(SEPARATOR) ? dir : dir + SEPARATOR;
	}

	/**
	 * 
	 */
	private void saveLastKnownSaveDir() {
		if (targetFileName != null && !targetFileName.isEmpty()) {
			ElsterUI.getPreferences().put(LAST_KNOWN_EXPORT_DIRECTORY, targetFileName.substring(0, targetFileName.lastIndexOf(SEPARATOR)));
		}
	}
	
	/**
	 * @return the elsterDTO
	 */
	protected ElsterDTO getElsterDTO() {
		return elsterDTO;
	}
	
	/**
	 * 
	 * @param newPeriod
	 */
	protected void adaptDtoToNewPeriod(YearMonth newPeriod) {
		// only adapt if there is an actual change in the requested period - and of course if the period supplied is
		// valid
		if (newPeriod != null && !newPeriod.equals(elsterDTO.getFilingPeriod())) {
			elsterDTO = ElsterUI.getDefault().getElsterService().adaptToPeriod(elsterDTO, newPeriod);
		}
	}
	
	/**
	 * @return the targetFileName
	 */
	protected String getTargetFileName() {
		return targetFileName;
	}

	/**
	 * @param targetFileName the targetFileName to set
	 */
	protected void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	/**
	 * 
	 * @return
	 */
	protected String generatePreview() {
		return ElsterUI.getDefault().getElsterService().generateXML(elsterDTO);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new TimeFrameSelectionPage());
		addPage(new CompanyNamePage());
		addPage(new AmountsPage());
		addPage(new ExportTargetSelectionPage());
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		String message = null;
		boolean success = false;
		try {
			performExport();
			saveLastKnownSaveDir();
			success = true;
		} catch (InvocationTargetException e) {
			message = e.getCause().getLocalizedMessage();
			success = false;
		} catch (Exception e) {
			message = e.getLocalizedMessage();
			success = false;
		}
		
		((AbstractElsterWizardPage)getContainer().getCurrentPage()).setErrorMessage(message);
		
		return success;
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	private void performExport() throws InvocationTargetException, InterruptedException {
		getContainer().run(false, false, new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("elsterExportTask", 2);
				monitor.internalWorked(1);
				ElsterUI.getDefault().getElsterService().writeToXmlFile(elsterDTO, targetFileName);
				monitor.internalWorked(2);
			}
		});
	}
	
//	public static void main(String[] args) {
//		String file = "Z:\\accounting\\hoppsasa_201501.xml";
//		System.out.println(file.substring(0, file.lastIndexOf("\\")));
//		String fileName = file.substring(file.lastIndexOf("\\") + 1);
//		System.out.println(fileName);
//		if (fileName.startsWith("ustva_")) {
//			System.out.println("keeping default filename");
//		} else {
//			System.out.println("new pattern");
//			fileName = fileName.substring(0, fileName.lastIndexOf("."));
//			System.out.println(fileName);
//		}
//	}
}
