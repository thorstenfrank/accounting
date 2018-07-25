package de.tfsw.accounting.reporting.birt;

import java.io.File;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.tfsw.accounting.ReportingService;

@Component(service = ReportingService.class, enabled = true, immediate = true)
public class BirtReportingService implements ReportingService {

	private static final Logger LOG = LogManager.getLogger(BirtReportingService.class);
	
	@Activate
	protected void activate() {
		LOG.info("<<< BIRT REPORTING IS ONLINE >>>");
	}
	
	@Override
	public void test() {
		LOG.info("I will report something now...");
		
		try {
			EngineConfig config = new EngineConfig();
			//config.setBIRTHome("C:/temp/birt");
			
			Platform.startup(config);
			
			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
					IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			
			IReportEngine engine = factory.createReportEngine(config);
			
			LOG.info("BIRT home: {}", config.getBIRTHome());
			
			
			PDFRenderOption pdfOptions = new PDFRenderOption();
			pdfOptions.setOutputFileName("C:\\temp\\output.pdf");
			pdfOptions.setOutputFormat("pdf");
			
			URL designUrl = FileLocator.resolve(
					new URL("platform:/plugin/de.tfsw.accounting.reporting.service/se_report.rptdesign"));
			LOG.debug("Raw report source URL: {}", designUrl.toString());
			File f = new File(designUrl.toURI());
			LOG.debug("Report source: {} exists? {}", designUrl.toString(), f.exists());
			
			IReportRunnable runnable = engine.openReportDesign(f.toString());
			IRunAndRenderTask task = engine.createRunAndRenderTask(runnable);
			task.setRenderOption(pdfOptions);
			task.run();
			task.close();
			engine.destroy();
		} catch (Exception e) {
			LOG.error("Error startup up BIRT", e);
		}

	}
}
