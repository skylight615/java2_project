package Server;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FunctionService {
    Data data;
    PrintWriter out;
    public FunctionService(PrintWriter out) throws IOException {
        this.data = new Data();
        this.out = out;
    }

    public void offerService(String instruction){
        String[] s = instruction.split("/");
        if (s[0].equals("GET")){
            switch (s[1]) {
                case "TagName" -> out.println(GetTagRepNum(s[2]));
                case "WikiTime" -> out.println(GetWikiTime(s[2]));
                case "repository" -> {
                    List<repository> list = new ArrayList<repository>();
                    if (s[2].equals("star")) {
                        if (s[3].charAt(0) == '=') {
                            list = GetRepEqualsStar(Integer.parseInt(s[3].substring(1)));
                        } else if (s[3].charAt(0) == '<') {
                            if (s[3].charAt(1) == '=') {
                                list = GetRepSmallAndEqualsStar(Integer.parseInt(s[3].substring(2)));
                            } else {
                                list = GetRepSmallStar(Integer.parseInt(s[3].substring(1)));
                            }
                        } else {
                            if (s[3].charAt(1) == '=') {
                                list = GetRepBigEqualStar(Integer.parseInt(s[3].substring(2)));
                            } else {
                                list = GetRepBigStar(Integer.parseInt(s[3].substring(1)));
                            }
                        }
                    } else if (s[2].equals("Tag")) {
                        list = GetTagRepTitleAndStars(s[3]);
                    } else if (s[2].equals("url")){
                        Optional<repository> tem = GetRepUrl(s[3]);
                        if (tem.isPresent()) {
                            list.add(tem.get());
                        }
                    }
                    if (list.size() != 0) {
                        out.println(dealRepositoryList(list));
                    } else {
                        out.println("Don't find");
                    }
                }
            }
        } else {
            out.println(RegisterNewUser(s[1] + "," + s[2]));
        }
        out.flush();
    };

    public String dealRepositoryList(List<repository> list){
        StringBuilder sb = new StringBuilder();
        list.forEach(o-> sb.append(o.title).append(",").append(o.starsNum).append(",").append(o.language).append(","));
        return sb.toString();
    }

    public int GetTagRepNum(String TagName){
        return (int) data.repositoryList.stream().filter(o -> o.topicList.contains(TagName)).count();
    }

    public int GetWikiTime(String time){
        if (time.contains("-")){
            return data.wikiMonth.get(time);
        }
        return data.wikiYear.get(time);
    }

    public List<repository> GetTagRepTitleAndStars(String TagName){
        return data.repositoryList.stream().filter(o->o.topicList.contains(TagName)).collect(Collectors.toList());
    }

    public List<repository> GetRepEqualsStar(int StarNum){
        return data.repositoryList.stream().filter(o->o.starsNum == StarNum).collect(Collectors.toList());
    }

    public List<repository> GetRepBigStar(int StarNum){
        return data.repositoryList.stream().filter(o->o.starsNum > StarNum).collect(Collectors.toList());
    }

    public List<repository> GetRepBigEqualStar(int StarNum){
        return data.repositoryList.stream().filter(o->o.starsNum >= StarNum).collect(Collectors.toList());
    }

    public List<repository> GetRepSmallStar(int StarNum){
        return data.repositoryList.stream().filter(o->o.starsNum < StarNum).collect(Collectors.toList());
    }

    public List<repository> GetRepSmallAndEqualsStar(int StarNum){
        return data.repositoryList.stream().filter(o->o.starsNum <= StarNum).collect(Collectors.toList());
    }

    public Optional<repository> GetRepUrl(String Title){
        return data.repositoryList.stream().filter(o->o.title.equals(Title)).findFirst();
    }

    public String RegisterNewUser(String information){
        String[] strings = information.split(",");
        String name = strings[0];
        String password = strings[1];
        try {
            FileReader fr = new FileReader("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\UserStore");
            FileWriter fw = new FileWriter("C:\\Users\\12078\\IdeaProjects\\Java2Project\\src\\main\\resources\\UserStore");
            char[] buffer = new char[1000000];
            int length = fr.read(buffer);
            String s = String.valueOf(buffer).substring(0,length);
            String[] entries = s.split("\n");
            boolean ifExist = Arrays.stream(entries).anyMatch(o-> o.split(",")[0].equals(name) && o.split(",")[1].equals(password));
            if (ifExist){
                return "The password has existed!";
            } else {
                fw.write(name+","+password+"\n");
                fw.flush();
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Successful";
    }
}
