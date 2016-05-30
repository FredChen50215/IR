package com.CrawlerMaven;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Hello world!
 *
 */

public class App 
{
	static String fbOwnerName;
	
	private App() {}
    public static void main(List<String> urlList) throws Exception
    {
    	/*初始化 WebDriver開啟Firefox瀏覽器*/
    	WebDriver driver = new FirefoxDriver();
    	
	    /*連至facebook登入頁面*/
	    driver.get("https://zh-tw.facebook.com/login/");
	    
	    /*依照id找尋欄位並填入帳號密碼  按下登入按鈕*/
	    WebElement mailElement = driver.findElement(By.id("email"));
	    mailElement.sendKeys("fred50215@yahoo.com.tw");
	    WebElement passwdElement = driver.findElement(By.id("pass"));
	    passwdElement.sendKeys("imboy7890");
	    WebElement loginElement =driver.findElement(By.id("loginbutton"));
	    loginElement.click();
	    
	    for(String url : urlList){
	        //模拟火狐浏览器
	        driver.get(url);
	        
	        int count = 0;
	    	try {
				Thread.sleep(5000);
				WebElement fbName = driver.findElement(By.id("fb-timeline-cover-name"));
				if(fbName!=null){
					System.out.println("網址擁有者姓名:"+fbName.getText());
			        fbOwnerName = fbName.getText();
				}		        
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        try {
//	        	int top = 0;
//	        	int bottom = 400;
	        //滚动
	        while(true){
	        	Thread.sleep(50);
	        	String sroll = "document.documentElement.scrollTop+=50";
	        	JavascriptExecutor je = (JavascriptExecutor) driver;
	        	je.executeScript(sroll);
//	        	List<WebElement> extendElement =driver.findElements(By.className("see_more_link"));
//	        	top +=50;
//	        	bottom +=50;
//	        	for(WebElement we : extendElement){
//	        		int weBottom = we.getLocation().y;
//	        		System.out.println("top:"+top);
//	        		System.out.println("bottom:"+bottom);
//	        		System.out.println("we:"+weBottom);
//	        		if(weBottom <bottom && weBottom > top)we.click();
//	        	}
	        	Thread.sleep(50);
	        	if(count == 100)break;
	        	count++;
	        }
	        }catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	        writeFile(driver,url);
	        IndexFiles indexfiles = new IndexFiles();
	        //製作index document
	        indexfiles.main(fbOwnerName);
	    }
        driver.close(); 
    }
    
    private static void writeFile(WebDriver driver,String url) throws IOException{
    	FileWriter fileWriter = new FileWriter(fbOwnerName+"CrawlerContent.txt");
    	
    	List<WebElement> elements = driver.findElements(By.className("userContentWrapper"));
        if(elements != null){
        	fileWriter.write("網址擁有者名:" + fbOwnerName+"\r\n");
        	fileWriter.write("網址:" + url+"\r\n");
        	for (int i = 0; i < elements.size(); i++) {
                WebElement user = elements.get(i).findElement(By.className("fwb"));
                WebElement userContent = elements.get(i).findElement(By.className("userContent"));
                //只寫入FB擁有者發布的貼文
                if(user != null){
                	if(user.getText().trim().equals(fbOwnerName.trim())){
                        if(userContent != null){
                        	System.out.println(userContent.getText());
                            fileWriter.write(userContent.getText()+"\r\n");
                        }
                        System.out.println("\n");
                    }
                }
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
