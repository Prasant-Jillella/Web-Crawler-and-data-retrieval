import edu.uci.ics.crawler4j.crawler.CrawlConfig; 
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args)throws Exception 
	{
		// TODO Auto-generated method stub
		String crawlStorageFolder = "/data/crawl";
		int numberOfCrawlers = 1;
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		
		int maxDepthOfCrawling = 16;
		int maxPagesToFetch = 20000;
		int politenessDelay = 200;
		
		
		config.setIncludeBinaryContentInCrawling(true);
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		config.setMaxPagesToFetch(maxPagesToFetch);
		config.setPolitenessDelay(politenessDelay);
		config.setUserAgentString("Rasvitha Kandur");
		
		/*
		* Instantiate the controller for this crawl.
		*/
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		/*
		* For each crawl, you need to add some seed urls. These are the first
		* URLs that are fetched and then the crawler starts following links
		* which are found in these pages
		*/
		controller.addSeed("https://www.nytimes.com/");
		/*
		* Start the crawl. This is a blocking operation, meaning that your code
		*/
		controller.start(MyCrawler.class, numberOfCrawlers);
	}
}
