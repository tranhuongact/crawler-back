package crawl.demo.mycrawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import crawl.demo.mycrawler.model.Post;
import crawl.demo.mycrawler.model.PostRequest;
import crawl.demo.mycrawler.service.PostService;
import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DemoJgroupCoor extends ReceiverAdapter {

    private final List<String> state;
    private JChannel channel;
    private View view;
    private final ConcurrentLinkedQueue<PostRequest> frontier;
    private final List<PostRequest> pageVisited;
    private final HashMap<String, Node> nodeMap;
    private final ScheduledExecutorService executorService;
    private Server server;

    public DemoJgroupCoor() {
        this.state = new LinkedList<String>();
        this.frontier = new ConcurrentLinkedQueue<>();
        this.nodeMap = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.pageVisited = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        new DemoJgroupCoor().start();
    }

    private void start() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("Distributed Web Crawler");
        channel.getState(null, 10000);
        server = new Server();
        server.run();
        channel.close();
    }

    private void handleLink() {
        if (!frontier.isEmpty()) {
            Node node;
            PostRequest request = null;
            for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
                node = entry.getValue();
                if (!entry.getValue().status) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        request = frontier.poll();
                        pageVisited.add(request);
                        String json = mapper.writeValueAsString(request);
                        String line = "Request. " + json;
                        try {
                            channel.send(new Message(entry.getValue().address, line));
                            System.out.println("Coor send request " + line + " to " + entry.getValue().address);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    node.status = true;
                    node.url = Objects.requireNonNull(request).getSeedPage();
                }
            }
        }
    }

    public void viewAccepted(View newView) {
        int index = newView.getMembers().size();
        if (newView.getMembers().get(index - 1) != newView.getMembers().get(0)) { //check neu ko la nut dieu phoi thi them vao nodeMap
            nodeMap.putIfAbsent(newView.getMembers().get(index - 1).toString(), new Node(newView.getMembers().get(index - 1), false, ""));
        }

        List<Address> left_members = View.leftMembers(this.view, newView);
        if (left_members != null && !left_members.isEmpty()) {
            for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
                Node node = entry.getValue();
                if (node.address == left_members.get(0)) {
                    if (node.status == true) {
                        for (PostRequest request : pageVisited) {
                            if (request.getSeedPage().equals(node.url)) {
                                frontier.add(request);
                            }
                        }
                    }
                    nodeMap.remove(entry);
                }
            }
        }

        handleLink();
        this.view = newView;
    }

    public void receive(Message msg) {
        String line = msg.getSrc() + ": " + msg.getObject();

        String res = msg.getObject().toString();
        res = res.substring(1, res.length() - 1);

        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);
            List<Post> postList = mapper.readValue(res, new TypeReference<List<Post>>() {});

            PostService service = new PostService();
            service.extractToExcel(postList);
            service.savePost(postList);

            for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
                Node node = entry.getValue();
                if (node.address == msg.getSrc()) {
                    node.status = false;
                }
            }

            handleLink();
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (state) {
            state.add(line);
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        List<String> list = Util.objectFromStream(new DataInputStream(input));

        synchronized (state) {
            state.clear();
            state.addAll(list);
        }
    }

    public static class Node {
        Address address;
        boolean status;
        String url;

        public Node() {
        }

        public Node(Address address, boolean status, String url) {
            this.address = address;
            this.status = status;
            this.url = url;
        }
    }

    public class Server {

        private DatagramSocket socket = null;
        private int SERVER_PORT = 7; // Cổng mặc định của Echo Server
        private byte[] buf = new byte[4096]; // Vùng đệm chứa dữ liệu cho gói tin nhận

        public Server() throws SocketException {
            System.out.println("Binding to port " + SERVER_PORT + ", please wait  ...");
            socket = new DatagramSocket(SERVER_PORT); // Tạo Socket với cổng là 7
        }

        public void run() {
            try {
                while (true) { // Tạo gói tin nhận
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet); // Chờ nhận gói tin gởi đến

                    // Lấy dữ liệu khỏi gói tin nhận
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    ObjectMapper mapper = new ObjectMapper();
                    PostRequest request = mapper.readValue(message, PostRequest.class);
                    frontier.add(request);
                    handleLink();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
