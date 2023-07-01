package pe.todotic.bookstoreapi_s2.exception;

import org.springframework.cache.interceptor.CacheOperationInvoker;

// Si se genera algun error de este tipo se retorna un error 500
public class StorageException extends RuntimeException {
    public StorageException (String message) {
        super(message);
    }

    public StorageException (String message, Throwable ex) {
        super(message, ex);
    }
}
