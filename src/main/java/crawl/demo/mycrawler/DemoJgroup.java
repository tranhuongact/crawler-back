package crawl.demo.mycrawler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crawl.demo.mycrawler.model.Post;
import crawl.demo.mycrawler.model.PostRequest;
import crawl.demo.mycrawler.service.PostService;
import org.jgroups.*;
import org.jgroups.util.Util;
import org.json.JSONArray;

public class DemoJgroup extends ReceiverAdapter {
    private JChannel channel;
    private View view;
    private List<Post> postList;
    private final List<String> state;

    public DemoJgroup() {
        this.postList = new ArrayList<>();
        this.state = new LinkedList<String>();
    }

    public static void main(String[] args) throws Exception {
        new DemoJgroup().listen();
    }

    private void listen() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("Distributed Web Crawler");
    }

    public void viewAccepted(View new_view) {
        view = channel.getView();
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        if (msg.getObject().toString().contains("Request. ")) {
            String[] messList = msg.getObject().toString().split("Request. ");
            PostRequest request = new PostRequest();
            ObjectMapper mapper = new ObjectMapper();
            String json = messList[1];
            try {
                request = mapper.readValue(json, PostRequest.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            PostService service = new PostService();
            postList = service.crawlPost(request);
            boolean isStatus = true;
            if (isStatus) {
                JSONArray array = new JSONArray(postList);
                Message message = new Message(view.getMembers().get(0), "\"" + array + "\"");
                try {
                    channel.send(message);
                    isStatus = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

}
