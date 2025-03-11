package task.library.exception;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return NotFoundException.authorNotFoundException();
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
