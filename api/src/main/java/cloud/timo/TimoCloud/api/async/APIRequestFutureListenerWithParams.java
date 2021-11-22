package cloud.timo.TimoCloud.api.async;

public interface APIRequestFutureListenerWithParams<T> extends APIRequestFutureListener<T> {

    /**
     * Called when the API request is completed
     */
    void requestComplete(T response);
}
