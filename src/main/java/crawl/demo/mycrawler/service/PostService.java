package crawl.demo.mycrawler.service;

import crawl.demo.mycrawler.model.Post;
import crawl.demo.mycrawler.model.PostRequest;
import crawl.demo.mycrawler.repository.PostRepository;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class PostService {

    private PostRepository postRepository;
    private ElasticsearchRestTemplate elasticsearchTemplate;

    private LinkedList<String> frontier;
    private Set<String> urlsVisited;
    private List<Post> postList;
    private int page = 1;

    public static final int COLUMN_INDEX_TITLE = 0;
    public static final int COLUMN_INDEX_PRICE = 1;
    public static final int COLUMN_INDEX_AREA = 2;
    public static final int COLUMN_INDEX_ADDRESS = 3;
    public static final int COLUMN_INDEX_TYPEAD = 4;
    public static final int COLUMN_INDEX_PHONE_NUMBER = 5;
    public static final int COLUMN_INDEX_IMAGE = 6;
    public static final int COLUMN_INDEX_LINK = 7;
    private static CellStyle cellStyleFormatNumber = null;

    @Autowired
    public PostService(PostRepository postRepository, ElasticsearchRestTemplate elasticsearchTemplate) {
        this.postRepository = postRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public PostService() {
    }


    public void resetGlobalVariables() {
        this.frontier = new LinkedList<String>();
        this.urlsVisited = new HashSet<String>();
        this.postList = new ArrayList<>();
    }

    public List<Post> crawlPost(PostRequest request) {
        resetGlobalVariables();
        frontier.add(request.getSeedPage());
        do {
            crawl(frontier.poll(), request);
            page++;
        } while (!frontier.isEmpty() && page <= 100);

        return postList;
    }

    private void crawl(String url, PostRequest request) {
        WebDriver driver = download(url);
        if (driver != null) {
            parseDocument(driver, request);
            driver.quit();
        }
    }

    private WebDriver download(String url) {
        try {
            if (!urlsVisited.contains(url)) {
                System.setProperty("webdriver.chrome.driver",
                        "C:\\chromedriver_win32\\chromedriver.exe");
                WebDriver driver = new ChromeDriver();
                driver.get(url);
                Thread.sleep(1000);

                urlsVisited.add(url);
                return driver;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseDocument(WebDriver driver, PostRequest request) {
        try {
            String nextPageUrl = driver.findElement(By.cssSelector(request.getNextPageTag())).getAttribute("href");
            if (!urlsVisited.contains(nextPageUrl)) {
                frontier.add(nextPageUrl);
            }

            List<WebElement> postsOnPage = driver.findElements(By.cssSelector(request.getPostWrapperTag()));
            for (WebElement item : postsOnPage) {
                Post post = new Post();
                String link = "";
                if (!request.getLinkTag().isEmpty() && request.getLinkTag().trim() != "") {
                    link = item.findElement(By.cssSelector(request.getLinkTag())).getAttribute("href");
                }

                if (!request.getImageTag().isEmpty() && request.getImageTag().trim() != "") {
                    String image = item.findElement(By.cssSelector(request.getImageTag())).getAttribute("src");
                    post.setImage(image);
                } else {
                    post.setImage("");
                }

                if (link != "") {
                    post.setLink(link);
                    driver = download(link);
                }

                if (driver != null) {
                    post = parseEachPage(driver, post, request);
                }

                postList.add(post);
            }

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Post parseEachPage(WebDriver driver, Post post, PostRequest request) {
        try {
            post.setTitle(driver.findElement(By.cssSelector(request.getTitleTag())).getText());
            post.setPrice(driver.findElement(By.cssSelector(request.getPriceTag())).getText());
            post.setArea(driver.findElement(By.cssSelector(request.getAreaTag())).getText());
            post.setAddress(driver.findElement(By.cssSelector(request.getAddressTag())).getText());
            post.setPhoneNumber(driver.findElement(By.cssSelector(request.getPhoneNumberTag())).getText());

            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public String extractToExcel(List<Post> postList) {
        try {
            String excelFilePath = "C:/crawler/bds" + System.currentTimeMillis() + ".xlsx";
            writeExcel(postList, excelFilePath);
            return "Successful!";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Failed";
    }

    private void writeExcel(List<Post> postList, String excelFilePath) {
        // Create Workbook
        SXSSFWorkbook workbook = new SXSSFWorkbook();

        // Create sheet
        SXSSFSheet sheet = workbook.createSheet("Bất động sản");

        // register the columns you wish to track and compute the column width
        sheet.trackAllColumnsForAutoSizing();

        int rowIndex = 0;

        // Write header
        writeHeader(sheet, rowIndex);

        // Write data
        rowIndex++;
        for (Post post : postList) {
            // Create row
            SXSSFRow row = sheet.createRow(rowIndex);
            // Write data on row
            writeBook(post, row);
            rowIndex++;
        }

        // Auto resize column witdth
        int numberOfColumn = 8; // sheet.getRow(0).getPhysicalNumberOfCells();
        autosizeColumn(sheet, numberOfColumn);

        // Create file excel
        createOutputFile(workbook, excelFilePath);
        System.out.println("Done!!!");
    }

    private void writeHeader(SXSSFSheet sheet, int rowIndex) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        // Create row
        SXSSFRow row = sheet.createRow(rowIndex);

        // Create cells
        SXSSFCell cell = row.createCell(COLUMN_INDEX_TITLE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Tiêu đề");

        cell = row.createCell(COLUMN_INDEX_PRICE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Giá");

        cell = row.createCell(COLUMN_INDEX_AREA);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Diện tích");

        cell = row.createCell(COLUMN_INDEX_ADDRESS);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Địa chỉ");

        cell = row.createCell(COLUMN_INDEX_PHONE_NUMBER);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Số điện thoại");

        cell = row.createCell(COLUMN_INDEX_IMAGE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Hình ảnh");

        cell = row.createCell(COLUMN_INDEX_LINK);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Link");
    }

    private static CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Calibri");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // font size

        // Create cell style
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private static CellStyle createStyleForHyperlink(Sheet sheet) {
        Font linkFont = sheet.getWorkbook().createFont();
        linkFont.setUnderline(Font.U_SINGLE);
        linkFont.setColor(IndexedColors.BLUE.getIndex());

        CellStyle linkStyle = sheet.getWorkbook().createCellStyle();
        linkStyle.setFont(linkFont);
        return linkStyle;
    }

    private void writeBook(Post post, SXSSFRow row) {
        SXSSFCell cell = row.createCell(COLUMN_INDEX_TITLE);
        cell.setCellValue(post.getTitle());

        cell = row.createCell(COLUMN_INDEX_PRICE);
        cell.setCellValue(post.getPrice());

        cell = row.createCell(COLUMN_INDEX_AREA);
        cell.setCellValue(post.getArea());

        cell = row.createCell(COLUMN_INDEX_ADDRESS);
        cell.setCellValue(post.getAddress());

        cell = row.createCell(COLUMN_INDEX_PHONE_NUMBER);
        cell.setCellValue(post.getPhoneNumber());

        Hyperlink link;
        cell = row.createCell(COLUMN_INDEX_IMAGE);
        if (post.getImage() != null) {
//            cell.setCellValue("file:///" + post.getImage());
            cell.setCellValue(post.getImage());
            link = row.getSheet().getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress(post.getImage());
            cell.setHyperlink(link);
            cell.setCellStyle(createStyleForHyperlink(row.getSheet()));
        }

        cell = row.createCell(COLUMN_INDEX_LINK);
        cell.setCellValue("Go to link");
        link = row.getSheet().getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
        link.setAddress(post.getLink());
        cell.setHyperlink(link);
        cell.setCellStyle(createStyleForHyperlink(row.getSheet()));
    }

    private void autosizeColumn(SXSSFSheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    private void createOutputFile(SXSSFWorkbook workbook, String excelFilePath) {
        try {
            OutputStream os = new FileOutputStream(excelFilePath);
            workbook.write(os);
            os.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String savePost(List<Post> postList) {
        try {
            for (Post post : postList) {
                post.setId(UUID.randomUUID().toString().replace("-", ""));
                postRepository.save(post);
            }
            return "Success";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Failed";
    }
}

