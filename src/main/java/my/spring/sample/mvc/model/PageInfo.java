package my.spring.sample.mvc.model;

public class PageInfo {

    private Long page;

    private Long limit;

    private Long count;

    private Long totalCount;

    private Long totalPages;

    public PageInfo(Long page, Long limit, Long count, Long totalCount) {
        this.page = page;
        this.limit = limit;
        this.count = count;
        this.totalCount = totalCount;
        this.totalPages = totalCount > 0 ? 1L : 0L;
        if(totalCount > 0 && limit > 0) {
            this.totalPages = totalCount/limit;
            if((totalCount % limit) > 0)
                this.totalPages += 1;
        }
    }
}
