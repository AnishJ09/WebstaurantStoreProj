package com.webstaurantstore.qa.testcases;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.netty.handler.timeout.TimeoutException;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ShoppingCartTest {

	public static void main(String[] args) {
		// Setup WebDriver
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(options);

		Wait<WebDriver> fluentWait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)) // Maximum time to
																									// wait
				.pollingEvery(Duration.ofMillis(1000)) // Polling interval
				.ignoring(NoSuchElementException.class) // Ignore specific exceptions
				.ignoring(TimeoutException.class);

		int tableCount = 0;
		int pageCount = 0;
		int pageIndex = 1;
		String lastFoundItemTitle = "";
		String lastFoundItemXpath = "";

		try {

			// Test Case Steps
			// 1. Go to https://www.webstaurantstore.com/
			driver.get("https://www.webstaurantstore.com/");
			driver.manage().window().maximize();

			// 2. Search for 'stainless work table.
			WebElement searchBox = driver.findElement(By.xpath("//*[@id=\"searchval\"]"));
			searchBox.sendKeys("stainless work table");
			searchBox.sendKeys(Keys.RETURN);

			// 3. Check the search result ensuring every product has the word 'Table' in its
			// title.

			while (true) {
				String pageXpath = "//*[@aria-label[contains(., 'page " + pageIndex + "')]]";
				List<WebElement> pages = driver.findElements(By.xpath(pageXpath));

				if (pages.isEmpty()) {
					break;
				}

				WebElement page = pages.get(0);
				page.click();
				pageCount++;

				List<WebElement> products = driver
						.findElements(By.xpath("//*[@id='ProductBoxContainer']/div[1]/a[1]/span[1]"));
				if (products.isEmpty()) {
					break;
				}
				for (WebElement product : products) {
					String title = product.getText();
					if (!title.toLowerCase().contains("table")) {
						System.err.println("Product title does not contain 'Table': " + title);
					} else {
						lastFoundItemTitle = title;
						lastFoundItemXpath = "//*[@id='ProductBoxContainer']/div[1]/a[1]/span[1][@data-testid='itemDescription' and contains(text(), '"
								+ lastFoundItemTitle + "')]";

					}

					tableCount++;
				}
				pageIndex++;

			}

			System.out.println("Number of Pages which contain 'Table': " + pageCount);
			System.out.println("Number of Product titles which contain 'Table': " + tableCount);

			// 4. Add the last found item to Cart

			if (!lastFoundItemTitle.isEmpty() && !lastFoundItemXpath.isEmpty()) {
				WebElement lastFoundItem = driver.findElement(By.xpath(lastFoundItemXpath));
				lastFoundItem.click();
				WebElement addToCartButton = driver.findElement(By.xpath("//*[@id=\"buyButton\"]"));
				addToCartButton.click();
				System.out.println("Added to cart: " + lastFoundItemTitle);
			}

			// 5. Empty Cart

			WebElement viewCart = fluentWait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'View Cart')]")));
			viewCart.click();
			WebElement emptyCart = fluentWait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//*[@id='main']/div[1]/div[1]/div[1]/div[1]/button[1]")));
			emptyCart.click();

			WebElement emptyCartButton = fluentWait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//body[@id='td']/div[11]/div/div/div/footer/button[1]")));

			new Actions(driver).moveToElement(emptyCartButton).perform();

			emptyCartButton.click();

			WebElement elementText = fluentWait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//*[@id='main']/div[1]/div[1]/div[1]/div[1]/div[2]/p[1]")));

			String text = elementText.getText();
			System.out.println("Text content: " + text);

		} finally {
			driver.quit();
		}

	}

}
