package de.tfsw.accounting.reporting.birt;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.tfsw.accounting.ReportingService;
import de.tfsw.accounting.util.FileUtil;

@Component(service = ReportingService.class, enabled = true, immediate = true)
public class BirtReportingService implements ReportingService {

	private static final Logger LOG = LogManager.getLogger(BirtReportingService.class);
	
	@Activate
	protected void activate() {
		LOG.info("<<< BIRT REPORTING IS ONLINE >>>");
	}
	
	@Override
	public void test(String targetLocation) {
		LOG.info("I will report something now...");
		
		try {
			EngineConfig config = new EngineConfig();
			config.setBIRTHome(FileUtil.absoluteInstanceAreaPath("reporting"));
			
			Platform.startup(config);
			
			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
					IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			
			IReportEngine engine = factory.createReportEngine(config);
			
			PDFRenderOption pdfOptions = new PDFRenderOption();
			pdfOptions.setOutputFileName(targetLocation);
			pdfOptions.setOutputFormat("pdf");
			
			URL designUrl = new URL("platform:/plugin/de.tfsw.accounting.reporting.service/se_report.rptdesign");
			LOG.debug("Raw report source URL: {}", designUrl.toString());
			
			Map<String, Object> params = new HashMap<>();
			params.put("userProfileName", "Test Output User Profile Name");
			
			LOG.info("Generating report to {}", targetLocation);
			IReportRunnable report = engine.openReportDesign(designUrl.openStream());
			IRunAndRenderTask task = engine.createRunAndRenderTask(report);
			task.setRenderOption(pdfOptions);
			task.setParameterValues(params);
			task.run();
			task.close();
			engine.destroy();
		} catch (Exception e) {
			LOG.error("Error startup up BIRT", e);
		}

	}
}
