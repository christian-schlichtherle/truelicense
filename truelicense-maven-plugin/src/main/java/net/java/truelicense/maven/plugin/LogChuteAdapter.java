package net.java.truelicense.maven.plugin;

import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * @author Christian Schlichtherle
 */
final class LogChuteAdapter implements LogChute {

    private final Log log;

    LogChuteAdapter(final Log log) { this.log = log; }

    public void init(RuntimeServices rs) throws Exception { }

    public void log(final int level, final String message) {
        if (!isLevelEnabled(level)) return;
        switch (level) {
            case DEBUG_ID:
                log.debug(message);
                break;
            case INFO_ID:
                log.info(message);
                break;
            case WARN_ID:
                log.warn(message);
                break;
            case ERROR_ID:
                log.error(message);
                break;
            default:
                assert false;
        }
    }

    public void log(final int level, final String message, final Throwable t) {
        if (!isLevelEnabled(level)) return;
        switch (level) {
            case DEBUG_ID:
                log.debug(message, t);
                break;
            case INFO_ID:
                log.info(message, t);
                break;
            case WARN_ID:
                log.warn(message, t);
                break;
            case ERROR_ID:
                log.error(message, t);
                break;
            default:
                assert false;
        }
    }

    public boolean isLevelEnabled(final int level) {
        switch (level) {
            case DEBUG_ID:
                return log.isDebugEnabled();
            case INFO_ID:
                return log.isInfoEnabled();
            case WARN_ID:
                return log.isWarnEnabled();
            case ERROR_ID:
                return log.isErrorEnabled();
            default:
                return false;
        }
    }
}
