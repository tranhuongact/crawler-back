package crawl.demo.mycrawler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import crawl.demo.mycrawler.exception.ResourceNotFoundException;
import crawl.demo.mycrawler.model.Bot;
import crawl.demo.mycrawler.model.PostRequest;
import crawl.demo.mycrawler.service.BotService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BotController {

    private BotService botService;

    @Autowired
    public BotController(BotService botService) {
        this.botService = botService;
    }

    @ApiOperation(value = "Add a Bot")
    @PostMapping("bots")
    public Bot createBot(@RequestBody PostRequest request) {
        return botService.createBot(request);
    }

    @ApiOperation(value = "Update a Bot")
    @PutMapping("bots/{id}")
    public Bot updateBot(@PathVariable(value = "id") String id,
                         @RequestBody PostRequest request) {
        try {
            return botService.updateBot(id, request);
        } catch (Exception e) {
            return botService.createBot(request);
        }
    }

    @ApiOperation(value = "Delete a Bot")
    @DeleteMapping("bots/{id}")
    public ResponseEntity<String> deleteBot(@PathVariable(value = "id") String id) throws ResourceNotFoundException {
        return botService.deleteBot(id);
    }

    @ApiOperation(value = "View a Bot by id")
    @GetMapping("bots/{id}")
    public Bot getBotById(@PathVariable(value = "id") String id) throws ResourceNotFoundException {
        return botService.getBotById(id);
    }

    @ApiOperation(value = "View all Bot")
    @GetMapping("bots")
    public List<Bot> getAllBots() {
        return botService.getAllBots();
    }

    @ApiOperation(value = "User send request")
    @PostMapping("crawl")
    public String crawl(@RequestBody PostRequest request) throws JsonProcessingException {
        return botService.crawl(request);
    }
}
