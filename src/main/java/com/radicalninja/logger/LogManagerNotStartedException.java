package com.radicalninja.logger;

class LogManagerNotStartedException extends RuntimeException {

    LogManagerNotStartedException() {
        super("LogManager not initialized. Call LogManager.init(Context) first.");
    }

}
