package my.spring.sample.mvc.utils;

import com.google.common.base.Strings;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

public class MongoQueryUtil {

    public static Criteria addCriteria(Criteria criteria, String name) {
        if(criteria == null) {
            return Criteria.where(name);
        } else {
            return criteria.and(name);
        }
    }

    public static Sort retrieveSort(String sortBy) {
        if(Strings.isNullOrEmpty(sortBy)) {
            sortBy = "updatedDesc";
        }
        switch(sortBy) {
            case "createdDesc":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "createdAsc":
                return Sort.by(Sort.Direction.ASC, "createdAt");
            case "updatedDesc":
                return Sort.by(Sort.Direction.DESC, "updatedAt");
            case "updatedAsc":
                return Sort.by(Sort.Direction.ASC, "updatedAt");
            default:
                return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
    }
}
