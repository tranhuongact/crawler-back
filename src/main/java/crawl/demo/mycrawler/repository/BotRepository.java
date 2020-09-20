package crawl.demo.mycrawler.repository;

import crawl.demo.mycrawler.model.Bot;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotRepository extends ElasticsearchCrudRepository<Bot, String> {

}
