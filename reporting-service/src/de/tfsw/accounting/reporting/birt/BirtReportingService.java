package de.tfsw.accounting.reporting.birt;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.tfsw.accounting.ReportingService;
import de.tfsw.accounting.model.UserProfile;
import de.tfsw.accounting.util.FileUtil;

@Component(service = ReportingService.class, enabled = true, immediate = true)
public class BirtReportingService implements ReportingService {

	private static final Logger LOG = LogManager.getLogger(BirtReportingService.class);
	
	@Activate
	protected void activate() {
		LOG.info("<<< BIRT REPORTING IS ONLINE >>>");
	}
	
	@SuppressWarnings("unchecked")
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
			
			UserProfile up = new UserProfile();
			up.setName("Hannes Haumichblau");
			up.setDescription("Awesomeness Consultant\nand\nmulti-line commenter");
			
			LOG.info("Generating report to {}", targetLocation);
			IReportRunnable report = engine.openReportDesign(designUrl.openStream());
			IRunAndRenderTask task = engine.createRunAndRenderTask(report);
			
			task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, this.getClass().getClassLoader());
			task.getAppContext().put("userProfile.input", up);
			
			task.setRenderOption(pdfOptions);
			task.run();
			
			LOG.debug("From report: {}", task.getAppContext().get("from.report")); 
			
			task.close();
			engine.destroy();
		} catch (Exception e) {
			LOG.error("Error startup up BIRT", e);
		}

	}
}
