package com.parq.parq.connection;

/**
 * Created by piotr on 14.01.17.
 */

public interface APIResponse {
    void responseSuccess(AbstractAPI abstractAPI);
    void responseError(AbstractAPI abstractAPI);
}
