package hiei.util;

import hiei.HieiMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HieiLogger {
    private final Logger log = LoggerFactory.getLogger(HieiMain.class);

    public void debug(String debug) { log.info(debug); }

    public void info(String info) { log.info(info); }

    public void warn(String msg) { log.warn(msg); }

    public void error(String msg) { log.error(msg); }

    public void error(Throwable error) { log.error(error.toString(), error); }

    public void error(String msg, Throwable error) { log.error(msg, error); }
}
