package crawl.demo.mycrawler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crawl.demo.mycrawler.exception.ResourceNotFoundException;
import crawl.demo.mycrawler.model.Bot;
import crawl.demo.mycrawler.model.PostRequest;
import crawl.demo.mycrawler.repository.BotRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class BotService {

    private ElasticsearchRestTemplate elasticsearchTemplate;

    private BotRepository botRepository;

    Client client;

    @Autowired
    public BotService(ElasticsearchRestTemplate elasticsearchTemplate, BotRepository botRepository) throws SocketException, UnknownHostException {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.botRepository = botRepository;
        client = new Client();
    }


    public Bot createBot(PostRequest request) {
        Bot bot = new Bot();
        bot.setId(UUID.randomUUID().toString().replace("-", ""));
        BeanUtils.copyProperties(request, bot);
        return botRepository.save(bot);
    }


    public Bot updateBot(String id, PostRequest request) throws ResourceNotFoundException {
        Optional<Bot> botOpt = botRepository.findById(id);
        if (botOpt.isPresent()) {
            Bot bot = botOpt.get();
            BeanUtils.copyProperties(request, bot);
            botRepository.save(bot);
            return bot;
        }
        return null;
    }

    public ResponseEntity<String> deleteBot(String id) throws ResourceNotFoundException {
        Optional<Bot> botOpt = botRepository.findById(id);
        if (botOpt.isPresent()) {
            botRepository.deleteById(id);
            return ResponseEntity.ok("Delete successfully!");
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public Bot getBotById(String id) throws ResourceNotFoundException {
        Optional<Bot> botOpt = botRepository.findById(id);
        if (botOpt.isPresent()) {
            return botOpt.get();
        }
        return null;
    }

    public List<Bot> getAllBots() {
        Iterator<Bot> iterator = botRepository.findAll().iterator();
        List<Bot> botList = new ArrayList<>();
        while (iterator.hasNext()) {
            botList.add(iterator.next());
        }
        return botList;
    }

    public String crawl(PostRequest request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);
        client.run(json);
        return "Success";
    }
}
