package crawl.demo.mycrawler.repository;

import crawl.demo.mycrawler.model.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchCrudRepository<Post, String> {
}
