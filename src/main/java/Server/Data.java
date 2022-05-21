package Server;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Data {
    ArrayList<repository> repositoryList = new ArrayList<>();
    ArrayList<Topics> topicsList = new ArrayList<>();
    HashMap<String,Integer> wikiYear = new HashMap<>();
    HashMap<String,Integer> wikiMonth = new HashMap<>();
    private String filePath = "C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\";

    public Data() throws IOException {
        loadTopics();
        loadRepositories();
        loadWikiTime();
    }

    private void loadWikiTime() throws IOException {
        for (int i = 0; i < 2; i++) {
            FileReader fr = new FileReader(filePath + "WikiTimeYear");
            if (i == 1) {
                fr = new FileReader(filePath + "WikiTimeMonth");
            }
            char[] buffer = new char[1000000];
            int length = fr.read(buffer);
            String s = String.valueOf(buffer).substring(0, length);
            String[] entries = s.split("\n");
            int index = i;
            Arrays.stream(entries).forEach(o->{
                String time = o.split(": ")[0];
                int number = Integer.parseInt(o.split(": ")[1]);
                if (index == 0){
                    wikiYear.put(time,number);
                } else {
                    wikiMonth.put(time,number);
                }
            });
        }
    }

    private void loadTopics() throws IOException {
        String fileName = "TopicRepositories";
        FileReader fr = new FileReader(filePath+fileName);
        char[] buffer = new char[1000000];
        int length = fr.read(buffer);
        String topics = String.valueOf(buffer).substring(0,length);
        String[] list = topics.split("\n");
        Arrays.stream(list).forEach(o->{
            String s1 = o.split(",")[0];
            String s2 = o.split(",")[1];
            if (s2.charAt(s2.length()-1) != 'k') {
                topicsList.add(new Topics(s1, Integer.parseInt(s2)));
            } else {
                int num = (int) (Double.parseDouble(s2.substring(0,s2.length()-1))*1000);
                topicsList.add(new Topics(s1, num));
            }
        });
    }

    private void loadRepositories() throws IOException {
        FileReader fr = new FileReader(filePath+"ProjectsTags");
        for (int i = 1; i <= 5; i++){
            char[] buffer = new char[1000000];
            int length = fr.read(buffer);
            String projects = String.valueOf(buffer).substring(0,length);
            String[] List = projects.split("\n");
            for (int j = 0; j < List.length; j+=3){
                String[] tem = List[j+2].split(" ");
                String language = null;
                String url = "https://github.com/"+List[j];
                if (tem.length != 1) {
                    language = tem[1];
                }
                String stars = tem[0];
                int starsNum = 0;
                if(stars.charAt(stars.length()-1) == 'k'){
                    starsNum = (int)(Double.parseDouble(stars.substring(0,stars.length()-1))*1000);
                } else {
                    starsNum = Integer.parseInt(stars);
                }
                if (language != null) {
                    repositoryList.add(new repository(List[j], List[j + 1], starsNum, language,url));
                } else {
                    repositoryList.add(new repository(List[j], List[j + 1], starsNum));
                }
            }
            if (i == 5){
                break;
            }
            fr = new FileReader(filePath+"ProjectsTags"+(i+1));
        }
    }
}
