**XScraper**
Selenium WebDriver project for automating Create a Scraper website using Java.

**testCase01**: Go to this website and click on "Hockey Teams: Forms, Searching and Pagination"

Iterate through the table and collect the Team Name, Year and Win % for the teams with Win % less than 40% (0.40)

Iterate through 4 pages of this data and store it in a ArrayList

Convert the ArrayList object to a JSON file named hockey-team-data.json. Feel free to use Jackson library. (In the example, Maven is used, but you can resolve dependencies similarly by copying Gradle import from Maven Central). Each Hashmap object should contain:

Epoch Time of Scrape

Team Name

Year

Win %

**testCase02**: Go to this website and click on "Oscar Winning Films"

Click on each year present on the screen and find the top 5 movies on the list - store in an ArrayList.

Keep a Boolean variable "isWinner" which will be true only for the best picture winner of that year.

Keep a variable to maintain the year from which the data is scraped

Convert the ArrayList object to a JSON file named oscar-winner-data.json. Each HashMap object should contain:

Epoch Time of Scrape

Year

Title

Nomination

Awards

isWinner

Store the file in the output folder in the root directory. Assert using TestNG that the file is present and not empty
