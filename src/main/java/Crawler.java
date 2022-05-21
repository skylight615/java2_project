import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static FileWriter fw = null;
    static List<Integer> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
//        CrawlWikisTime();
//        CrawWikisTimeMonth();
//        CrawTopicsRepositories();
        CrawProjectsTags("https://github.com/search?o=desc&q=deep+learning+topics%3A%3E0+stars%3A%3E123&s=stars&type=Repositories",1);
//        CrawProjectsTags("https://github.com/search?o=desc&q=deep+learning+topics%3A%3E0+stars%3A32..123&s=stars&type=Repositories",2);
//        CrawProjectsTags("https://github.com/search?o=desc&q=deep+learning+topics%3A%3E0+stars%3A14..31&s=stars&type=Repositories",3);
//        CrawProjectsTags("https://github.com/search?o=desc&q=deep+learning+topics%3A%3E0+stars%3A7..13&s=stars&type=Repositories",4);
//        CrawProjectsTags("https://github.com/search?o=desc&q=deep+learning+topics%3A%3E0+stars%3A4..6&s=stars&type=Repositories",5);
    }

    /*search the created time of wikis' information, unit: year
    * store the statistics into the WikiTimeYear
    * due to 2022.4.30
    * */
    private static void CrawlWikisTime() throws IOException {
        File file = new File("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\WikiTimeYear");
        fw = new FileWriter(file);
        int year = 2008;
        for (int i = 0; i < 14; i++){
            String front = "https://github.com/search?l=&q=Deep+Learning+updated%3A%3C%3D";
            String rear = "-12-31&type=wikis";
            String url = String.format("%s%d%s", front, year, rear);
            Document doc = Jsoup.connect(url).get();
            Elements e = doc.select("#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results " +
                    "> div > div.d-flex.flex-column.flex-md-row.flex-justify-between.border-bottom.pb-3.position-relative > h3");
            String n = e.text().split(" ")[0];
            n = n.replaceAll(",", "");
            int number = Integer.parseInt(n);
            if (list.size() != 0)
                fw.write(String.format("%d: %d\n", year, number - list.get(list.size() - 1)));
            else
                fw.write(String.format("%d: %d\n", year, number));
            list.add(number);
            System.out.println(year);
            year++;
            fw.flush();
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        fw.close();
    }

    /*search the created time of wikis' information, unit: Month
     * store the statistics into the WikiTimeMonth
     * data from 2008.5.1 to 2022.4.30
     * */
    private static void CrawWikisTimeMonth() throws IOException {
        File file = new File("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\WikiTimeMonth");
        fw = new FileWriter(file);
        int year = 2008;
        int month = 6;
        List<Integer> list = new ArrayList<>();
        while (year < 2022 || month < 6){
            String front = "https://github.com/search?l=&q=Deep+Learning+updated%3A%3C";
            String rear = "-01&type=wikis";
            String url = null;
            if (month > 9) {
                url = String.format("%s%d-%d%s", front, year, month, rear);
            } else {
                url = String.format("%s%d-0%d%s", front, year, month, rear);
            }
            Document doc = Jsoup.connect(url).get();
            Elements e = doc.select("#js-pjax-container > div > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results " +
                    "> div > div.d-flex.flex-column.flex-md-row.flex-justify-between.border-bottom.pb-3.position-relative > h3");
            String n = e.text().split("\\s+")[0];
            n = n.replaceAll(",", "");
            int number = Integer.parseInt(n);
            if (list.size() != 0)
                fw.write(String.format("%d-%d: %d\n", (month==1)?year-1:year, (month==1)?12:month-1, number - list.get(list.size() - 1)));
            else
                fw.write(String.format("%d-%d: %d\n", year, (month==1)?12:month-1, number));
            list.add(number);
            System.out.println((month==1)?year-1:year+" "+((month==1)?12:month-1));
            if (month == 12){
                month = 1;
                year++;
            } else {
                month++;
            }
            fw.flush();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
    * Craw the titles and the number of repositories of topics about deep learning
    * */
    private static void CrawTopicsRepositories() throws IOException {
        File file = new File("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\TopicRepositories");
        fw = new FileWriter(file);
        Document Page = Jsoup.connect("https://github.com/search?q=deep+learning&type=topics").get();
        for (int i = 0; i < 25; i++){
            for (int j = 1; j <=10; j++) {
                Elements e = Page.select(String.format("#topic_search_results > div.topic-list > div:nth-child(%d) > div.mt-n1.flex-auto",j));
                String title = e.select("a[title]").text();
                String repositories = e.select("span.mr-3").text().split("\\s+")[0];
                fw.write(String.format("%s,%s\n",title,repositories));
                fw.flush();
            }
            Elements nextURL = Page.select("#topic_search_results > div.paginate-container.codesearch-pagination-container > div > a.next_page");
            String URL = String.format("https://github.com%s",nextURL.attr("href"));
            Page = Jsoup.connect(URL).get();
            System.out.println(i+1);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Craw the Projects' title and its tags,stars into the file "ProjectsTags"
    * and the store structure in the file is
    *               title
    *               tags
    *               stars (not all entry has)->language
    * each file has 1000 entries
    * ProjectsTags -> stars >= 123(finished)
    * ProjectsTags2 -> 32<=stars <= 122(finished)
    * ProjectsTags3 -> 14<=stars <= 31(finished)
    * ProjectsTags4 -> 7<=stars <= 13(finished)
    * ProjectsTags5 -> 4<=stars <= 6(finished)
    * */
    private static void CrawProjectsTags(String url,int index) throws IOException {
        File file = new File("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\ProjectsTags"+index);
        fw = new FileWriter(file);
        Document Page = Jsoup.connect(url).get();
        for (int i = 0; i < 100; i++){
            for (int j = 1; j <=10; j++) {
                Elements e = Page.select(String.format("#js-pjax-container > div.container-lg.px-md-2.mt-lg-4.clearfix > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > ul > li:nth-child(%d) > div.mt-n1.flex-auto",j));
                String title = e.select("div.f4.text-normal").text();
                String tags = e.select("a[data-ga-click]").text();
                String stars = e.select("a.Link--muted").text().split("\\s+")[0];
                String language = e.select("span[itemprop]").text();
                fw.write(String.format("%s\n%s\n%s %s\n",title,tags,stars,language));
                fw.flush();
            }
            Elements nextURL = Page.select("#js-pjax-container > div.container-lg.px-md-2.mt-lg-4.clearfix > div.col-12.col-md-9.float-left.px-2.pt-3.pt-md-0.codesearch-results > div > div.paginate-container.codesearch-pagination-container > div > a.next_page");
            String URL = String.format("https://github.com%s",nextURL.attr("href"));
            Page = Jsoup.connect(URL).get();
            System.out.println(i+1);
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
