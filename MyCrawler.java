import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js"
			+ "|mp3|zip|gz))$");
	
	BufferedWriter bw1 = null;
	BufferedWriter bw2 = null;
	BufferedWriter bw3 = null;
	BufferedWriter txt1 = null;
	String fetchStatistics = null;
	String visitStatistics = null;
	String urlStatistics = null;
	
	int noOfFetchesAttempted = 0;
	int noOfFetchesSucceeded = 0;
	int noOfFetchesAborted = 0;
	int noOfFetchesFailed = 0;
	
	int noOfUrlsExtracted = 0;
	int noOfUniqueUrls = 0;
	int noOfUniqueUrlsWithinSite = 0;
	int noOfUniqueUrlsOutsideSite = 0;
	
	int noOf200s = 0;
	int noOf301s = 0;
	int noOf401s = 0;
	int noOf403s = 0;
	int noOf404s = 0;
	
	int noOfFilesUnder1kb = 0;
	int noOfFiles1To10kb = 0;
	int noOfFiles10to100kb = 0;
	int noOfFiles100to1000kb = 0;
	int noOfFilesOver1000kb = 0;
	
	int noOfHtmlFiles = 0;
	int noOfGifFiles = 0;
	int noOfTifFiles = 0;
	int noOfJpegFiles = 0;
	int noOfPngFiles = 0;
	int noOfPdfFiles = 0;	
	
	@Override
	public void onStart() {
		try {
			bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("fetch_NYTimes.csv", true), "UTF-8"));
			bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("visit_NYTimes.csv", true), "UTF-8"));
			bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("urls_NYTimes.csv", true), "UTF-8"));
			txt1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("CrawlReport_NYTimes.txt", true), "UTF-8"));
			
			try {
	            txt1.write("Name: Rasvitha Kandur");
	            txt1.newLine();	
	            txt1.write("USC ID:5987641452");
	            txt1.newLine();
	            txt1.write("News site crawled: https://www.nytimes.com/");
	            txt1.newLine();
	            txt1.newLine();	                        			
	        }
	        catch (Exception e) { }			
		}
		catch (UnsupportedEncodingException e) { }
        catch (FileNotFoundException e) { }
	}
	
	/**
	* This method receives two parameters. The first parameter is the page
	* in which we have discovered this new url and the second parameter is
	* the new url. You should implement this function to specify whether
	* the given url should be crawled or not (based on your crawling logic).
	* In this example, we are instructing the crawler to ignore urls that
	* have css, js, git, ... extensions and to only accept urls that start
	* with "httpss://www.viterbi.usc.edu/". In this case, we didn't need the
	* referringPage parameter to make the decision.
	*/
	
	ArrayList<String> urlsList = new ArrayList<String>();
	boolean isUnique = false;
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{		
		String href = url.getURL();		
		try 
		{
			if(!urlsList.contains(href)) 
			{
				urlsList.add(href);
				noOfUniqueUrls++;
				isUnique = true;
			}
			else isUnique = false;
			
			noOfUrlsExtracted++;
            StringBuffer oneLine = new StringBuffer();
            oneLine.append(href.replaceAll(",", "-"));
            oneLine.append(",");
            if(href.startsWith("https://www.nytimes.com/")) 
			{
            	oneLine.append("OK");
            	if(isUnique) noOfUniqueUrlsWithinSite++;
            }
            else {
            	oneLine.append("N_OK");
            	if(isUnique) noOfUniqueUrlsOutsideSite++;
            }
            bw3.write(oneLine.toString());
        }
        catch (Exception e) { }	
		finally { 
			try { bw3.newLine(); }
			catch(Exception ex) { }
		}
				
		return !FILTERS.matcher(href).matches() && href.startsWith("https://www.nytimes.com/");
	}
		
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {		
		try
        {		
			noOfFetchesAttempted++;
			
			if(statusCode == 200) {
				noOfFetchesSucceeded++;
				noOf200s++;
			}
			else {
				noOfFetchesFailed++;
				if(statusCode == 301) noOf301s++;
				if(statusCode == 401) noOf401s++;
				if(statusCode == 403) noOf403s++;
				if(statusCode == 404) noOf404s++;
			}
			
            StringBuffer oneLine = new StringBuffer();
            oneLine.append(webUrl.getURL().replaceAll(",", "-"));
            oneLine.append(",");
            oneLine.append(statusCode);                
            bw1.write(oneLine.toString()); 
            bw1.newLine();
        }
        catch (Exception e) { 
        	noOfFetchesAborted++;
        }		
	}
	
	/**
	* This function is called when a page is fetched and ready
	* to be processed by your program.
	*/
	@Override
	public void visit(Page page) {		
		try {
			String url = page.getWebURL().getURL();
			byte[] pageContent = page.getContentData();
			double fileSize = (pageContent == null? 0 : pageContent.length)/1024.0;
			
			if(fileSize < 1) noOfFilesUnder1kb++;
			else if(fileSize >= 1 && fileSize < 10) noOfFiles1To10kb++;
			else if(fileSize >= 10 && fileSize < 100) noOfFiles10to100kb++;
			else if(fileSize >= 100 && fileSize < 1024) noOfFiles100to1000kb++;
			else if(fileSize >= 1024) noOfFilesOver1000kb++;
			
            StringBuffer oneLine = new StringBuffer();
            oneLine.append(url.replaceAll(",", "-"));
            oneLine.append(",");
            oneLine.append(fileSize + " kb");
            oneLine.append(",");
            
            if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				Set<WebURL> links = htmlParseData.getOutgoingUrls();
				int linksSize = htmlParseData == null? 0 : links.size();
				oneLine.append(linksSize);
			}
            else {
            	ParseData parseData = page.getParseData();
            	Set<WebURL> links = parseData.getOutgoingUrls();
            	int linksSize = parseData == null? 0 : links.size();
				oneLine.append(linksSize);
            }
            oneLine.append(",");
            
            String contentType = page.getContentType().split(";")[0];            
            oneLine.append(contentType);                                  
            bw2.write(oneLine.toString());
            
            if(contentType.equals("text/html")) noOfHtmlFiles++;
            else if(contentType.equals("image/gif")) noOfGifFiles++;
            else if(contentType.equals("image/tif")) noOfTifFiles++;
            else if(contentType.equals("image/jpeg")) noOfJpegFiles++;
            else if(contentType.equals("image/png")) noOfPngFiles++;
            else if(contentType.equals("application/pdf")) noOfPdfFiles++;
            
        }
        catch (IOException e) { }
		finally { 
			try { bw2.newLine(); }
			catch(Exception ex) { }
		}
	}
	
	@Override
	public void onBeforeExit() {
		try {
			txt1.write("Fetch Statistics");
			txt1.newLine();
			txt1.write("================");
			txt1.newLine();
			txt1.write("# fetches attempted: " + noOfFetchesAttempted);
			txt1.newLine();
			txt1.write("# fetches succeeded: " + noOfFetchesSucceeded);
			txt1.newLine();
			txt1.write("# fetches aborted: " + noOfFetchesAborted);
			txt1.newLine();
			txt1.write("# fetches failed: " + noOfFetchesFailed);			
			txt1.newLine();
			txt1.newLine();
			
			txt1.write("Outgoing URLs");
			txt1.newLine();
			txt1.write("=============");
			txt1.newLine();
			txt1.write("Total URLs extracted: " + noOfUrlsExtracted);
			txt1.newLine();
			txt1.write("# unique URLs extracted: " + noOfUniqueUrls);
			txt1.newLine();
			txt1.write("# unique URLs within News Site: " + noOfUniqueUrlsWithinSite);
			txt1.newLine();
			txt1.write("# unique URLs outside News Site: " + noOfUniqueUrlsOutsideSite);			
			txt1.newLine();
			txt1.newLine();
			
			txt1.write("Status Codes");
			txt1.newLine();
			txt1.write("============");
			txt1.newLine();
			txt1.write("200 OK: " + noOf200s);
			txt1.newLine();
			txt1.write("301 Moved Permanently: " + noOf301s);
			txt1.newLine();
			txt1.write("401 Unauthorized: " + noOf401s);
			txt1.newLine();
			txt1.write("403 Forbidden: " + noOf403s);			
			txt1.newLine();
			txt1.write("404 Not Found: " + noOf404s);			
			txt1.newLine();
			txt1.newLine();
			
			txt1.write("File Sizes");
			txt1.newLine();
			txt1.write("==========");
			txt1.newLine();
			txt1.write("< 1KB: " + noOfFilesUnder1kb);
			txt1.newLine();
			txt1.write("1KB ~ <10KB: " + noOfFiles1To10kb);
			txt1.newLine();
			txt1.write("10KB ~ <100KB: " + noOfFiles10to100kb);
			txt1.newLine();
			txt1.write("100KB ~ <1MB: " + noOfFiles100to1000kb);			
			txt1.newLine();
			txt1.write(">= 1MB: " + noOfFilesOver1000kb);			
			txt1.newLine();
			txt1.newLine();
			
			txt1.write("Content Types");
			txt1.newLine();
			txt1.write("=============");
			txt1.newLine();
			txt1.write("text/html: " + noOfHtmlFiles);
			txt1.newLine();
			txt1.write("image/gif: " + noOfGifFiles);
			txt1.newLine();
			txt1.write("image/tif: " + noOfTifFiles);
			txt1.newLine();
			txt1.write("image/jpeg: " + noOfJpegFiles);
			txt1.newLine();
			txt1.write("image/png: " + noOfPngFiles);			
			txt1.newLine();
			txt1.write("application/pdf: " + noOfPdfFiles);			
			txt1.newLine();	
			
			bw1.close(); bw2.close(); bw3.close(); txt1.close();
		}
		catch(IOException ex) {}
	}
}
