package application.management.task.repository;

import application.management.task.model.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface HistoryRepository extends MongoRepository<History, BigInteger> {

    List<History> findAllByApplicationId(BigInteger id);
}
