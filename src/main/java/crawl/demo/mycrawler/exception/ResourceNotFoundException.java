package crawl.demo.mycrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{
    private final static Long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

