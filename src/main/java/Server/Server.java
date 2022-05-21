package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    /*
    * 展示tag对应的repo数量
    * GET/TagName/title
    * 展示对应世间的wiki数量
    * GET/WikiTime/time
    * 搜索
    * GET/repository/Tag/TagTitle
    * GET/repository/star/=starnum
    * GET/repository/star/>starnum
    * GET/repository/star/<starnum
    * GET/repository/url/title
    * REGISTER/name/password
    * */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            Runnable runnable = new Service(serverSocket.accept());
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    static class Service implements Runnable{
        BufferedReader input = null;
        PrintWriter output = null;
        Socket socket = null;

        public Service(Socket socket) throws IOException {
            this.socket = socket;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());
        }


        @Override
        public void run() {
            try {
                FunctionService fs = new FunctionService(output);
                fs.offerService(input.readLine());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
