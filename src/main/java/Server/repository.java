package Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class repository {
    String title;
    List<String> topicList = new ArrayList<>();
    int starsNum;
    String language;
    String url;

    public repository(String title, String topics, int starsNum, String language,String url){
        this.title = title;
        String[] topicsList = topics.split(" ");
        this.topicList.addAll(Arrays.asList(topicsList));
        this.starsNum = starsNum;
        this.language = language;
        this.url = url;
    }

    public repository(String title, String topics, int starsNum){
        this.title = title;
        String[] topicsList = topics.split(" ");
        this.topicList.addAll(Arrays.asList(topicsList));
        this.starsNum = starsNum;
        this.language = null;
    }
}
