import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

// define the USER_HOME variable setting its value
// to that of the "user.home" system property
def USER_HOME = System.getProperty("user.home")

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %5level %logger{1} - %msg%n"
    }
}

appender("FILE", FileAppender) {
    // make use of the USER_HOME variable
    println "Setting [file] property to [${USER_HOME}/myApp.log]"
    file = "${USER_HOME}/RTS.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%msg%n"
    }
}

root(INFO, ["STDOUT"])
