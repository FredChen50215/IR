package com.CrawlerMaven;

import java.util.List;

import org.jsoup.nodes.Element;
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
	private App() {}
    public static void main(List<String> urlList)
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
	        	if(count == 500)break;
	        	count++;
	        }
	        }catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	        List<WebElement> elements = driver.findElements(By.className("userContentWrapper"));
	        int j = 0;
	        for (int i = 0; i < elements.size(); i++) {
	            WebElement user = elements.get(i).findElement(By.className("fwb"));
	            WebElement userContent = elements.get(i).findElement(By.className("userContent"));
	            System.out.println((j++) + " :" + user.getText());
	            System.out.println("內容 :" + userContent.getText());
	            System.out.println("\n");
	        }
	    }
        driver.close(); 
    }
}
