package liaison.linkit.chat.domain.repository;


import liaison.linkit.chat.domain.CrawlingInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrawlingRepository extends MongoRepository<CrawlingInfo, String> {
    /**
     * Finds CrawlingInfo by name.
     *
     * @param name the name of the CrawlingInfo document.
     * @return the matching CrawlingInfo object or null if not found.
     */
    CrawlingInfo findByName(String name);
}
